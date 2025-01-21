package PrimeTechV2.Components.Handling

import com.qualcomm.robotcore.hardware.Servo
import com.rowanmcalpin.nextftc.core.Subsystem
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.ftc.OpModeData
import com.rowanmcalpin.nextftc.ftc.hardware.ServoToPosition

object Claw: Subsystem() {
    // region Declare Components
    lateinit var openingServo: Servo
    val openingServoName = "openingServo"

    lateinit var rotationServo: Servo
    val rotationServoName = "rotationServo"

    lateinit var frontBackServo: Servo
    val frontBackServoName = "frontBackServoRight"
    // endregion Declare Components

    // region Declare Values
    val openPosition = 0.5
    val closePosition = 0.0

    val frontPosition = 0.95
    val midPosition = 0.4
    val backPosition = 0.0

    val rotationVertical = 0.25
    val rotationHorizontal = 0.5
    // endregion Declare Values

    // region Commands
    val open: Command
        get() = ServoToPosition(
            openingServo,
            openPosition,
            this)

    val close: Command
        get() = ServoToPosition(
            openingServo,
            closePosition,
            this)

    val vertical: Command
        get() = ServoToPosition(
            rotationServo,
            rotationVertical,
            this)

    val horizontal: Command
        get() = ServoToPosition(
            rotationServo,
            rotationHorizontal,
            this)

    val front: Command
        get() = ServoToPosition(
            frontBackServo,
            frontPosition,
            this)

    val mid: Command
        get() = ServoToPosition(
            frontBackServo,
            midPosition,
            this)

    val back: Command
        get() = ServoToPosition(
            frontBackServo,
            backPosition,
            this)
    // endregion Commands

    override fun initialize() {
        openingServo = OpModeData.hardwareMap.get(Servo::class.java, openingServoName)
        rotationServo = OpModeData.hardwareMap.get(Servo::class.java, rotationServoName)
        frontBackServo = OpModeData.hardwareMap.get(Servo::class.java, frontBackServoName)
    }
}