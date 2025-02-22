package PrimeTechV2.Components.Handling

import com.qualcomm.robotcore.hardware.Servo
import com.rowanmcalpin.nextftc.core.Subsystem
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.ftc.OpModeData
import com.rowanmcalpin.nextftc.ftc.hardware.ServoToPosition

object Claw : Subsystem() {
    // region Declare Components
    private lateinit var openingServo: Servo
    private val openingServoName = "openingServo"

    private lateinit var rotationServo: Servo
    private val rotationServoName = "rotationServo"

    private lateinit var frontBackServo: Servo
    private val frontBackServoName = "frontBackServoRight"
    // endregion Declare Components

    // region Declare Values
    private val openPosition = 0.0
    private val closedPosition = 0.9

    private val frontPosition = 0.0
    private val grabSpecimenClawPivotPosition = 0.55
    private val midPosition = 0.5
    private val backPosition = 0.8

    private val rotationVertical = 0.0
    private val rotationHorizontal = 1.0
    // endregion Declare Values

    // region Commands
    val open: Command
        get() = ServoToPosition(
            openingServo,
            openPosition,
            this
        )

    val close: Command
        get() = ServoToPosition(
            openingServo,
            closedPosition,
            this
        )

    val vertical: Command
        get() = ServoToPosition(
            rotationServo,
            rotationVertical,
            this
        )

    val horizontal: Command
        get() = ServoToPosition(
            rotationServo,
            rotationHorizontal,
            this
        )

    val front: Command
        get() = ServoToPosition(
            frontBackServo,
            frontPosition,
            this
        )

    val mid: Command
        get() = ServoToPosition(
            frontBackServo,
            midPosition,
            this
        )

    val back: Command
        get() = ServoToPosition(
            frontBackServo,
            backPosition,
            this
        )

    val grabSpecimenClawPivot: Command
        get() = ServoToPosition(
            frontBackServo,
            grabSpecimenClawPivotPosition,
            this
        )
    // endregion Commands

    override fun initialize() {
        openingServo = OpModeData.hardwareMap.get(Servo::class.java, openingServoName)
        rotationServo = OpModeData.hardwareMap.get(Servo::class.java, rotationServoName)
        frontBackServo = OpModeData.hardwareMap.get(Servo::class.java, frontBackServoName)
    }
}