# How FTC code actually runs

Read this before anything else. Robot code is structured very differently from the programs you
write for a contest, and almost all of the difference comes from one rule.

## The one rule: you cannot wait

An FTC program is an **OpMode**. The SDK on the robot calls four methods on it:

```
init()       once, when the driver selects your OpMode on the Driver Hub
init_loop()  repeatedly, while it is selected but not started  (rarely used here)
start()      once, when the driver presses play
loop()       repeatedly, about 50 times a second, until stop
```

`loop()` **must return immediately**. While you are inside it, nothing else happens: no motor
commands go out, no sensors are read, and the SDK cannot talk to the hardware. Take too long and
the SDK decides your OpMode has hung and kills it.

So you can never write the obvious thing:

```java
raiseArm();
waitUntilArmIsUp();   // <-- impossible, this would block loop()
openClaw();
```

Instead you **remember where you are** and do one slice of work per call:

```java
switch (step) {
    case 1:
        raiseArm();       // start it, return straight away
        step = 2;
        break;
    case 2:
        if (armIsUp()) {  // asked again next loop, and the one after...
            openClaw();
            step = 3;
        }
        break;
}
```

That is a **state machine**, and it is why nearly every file in this repo has a `switch` or
`when` on a step number. [gm0's page on finite state machines](https://gm0.org/en/latest/docs/software/concepts/finite-state-machines.html)
is worth reading once, slowly.

If you know coroutines or `async`/`await`: this is the same problem those solve, written out by
hand. Java on the Control Hub does not have them.

### What follows from the rule

- **No `sleep()`, no `Thread.sleep()`, no busy-wait loops.** To wait, record the time and check
  it on later loops. That is all `PrimeTechV3.Components.Delay` does.
- **Anything that must keep happening needs calling every loop.** A PID controller only corrects
  the motor on the loop where you call its `update()`. Miss it and the arm keeps whatever power
  it last had and drifts. This is the single most common bug in this kind of code.
- **Do not restart an action you have already started.** Calling `followPath()` every loop
  restarts the path from the beginning every loop, and the robot never moves. The `stepStarted`
  flag in the autonomous OpModes exists purely to stop this.

## Talking to the hardware

The robot has a **configuration** stored on the Driver Hub: a list of which motor is plugged into
which port, each with a name you chose. Code looks devices up by those names:

```java
DcMotorEx pivotMotor = hardwareMap.get(DcMotorEx.class, "motorPivot");
```

The string has to match the configuration **exactly**. If it does not, you get a crash the moment
the OpMode initialises - which is actually the good case, because it fails loudly and immediately.
Our names are listed in [robot-configuration.md](robot-configuration.md).

### Motors

```java
motor.setPower(0.5);              // -1.0 to 1.0, a fraction of full power - not a speed
int ticks = motor.getCurrentPosition();   // encoder counts, measures how far it has turned
```

Power is not speed. `setPower(0.5)` at the bottom of a heavy arm may not move it at all; the same
0.5 at the top may whip it round. Closing that gap is what a PID controller is for.

**Encoder ticks** are the unit for every position in this codebase. They are raw counts from the
sensor on the motor, so they are specific to that motor and gearbox - 900 ticks means nothing
except "as far as our lift goes when scoring a sample". Every tick value in this repo was found
by driving the robot by hand and reading the number off the dashboard.

`setZeroPowerBehavior` decides what happens at zero power: `BRAKE` stops dead, `FLOAT` coasts.

### Servos

```java
servo.setPosition(0.9);   // 0.0 to 1.0, where in its range to go
```

A servo takes itself to the position you ask for. The catch is that **you cannot read where it
is**: there is no `getActualPosition()`. So code can never truly know a servo has arrived, and
instead assumes it takes a fixed time - see `SERVO_TRAVEL_TIME_MS` in
`PrimeTechV3.Components.Claw`. If a sequence runs ahead of the hardware, that constant is the
first thing to raise.

Servos will also cheerfully drive into a mechanical stop and strip their own gears. Move in small
steps when testing.

### Telemetry

There is no `printf`, and no console. To see anything:

```java
telemetry.addData("pivot ticks", pivotMotor.getCurrentPosition());
telemetry.update();      // nothing appears until you call this
```

That shows up on the Driver Hub. For anything numeric you actually care about, use
**[FTC Dashboard](https://acmerobotics.github.io/ftc-dashboard/)** instead: connect a laptop to
the robot's wifi, open `192.168.43.1:8080/dash`, and you get live graphs plus editable values.
Any `public static` field in a class marked `@Config` can be edited from there **while the robot
is running**, which is the only sane way to tune a PID. Values edited there are lost when the
OpMode stops, so copy the good ones back into the source.

## PID, briefly

Several components hold a motor at a target position with a PID controller. You do not need the
theory to work on this code, but you do need the vocabulary:

- **error** = target position - current position.
- **P** (proportional): power proportional to the error. Bigger P, harder push. Too big and it
  overshoots and oscillates.
- **I** (integral): accumulates error over time, to defeat a steady offset that P alone never
  quite closes. Usually 0 in this codebase.
- **D** (derivative): pushes back against fast movement. Damping. Lets you use a bigger P
  without the oscillation.
- **tolerance**: how close counts as arrived. Too tight and the mechanism never reports done, and
  a sequence waiting on it stalls forever.
- **feedforward**: power added from what you already know about the situation, rather than from
  the error - for example extra power to hold an arm up against gravity, scaled by how far out
  it is extended.

Tune with `PrimeTech/OpModes/Utils/Tuners/PivotAndExtensionPIDTuner`, watching the graph of
position against target on the dashboard.

## The game: Into The Deep (2024-2025)

The field is 12 by 12 feet, which is 144 by 144 inches - the unit Pedro Pathing uses.

A match is 30 seconds of **autonomous** (no driver input at all, the robot runs a preprogrammed
routine) then 2 minutes of **driver-controlled**, whose last 30 seconds are the **endgame**.

The words the code uses:

| Term | What it is |
|---|---|
| **Sample** | A plastic block. Yellow ones are neutral; red and blue belong to an alliance. |
| **Specimen** | A sample with a clip on it, which can be hung on a bar. |
| **Submersible** | The structure in the middle of the field, with samples inside it. |
| **Basket** | Where samples are scored, in the corner. A high and a low one. |
| **Chamber** | The bars where specimens are hung. A high and a low one. |
| **Observation zone** | Corner area. The human player can take samples from here and hand back specimens, which is why our autonomous spends time pushing samples into it. |
| **Ascent** | Climbing at the end of the match, for extra points. |

Our two autonomous routines are named after which of these they go for: `SpecimenAuto` hangs
specimens on the chamber, `SampleAuto` was meant to put samples in the basket.

## Where to go next

- [gm0](https://gm0.org/) - the community reference for FTC software. Genuinely good.
- [FTC official docs](https://ftc-docs.firstinspires.org/en/latest/programming_resources/index.html)
- [Pedro Pathing docs](https://pedropathing.com/) - the path-following library we use in autonomous.
