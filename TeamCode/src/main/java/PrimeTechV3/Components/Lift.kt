package PrimeTechV3.Components

import com.arcrobotics.ftclib.controller.PIDController
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

/**
 * The lift (also called the extension): two motors that slide the arm in and out.
 *
 * Positions are in **encoder ticks**, the raw counts reported by the motor's encoder.
 * Bigger number = further extended. 0 is fully retracted. The tick values below were
 * found by driving the lift by hand and reading the encoder off the dashboard.
 *
 * The lift is held in place by a [PIDController] running in [update]: every loop we
 * compare where the lift *is* against where we *want* it, and set motor power from the
 * difference. [update] therefore has to be called on every single loop of the OpMode,
 * or the lift will keep whatever power it had last and drift.
 */
object Lift {
    // region Hardware
    // Two motors pull the same slide, so they always get the same power. The left one is
    // reversed because it is mounted mirrored. Config names come from the Driver Hub.
    private lateinit var liftMotorLeft: DcMotorEx   // config name: "extensionLeft"
    private lateinit var liftMotorRight: DcMotorEx  // config name: "extensionRight"
    // endregion Hardware

    // region PID tuning
    // Classic PID gains. In short: the controller computes an "error" (target minus
    // current position) and turns it into motor power using these three numbers.
    //   proportional - how hard to push, in proportion to how far off we are.
    //   integral     - corrects a steady offset that never goes away. Unused here (0.0).
    //   derivative   - damping; pushes back against fast movement so we do not overshoot.
    private const val PROPORTIONAL_GAIN = 0.014
    private const val INTEGRAL_GAIN = 0.0
    private const val DERIVATIVE_GAIN = 0.0002

    /** How close (in ticks) counts as "arrived". Too small and the lift never reports done. */
    private const val POSITION_TOLERANCE_TICKS = 25.0

    private lateinit var controller: PIDController
    // endregion PID tuning

    // region Encoder-reset timings
    // resetEncoders() drives the lift gently down into its hard stop and calls that zero.
    // We cannot ask the slide "are you at the bottom?", so this is done purely on a timer.
    /** Power used to walk the lift down into the stop. Negative = retract. */
    private const val RETRACT_POWER = -0.5

    /** How long to drive down for. Power ramps from [RETRACT_POWER] to 0 across this time. */
    private const val RETRACT_DURATION_MS = 300L

    /** Extra time with the motors off, letting the slide settle before we zero the encoders. */
    private const val SETTLE_DURATION_MS = 50L
    // endregion Encoder-reset timings

    /** Target height in encoder ticks. Read-only from outside; set it with [setLiftPosition]. */
    var targetTicks = 0.0
        private set

    private var resetStartTimeMs: Long = 0

    /** Tick target for each named position. */
    private val targetTicksByPosition = mapOf(
        LiftPosition.LOW to 0.0,
        LiftPosition.BEFORE_SCORE_SPECIMEN to 90.0,
        LiftPosition.SCORE_SPECIMEN to 360.0,
        LiftPosition.SCORE_SAMPLE to 900.0,
        LiftPosition.LOAD_SPECIMEN to 100.0,
        LiftPosition.LOAD_SAMPLE to 100.0
    )

    // region Current state
    /** The named position most recently asked for. */
    private var requestedPosition: LiftPosition = LiftPosition.LOW

    /** Whether we are holding still, driving to a target, or re-zeroing the encoders. */
    private var movementState: LiftState = LiftState.IDLE
    // endregion Current state

    /** Looks up the motors and sets up the controller. Call once from the OpMode's `init`. */
    fun init(hardwareMap: HardwareMap) {
        controller = PIDController(PROPORTIONAL_GAIN, INTEGRAL_GAIN, DERIVATIVE_GAIN)

        liftMotorLeft = hardwareMap.get(DcMotorEx::class.java, "extensionLeft")
        liftMotorRight = hardwareMap.get(DcMotorEx::class.java, "extensionRight")

        // FLOAT means "coast when power is 0" rather than braking, so the slide can be
        // pushed by hand and does not fight gravity when we are not driving it.
        liftMotorLeft.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        liftMotorRight.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT

        // STOP_AND_RESET_ENCODER zeroes the counts, so wherever the lift sits at init
        // becomes position 0. Then we switch to RUN_WITHOUT_ENCODER because we drive the
        // motors with our own PID above rather than letting the motor controller do it.
        liftMotorLeft.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        liftMotorRight.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

        liftMotorLeft.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        liftMotorRight.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        liftMotorLeft.direction = DcMotorSimple.Direction.REVERSE

        controller.setTolerance(POSITION_TOLERANCE_TICKS)
    }

