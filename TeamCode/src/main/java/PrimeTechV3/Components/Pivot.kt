package PrimeTechV3.Components

import com.arcrobotics.ftclib.controller.PIDController
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap

/**
 * The pivot: the single motor that rotates the whole arm up and down.
 *
 * Positions are in **encoder ticks** - 0 is the arm resting down, larger numbers are
 * further up. Do not confuse this with the claw's own small pivot servo in [Claw].
 *
 * Like [Lift], the arm is held at its target by a [PIDController] run from [update],
 * so [update] must be called every loop.
 */
object Pivot {
    // region Hardware
    private lateinit var pivotMotor: DcMotorEx  // config name: "motorPivot"
    // endregion Hardware

    // region PID tuning
    // See the comment in Lift for what proportional / integral / derivative mean.
    private const val PROPORTIONAL_GAIN = 0.003
    private const val INTEGRAL_GAIN = 0.0
    private const val DERIVATIVE_GAIN = 0.0003

    /** How close (in ticks) normally counts as "arrived". */
    private const val DEFAULT_TOLERANCE_TICKS = 20.0

    /**
     * A looser tolerance used only for [PivotPosition.GRAB_SPECIMEN].
     *
     * Taking a specimen off the wall pushes the arm around, so the PID cannot settle as
     * tightly there. Without this the arm would never report "done" and the auto would stall.
     */
    private const val GRAB_SPECIMEN_TOLERANCE_TICKS = 50.0

    private lateinit var controller: PIDController
    // endregion PID tuning

    /** Target angle in encoder ticks. Read-only from outside; set it with [setPivotPosition]. */
    var targetTicks = 0.0
        private set

    /** Tick target for each named position. */
    private val targetTicksByPosition = mapOf(
        PivotPosition.LOW to 0.0,
        PivotPosition.SCORE_SPECIMEN to 2050.0,
        PivotPosition.SCORE_SAMPLE to 2100.0,
        PivotPosition.GRAB_SPECIMEN to 350.0
    )

    // region Current state
    /** The named position most recently asked for. */
    private var requestedPosition: PivotPosition = PivotPosition.LOW

    /** Whether the arm is holding still or driving to a target. */
    private var movementState: PivotState = PivotState.IDLE
    // endregion Current state

    /** Looks up the motor and sets up the controller. Call once from the OpMode's `init`. */
    fun init(hardwareMap: HardwareMap) {
        controller = PIDController(PROPORTIONAL_GAIN, INTEGRAL_GAIN, DERIVATIVE_GAIN)

        pivotMotor = hardwareMap.get(DcMotorEx::class.java, "motorPivot")

        // Coast rather than brake when power is 0, so the arm can be moved by hand.
        pivotMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT

        // Zero the encoder here, so wherever the arm rests at init counts as 0 ticks.
        // Then hand control to our own PID instead of the motor controller's.
        pivotMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        pivotMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        controller.setTolerance(DEFAULT_TOLERANCE_TICKS)
    }

    /** Call once from the OpMode's `start`. */
    fun start() {
        this.reset()
    }

    /** Sends the arm back down to its resting position. */
    fun reset() {
        setPivotPosition(PivotPosition.LOW)
    }

    /** True when the arm has arrived at its target. */
    fun isDone(): Boolean {
        return this.isAtTarget()
    }

    /** Rotates the arm to one of the named positions. */
    fun setPivotPosition(position: PivotPosition) {
        requestedPosition = position
        targetTicks = targetTicksByPosition[position] ?: 0.0

        controller.setTolerance(
            if (position == PivotPosition.GRAB_SPECIMEN) GRAB_SPECIMEN_TOLERANCE_TICKS
            else DEFAULT_TOLERANCE_TICKS
        )

        controller.setPoint = targetTicks
        movementState = PivotState.MOVING
    }

    /** Must be called every loop. Drives the motor towards [targetTicks]. */
    fun update() {
        val currentPositionTicks = pivotMotor.currentPosition.toDouble()
        val power = controller.calculate(currentPositionTicks).coerceIn(-1.0, 1.0)

        pivotMotor.power = power

        if (movementState == PivotState.MOVING && controller.atSetPoint())
            movementState = PivotState.IDLE
    }

    /** True when the arm is not moving to a target, i.e. it arrived or was never sent anywhere. */
    fun isAtTarget(): Boolean {
        return movementState == PivotState.IDLE
    }

    /** The named arm angles. */
    enum class PivotPosition {
        /** Arm down, resting. */
        LOW,

        /** Arm up at the chamber, to hang a specimen. */
        SCORE_SPECIMEN,

        /** Arm up over the basket, to drop a sample in. */
        SCORE_SAMPLE,

        /** Arm just off the floor, to take a specimen off the wall. */
        GRAB_SPECIMEN
    }

    /** What the arm is currently busy doing. */
    enum class PivotState {
        IDLE, MOVING
    }
}
