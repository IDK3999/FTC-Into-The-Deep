package PrimeTechV2.Components.Handling

import com.arcrobotics.ftclib.controller.PIDController
import com.rowanmcalpin.nextftc.core.Subsystem
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.HoldPosition
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorEx
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorGroup
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.RunToPosition
import primeNext.core.control.controllers.FTCLibPIDControllerWrapper

object Lift : Subsystem() {
    // region Declare Components
    private lateinit var liftMotorLeft: MotorEx
    private lateinit var liftMotorRight: MotorEx

    private lateinit var motors: MotorGroup

    private val liftMotorLeftName = "extensionLeft"
    private val liftMotorRightName = "extensionRight"

    private val p = 0.01
    private val i = 0.0
    private val d = 0.0
    private val tolerance = 30.0
    private val controller = FTCLibPIDControllerWrapper(PIDController(p, i, d), tolerance)
    // endregion Declare Components

    // region Declare Values
    private val lowPosition = 0.0
    private val midPosition = 70.0
    private val highPosition = 300.0
    private val scoreBasketPosition = 2000.0
    private val loadFromGroundPosition = 100.0
    // endregion Declare Values

    // region Commands
    override val defaultCommand
        get() = HoldPosition(motors, controller, this)

    val toLow: Command
        get() = RunToPosition(
            motors,
            lowPosition,
            controller,
            this
        )

    val toMid: Command
        get() = RunToPosition(
            motors,
            midPosition,
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

    val toScoreBasket: Command
        get() = RunToPosition(
            motors,
            scoreBasketPosition,
            controller,
            this
        )

    val toLoadFromGround: Command
        get() = RunToPosition(
            motors,
            loadFromGroundPosition,
            controller,
            this
        )
    // endregion Commands

    override fun initialize() {
        liftMotorLeft = MotorEx(liftMotorLeftName).reverse()
        liftMotorRight = MotorEx(liftMotorRightName)

        motors = MotorGroup(liftMotorRight, liftMotorLeft)
    }
}
