package PrimeTech.Components.Drivetrain;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static PrimeTech.Global.Global.gamepad2;
import static PrimeTech.Global.Global.hardwareMap;
import static PrimeTech.Global.Global.telemetry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import PrimeTech.Components.Outtake.Extension;
import PrimeTech.Components.Outtake.Pivot;

/**
 * The four-wheel mecanum drivetrain, driven from <b>gamepad 2</b>. (Gamepad 1 runs the arm -
 * see {@link PrimeTech.Components.Gamepad.GamepadTracker}.)
 *
 * <p>Mecanum wheels have rollers set at 45 degrees, which lets the robot slide sideways as
 * well as drive and turn. Each wheel gets a different mix of the three stick inputs:
 *
 * <pre>
 *   forward  both sticks' y  - all four wheels the same way
 *   strafe   left stick x    - front and back wheels opposite ways
 *   turn     right stick x   - left and right sides opposite ways
 * </pre>
 *
 * <p>Adding those three together per wheel is the whole of mecanum driving.
 */
public class Drivetrain {
    /**
     * Scales every motor power. 1.0 is full speed.
     *
     * <p>Turn it down to make the robot gentler, or to stretch the battery near the end of a
     * match when a full-power drivetrain browns out the control hub.
     */
    private static final double POWER_SCALE = 1.0;

    private static Drivetrain instance = null;

    DcMotor leftBack = null;
    DcMotor leftFront = null;
    DcMotor rightBack = null;
    DcMotor rightFront = null;

    public static synchronized Drivetrain getInstance() {
        if (instance == null) {
            instance = new Drivetrain();
        }
        return instance;
    }

    public void init() {
        // TODO: Check if FLOAT is better than BRAKE for movement
        // BRAKE stops the robot dead when the sticks are released, which makes it easier to
        // place precisely; FLOAT would let it coast.

        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        leftBack.setZeroPowerBehavior(BRAKE);
        // The left motors are mounted mirrored, so they must be reversed for "forward"
        // to mean the same thing on both sides.
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);

        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftFront.setZeroPowerBehavior(BRAKE);
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);

        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightBack.setZeroPowerBehavior(BRAKE);

        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightFront.setZeroPowerBehavior(BRAKE);
    }

    /** Call once per loop. */
    public void loop() {
        // Stick y is negative when pushed forward, hence the minus sign.
        double forward = applyStickCurve(-gamepad2.left_stick_y);
        double strafe = applyStickCurve(gamepad2.left_stick_x);
        double turn = applyStickCurve(gamepad2.right_stick_x);

        leftFront.setPower((forward + strafe + turn) * POWER_SCALE);
        leftBack.setPower((forward - strafe + turn) * POWER_SCALE);
        rightFront.setPower((forward - strafe - turn) * POWER_SCALE);
        rightBack.setPower((forward + strafe - turn) * POWER_SCALE);

        telemetry.addData("extension_pos: ", Extension.extensionMotorRight.getCurrentPosition());
        telemetry.addData("extension_target: ", Extension.targetTicks);
        telemetry.addData("pivot_pos: ", Pivot.pivotMotor.getCurrentPosition());
        telemetry.addData("pivot_target: ", Pivot.targetTicks);
        telemetry.update();
    }

    /**
     * Bends the stick response so small movements are gentle and large ones still reach full
     * power - much easier to drive precisely than a straight 1:1 mapping.
     *
     * <p>The curve is {@code 0.5 * tan(1.12 * x)}. It is steepest at the ends, so most of the
     * stick's travel covers the slow half of the speed range.
     *
     * <p>Two things worth knowing. At full stick this returns about 1.04, slightly over 1.
     * And because the three inputs are summed per wheel above, a full diagonal while turning
     * asks for up to about 3.1. The SDK silently clamps anything outside -1..1, so the robot
     * still drives, but in that situation the mix between forward, strafe and turn is no
     * longer what the sticks asked for. Normalising the four powers instead of clamping would
     * fix it - but that changes how the robot handles, so re-test with the drivers if you do.
     *
     * @param stickValue raw stick axis, -1.0 to 1.0.
     * @return the scaled power.
     */
    private double applyStickCurve(double stickValue) {
        return 0.5 * Math.tan(1.12 * stickValue);
    }
}
