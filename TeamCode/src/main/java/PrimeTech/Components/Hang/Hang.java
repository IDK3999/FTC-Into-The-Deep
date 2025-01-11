package PrimeTech.Components.Hang;

import static PrimeTech.Global.Global.hardwareMap;

import com.qualcomm.robotcore.hardware.Servo;

import PrimeTech.Components.Gamepad.Gamepad;

public class Hang {
    // TODO: Edit with correct values
    public static final double OPEN_POS = 1.0;
    public static final double CLOSED_POS = 0.0;
    private static Hang instance = null;
    Servo leftHangServo = null;
    Servo rightHangServo = null;

    public static synchronized Hang getInstance() {
        if (instance == null) {
            instance = new Hang();
        }
        return instance;
    }

    public void init() {
        leftHangServo = hardwareMap.get(Servo.class, "leftHangServo");
        leftHangServo.setDirection(Servo.Direction.REVERSE);
        leftHangServo.setPosition(CLOSED_POS);

        rightHangServo = hardwareMap.get(Servo.class, "rightHangServo");
        rightHangServo.setPosition(CLOSED_POS);
    }

    public void loop() {
        if (Gamepad.getInstance().dpad_up()) {
            leftHangServo.setPosition(OPEN_POS);
            rightHangServo.setPosition(OPEN_POS);
        }
    }
}
