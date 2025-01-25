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
            ParallelGroup(
                FollowPath(PreloadSpecimenPaths.scorePreloadPath, true),
                Pivot.toHigh
            ),
            // Scoring mechanism
            Lift.toHigh,
            Delay(0.2),
            Claw.open,
            Delay(0.2),
            Pivot.toMid,
            Delay(0.1),
            ActionGroups.initializeHandling
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

    val parkFromScore: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.parkFromScorePath, true)
        )

    val park: Command
        get() = SequentialGroup(
            FollowPath(PreloadSpecimenPaths.parkPath, true)
        )
}