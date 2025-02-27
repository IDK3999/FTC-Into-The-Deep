package PrimeTechV2.OpModes.Auto.Right.Specimen

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

@Autonomous(name = "Specimen")
class SpecimenAuto : PedroOpMode(Claw, Lift, Pivot) {
    val fConstants: FConstants = FConstants()
    val lConstants: LConstants = LConstants()

    override fun onInit() {
        Constants.setConstants(FConstants::class.java, LConstants::class.java)

        follower = Follower(hardwareMap)
        follower.setStartingPose(SpecimenPaths.start)

        OpModeData.telemetry = telemetry
    }

    override fun onUpdate() {
        telemetry.addData("x", follower.pose.x)
        telemetry.addData("y", follower.pose.y)
        telemetry.addData("heading", follower.pose.heading)
        telemetry.update()
    }

    override fun onStartButtonPressed() {
        // TODO: Try to move this to init
        SpecimenPaths.buildObsZonePushbotPaths()

        CommandManager.scheduleCommand(
            SequentialGroup(
                ActionGroups.initializeClaw,
                SpecimenActions.scorePreload,
                SpecimenActions.get1,
                SpecimenActions.give1,
                SpecimenActions.get2,
                SpecimenActions.give2,
                SpecimenActions.get3,
                SpecimenActions.give3,
                SpecimenActions.load2,
                SpecimenActions.score2,
                SpecimenActions.load3,
                SpecimenActions.score3,
                SpecimenActions.load4,
                SpecimenActions.score4,
                SpecimenActions.load5,
                SpecimenActions.score5,
                SpecimenActions.park,
                ActionGroups.initializeHandling
            )
        )
    }
}