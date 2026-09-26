# PrimeTechV3

**The version to learn from and build on.** Written in Kotlin, and used for both autonomous
routines we competed with. It also works for TeleOp, though no TeleOp was written against it.

If you have not read [docs/getting-started.md](../../../../../docs/getting-started.md) yet, read
that first - none of the below will make sense without the "you cannot wait" rule.

## Layout

```
Components/    one file per physical mechanism
  Claw.kt      three servos: grip, wrist, claw pivot
  Lift.kt      two motors that slide the arm in and out
  Pivot.kt     one motor that rotates the whole arm
  Pedro.kt     wrapper around the path-following library
  Delay.kt     a stopwatch, for pausing between steps

Actions/
  Actions.kt   combines the components into whole jobs ("score a specimen")

OpModes/Auto/
  Specimen/    SpecimenAuto.kt + SpecimenPaths.kt   - the complete routine
  Sample/      SampleAuto.kt   + SamplePaths.kt     - unfinished, drives but scores nothing
```

## The one pattern to understand

Every component looks the same, and that is the whole design:

```kotlin
object Thing {
    fun init(hardwareMap: HardwareMap)   // look up the hardware. Once, from OpMode init()
    fun start()                          // once, from OpMode start()
    fun update()                         // EVERY loop. Motors only move because of this
    fun isDone(): Boolean                // has it arrived?
    fun setSomething(target: ...)         // start a movement, return immediately
}
```

Because `Pedro` has the same shape as `Lift`, an autonomous step can wait on "drive along this
path" exactly the way it waits on "raise the arm" - `Actions.isDone() && Pedro.isDone()`. That
uniformity is what makes the routines readable.

Two consequences worth burning in:

- **`update()` must be called every loop.** A component whose `update()` is not called keeps
  whatever motor power it last had. `Actions.update()` calls `Lift.update()` and `Pivot.update()`
  for you; `Pedro.update()` you call yourself.
- **`isDone()` is true before you start, too.** Both "finished" and "never started" are the same
  state, so `startAction(X); if (isDone())` reports done immediately. That is what the
  `stepStarted` flag in the OpModes guards against.

## Three levels

**Components** know about hardware and nothing else. `Lift` knows it has two motors and a PID
controller, and how many ticks the high basket is.

**`Actions`** knows the order things must happen in. Scoring a sample means: tuck the claw, swing
the arm up, *then* extend (extending low would hit the basket), *then* tip the claw, *then* let go.
It is a state machine over numbered steps - `currentAction` says which job, `currentStep` says how
far in.

**OpModes** know the match plan: which actions, and which paths, in what order.

To add a new job for the arm, add a value to `Actions.RobotAction` and a branch in
`Actions.update()`. To change where the robot drives, edit the poses in a `...Paths.kt` file.

## The paths files

`SpecimenPaths` and `SamplePaths` hold every field position and path for their routine. Field
coordinates are inches, 0 to 144 on each axis; headings are radians.

Both routines hold a **constant heading** throughout - the robot never turns, it only slides. For
the specimen routine that heading is 180 degrees, because the claw scores over the back of the
robot, which is why larger x means "further onto the chamber".

Paths are chained: each one starts where the last ended. So moving one position shifts everything
after it, and re-testing means re-testing the whole run.

`build()` must be called once from the OpMode's `init()`, because building a path needs the
`Follower`, which does not exist until then. Paths are `lateinit`, so touching one before `build()`
throws `UninitializedPropertyAccessException`.

## Known issues

Real problems, left in deliberately rather than fixed blind - none of this can be tested without
the robot.

- **`Actions.GRAB_SAMPLE` hangs forever.** Step 4 is missing the `currentStep++` that every other
  step has, so it closes the grip and stops there. No autonomous used it, so it never showed up at
  a competition. Marked in the source.
- **`SampleAuto` scores nothing.** It drives to the basket and parks; steps 2 and 3 are empty
  placeholders. `SpecimenAuto` is the one to copy scoring steps from.
- **`Actions.SCORE_SAMPLE` is never called** by any OpMode here, so it is untested on the robot.
- **The fourth specimen is collected but never hung.** The run did not fit in 30 seconds.
  `scoreSpecimen4Path` is built and tuned, so finishing it means adding four more steps.
- **`driveBehindSample3Path` / `pushSample3Path` are built but never followed** - same reason. The
  coordinates are tuned, so they are kept.
- **`Claw.isDone()` is a 150 ms guess**, not a measurement, because servos give no position
  feedback. If a sequence runs ahead of the hardware, raise `SERVO_TRAVEL_TIME_MS` first.
- **`Delay` is a single shared timer.** One wait at a time. Fine for one autonomous sequence; if
  you ever need two at once, make it a normal class.

## Why this version and not the others

`PrimeTech` (V1) does the same job with global mutable state and the arm's behaviour smeared
across `AllModes`, `FSMModes` and `Outtake`. `PrimeTechV2` is cleaner to read but depends on the
NextFTC library, which the team moved off. This one is plain Kotlin against the FTC SDK, with each
mechanism in exactly one file.
