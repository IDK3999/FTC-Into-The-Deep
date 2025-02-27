package PrimeTechV2.OpModes.Auto.Right.Specimen

import PrimeTechV2.Components.ActionGroups.ActionGroups
import PrimeTechV2.Components.ActionGroups.ActionGroups.initializeHandling
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup
import com.rowanmcalpin.nextftc.core.command.utility.delays.Delay
import com.rowanmcalpin.nextftc.pedro.FollowPath

object SpecimenActions {
    val scorePreload: Command
        get() = SequentialGroup(
            ParallelGroup(
                SequentialGroup(
                    Delay(0.5),
                    ActionGroups.beforeScoreSpecimen
                ),
                FollowPath(SpecimenPaths.scorePreloadPath, true)
            ),
            ActionGroups.scoreSpecimen
        )

    val get1: Command
        get() = ParallelGroup(
            ActionGroups.beforeLoadSpecimen,
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
            FollowPath(SpecimenPaths.load2Path, true),
            Delay(0.5),
            ActionGroups.loadSpecimen
        )

    val score2: Command
        get() = SequentialGroup(
            ParallelGroup(
                ActionGroups.beforeScoreSpecimen,
                FollowPath(SpecimenPaths.score2Path, true)
            ),
            ActionGroups.scoreSpecimen
        )

    val load3: Command
        get() = SequentialGroup(
            ParallelGroup(
                ActionGroups.beforeLoadSpecimen,
                FollowPath(SpecimenPaths.load3Path, true)
            ),
            ActionGroups.loadSpecimen
        )

    val score3: Command
        get() = SequentialGroup(
            ParallelGroup(
                ActionGroups.beforeScoreSpecimen,
                FollowPath(SpecimenPaths.score3Path, true)
            ),
            ActionGroups.scoreSpecimen
        )

    val load4: Command
        get() = SequentialGroup(
            ParallelGroup(
                ActionGroups.beforeLoadSpecimen,
                FollowPath(SpecimenPaths.load4Path, true)
            ),
            ActionGroups.loadSpecimen
        )

    val score4: Command
        get() = SequentialGroup(
            ParallelGroup(
                ActionGroups.beforeScoreSpecimen,
                FollowPath(SpecimenPaths.score4Path, true)
            ),
            ActionGroups.scoreSpecimen
        )

    val load5: Command
        get() = SequentialGroup(
            ParallelGroup(
                ActionGroups.beforeLoadSpecimen,
                FollowPath(SpecimenPaths.load5Path, true)
            ),
            ActionGroups.loadSpecimen
        )

    val score5: Command
        get() = SequentialGroup(
            ParallelGroup(
                ActionGroups.beforeScoreSpecimen,
                FollowPath(SpecimenPaths.score5Path, true)
            ),
            ActionGroups.scoreSpecimen
        )

    val park: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.parkPath, true)
        )
}