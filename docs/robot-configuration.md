# Robot configuration

Every motor and servo is looked up by a **name** that must match the configuration stored on the
Driver Hub. Get one wrong and the OpMode throws the instant it initialises.

These names are scattered through the code as string literals, so this page is the one place they
are all listed. If the configuration on the Driver Hub is ever rebuilt from scratch, these are the
names to use.

## Drivetrain

Four motors, mecanum wheels.

| Config name | Type | Direction | Used by |
|---|---|---|---|
| `leftFront` | `DcMotor` | `REVERSE` | `Drivetrain`, and Pedro via `FConstants` |
| `leftBack` | `DcMotor` | `REVERSE` | same |
| `rightFront` | `DcMotor` | `FORWARD` | same |
| `rightBack` | `DcMotor` | `FORWARD` | same |

The left side is reversed because those motors are mounted mirrored. Set to `BRAKE` at zero power,
so the robot stops dead when the sticks are released.

Note these four are driven from **two** places that must agree: `PrimeTech`'s `Drivetrain` for
TeleOp, and Pedro Pathing for autonomous (configured in
`pedroPathing/constants/FConstants.java`).

## Arm

| Config name | Type | Direction | Notes |
|---|---|---|---|
| `motorPivot` | `DcMotorEx` | `FORWARD` | Rotates the whole arm. Encoder: 8192 ticks per revolution. |
| `extensionLeft` | `DcMotorEx` | `REVERSE` | Slide. Mounted mirrored, hence reversed. |
| `extensionRight` | `DcMotorEx` | `FORWARD` | Slide. **Its encoder is the one the PID reads** - the left motor's is ignored. |

Both arm axes run in `RUN_WITHOUT_ENCODER` mode with `FLOAT` at zero power. That sounds backwards
but is deliberate: the code runs its own PID controller, so the motor controller must not also try
to hold a position. `FLOAT` lets the arm be pushed by hand when the OpMode is not driving it.

## Claw

| Config name | Type | What it does |
|---|---|---|
| `openingServo` | `Servo` | Opens and closes the jaws (the "grip"). |
| `rotationServo` | `Servo` | Rolls the jaws sideways (the "wrist"), to line up with an angled sample. |
| `frontBackServoLeft` | `Servo` | Swings the claw front-to-back. Paired with the right one. |
| `frontBackServoRight` | `Servo` | Same. |

Two notes on the pair:

- They are mounted mirrored, so **one of them must be reversed**. `TestServo` and
  `PivotAndExtensionPIDTuner` both do this to the left one; `PrimeTech`'s `Claw` does not, which
  looks like a bug - see that file's comment.
- `PrimeTechV3` sidesteps the problem entirely by driving only `frontBackServoRight`.

## Odometry

| Config name | Type | Notes |
|---|---|---|
| `pinpoint` | goBILDA Pinpoint | Tracks the robot's position on the field. Configured in `pedroPathing/constants/LConstants.java`. |

Pod type is `goBILDA_SWINGARM_POD`, with offsets `forwardY = 5.05` and `strafeX = -6.317` inches.
Those offsets describe where the pods sit relative to the centre of the robot, so if the pods are
ever moved they must be re-measured - every autonomous path depends on them.

## Units used in the code

| Unit | Where | Meaning |
|---|---|---|
| Encoder ticks | Arm positions (`Lift`, `Pivot`) | Raw counts from that motor's encoder. Meaningful only for that mechanism. |
| Servo position | Claw | 0.0 to 1.0 across the servo's range. Not an angle. |
| Inches | Pedro Pathing poses | Field coordinates, 0 to 144 on each axis. |
| Radians | Pedro Pathing headings | Hence `Math.toRadians(180.0)` everywhere. |
| Motor power | Everything | -1.0 to 1.0, a fraction of available power. Not a speed. |
