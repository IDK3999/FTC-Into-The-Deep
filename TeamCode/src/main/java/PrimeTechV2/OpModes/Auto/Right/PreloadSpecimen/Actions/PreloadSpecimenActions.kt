package PrimeTechV2.OpModes.Auto.Right.PreloadSpecimen.Actions

import PrimeTechV2.Components.ActionGroups.ActionGroups
import PrimeTechV2.Components.Handling.Claw
import PrimeTechV2.Components.Handling.Lift
import PrimeTechV2.Components.Handling.Pivot
import PrimeTechV2.OpModes.Auto.Right.PreloadSpecimen.Paths.PreloadSpecimenPaths
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup
import com.rowanmcalpin.nextftc.core.command.utility.delays.Delay
import com.rowanmcalpin.nextftc.pedro.FollowPath

object PreloadSpecimenActions {
    val scorePreload: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.scorePreloadPath, true),
            ActionGroups.scoreSpecimen
        )

    val get1: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.get1Path, true)
        )

    val give1: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.give1Path, true)
        )

    val get2: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.get2Path, true)
        )

    val give2: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.give2Path, true)
        )

    val get3: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.get3Path, true)
        )

    val give3: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.give3Path, true)
        )

    val load2: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.load2Path, true),
            ActionGroups.loadSpecimen
        )

    val score2: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.score2Path, true),
            ActionGroups.scoreSpecimen
        )

    val load3: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.load3Path, true),
            ActionGroups.loadSpecimen
        )

    val score3: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.score3Path, true),
            ActionGroups.scoreSpecimen
        )

    val load4: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.load4Path, true),
            ActionGroups.loadSpecimen
        )

    val score4: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.score4Path, true),
            ActionGroups.scoreSpecimen
        )

    val load5: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.load5Path, true),
            ActionGroups.loadSpecimen
        )

    val score5: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.score5Path, true),
            ActionGroups.scoreSpecimen
        )

    val park: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.parkPath, true)
        )
}