package primeNext.ftc.hardware

import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.Servo
import com.rowanmcalpin.nextftc.core.Subsystem
import com.rowanmcalpin.nextftc.core.command.Command
import kotlin.math.abs

/**
 * This command moves an Axon servo to a target position, and
 * uses an analog input to correctly mark the command as done.
 *
 * @param servo the servo to move
 * @param targetPosition the position to move the servo to
 * @param analogInput the analog input to read the servo position from
 * @param subsystems the subsystems this command interacts with (should be whatever
 *                      subsystem holds this command)
 * @param tolerance the (optional) tolerance value for the servo position
 */
class AxonServoToPosition @JvmOverloads constructor(
    private val servo: Servo,
    private val targetPosition: Double,
    private val analogInput: AnalogInput,
    override val subsystems: Set<Subsystem>,
    private val tolerance: Double = DEFAULT_TOLERANCE
) : Command() {
    constructor(
        servo: Servo,
        targetPosition: Double,
        analogInput: AnalogInput,
        subsystem: Subsystem,
        tolerance: Double = DEFAULT_TOLERANCE
    ) :
            this(servo, targetPosition, analogInput, setOf(subsystem), tolerance)

    companion object {
        private const val DEFAULT_TOLERANCE = 0.05
        private const val MAX_VOLTAGE = 3.3
    }

    override val isDone: Boolean
        get() {
            val currentPosition = analogInput.voltage / MAX_VOLTAGE
            val directDiff = abs(currentPosition - targetPosition)
            return directDiff <= tolerance
        }

    override fun start() {
        servo.position = targetPosition
    }
}