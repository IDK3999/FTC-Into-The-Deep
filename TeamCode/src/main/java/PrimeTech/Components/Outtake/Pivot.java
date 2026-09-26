package PrimeTech.Components.Outtake;

import static PrimeTech.Global.Global.hardwareMap;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import PrimeTech.Components.Gamepad.GamepadTracker;

/**
 * The pivot: the motor that rotates the whole arm up and down.
 *
 * <p>Positions are in <b>encoder ticks</b> - raw counts from the motor's encoder, with 0
 * being the arm resting down. Not to be confused with the claw's own small pivot servos in
 * {@link Claw}.
 *
 * <p>In TeleOp the bumpers nudge {@link #targetTicks} up and down, and a PID controller in
 * {@link #runToTarget} drives the motor to whatever that target currently is.
 */
public class Pivot {
    /** Highest the arm is allowed to go, in encoder ticks. */
    public static final double MAX_TICKS = 2100;

    /** Lowest the arm is allowed to go, in encoder ticks. */
    public static final double MIN_TICKS = 0.0;

    /**
     * Encoder counts per degree of arm rotation.
     *
     * <p>8192 is one full turn for this encoder, so dividing by 360 converts ticks to
     * degrees. Used for the feedforward term and by {@link Extension}.
     */
    public static final double TICKS_PER_DEGREE = (double) 8192 / 360;

    /** How close to the target counts as arrived, in ticks. Read by {@link Outtake}. */
    public static final double TOLERANCE_TICKS = 100;

    // region PID tuning
    // proportional - push in proportion to how far off we are.
    // integral     - correct a steady offset. Unused here (0).
    // derivative   - damping, so the arm does not overshoot and oscillate.
    public static double proportionalGain = 0.003;
    public static double integralGain = 0.0;
    public static double derivativeGain = 0.0003;

    /**
     * Gravity feedforward gain - <b>currently 0, so the feedforward does nothing.</b>
     *
     * <p>If raised, it would add extra power to hold the arm up against its own weight, and
     * would need tuning on the robot. See {@link #runToTarget} for the formula.
     */
    public static double feedforwardGain = 0.0;
    // endregion PID tuning

    /** Where we want the arm to be, in ticks. Also set from {@link PrimeTech.Components.Modes.AllModes}. */
    public static double targetTicks = 0;

    /** The arm motor. Config name: "motorPivot". */
    public static DcMotorEx pivotMotor = null;

    private static Pivot instance = null;

    /** How far each bumper press moves the target, in ticks. */
    public final double ticksPerPress = 50;

    /** Whether the target is at a limit or free to move in either direction. */
    LimitState limitState = LimitState.AT_MIN;

    private PIDController controller;

    public static synchronized Pivot getInstance() {
        if (instance == null) {
            instance = new Pivot();
        }
        return instance;
    }

    /** The arm's current angle above its resting position, in degrees. */
    public static double getPivotAngleDegrees() {
        return pivotMotor.getCurrentPosition() / TICKS_PER_DEGREE;
    }

    public void init() {
        controller = new PIDController(proportionalGain, integralGain, derivativeGain);

        pivotMotor = hardwareMap.get(DcMotorEx.class, "motorPivot");

        // FLOAT coasts at zero power instead of braking, so the arm can be moved by hand.
        pivotMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // We run our own PID below, so the motor controller must not also try to hold a
        // position - hence RUN_WITHOUT_ENCODER. The encoder is still readable.
        pivotMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void start() {
        targetTicks = 0;
        limitState = LimitState.AT_MIN;
    }

    /** Reads the bumpers and drives towards the resulting target. Call once per loop. */
    public void loop() {
        double target = updateTargetFromGamepad();
        runToTarget(target);
    }

    /**
     * Moves {@link #targetTicks} while a bumper is held, clamped to
     * {@link #MIN_TICKS}..{@link #MAX_TICKS}.
     *
     * <p>Uses the <em>held</em> bumper state rather than a single press, so holding the
     * bumper ramps the arm steadily instead of stepping once per press.
     *
     * @return the new target, in ticks.
     */
    public double updateTargetFromGamepad() {
        switch (limitState) {
            case AT_MIN:
                // At the bottom: only up is allowed.
                if (GamepadTracker.getInstance().rightBumperHeld()) {
                    targetTicks += ticksPerPress;
                    limitState = LimitState.IN_RANGE;
                }
                break;
            case AT_MAX:
                // At the top: only down is allowed.
                if (GamepadTracker.getInstance().leftBumperHeld()) {
                    targetTicks -= ticksPerPress;
                    limitState = LimitState.IN_RANGE;
                }
                break;
            case IN_RANGE:
                if (GamepadTracker.getInstance().rightBumperHeld()) {
                    targetTicks += ticksPerPress;
                }
                if (GamepadTracker.getInstance().leftBumperHeld()) {
                    targetTicks -= ticksPerPress;
                }
                if (targetTicks > MAX_TICKS) {
                    limitState = LimitState.AT_MAX;
                    targetTicks = MAX_TICKS;
                }
                if (targetTicks < MIN_TICKS) {
                    limitState = LimitState.AT_MIN;
                    targetTicks = MIN_TICKS;
                }
                break;
        }
        return targetTicks;
    }

    /**
     * Sets motor power to drive the arm towards {@code target}. Must be called every loop.
     *
     * @param target where to drive to, in encoder ticks.
     */
    public void runToTarget(double target) {
        controller.setPID(proportionalGain, integralGain, derivativeGain);
        int currentTicks = pivotMotor.getCurrentPosition();
        double pidOutput = controller.calculate(currentTicks, target);

        // Gravity feedforward: the arm is heaviest to hold when horizontal (cos of the angle
        // is largest there) and the longer the slide is extended the more leverage it has,
        // hence scaling by the extension position. Currently multiplied by
        // feedforwardGain = 0, so this whole term is 0.
        double feedforward = Math.cos(Math.toRadians(currentTicks / TICKS_PER_DEGREE))
                * feedforwardGain
                * (1 + Extension.extensionMotorRight.getCurrentPosition() * 0.002);

        pivotMotor.setPower(pidOutput + feedforward);
    }

    /** Whether the target has hit one of its limits. */
    enum LimitState {
        AT_MAX, IN_RANGE, AT_MIN
    }
}
