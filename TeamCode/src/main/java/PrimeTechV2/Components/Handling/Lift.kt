package PrimeTechV2.Components.Handling

import PrimeTechV2.Utils.PIDControllerWrapper
import com.arcrobotics.ftclib.controller.PIDController
import com.rowanmcalpin.nextftc.core.Subsystem
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorEx
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorGroup
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.RunToPosition

object Lift: Subsystem() {
    // region Declare Components
    lateinit var liftMotorLeft: MotorEx
    lateinit var liftMotorRight: MotorEx

    var motors: MotorGroup = MotorGroup(liftMotorLeft, liftMotorRight)

    val liftMotorLeftName = "extensionLeft"
    val liftMotorRightName = "extensionRight"

    val p = 0.01
    val i = 0.15
    val d = 0.00027
    val tolerance = 10.0
    val controller = PIDControllerWrapper(PIDController(p, i, d), tolerance)
    // endregion Declare Components
    val lowPosition = 0.0
    val highPosition = 1500.0
    val grabPosition = 40.0
    // region Declare Values

    // endregion Declare Values

    // region Commands
    val toLow: Command
        get() = RunToPosition(
            motors,
            lowPosition,
            controller,
            this
        )

    val toHigh: Command
        get() = RunToPosition(
            motors,
            highPosition,
            controller,
            this
        )

    val toGrabSpecimen: Command
        get() = RunToPosition(
            motors,
            grabPosition,
            controller,
            this
        )
    // endregion Commands

    override fun initialize() {
        liftMotorLeft = MotorEx(liftMotorLeftName)
        liftMotorRight = MotorEx(liftMotorRightName)
    }
}