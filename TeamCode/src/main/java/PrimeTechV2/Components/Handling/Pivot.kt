package PrimeTechV2.Components.Handling

import com.arcrobotics.ftclib.controller.PIDController
import com.rowanmcalpin.nextftc.core.Subsystem
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.HoldPosition
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorEx
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.RunToPosition
import primeNext.core.control.controllers.FTCLibPIDControllerWrapper

object Pivot : Subsystem() {
    // region Declare Components
    private lateinit var pivotMotor: MotorEx

    private val pivotMotorName = "motorPivot"

    private val p = 0.00285
    private val i = 0.025
    private val d = 0.00028
    private val highTolerance = 100.0
    private val lowTolerance = 15.0
    private val controllerHighTol =
        FTCLibPIDControllerWrapper(PIDController(p, i, d), highTolerance)
    private val controllerLowTol = FTCLibPIDControllerWrapper(PIDController(p, i, d), lowTolerance)
    // endregion Declare Components

    // region Declare Values
    private val lowPosition = 0.0
    private val highPosition = 1985.0
    private val beforeClosingFromHighPosition = 1800.0
    private val grabSpecimenPivotPosition = 200.0
    // endregion Declare Values

    // region Commands
    override val defaultCommand
        get() = HoldPosition(pivotMotor, controllerLowTol, this)

    val toLow: Command
        get() = RunToPosition(
            pivotMotor,
            lowPosition,
            controllerHighTol,
            this
        )

    val toHigh: Command
        get() = RunToPosition(
            pivotMotor,
            highPosition,
            controllerLowTol,
            this
        )

    val toBeforeClosingFromHigh: Command
        get() = RunToPosition(
            pivotMotor,
            beforeClosingFromHighPosition,
            controllerLowTol,
            this
        )

    val toGrabSpecimenPivot: Command
        get() = RunToPosition(
            pivotMotor,
            grabSpecimenPivotPosition,
            controllerHighTol,
            this
        )
    // endregion Commands

    override fun initialize() {
        pivotMotor = MotorEx(pivotMotorName)
    }
}