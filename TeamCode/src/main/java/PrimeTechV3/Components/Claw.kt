package PrimeTechV3.Components

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

/**
 * The claw on the end of the arm. It is built from three servos:
 *
 * - **grip** - opens and closes the jaws, so the robot can hold a sample or a specimen.
 * - **wrist** - rolls the jaws sideways. Samples lie flat on the floor in different
 *   orientations, so the jaws sometimes have to be turned 90 degrees to line up with one.
 * - **claw pivot** - swings the whole claw forwards or backwards relative to the arm.
 *   Not to be confused with [Pivot], which is the motor that raises the whole arm.
 *
 * Servos are *open loop*: we tell one to go to a position and it goes there on its own,
 * but we get no feedback about where it actually is. That is why [isDone] is a fixed
 * time estimate ([SERVO_TRAVEL_TIME_MS]) rather than a real measurement - see its docs.
 *
 * This is a Kotlin `object`, which means there is exactly one Claw and you use it as
 * `Claw.openGrip()` without creating an instance. (In C++ terms: a singleton whose
 * members are all static.)
 */
object Claw {
    // region Hardware
    // The strings are device names from the Robot Configuration on the Driver Hub.
    // They must match that config exactly, so do not rename them here alone.
    private lateinit var gripServo: Servo          // config name: "openingServo"
    private lateinit var wristServo: Servo         // config name: "rotationServo"
    private lateinit var clawPivotServo: Servo     // config name: "frontBackServoRight"
    // endregion Hardware

    // region Servo positions
    // Servo positions are always in the range 0.0 .. 1.0. These numbers were found by
    // hand with the "test servo" OpMode (see PrimeTech/OpModes/Utils/Tuners/TestServo).
    private const val GRIP_OPEN = 0.9
    private const val GRIP_CLOSED = 0.1

    private const val CLAW_PIVOT_FRONT = 0.0
    private const val CLAW_PIVOT_BACK = 1.0
    private const val CLAW_PIVOT_SCORE_SPECIMEN = 0.65
    private const val CLAW_PIVOT_GRAB_SPECIMEN = 0.50
    private const val CLAW_PIVOT_SCORE_SAMPLE = 0.9
    private const val CLAW_PIVOT_GRAB_SAMPLE = 0.1

    private const val WRIST_VERTICAL = 0.5
    private const val WRIST_HORIZONTAL = 0.16
    // endregion Servo positions

    // region Current state
    // These only record what we last *asked* for. Because servos give no feedback,
    // they are not a guarantee of where the claw physically is.
    var gripState: GripState = GripState.CLOSED
        private set
    var wristState: WristRotation = WristRotation.VERTICAL
        private set
    var clawPivotState: ClawPivotState = ClawPivotState.BACK
        private set
    // endregion Current state

    // region Movement timer
    /** How long we assume a servo needs to finish moving, in milliseconds. */
    private const val SERVO_TRAVEL_TIME_MS = 150L

    /** When the most recent servo command was issued. Used by [isDone]. */
    private var lastMoveStartTimeMs: Long = 0
    // endregion Movement timer

    /** Looks the servos up in the robot configuration. Call once from the OpMode's `init`. */
    fun init(hardwareMap: HardwareMap) {
        gripServo = hardwareMap.get(Servo::class.java, "openingServo")
        wristServo = hardwareMap.get(Servo::class.java, "rotationServo")
        clawPivotServo = hardwareMap.get(Servo::class.java, "frontBackServoRight")
    }

    /** Call once from the OpMode's `start`, i.e. when the driver presses play. */
    fun start() {
        this.reset()
    }

    /** Moves the claw to its safe starting shape: jaws closed, wrist upright, pivoted back. */
    fun reset() {
        gripState = GripState.CLOSED
        wristState = WristRotation.VERTICAL
        clawPivotState = ClawPivotState.BACK

        gripServo.position = GRIP_CLOSED
        wristServo.position = WRIST_VERTICAL
        clawPivotServo.position = CLAW_PIVOT_BACK

        lastMoveStartTimeMs = System.currentTimeMillis()
    }

    /**
     * True once [SERVO_TRAVEL_TIME_MS] has passed since the last servo command.
     *
     * This is a guess, not a measurement. If a servo is slower than that (because it is
     * pushing against something, or has a long way to travel) this returns true while the
     * claw is still moving. If a sequence in [PrimeTechV3.Actions.Actions] is running
     * ahead of the hardware, raising [SERVO_TRAVEL_TIME_MS] is the first thing to try.
     */
    fun isDone(): Boolean {
        return System.currentTimeMillis() - lastMoveStartTimeMs >= SERVO_TRAVEL_TIME_MS
    }

    // region Grip
    fun openGrip() {
        gripServo.position = GRIP_OPEN
        gripState = GripState.OPEN
        lastMoveStartTimeMs = System.currentTimeMillis()
    }

    fun closeGrip() {
        gripServo.position = GRIP_CLOSED
        gripState = GripState.CLOSED
        lastMoveStartTimeMs = System.currentTimeMillis()
    }
    // endregion Grip

    // region Wrist
    fun rotateWristVertical() {
        wristServo.position = WRIST_VERTICAL
        wristState = WristRotation.VERTICAL
        lastMoveStartTimeMs = System.currentTimeMillis()
    }

    fun rotateWristHorizontal() {
        wristServo.position = WRIST_HORIZONTAL
        wristState = WristRotation.HORIZONTAL
        lastMoveStartTimeMs = System.currentTimeMillis()
    }
    // endregion Wrist

    // region Claw pivot
    /** Swings the claw to one of the named pivot positions. */
    fun setClawPivot(target: ClawPivotState) {
        val position = when (target) {
            ClawPivotState.FRONT -> CLAW_PIVOT_FRONT
            ClawPivotState.BACK -> CLAW_PIVOT_BACK
            ClawPivotState.SCORE_SPECIMEN -> CLAW_PIVOT_SCORE_SPECIMEN
            ClawPivotState.GRAB_SPECIMEN -> CLAW_PIVOT_GRAB_SPECIMEN
            ClawPivotState.SCORE_SAMPLE -> CLAW_PIVOT_SCORE_SAMPLE
            ClawPivotState.GRAB_SAMPLE -> CLAW_PIVOT_GRAB_SAMPLE
        }

        clawPivotServo.position = position
        clawPivotState = target
        lastMoveStartTimeMs = System.currentTimeMillis()
    }
    // endregion Claw pivot

    /** Whether the jaws are gripping something. */
    enum class GripState {
        OPEN, CLOSED
    }

    /** How far the wrist is rolled: upright, or turned 90 degrees to grab a sideways sample. */
    enum class WristRotation {
        VERTICAL, HORIZONTAL
    }

    /** Where the claw is swung relative to the arm. */
    enum class ClawPivotState {
        FRONT, BACK, SCORE_SPECIMEN, GRAB_SPECIMEN, SCORE_SAMPLE, GRAB_SAMPLE
    }
}
