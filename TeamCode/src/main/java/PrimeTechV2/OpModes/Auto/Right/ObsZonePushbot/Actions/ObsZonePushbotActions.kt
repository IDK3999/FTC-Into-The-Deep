package PrimeTechV2.OpModes.Auto.Right.ObsZonePushbot.Actions

import PrimeTechV2.Components.ActionGroups.ActionGroups
import PrimeTechV2.Components.Handling.Claw
import PrimeTechV2.Components.Handling.Lift
import PrimeTechV2.Components.Handling.Pivot
import PrimeTechV2.OpModes.Auto.Right.ObsZonePushbot.Paths.ObsZonePushbotPaths
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup
import com.rowanmcalpin.nextftc.core.command.utility.delays.Delay
import com.rowanmcalpin.nextftc.pedro.FollowPath

object ObsZonePushbotActions {
    val scorePreload: Command
        get() = SequentialGroup(
            FollowPath(ObsZonePushbotPaths.scorePreloadPath, true),
            // Scoring mechanism
            Pivot.toHigh,
            Lift.toHigh,
            Delay(0.5),
            Claw.open,
            Delay(2.0),
            Pivot.toMid,
            Delay(2.0),
            ActionGroups.initializeHandling
        )

    val get1: Command
        get() = SequentialGroup(
            FollowPath(ObsZonePushbotPaths.get1Path, true)
        )

    val give1: Command
        get() = SequentialGroup(
            FollowPath(ObsZonePushbotPaths.give1Path, true)
        )

    val get2: Command
        get() = SequentialGroup(
            FollowPath(ObsZonePushbotPaths.get2Path, true)
        )

    val give2: Command
        get() = SequentialGroup(
            FollowPath(ObsZonePushbotPaths.give2Path, true)
        )

    val get3: Command
        get() = SequentialGroup(
            FollowPath(ObsZonePushbotPaths.get3Path, true)
        )

    val give3: Command
        get() = SequentialGroup(
            FollowPath(ObsZonePushbotPaths.give3Path, true)
        )

    val park: Command
        get() = SequentialGroup(
            FollowPath(ObsZonePushbotPaths.parkPath, true)
        )
}