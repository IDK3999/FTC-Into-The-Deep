package PrimeTechV2.Components.ActionGroups

import PrimeTechV2.Components.Handling.Claw
import PrimeTechV2.Components.Handling.Lift
import PrimeTechV2.Components.Handling.Pivot
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup
import com.rowanmcalpin.nextftc.core.command.groups.ParallelRaceGroup
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup
import com.rowanmcalpin.nextftc.core.command.utility.delays.Delay

/**
 * Whole jobs, built by combining the components' commands.
 *
 * This is the part of NextFTC worth understanding. Instead of writing a step machine by hand
 * (as `PrimeTech` and `PrimeTechV3` both do), you describe *what* should happen and the
 * library works out the sequencing:
 *
 * - [SequentialGroup] runs its commands one after another, each waiting for the last.
 * - [ParallelGroup] runs them all at once and finishes when the slowest one does.
 * - [ParallelRaceGroup] runs them all at once and finishes when the *first* one does -
 *   which is how [scoreSpecimen] below gets a 2 second timeout: whichever finishes first,
 *   the real work or the `Delay(2.0)`, ends the group.
 *
 * Note every property here is a `get()` rather than a stored value, so each use builds a
 * fresh command. Commands carry state while they run, so reusing one instance twice would
 * misbehave.
 */
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
