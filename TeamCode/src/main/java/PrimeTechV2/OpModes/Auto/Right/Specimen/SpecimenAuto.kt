package PrimeTechV2.OpModes.Auto.Right.Specimen

import PrimeTechV2.Components.ActionGroups.ActionGroups
import PrimeTechV2.Components.Handling.Claw
import PrimeTechV2.Components.Handling.Lift
import PrimeTechV2.Components.Handling.Pivot
import PrimeTechV2.OpModes.Auto.Right.Specimen.Actions.SpecimenActions
import PrimeTechV2.OpModes.Auto.Right.Specimen.Path.SpecimenPaths
import com.pedropathing.follower.Follower
import com.pedropathing.util.Constants
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.rowanmcalpin.nextftc.core.command.CommandManager
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup
import com.rowanmcalpin.nextftc.ftc.OpModeData
import com.rowanmcalpin.nextftc.pedro.PedroOpMode
import pedroPathing.constants.FConstants
import pedroPathing.constants.LConstants

@Autonomous(name = "Steroid Specimen Auto")
class SpecimenAuto : PedroOpMode(Claw, Lift, Pivot) {
    val fConstants: FConstants = FConstants()
    val lConstants: LConstants = LConstants()

    override fun onInit() {
        Constants.setConstants(FConstants::class.java, LConstants::class.java)

        follower = Follower(hardwareMap)
//        follower.resetIMU()
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

        SpecimenPaths.buildSpecimenPaths()

        CommandManager.scheduleCommand(
            SequentialGroup(
                ActionGroups.initializeHandling,
                SpecimenActions.scorePreload
//                SpecimenActions.getAndGiveAll,
//                SpecimenActions.grabSample1,
//                SpecimenActions.scoreSample1,
//                SpecimenActions.grabSample2,
//                SpecimenActions.scoreSample2,
//                SpecimenActions.grabSample3,
//                SpecimenActions.scoreSample3,
//                SpecimenActions.grabSample4,
//                SpecimenActions.scoreSample4,
//                SpecimenActions.park
            )
        )
    }
}