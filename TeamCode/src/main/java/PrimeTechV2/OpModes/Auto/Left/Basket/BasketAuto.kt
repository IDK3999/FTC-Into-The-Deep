package PrimeTechV2.OpModes.Auto.Left.Basket

import PrimeTechV2.Components.ActionGroups.ActionGroups
import PrimeTechV2.Components.Handling.Claw
import PrimeTechV2.Components.Handling.Lift
import PrimeTechV2.Components.Handling.Pivot
import com.pedropathing.follower.Follower
import com.pedropathing.util.Constants
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.rowanmcalpin.nextftc.core.command.CommandManager
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup
import com.rowanmcalpin.nextftc.ftc.OpModeData
import com.rowanmcalpin.nextftc.pedro.PedroOpMode
import pedroPathing.constants.FConstants
import pedroPathing.constants.LConstants

@Autonomous(name = "Basket")
class BasketAuto : PedroOpMode(Claw, Lift, Pivot) {
    val fConstants: FConstants = FConstants()
    val lConstants: LConstants = LConstants()

    override fun onInit() {
        Constants.setConstants(FConstants::class.java, LConstants::class.java)

        follower = Follower(hardwareMap)
        follower.setStartingPose(BasketPaths.start)

        OpModeData.telemetry = telemetry
    }

    override fun onUpdate() {
        telemetry.addData("x", follower.pose.x)
        telemetry.addData("y", follower.pose.y)
        telemetry.addData("heading", follower.pose.heading)
        telemetry.update()
    }

    override fun onStartButtonPressed() {
        BasketPaths.buildBasketPaths()

        CommandManager.scheduleCommand(
            SequentialGroup(
                ActionGroups.initializeHandling,
                BasketActions.scorePreload,
                BasketActions.load1FromGround,
                BasketActions.score1,
                BasketActions.load2FromGround,
                BasketActions.score2,
                BasketActions.load3FromGround,
                BasketActions.score3,
                ActionGroups.initializeHandling
            )
        )
    }
}