package PrimeTechV2.OpModes.Auto.Right.Specimen.Actions

import PrimeTechV2.Components.ActionGroups.ActionGroups
import PrimeTechV2.Components.Handling.Claw
import PrimeTechV2.Components.Handling.Pivot
import PrimeTechV2.OpModes.Auto.Right.Specimen.Paths.SpecimenPaths
import android.app.Notification.Action
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup
import com.rowanmcalpin.nextftc.core.command.utility.delays.Delay
import com.rowanmcalpin.nextftc.pedro.FollowPath

object SpecimenActions {
    val scorePreload: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.scorePreloadPath, true),
            ActionGroups.scoreSpecimen
        )

    val get1: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.get1Path, true)
        )

    val give1: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.give1Path, true)
        )

    val get2: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.get2Path, true)
        )

    val give2: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.give2Path, true)
        )

    val get3: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.get3Path, true)
        )

    val give3: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.give3Path, true)
        )

    val load2: Command
        get() = SequentialGroup(
//            Claw.grabSpecimenClawPivot,
//            Pivot.toGrabSpecimenPivot,
            FollowPath(SpecimenPaths.load2Path, true),
            ActionGroups.loadSpecimen
        )

    val score2: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.score2Path, true),
            ActionGroups.scoreSpecimen
        )

    val load3: Command
        get() = SequentialGroup(
            ActionGroups.beforeLoadSpecimen,
            FollowPath(SpecimenPaths.load3Path, true),
            Delay(0.3),
            ActionGroups.loadSpecimen
        )

    val score3: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.score3Path, true),
            ActionGroups.scoreSpecimen
        )

    val load4: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.load4Path, true),
            ActionGroups.loadSpecimen
        )

    val score4: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.score4Path, true),
            ActionGroups.scoreSpecimen
        )

    val load5: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.load5Path, true),
            ActionGroups.loadSpecimen
        )

    val score5: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.score5Path, true),
            ActionGroups.scoreSpecimen
        )

    val park: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.parkPath, true)
        )
}