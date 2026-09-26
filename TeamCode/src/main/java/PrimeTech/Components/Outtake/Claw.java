package PrimeTech.Components.Outtake;

import static PrimeTech.Global.Global.hardwareMap;

import com.qualcomm.robotcore.hardware.Servo;

import PrimeTech.Components.Gamepad.GamepadTracker;

/**
 * The claw on the end of the arm, driven by four servos:
 *
 * <ul>
 *   <li><b>grip</b> - opens and closes the jaws.</li>
 *   <li><b>wrist</b> - rolls the jaws sideways, to line up with a sample lying at an angle.</li>
 *   <li><b>claw pivot</b> (a pair, left and right) - swings the whole claw front-to-back.
 *       Not the same thing as {@link Pivot}, which rotates the entire arm.</li>
 * </ul>
 *
 * <p>All positions are servo positions in the range 0.0 to 1.0, not angles or distances.
 *
 * <p>Servos are open loop: we can tell one where to go but cannot read back where it is, so
 * nothing here can report when a movement has finished.
 */
public class Claw {
    // region Servo positions
    public static final double GRIP_OPEN = 0.9;
    public static final double GRIP_CLOSED = 0.1;

    public static final double CLAW_PIVOT_FRONT = 0.0;
    public static final double CLAW_PIVOT_MID = 0.5;
    public static final double CLAW_PIVOT_SCORE_SAMPLE = 0.65;
    public static final double CLAW_PIVOT_BACK = 1.0;

    /** Wrist upright, jaws level - the normal carrying position. */
    public static final double WRIST_STRAIGHT = 0.5;

    /** Wrist rolled 90 degrees, for grabbing a sample lying sideways. */
    public static final double WRIST_TURNED_90 = 0.16;
    // endregion Servo positions

    private static Claw instance = null;

    // region Hardware
    // Strings are device names from the Robot Configuration on the Driver Hub.
    public Servo gripServo = null;             // config name: "openingServo"
    public Servo wristServo = null;            // config name: "rotationServo"
    public Servo clawPivotServoLeft = null;    // config name: "frontBackServoLeft"
    public Servo clawPivotServoRight = null;   // config name: "frontBackServoRight"
    // endregion Hardware

    // region Current state
    // Only what we last asked for - servos give no feedback, so this is not a measurement.
    WristRotation wristRotation = WristRotation.STRAIGHT;
    GripState gripState = GripState.CLOSED;
    // endregion Current state

    public static synchronized Claw getInstance() {
        if (instance == null) {
            instance = new Claw();
        }
        return instance;
    }

    public void init() {
        gripServo = hardwareMap.get(Servo.class, "openingServo");

        wristServo = hardwareMap.get(Servo.class, "rotationServo");

        // NOTE: the two claw-pivot servos are mounted mirrored, so one of them normally has
        // to be reversed (see the TestServo and PivotAndExtensionPIDTuner OpModes, which
        // both call setDirection(REVERSE) on the left one). That call is missing here, so in
        // this TeleOp the two servos are driven to the same position and fight each other.
        // PrimeTechV3 sidesteps the problem by only using the right servo. Check this on the
        // robot before relying on the left one.
        clawPivotServoLeft = hardwareMap.get(Servo.class, "frontBackServoLeft");

        clawPivotServoRight = hardwareMap.get(Servo.class, "frontBackServoRight");
    }

    /** Moves the claw to its safe starting shape: jaws closed, wrist straight, pivoted back. */
    public void start() {
        gripState = GripState.CLOSED;
        wristRotation = WristRotation.STRAIGHT;

        gripServo.setPosition(GRIP_CLOSED);
        wristServo.setPosition(WRIST_STRAIGHT);

        clawPivotServoLeft.setPosition(CLAW_PIVOT_BACK);
        clawPivotServoRight.setPosition(CLAW_PIVOT_BACK);
    }

    /**
     * Toggles the jaws open/closed when cross is pressed. Call once per loop.
     *
     * <p>Uses {@code crossPressed()} (one loop only) rather than the button's raw state, so
     * one press gives exactly one toggle.
     */
    public void updateGripToggle() {
        switch (gripState) {
            case OPEN:
                if (GamepadTracker.getInstance().crossPressed()) {
                    close();
                }
                break;
            case CLOSED:
                if (GamepadTracker.getInstance().crossPressed()) {
                    open();
                }
                break;
        }
    }

    /** Toggles the wrist between straight and rolled 90 degrees on triangle. Call once per loop. */
    public void updateWristToggle() {
        switch (wristRotation) {
            case STRAIGHT:
                if (GamepadTracker.getInstance().trianglePressed()) {
                    setWrist(WRIST_TURNED_90);
                    wristRotation = WristRotation.TURNED_90;
                }
                break;
            case TURNED_90:
                if (GamepadTracker.getInstance().trianglePressed()) {
                    setWrist(WRIST_STRAIGHT);
                    wristRotation = WristRotation.STRAIGHT;
                }
                break;
        }
    }

    /** Rolls the wrist back upright and records it. */
    public void straightenWrist() {
        wristRotation = WristRotation.STRAIGHT;
        setWrist(WRIST_STRAIGHT);
    }

    /** @param position servo position 0.0 to 1.0, e.g. {@link #WRIST_STRAIGHT}. */
    public void setWrist(double position) {
        wristServo.setPosition(position);
    }

    /**
     * Swings the claw front-to-back. Both pivot servos get the same position.
     *
     * @param position servo position 0.0 to 1.0, e.g. {@link #CLAW_PIVOT_MID}.
     */
    public void setClawPivot(double position) {
        clawPivotServoRight.setPosition(position);
        clawPivotServoLeft.setPosition(position);
    }

    public void open() {
        gripServo.setPosition(GRIP_OPEN);
        gripState = GripState.OPEN;
    }

    public void close() {
        gripServo.setPosition(GRIP_CLOSED);
        gripState = GripState.CLOSED;
    }

    /** How far the wrist is rolled. */
    enum WristRotation {
        STRAIGHT, TURNED_90
    }

    /** Whether the jaws are gripping something. */
    enum GripState {
        OPEN, CLOSED
    }
}
