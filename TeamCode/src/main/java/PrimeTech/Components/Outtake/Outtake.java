package PrimeTech.Components.Outtake;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Modes.FSMModes;

public class Outtake {
    private static Outtake instance = null;

    public static synchronized Outtake getInstance() {
        if (instance == null) {
            instance = new Outtake();
        }
        return instance;
    }

    public void init() {
        Claw_vechi.getInstance().init();
        Extension.getInstance().init();
        Pivot.getInstance().init();
    }

    public void loop() {
        InitPos.getInstance().return_to_init_pos();
    }
}
