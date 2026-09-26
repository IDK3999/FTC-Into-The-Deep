package PrimeTechV2.Components.Handling

import PrimeTechV2.primeNext.core.control.controllers.FTCLibPIDControllerWrapper
import com.arcrobotics.ftclib.controller.PIDController
import com.rowanmcalpin.nextftc.core.Subsystem
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup
import com.rowanmcalpin.nextftc.core.command.utility.delays.Delay
import com.rowanmcalpin.nextftc.core.command.utility.delays.WaitUntil
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.HoldPosition
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorEx
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.RunToPosition
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.SetPower

/**
 * The arm rotation motor, as a NextFTC [Subsystem]. Positions are in encoder ticks.
 *
 * Two PID controllers are kept, differing only in tolerance: a loose one for moves where
 * "close enough" is fine, and a tight one where the arm has to be accurate. A move that
 * cannot settle within its tolerance never reports finished, which would stall the sequence.
 */
object Pivot : Subsystem() {
    // region Declare Components
    private lateinit var pivotMotor: MotorEx

    private val pivotMotorName = "motorPivot"

    private val p = 0.003
    private val i = 0.01
    private val d = 0.0002
    private val highTolerance = 40.0
    private val lowTolerance = 20.0
    private val controllerHighTol =
        FTCLibPIDControllerWrapper(PIDController(p, i, d), highTolerance)
    private val controllerLowTol = FTCLibPIDControllerWrapper(PIDController(p, i, d), lowTolerance)
    // endregion Declare Components

    // region Declare Values
    private val lowPosition = 0.0
    private val highPosition = 2020.0
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

    val forceToHigh: Command
        get() = SequentialGroup(
            SetPower(
                pivotMotor,
                1.0,
                this
            ),
            Delay(0.5),
            WaitUntil(
                { pivotMotor.velocity <= 30; }
            ),
            SetPower(
                pivotMotor,
                0.0,
                this
            )
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