package primeNext.ftc.hardware

import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.Servo
import com.rowanmcalpin.nextftc.core.Subsystem
import com.rowanmcalpin.nextftc.core.command.Command
import kotlin.math.abs

/**
 * This command moves multiple Axon servos to a target position, and
 * uses one or multiple analog inputs to correctly mark the command as done.
 *
 * @param servos the list of servos to move
 * @param targetPosition the position to move the servos to
 * @param analogInputs the list of analog inputs to read the servos' positions from
 * @param subsystems the subsystems this command interacts with (should be whatever
 *                      subsystem holds this command)
 * @param tolerance the (optional) tolerance value for the servos' positions
 */
class MultipleAxonServosToPosition @JvmOverloads constructor(
    private val servos: List<Servo>,
    private val targetPosition: Double,
    private val analogInputs: List<AnalogInput>,
    override val subsystems: Set<Subsystem> = setOf(),
    private val tolerance: Double = DEFAULT_TOLERANCE
) : Command() {
    constructor(
        servos: List<Servo>,
        targetPosition: Double,
        analogInputs: List<AnalogInput>,
        subsystem: Subsystem,
        tolerance: Double = DEFAULT_TOLERANCE
    ) :
            this(servos, targetPosition, analogInputs, setOf(subsystem), tolerance)

    companion object {
        private const val DEFAULT_TOLERANCE = 0.05
        private const val MAX_VOLTAGE = 3.3
    }

    override val isDone: Boolean
        get() {
            return analogInputs.all { analogInput ->
                val currentPosition = analogInput.voltage / MAX_VOLTAGE
                val directDiff = abs(currentPosition - targetPosition)
                directDiff <= tolerance
            }
        }

    override fun start() {
        servos.forEach {
            it.position = targetPosition
        }
    }
}