package PrimeTechV2.Components.ActionGroups

import PrimeTechV2.Components.Handling.Claw
import PrimeTechV2.Components.Handling.Lift
import PrimeTechV2.Components.Handling.Pivot
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup
import com.rowanmcalpin.nextftc.core.command.groups.ParallelRaceGroup
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup
import com.rowanmcalpin.nextftc.core.command.utility.delays.Delay

object ActionGroups {
    val initializeHandling: Command
        get() = ParallelGroup(
            SequentialGroup(
                Lift.toLow,
                Delay(1.0),
                Pivot.toLow,
            ),
            Claw.close,
            Claw.vertical,
            Claw.back
        )

    val initializeClaw: Command
        get() = ParallelGroup(
            Claw.close,
            Claw.vertical,
            Claw.back
        )

    val beforeScoreSpecimen: Command
        get() = ParallelGroup(
            Lift.toMid,
            Pivot.toHigh,
            Claw.back,
            Claw.vertical
        )

    val scoreSpecimen: Command
        get() = SequentialGroup(
            Lift.toHigh,
            Claw.open,
            Delay(0.15)
        )

    val beforeLoadSpecimen: Command
        get() = ParallelGroup(
            Lift.toLow,
            ParallelRaceGroup(
                Pivot.toGrabSpecimenPivot,
                Delay(1.5)
            ),
            Claw.open,
            Claw.vertical,
            Claw.grabSpecimenClawPivot
        )

    val loadSpecimen: Command
        get() = SequentialGroup(
            Claw.close,
            Delay(0.15),
            Claw.back,
            Delay(0.15)
        )

    val beforeScoreBasket: Command
        get() = ParallelGroup(
            Claw.mid,
            Claw.vertical,
            SequentialGroup(
                Pivot.toHigh,
                Lift.toScoreBasket
            )
        )

    val scoreBasket: Command
        get() = SequentialGroup(
            beforeScoreBasket,
            Claw.back,
            Delay(0.15),
            Claw.open,
            Delay(0.15),
            Claw.back,
            Delay(0.15),
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
            Claw.close,
            Delay(0.15),
            initializeHandling
        )
}
