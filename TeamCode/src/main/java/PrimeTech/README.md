# PrimeTech (V1)

The first version, in Java. **This is the TeleOp we actually drove all season**, and it holds the
PID tuning OpModes that are still the right tool for tuning the arm.

It is also, structurally, the worst code in the repo. Read it to understand what the robot did and
to use the tuners - but copy [`PrimeTechV3`](../PrimeTechV3/) instead when writing anything new.

## Layout

```
Global/Global.java             static holder for hardwareMap / telemetry / gamepads
Components/
  Gamepad/GamepadTracker.java  turns button state into "just pressed" vs "held"
  Drivetrain/Drivetrain.java   the four mecanum wheels (gamepad 2)
  Outtake/
    Claw.java                  grip, wrist and claw-pivot servos
    Pivot.java                 arm rotation motor + PID
    Extension.java             slide motors + PID
    Outtake.java               top level; owns the safe re-initialise on circle
  Modes/
    FSMModes.java              which scoring mode we are in, switched on the d-pad
    AllModes.java              what the arm does in each mode
OpModes/
  Tele/TeleSimple.java         the TeleOp. Start here - it has the full controls list
  Auto/NetPushbot.java         drive forward and park. @Disabled
  Utils/Tuners/                PID and servo tuning OpModes - still useful
  Utils/InitializeForAssembly/ holds servos at known positions while building the robot
```

## Controls

Two drivers. **Gamepad 1** runs the arm, **gamepad 2** drives.

| Gamepad 1 | Action |
|---|---|
| d-pad up | Basket mode: arm up over the basket |
| d-pad down | Floor mode: arm down to grab samples. Press again to toggle the claw shape |
| d-pad left | Chamber mode: arm up to hang specimens |
| d-pad right | Wall mode: arm out to collect specimens |
| cross | Open / close the claw |
| triangle | Roll the wrist 90 degrees (floor mode) |
| circle | Re-initialise the arm: retract the slide, then lower |
| bumpers | Raise / lower the arm, or snap the slide in and out within a mode |
| triggers | Extend / retract the slide, proportionally |

| Gamepad 2 | Action |
|---|---|
| left stick | Drive and strafe |
| right stick | Turn |

## How it hangs together

`TeleSimple.loop()` calls three things: `Drivetrain`, `GamepadTracker`, `Outtake`.

`Outtake` normally just delegates to `FSMModes.update()`, which runs the current mode's behaviour
from `AllModes` and then checks the d-pad for a mode change.

Each mode in `AllModes` has three methods: `init...` when the mode is selected, `update...` every
loop while it is active, and `hold...` once the arm has reached position - which is where the
driver gets manual control back.

### The one genuinely good idea here

`AllModes.runToPositionInOrder` moves the arm in a **safe order**, and it is worth understanding
because the same constraint bites in any arm design. Swinging the arm while the slide is extended
puts the claw a long way from the robot, where it can hit the field or the floor. So the sequence
is always: retract the slide → swing the arm → extend again. Each stage keeps driving the axis it
is not waiting on, so nothing sags under gravity meanwhile.

`Outtake`'s re-initialise on circle does the same thing for the same reason.

### Why not to copy the rest

- **`Global`** is global mutable state: four static fields that every component reaches into.
  Forget one line in an OpMode's `init()` and you get a `NullPointerException` from somewhere
  unrelated. `PrimeTechV3` passes `hardwareMap` in as a parameter instead. The file's own comment
  explains this at more length.
- **Everything is a singleton** via `getInstance()`, so any class can reach any other class's
  state and it is very hard to see what depends on what.
- **The arm's behaviour is spread across three files** - `Outtake`, `FSMModes` and `AllModes` all
  hold part of it, and `AllModes` both decides targets and drives motors.
- **`AllModes.Mode` and `FSMModes.RobotMode` are two enums for the same thing**, differing only by
  `GENERAL`.
- **Mutable public statics as tuning knobs** (`Extension.maxTicks` is rewritten by `AllModes`
  depending on mode) make the state hard to follow.

## Known issues

- **`Claw`'s two claw-pivot servos are not mirrored.** `TestServo` and
  `PivotAndExtensionPIDTuner` both call `setDirection(REVERSE)` on `frontBackServoLeft`;
  `Claw.init()` does not. Since both then get the same position, they fight each other. This looks
  like a real bug, and is probably why `PrimeTechV3` drives only the right servo. Flagged in the
  source - check on the robot.
- **The drive stick curve can ask for more power than exists.** `applyStickCurve` returns about
  1.04 at full stick, and the three inputs are summed per wheel, so a full diagonal while turning
  asks for roughly 3.1. The SDK clamps it, so the robot drives, but the mix between forward,
  strafe and turn is no longer what the sticks asked for. Normalising instead of clamping would fix
  it - but that changes the feel, so re-test with the drivers.
- **`Pivot`'s feedforward gain is 0**, so that whole term does nothing. The formula is there and
  looks right; it was never tuned.
- **Neither arm axis has a lower limit switch.** Encoders are zeroed wherever the arm happens to be
  at `init()`, so the robot must be put in the same starting pose every time.

## The tuners are still worth using

`OpModes/Utils/Tuners/PivotAndExtensionPIDTuner` is the tool for tuning the arm's PID gains, and it
works against the current robot regardless of which version's code you are writing. It exposes
everything to [FTC Dashboard](https://acmerobotics.github.io/ftc-dashboard/) so you can edit gains
and watch the response live. `PivotPIDTuner`'s header comments walk through the procedure.

`TestServo` is how every servo position constant in this repo was found - including
`PrimeTechV3`'s.
