package PrimeTechV2.Components.Handling

import PrimeTechV2.primeNext.core.control.controllers.FTCLibPIDControllerWrapper
import com.arcrobotics.ftclib.controller.PIDController
import com.rowanmcalpin.nextftc.core.Subsystem
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.HoldPosition
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorEx
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorGroup
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.RunToPosition

/**
 * The lift, as a NextFTC [Subsystem]. Positions are in encoder ticks.
 *
 * Two things to notice:
 *
 * - [defaultCommand] is [HoldPosition], which NextFTC runs whenever nothing else has claimed
 *   this subsystem. That is what stops the slide sagging between actions - in `PrimeTechV3`
 *   the same job is done by calling `Lift.update()` every loop.
 * - [MotorGroup] ties the two motors together so one command drives both.
 */
object Lift : Subsystem() {
    // region Declare Components
    private lateinit var liftMotorLeft: MotorEx
    private lateinit var liftMotorRight: MotorEx

    private lateinit var liftMotors: MotorGroup

    private val liftMotorLeftName = "extensionLeft"
    private val liftMotorRightName = "extensionRight"

    private val p = 0.012
    private val i = 0.12
    private val d = 0.000287
    private val tolerance = 35.0
    private val controller = FTCLibPIDControllerWrapper(PIDController(p, i, d), tolerance)
    // endregion Declare Components

    // region Declare Values
    private val lowPosition = 0.0
    private val midPosition = 270.0
    private val highPosition = 310.0
    private val scoreBasketPosition = 900.0
    private val loadFromGroundPosition = 100.0
    private val loadFromWallPosition = 100.0
    // endregion Declare Values

    // region Commands
    override val defaultCommand
        get() = HoldPosition(liftMotors, controller, this)

    val toLow: Command
        get() = RunToPosition(
            liftMotors,
            lowPosition,
            controller,
            this
        )

    val toMid: Command
        get() = RunToPosition(
            liftMotors,
            midPosition,
            controller,
            this
        )

    val toMid2: Command
        get() = RunToPosition(
            liftMotors,
            midPosition - 200,
            controller,
            this
        )

    val toHigh: Command
        get() = RunToPosition(
            liftMotors,
            highPosition,
            controller,
            this
        )

    val toScoreBasket: Command
        get() = RunToPosition(
            liftMotors,
            scoreBasketPosition,
            controller,
            this
        )

    val toLoadFromGround: Command
        get() = RunToPosition(
            liftMotors,
            loadFromGroundPosition,
            controller,
            this
        )

    val toLoadFromWall: Command
        get() = RunToPosition(
            liftMotors,
            loadFromWallPosition,
            controller,
            this
        )
    // endregion Commands

    override fun initialize() {
        liftMotorLeft = MotorEx(liftMotorLeftName).reverse()
        liftMotorRight = MotorEx(liftMotorRightName)

        liftMotors = MotorGroup(liftMotorRight, liftMotorLeft)
    }
}
