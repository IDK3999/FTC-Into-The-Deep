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
        get() = SequentialGroup(
            Lift.toLow,
            Delay(0.2),
            Pivot.toLow,
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
            SequentialGroup(
                Pivot.toHigh,
                Lift.toMid
            ),
            Claw.back,
            Claw.vertical
        )

    val beforeScoreSpecimen2: Command
        get() = ParallelGroup(
            SequentialGroup(
                Pivot.toHigh,
                Lift.toMid2
            ),
            Claw.back,
            Claw.vertical
        )

    val scoreSpecimen: Command
        get() = ParallelRaceGroup(
            SequentialGroup(
//            beforeScoreSpecimen,
                Lift.toHigh,
                Claw.open,
                Delay(0.15)
            ),
            Delay(2.0)
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
            Delay(0.5),
            Lift.toLoadFromWall,
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
            Claw.basketBack,
            Delay(0.15),
            Claw.open,
            Delay(0.15),
            Claw.basketBack,
            Delay(0.15),
            Claw.mid,
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
            Claw.close,
            Delay(0.15),
            initializeHandling
        )
}
