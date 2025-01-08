package PrimeTech.Components.Outtake;

import static PrimeTech.Global.Global.hardwareMap;

import com.qualcomm.robotcore.hardware.Servo;

import PrimeTech.Components.Gamepad.Gamepad;

public class Claw_vechi {
    private static Claw_vechi instance = null;
    Servo openingServo = null;
    Servo rotationServo = null;
    Servo frontBackServo_left = null;
    Servo frontBackServo_right = null;

    enum OpenState {
        OPEN, CLOSED
    }

    OpenState openState = OpenState.CLOSED;

    enum FrontBackState {
        FRONT, BACK
    }

    FrontBackState frontBackState = FrontBackState.FRONT;

    enum LeftRightState {
        RIGHT, LEFT, INRANGE
    }

    LeftRightState leftRightState = LeftRightState.INRANGE;

    // Servo positions
    // TODO: Adjust with actual positions
    public static final double OPEN_POS = 0.25;
    public static final double CLOSED_POS = 0.0;

    public static final double FRONT_POS = 0.875;
    public static final double BACK_POS = 0.0;

    public static final double ROTATION_INIT = 0.0;

    public static final double ROTATION_INCREMENT = 0.05;
    public static final double RIGHT_FINAL_STATE = 0.25;
    public static final double LEFT_FINAL_STATE = 0.75;
    public static final double MID_POS = 0.5;

    public static synchronized Claw_vechi getInstance() {
        if (instance == null) {
            instance = new Claw_vechi();
        }
        return instance;
    }

    public void init() {
        openingServo = hardwareMap.get(Servo.class, "openingServo");
        openingServo.setPosition(CLOSED_POS);

        rotationServo = hardwareMap.get(Servo.class, "rotationServo");
        rotationServo.setPosition(MID_POS);

        frontBackServo_left = hardwareMap.get(Servo.class, "frontBackServoLeft");
        frontBackServo_left.setDirection(Servo.Direction.REVERSE);
        frontBackServo_left.setPosition(BACK_POS);

        frontBackServo_right = hardwareMap.get(Servo.class, "frontBackServoRight");
        frontBackServo_right.setPosition(BACK_POS);


    }

    public void loop() {
        // Opening/closing FSM
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

        // Front/back movement FSM
        ///trebuie schimbat pe dpad_up&down
        switch (frontBackState) {
            case FRONT:
                if (Gamepad.getInstance().triangle()) {
                    // Transition to BACK state
                    frontBackServo_right.setPosition(BACK_POS);
                    frontBackServo_left.setPosition(BACK_POS);
                    frontBackState = FrontBackState.BACK;
                }
                break;
            case BACK:
                if (Gamepad.getInstance().triangle()) {
                    // Transition to FRONT state
                    frontBackServo_right.setPosition(FRONT_POS);
                    frontBackServo_left.setPosition(FRONT_POS);
                    frontBackState = FrontBackState.FRONT;
                }
                break;
        }

        // Left/right movement FSM
        switch (leftRightState) {
            case RIGHT:
                if (Gamepad.getInstance().dpad_right()) {
                    // Transition to INRANGE state
                    rotationServo.setPosition(rotationServo.getPosition() - ROTATION_INCREMENT);
                    leftRightState = LeftRightState.INRANGE;
                }
                break;
            case LEFT:
                if (Gamepad.getInstance().dpad_left()) {
                    // Transition to INRANGE state
                    rotationServo.setPosition(rotationServo.getPosition() + ROTATION_INCREMENT);
                    leftRightState = LeftRightState.INRANGE;
                }
                break;
            case INRANGE:
                if (Gamepad.getInstance().dpad_right()) {
                    rotationServo.setPosition(rotationServo.getPosition() - ROTATION_INCREMENT);
                    if (rotationServo.getPosition() < LEFT_FINAL_STATE) {
                        // Transition to LEFT state
                        rotationServo.setPosition(LEFT_FINAL_STATE);
                        leftRightState = LeftRightState.LEFT;
                    }
                }
                if (Gamepad.getInstance().dpad_left()) {
                    rotationServo.setPosition(rotationServo.getPosition() + ROTATION_INCREMENT);
                    if (rotationServo.getPosition() > RIGHT_FINAL_STATE) {
                        // Transition to RIGHT state
                        rotationServo.setPosition(RIGHT_FINAL_STATE);
                        leftRightState = LeftRightState.RIGHT;
                    }
                }
                break;
        }
    }

    public void change_to_CLOSED_POS(){
        openState = OpenState.CLOSED;
    }
    public void change_to_ROTATION_INIT(){
        leftRightState = LeftRightState.INRANGE;
    }
    public void change_to_BACK_POS(){
        frontBackState =FrontBackState.BACK;
    }

}
