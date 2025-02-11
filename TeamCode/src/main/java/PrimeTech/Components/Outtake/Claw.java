package PrimeTech.Components.Outtake;

import static PrimeTech.Global.Global.hardwareMap;

import com.qualcomm.robotcore.hardware.Servo;

import PrimeTech.Components.Gamepad.Gamepad;

public class Claw {
    // Servo positions
    public static final double OPEN_POS = 0.65;
    public static final double CLOSED_POS = 1;
    public static final double FRONT_POS = 0.95;
    public static final double MID_POS = 0.47;
    public static final double OUTTAKE_SAMPLE_PIVOT_POS = 0.35;
    public static final double BACK_POS = 0.0;
    public static final double ROTATION_INIT = 0.25;
    public static final double ROTATION_PERPENDICULAR = 0.6;
    private static Claw instance = null;
    public Servo openingServo = null;
    public Servo rotationServo = null;
    public Servo frontBackServo_left = null;
    public Servo frontBackServo_right = null;
    Rotation rotation = Rotation.ZERO;
    OpenState openState = OpenState.CLOSED;
    FrontBackState frontBackState = FrontBackState.FRONT;

    public static synchronized Claw getInstance() {
        if (instance == null) {
            instance = new Claw();
        }
        return instance;
    }

    public void init() {
        openingServo = hardwareMap.get(Servo.class, "openingServo");

        rotationServo = hardwareMap.get(Servo.class, "rotationServo");

        frontBackServo_left = hardwareMap.get(Servo.class, "frontBackServoLeft");
        frontBackServo_left.setDirection(Servo.Direction.REVERSE);

        frontBackServo_right = hardwareMap.get(Servo.class, "frontBackServoRight");
    }

    public void start() {
        frontBackState = FrontBackState.BACK;
        openState = OpenState.CLOSED;
        rotation = Rotation.ZERO;

        openingServo.setPosition(CLOSED_POS);
        rotationServo.setPosition(ROTATION_INIT);

        frontBackServo_left.setPosition(BACK_POS);
        frontBackServo_right.setPosition(BACK_POS);
    }

    public void openState_method() {
        switch (openState) {
            case OPEN:
                if (Gamepad.getInstance().cross()) {
                    // Transition to CLOSED state
                    openingServo.setPosition(CLOSED_POS);
                    openState = OpenState.CLOSED;
                }
                break;
            case CLOSED:
                if (Gamepad.getInstance().cross()) {
                    // Transition to OPEN state
                    openingServo.setPosition(OPEN_POS);
                    openState = OpenState.OPEN;
                }
                break;
        }
    }

    public void intake_rotation() {
        switch (rotation) {
            case ZERO:
                if (Gamepad.getInstance().triangle()) {
                    rotate(ROTATION_PERPENDICULAR);
                    rotation = Rotation.NINETIES;
                }
                break;
            case NINETIES:
                if (Gamepad.getInstance().triangle()) {
                    rotate(ROTATION_INIT);
                    rotation = Rotation.ZERO;
                }
                break;
        }
    }

    public void change_to_rotation_ZERO() {
        rotation = Rotation.ZERO;
    }

    public void rotate(double angle) {
        rotationServo.setPosition(angle);
    }

    public void pivot(double angle) {
        frontBackServo_right.setPosition(angle);
        frontBackServo_left.setPosition(angle);
    }

    public void change_to_OPEN_POS() {
        openingServo.setPosition(OPEN_POS);
        openState = OpenState.OPEN;
    }


    enum Rotation {
        ZERO, NINETIES
    }

    enum OpenState {
        OPEN, CLOSED
    }

    enum FrontBackState {
        FRONT, BACK
    }

}
