package PrimeTechV2.OpModes.Auto.Right.PreloadSpecimen

import PrimeTechV2.Components.ActionGroups.ActionGroups
import PrimeTechV2.Components.Handling.Claw
import PrimeTechV2.Components.Handling.Lift
import PrimeTechV2.Components.Handling.Pivot
import PrimeTechV2.OpModes.Auto.Right.PreloadSpecimen.Actions.PreloadSpecimenActions
import PrimeTechV2.OpModes.Auto.Right.PreloadSpecimen.Paths.PreloadSpecimenPaths
import com.pedropathing.follower.Follower
import com.pedropathing.util.Constants
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.rowanmcalpin.nextftc.core.command.CommandManager
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup
import com.rowanmcalpin.nextftc.ftc.OpModeData
import com.rowanmcalpin.nextftc.pedro.PedroOpMode
import pedroPathing.constants.FConstants
import pedroPathing.constants.LConstants

@Autonomous(name = "Steroid Bring To Observation Zone")
class PreloadSpecimenAuto: PedroOpMode(Claw, Lift, Pivot) {
    val fConstants: FConstants = FConstants()
    val lConstants: LConstants = LConstants()

    override fun onInit() {
        Constants.setConstants(FConstants::class.java, LConstants::class.java)

        follower = Follower(hardwareMap)
//        follower.resetIMU()
        follower.setStartingPose(PreloadSpecimenPaths.start)

        OpModeData.telemetry = telemetry
    }

    override fun onUpdate() {
        telemetry.addData("x", follower.pose.x)
        telemetry.addData("y", follower.pose.y)
        telemetry.addData("heading", follower.pose.heading)
        telemetry.update()
    }

    override fun onStartButtonPressed() {

        PreloadSpecimenPaths.buildObsZonePushbotPaths()

        CommandManager.scheduleCommand(
            SequentialGroup(
                ActionGroups.initializeHandling,
                PreloadSpecimenActions.scorePreload,
                PreloadSpecimenActions.parkFromScore
//                ObsZonePushbotActions.get1,
//                ObsZonePushbotActions.give1
//                ObsZonePushbotActions.get2,
//                ObsZonePushbotActions.give2,
//                ObsZonePushbotActions.get3,
//                ObsZonePushbotActions.give3,
//                ObsZonePushbotActions.park
            )
        )
    }
}