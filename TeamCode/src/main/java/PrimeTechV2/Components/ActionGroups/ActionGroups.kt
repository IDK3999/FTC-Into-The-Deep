package PrimeTechV2.Components.ActionGroups

import PrimeTechV2.Components.Handling.Claw
import PrimeTechV2.Components.Handling.Lift
import PrimeTechV2.Components.Handling.Pivot
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup
import com.rowanmcalpin.nextftc.core.command.utility.delays.Delay

object ActionGroups {
    val initializeHandling: Command
        get() = SequentialGroup(
            Lift.toLow,
            ParallelGroup(
                Pivot.toLow,
                Claw.close,
                Claw.vertical,
                Claw.back
            )
        )

    val scoreSpecimen: Command
        get() = SequentialGroup(
            ParallelGroup(
                Lift.toMid,
                Pivot.toHigh,
            ),
            Lift.toHigh,
            Delay(0.1),
            Claw.open,
            Delay(0.1),
            Pivot.toBeforeClosingFromHigh,
            initializeHandling
        )

    val beforeLoadSpecimen: Command
        get() = ParallelGroup(
            Claw.open,
            Claw.vertical,
            Claw.grabSpecimenClawPivot
        )

    val loadSpecimen: Command
        get() = SequentialGroup(
            Claw.close,
            Delay(0.2),
            Claw.back,
            Delay(0.2),
            initializeHandling
        )

    val beforeScoreBasket: Command
        get() = ParallelGroup(
            Claw.mid,
            Claw.vertical,
            Pivot.toHigh,
            Lift.toScoreBasket
        )

    val scoreBasket: Command
        get() = SequentialGroup(
            beforeScoreBasket,
            Claw.back,
            Delay(0.5),
            Claw.open,
            Delay(0.1),
            Claw.back,
            Delay(0.1),
            initializeHandling
        )

    val beforeLoadFromGround: Command
        get() = ParallelGroup(
            Claw.open,
            Claw.vertical,
            Claw.front
        )

    val loadFromGround: Command
        get() = SequentialGroup(
            beforeLoadFromGround,
            Lift.toLoadFromGround,
            Delay(0.2),
            Claw.close,
            Delay(0.2),
            Claw.back,
            initializeHandling
        )
}
