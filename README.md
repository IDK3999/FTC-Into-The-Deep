# Into The Deep Code

Code for the robot made by [PrimeTech (RO025)](https://primetechrobotics.com/) for the
[FTC competition](https://www.firstinspires.org/robotics/ftc) in the 2024-2025 season.

## New to the team? Start here

If you have never written robot code before, read these in order:

1. **[docs/getting-started.md](docs/getting-started.md)** - how FTC code actually runs. The one
   idea you must have before anything else makes sense is that your code cannot wait for
   things; read this first.
2. **[docs/java-and-kotlin-for-cpp.md](docs/java-and-kotlin-for-cpp.md)** - if you know C++
   from the olympiad but not Java or Kotlin. What maps across, what does not, and which
   competitive-programming habits to unlearn.
3. **[docs/robot-configuration.md](docs/robot-configuration.md)** - the motors and servos on
   the robot, and the names the code looks them up by.
4. **[`PrimeTechV3`](TeamCode/src/main/java/PrimeTechV3/)** - then read this package. It has
   its own README explaining how it is put together.

## Structure (in `TeamCode`)

Three versions of the robot code, written in that order across the season. Each has its own
README with the detail.

| Package | Language | Use it for | Read it? |
|---|---|---|---|
| [`PrimeTech`](TeamCode/src/main/java/PrimeTech/) | Java | The TeleOp actually driven all season, plus the PID tuning OpModes | Yes, for the tuners and to see what we drove - but do not copy its structure |
| [`PrimeTechV2`](TeamCode/src/main/java/PrimeTechV2/) | Kotlin | An experiment with the [NextFTC](https://nextftc.dev/) command library. Abandoned; every OpMode is `@Disabled` | Only out of interest |
| ✔ [`PrimeTechV3`](TeamCode/src/main/java/PrimeTechV3/) | Kotlin | The autonomous routines we competed with. **The one to learn from and build on** | Yes - start here |

`PrimeTech` is based on [finite state machines](https://gm0.org/en/latest/docs/software/concepts/finite-state-machines.html)
and split into components, but it grew organically over the season and it shows: global
mutable state, singletons everywhere, and the arm's behaviour spread across three files.
`PrimeTechV3` does the same job far more cleanly, and is the version to extend.

There is also [`pedroPathing`](TeamCode/src/main/java/pedroPathing/), which is not ours - see
below.

## Building and deploying

Open the project in [Android Studio](https://developer.android.com/studio), connect to the
Control Hub, and hit Run. If you have never done this, the
[FTC docs](https://ftc-docs.firstinspires.org/en/latest/programming_resources/index.html) walk
through it, and [gm0](https://gm0.org/) is the best general reference for everything FTC.

Building needs the Android SDK, so it only works from Android Studio - not from a plain
`./gradlew build` on a machine without it.

## Made using [Pedro Pathing Quickstart](https://github.com/Pedro-Pathing/Quickstart)

[Pedro Pathing docs](https://pedropathing.com/).

Pedro Pathing is the library that drives the robot along a path during autonomous. Two parts of
this repo come from its quickstart and are **not** team code:

- `pedroPathing/constants/` - tuned numbers for our robot. Real values, edit when the robot
  changes, but do not restructure.
- `pedroPathing/tuners_tests/` - upstream tuning OpModes. Left exactly as they came, so they can
  be replaced wholesale when the library updates. Do not edit these.

Please note that this documentation has been written by AI, most of it being the AI taking comments from inside the project and putting them inside READMEs. In addition to that, an AI also rewrote the code to make it more human-readable, so it might not behave well on a robot. If you wish to use this code on an actual robot, please use https://github.com/PrimeTech-Robotics/FTC-Into-The-Deep instead.

