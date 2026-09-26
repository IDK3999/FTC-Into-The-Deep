# PrimeTechV2

An experiment with [NextFTC](https://nextftc.dev/), a *command-based* library. Kotlin.

**Nothing here runs.** Both of its OpModes are `@Disabled`, so none of it appears on the Driver
Hub. The team moved back to hand-written state machines, and
[`PrimeTechV3`](../PrimeTechV3/) is what actually competed.

It is kept for one reason: it is a clean, short illustration of a genuinely different way to
structure robot code, and the comparison is instructive. Do not build on it.

## Layout

```
Components/
  Handling/Claw.kt, Lift.kt, Pivot.kt   subsystems, each exposing commands
  ActionGroups/ActionGroups.kt          commands combined into whole jobs
primeNext/                              our own additions to the library
  core/control/controllers/             adapter so FTCLib's PIDController fits NextFTC
  core/commands/groups/DeadlineGroup.kt
  ftc/hardware/AxonServoToPosition.kt   a servo command that can actually tell when it is done,
                                        by reading an Axon servo's analog feedback wire
OpModes/Auto/
  Left/Basket/                          basket-side auto. @Disabled
  Right/Specimen/                       specimen-side auto. @Disabled
```

## The idea

Instead of writing a step machine, you describe *what* should happen and the library sequences it:

```kotlin
val scoreSpecimen: Command
    get() = ParallelRaceGroup(
        SequentialGroup(
            Lift.toHigh,      // waits for the lift to arrive
            Claw.open,
            Delay(0.15)
        ),
        Delay(2.0)            // ...but give up after 2 seconds regardless
    )
```

- `SequentialGroup` - one after another, each waiting for the last.
- `ParallelGroup` - all at once, done when the slowest finishes.
- `ParallelRaceGroup` - all at once, done when the *first* finishes. That is how the timeout above
  works.

A whole autonomous becomes one nested expression:

```kotlin
override fun onStartButtonPressed() {
    CommandManager.scheduleCommand(
        SequentialGroup(
            ActionGroups.initializeClaw,
            SpecimenActions.scorePreload,
            SpecimenActions.get1,
            ...
        )
    )
}
```

There is no `loop()` and no step counter. Compare `OpModes/Auto/Right/Specimen/SpecimenAuto.kt`
here against `PrimeTechV3`'s `SpecimenAuto.kt`, which spells the same run out as 27 numbered steps.
Reading the two side by side is the most useful thing in this package.

Two further mechanisms worth knowing about, because `PrimeTechV3` has to solve both by hand:

- **`defaultCommand`.** `Lift` declares `HoldPosition` as its default, which the library runs
  whenever nothing else has claimed the lift. That is what stops the slide sagging between
  actions - `PrimeTechV3` does the same job by calling `Lift.update()` every loop.
- **Subsystem ownership.** A subsystem runs one command at a time, so two actions cannot fight over
  the same servo. `PrimeTechV3` has no equivalent; you simply have to not do that.

## Why it was dropped

Chiefly that the team wanted its own library rather than a dependency, which is what `primeNext/`
was the beginning of. The practical costs while it lasted:

- Every behaviour is an object graph built fresh on each use (note every command is a `get()`, not
  a stored value - commands hold state while running, so reusing one instance misbehaves). When a
  sequence stalls, there is no step number to print; you are debugging a tree.
- Tuning meant reaching through the library's abstractions to the hardware underneath.
- The library was at `0.5.8-beta` during the season, so its API was still moving.

None of that makes the command-based approach wrong - it is how most large FTC and FRC codebases are
organised, and it scales much better than 27 hand-numbered steps. It just was not what this team
wanted to maintain.

## If you delete this package

It is the only thing using the `com.rowanmcalpin.nextftc` dependencies, so
`build.dependencies.gradle` could lose three lines too.
