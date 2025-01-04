package PrimeTech.Components.Gamepad;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;

public class Gamepad {
    private static Gamepad instance = null;
    com.qualcomm.robotcore.hardware.Gamepad currentGamepad = null;
    com.qualcomm.robotcore.hardware.Gamepad previousGamepad = null;

    public static synchronized Gamepad getInstance() {
        if (instance == null) {
            instance = new Gamepad();
        }
        return instance;
    }

    public void init() {
        currentGamepad = new com.qualcomm.robotcore.hardware.Gamepad();
        currentGamepad.copy(gamepad1);

        previousGamepad = new com.qualcomm.robotcore.hardware.Gamepad();
    }

    public void loop() {
        previousGamepad.copy(currentGamepad);
        currentGamepad.copy(gamepad1);
    }

    public boolean triangle() {
        return (currentGamepad.triangle && !previousGamepad.triangle);
    }

    public boolean square() {
        return (currentGamepad.square && !previousGamepad.square);
    }

    public boolean circle() {
        return (currentGamepad.circle && !previousGamepad.circle);
    }

    public boolean cross() {
        return (currentGamepad.cross && !previousGamepad.cross);
    }

    public boolean dpad_up() {
        return currentGamepad.dpad_up;
    }

    public boolean dpad_down() {
        return currentGamepad.dpad_down && !previousGamepad.dpad_down;
    }

    public double right_trigger() {
        return currentGamepad.right_trigger;
    }

    public double left_trigger() {
        return currentGamepad.left_trigger;
    }

    public boolean left_bumper() {
        return currentGamepad.left_bumper;
    }

    public boolean right_bumper() {
        return currentGamepad.right_bumper;
    }

    public boolean dpad_left() {
        return currentGamepad.dpad_left && !previousGamepad.dpad_left;
    }

    public boolean dpad_right() {
        return currentGamepad.dpad_right && !previousGamepad.dpad_right;
    }
}
