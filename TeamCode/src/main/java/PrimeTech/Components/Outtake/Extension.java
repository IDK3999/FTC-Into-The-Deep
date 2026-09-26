package PrimeTech.Components.Outtake;

import static PrimeTech.Global.Global.hardwareMap;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import PrimeTech.Components.Gamepad.GamepadTracker;

/**
 * The extension (also called the lift): two motors that slide the arm in and out.
 *
 * <p>Positions are in <b>encoder ticks</b>, 0 being fully retracted. In TeleOp the triggers
 * move {@link #targetTicks} and a PID controller in {@link #runToTarget} chases it.
 *
 * <p>Both motors always get the same power because they pull the same slide; the left one is
 * reversed because it is mounted mirrored.
 */
public class Extension {
    /** Fully retracted, in encoder ticks. */
    public static final double MIN_TICKS = 0.0;

    /** How close to the target counts as arrived, in ticks. Read by {@link Outtake}. */
    public static final double TOLERANCE_TICKS = 40;

    /**
     * Current upper limit on {@link #targetTicks}.
     *
     * <p>Not a constant: {@link PrimeTech.Components.Modes.AllModes} swaps it between
     * {@link #FULL_RANGE_MAX_TICKS} and {@link #SPECIMEN_MAX_TICKS} depending on the mode.
     */
    public static double maxTicks = 600;

    /** The normal full extension limit, in ticks. */
    public static double FULL_RANGE_MAX_TICKS = 600;

    /**
     * A reduced limit used while scoring specimens, in ticks.
     *
     * <p>Extending further than this at chamber height would let the arm reach outside the
     * robot's legal size, or crash it into the chamber.
     */
    public static double SPECIMEN_MAX_TICKS = 370;

    // region PID tuning
    // See Pivot for what proportional / integral / derivative do.
    public static double proportionalGain = 0.01;
    public static double integralGain = 0;
    public static double derivativeGain = 0.00025;

    /**
     * Gravity feedforward gain, applied in {@link #runToTarget}.
     *
     * <p>Unlike {@link Pivot}'s, this one is actually in use: when the arm is raised the
     * slide has to hold its own weight, so a little extra power is added in proportion to
     * how far up the arm is.
     */
    public static double feedforwardGain = 0.1;
    // endregion PID tuning

    /** Where we want the slide to be, in ticks. Also set from {@code AllModes}. */
    public static double targetTicks = 0;

    /** Config name: "extensionRight". Its encoder is the one the PID reads. */
    public static DcMotorEx extensionMotorRight = null;

    private static Extension instance = null;

    /** How far a fully pulled trigger moves the target per loop, in ticks. */
    public final double ticksPerFullTrigger = 25.0;

    /** Config name: "extensionLeft". Reversed, since it is mounted mirrored. */
    public DcMotorEx extensionMotorLeft = null;

    /** Whether the target is at a limit or free to move in either direction. */
    LimitState limitState = LimitState.AT_MIN;

    private PIDController controller;

    public static synchronized Extension getInstance() {
        if (instance == null) {
            instance = new Extension();
        }
        return instance;
    }

    public void init() {
        controller = new PIDController(proportionalGain, integralGain, derivativeGain);

        extensionMotorLeft = hardwareMap.get(DcMotorEx.class, "extensionLeft");
        extensionMotorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        extensionMotorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        extensionMotorLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        extensionMotorRight = hardwareMap.get(DcMotorEx.class, "extensionRight");
        extensionMotorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        extensionMotorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void start() {
        targetTicks = 0;
        limitState = LimitState.AT_MIN;
    }

    /** Reads the triggers and drives towards the resulting target. Call once per loop. */
    public void loop() {
        double target = updateTargetFromGamepad();
        runToTarget(target);
    }

    /**
     * Moves {@link #targetTicks} based on the triggers, clamped to
     * {@link #MIN_TICKS}..{@link #maxTicks}.
     *
     * <p>The triggers are analog, so a light pull extends slowly and a full pull extends at
     * {@link #ticksPerFullTrigger} per loop - the arm is proportional rather than on/off.
     *
     * @return the new target, in ticks.
     */
    public double updateTargetFromGamepad() {
        double previousTarget = targetTicks;
        switch (limitState) {
            case AT_MIN:
                // Fully retracted: only extending is allowed.
                targetTicks += ticksPerFullTrigger * GamepadTracker.getInstance().rightTrigger();
                if (previousTarget != targetTicks) {
                    limitState = LimitState.IN_RANGE;
                }
                break;
            case AT_MAX:
                // Fully extended: only retracting is allowed.
                targetTicks -= ticksPerFullTrigger * GamepadTracker.getInstance().leftTrigger();
                if (previousTarget != targetTicks) {
                    limitState = LimitState.IN_RANGE;
                }
                break;
            case IN_RANGE:
                targetTicks += ticksPerFullTrigger * GamepadTracker.getInstance().rightTrigger()
                        - ticksPerFullTrigger * GamepadTracker.getInstance().leftTrigger();
                if (targetTicks > maxTicks) {
                    clampToMax();
                }
                if (targetTicks < MIN_TICKS) {
                    clampToMin();
                }
                break;
        }
        return targetTicks;
    }

    /** Pins the target to fully retracted. */
    public void clampToMin() {
        limitState = LimitState.AT_MIN;
        targetTicks = MIN_TICKS;
    }

    /** Pins the target to the current {@link #maxTicks}. */
    public void clampToMax() {
        limitState = LimitState.AT_MAX;
        targetTicks = maxTicks;
    }

    /**
     * Sets motor power to drive the slide towards {@code target}. Must be called every loop.
     *
     * @param target where to drive to, in encoder ticks.
     */
    public void runToTarget(double target) {
        controller.setPID(proportionalGain, integralGain, derivativeGain);
        int currentTicks = extensionMotorRight.getCurrentPosition();
        double pidOutput = controller.calculate(currentTicks, target);

        // Gravity feedforward: the further the arm is rotated up, the more of the slide's
        // weight the motors have to hold, so add power in proportion to sin(arm angle).
        double feedforward =
                Math.sin(Math.toRadians(Pivot.getPivotAngleDegrees())) * feedforwardGain;

        double power = pidOutput + feedforward;

        extensionMotorRight.setPower(power);
        extensionMotorLeft.setPower(power);
    }

    /** Lets the target move freely again, e.g. after a mode change repositioned the slide. */
    public void setLimitStateInRange() {
        limitState = LimitState.IN_RANGE;
    }

    /** Whether the target has hit one of its limits. */
    enum LimitState {
        AT_MAX, IN_RANGE, AT_MIN
    }
}
