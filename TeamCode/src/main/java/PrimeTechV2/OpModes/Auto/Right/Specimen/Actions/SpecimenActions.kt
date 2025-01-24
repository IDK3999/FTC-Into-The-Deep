package PrimeTechV2.OpModes.Auto.Right.Specimen.Actions

import PrimeTechV2.Components.ActionGroups.ActionGroups
import PrimeTechV2.Components.Handling.Lift
import PrimeTechV2.Components.Handling.Pivot
import PrimeTechV2.OpModes.Auto.Right.Specimen.Path.SpecimenPaths
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup
import com.rowanmcalpin.nextftc.pedro.FollowPath

object SpecimenActions {
    val scorePreload: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.scorePreloadPath, true),
            // Scoring mechanism
            Pivot.toMid,
            Lift.toHigh
        )

    val getAndGiveAll: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.getAndGiveAllPath, true),
        )

    val grabSample1: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.grabSample1Path, true),
            // Grab Mechanism
            ActionGroups.grabSpecimen
        )

    val scoreSample1: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.scoreSample1Path, true),
            // Scoring mechanism
        )

    val grabSample2: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.grabSample2Path, true),
            // Grab Mechanism
        )

    val scoreSample2: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.scoreSample2Path, true),
            // Scoring mechanism
        )

    val grabSample3: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.grabSample3Path, true),
            // Grab Mechanism
        )

    val scoreSample3: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.scoreSample3Path, true),
            // Scoring mechanism
        )

    val grabSample4: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.grabSample4Path, true),
            // Grab Mechanism
        )

    val scoreSample4: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.scoreSample4Path, true),
            // Scoring mechanism
        )

    val park: Command
        get() = SequentialGroup(
            FollowPath(SpecimenPaths.parkPath, true),
        )
}