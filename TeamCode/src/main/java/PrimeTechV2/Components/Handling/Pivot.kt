package PrimeTechV2.Components.Handling

import PrimeTechV2.Utils.PIDControllerWrapper
import com.arcrobotics.ftclib.controller.PIDController
import com.rowanmcalpin.nextftc.core.Subsystem
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.HoldPosition
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorEx
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.RunToPosition

object Pivot: Subsystem() {
    // region Declare Components
    lateinit var pivotMotor: MotorEx

    val pivotMotorName = "motorPivot"

    val p = 0.0026
    val i = 0.015
    val d = 0.0005
    val tolerance = 50.0
    val controller = PIDControllerWrapper(PIDController(p, i, d), tolerance)
    // endregion Declare Components

    // region Declare Values
    val lowPosition = 0.0
    val highPosition = 1919.0
    val midPosition = 1050.0
    // endregion Declare Values

    // region Commands
    override val defaultCommand
        get() = HoldPosition(pivotMotor, controller, this)

    val toLow: Command
        get() = RunToPosition(
            pivotMotor,
            lowPosition,
            controller,
            this
        )

    val toHigh: Command
        get() = RunToPosition(
            pivotMotor,
            highPosition,
            controller,
            this
        )

    val toMid: Command
        get() = RunToPosition(
            pivotMotor,
            midPosition,
            controller,
            this
        )
    // endregion Commands

    override fun initialize() {
        pivotMotor = MotorEx(pivotMotorName)
    }
}