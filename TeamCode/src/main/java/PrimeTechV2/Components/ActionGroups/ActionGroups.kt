package PrimeTechV2.Components.ActionGroups

import PrimeTechV2.Components.Handling.Claw
import PrimeTechV2.Components.Handling.Lift
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup

object ActionGroups {
    val grabSpecimen: Command
        get() = SequentialGroup(
            ParallelGroup(
                Claw.open,
                Claw.vertical,
                Claw.front,
            ),
            Lift.toGrab,
            Claw.close,
            Lift.toLow
        )
}