    /** Call once from the OpMode's `start`. */
    fun start() {
        this.reset()
    }

    /** Sends the lift back to fully retracted. */
    fun reset() {
        setLiftPosition(LiftPosition.LOW)
    }

    /** True when the lift has arrived (or has finished re-zeroing). */
    fun isDone(): Boolean {
        return this.isAtTarget()
    }

    /** Drives the lift to one of the named positions. */
    fun setLiftPosition(position: LiftPosition) {
        requestedPosition = position
        targetTicks = targetTicksByPosition[position] ?: 0.0
        controller.setPoint = targetTicks
        movementState = LiftState.MOVING
    }

    /**
     * Re-zeroes the encoders by retracting into the hard stop.
     *
     * Encoder counts drift over a match (a slipped belt, a stalled motor), so after a few
     * cycles "0 ticks" is no longer really the bottom. This runs the lift gently down,
     * waits, then declares that spot to be zero. Takes about
     * [RETRACT_DURATION_MS] + [SETTLE_DURATION_MS] milliseconds; [update] does the work.
     */
    fun resetEncoders() {
        resetStartTimeMs = System.currentTimeMillis()
        movementState = LiftState.RESETTING
    }

    /** Must be called every loop. Drives the motors towards [targetTicks]. */
    fun update() {
        if (movementState == LiftState.RESETTING) {
            updateEncoderReset()
            return
        }

        // Only the right encoder is read; both motors share one slide, so one is enough.
        val currentPositionTicks = liftMotorRight.currentPosition.toDouble()
        val power = controller.calculate(currentPositionTicks).coerceIn(-1.0, 1.0)

        liftMotorLeft.power = power
        liftMotorRight.power = power

        if (movementState == LiftState.MOVING && controller.atSetPoint())
            movementState = LiftState.IDLE
    }

    /** One loop of the timed encoder-reset routine started by [resetEncoders]. */
    private fun updateEncoderReset() {
        val elapsedMs = System.currentTimeMillis() - resetStartTimeMs

        if (elapsedMs < RETRACT_DURATION_MS) {
            // Ease off as we approach the stop: full RETRACT_POWER at the start, 0 at the end.
            // Hitting the stop at full power would jolt the slide and skip encoder counts.
            val rampRemaining = 1.0 - elapsedMs / RETRACT_DURATION_MS.toDouble()
            val power = RETRACT_POWER * rampRemaining
            liftMotorLeft.power = power
            liftMotorRight.power = power
        } else if (elapsedMs < RETRACT_DURATION_MS + SETTLE_DURATION_MS) {
            // Motors off, let the slide settle against the stop.
            liftMotorLeft.power = 0.0
            liftMotorRight.power = 0.0
        } else {
            // Wherever we are now becomes the new zero.
            liftMotorLeft.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            liftMotorRight.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

            liftMotorLeft.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            liftMotorRight.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

            movementState = LiftState.IDLE
            requestedPosition = LiftPosition.LOW
            targetTicks = 0.0
            controller.setPoint = targetTicks
        }
    }

    /** True when the lift is not moving to a target, i.e. it arrived or was never sent anywhere. */
    fun isAtTarget(): Boolean {
        return movementState == LiftState.IDLE
    }

    /** The named heights the lift is driven to. */
    enum class LiftPosition {
        /** Fully retracted. */
        LOW,

        /** Just off the bottom, ready to raise the arm without clipping the specimen. */
        BEFORE_SCORE_SPECIMEN,

        /** Extended to hang a specimen on the chamber bar. */
        SCORE_SPECIMEN,

        /** Extended high enough to drop a sample into the top basket. */
        SCORE_SAMPLE,

        /** Extended to take a specimen off the wall. */
        LOAD_SPECIMEN,
        /** Extended to pick a sample up off the floor. */
        LOAD_SAMPLE
    }

    /** What the lift is currently busy doing. */
    enum class LiftState {
        IDLE, MOVING, RESETTING
    }
}
