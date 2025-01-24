package PrimeTechV2.Components.ActionGroups

import PrimeTechV2.Components.Handling.Claw
import PrimeTechV2.Components.Handling.Lift
import PrimeTechV2.Components.Handling.Pivot
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup

object ActionGroups {
    val initializeHandling: Command
        get() = ParallelGroup(
            Lift.toLow,
            Pivot.toLow,
            Claw.close,
            Claw.vertical,
            Claw.back
        )

    val grabSpecimen: Command
        get() = SequentialGroup(
            ParallelGroup(
                Claw.open,
                Claw.vertical,
                Claw.front,
            ),
            Lift.toGrabSpecimen,
            Claw.close,
            Lift.toLow
        )
}