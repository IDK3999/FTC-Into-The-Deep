package PrimeTech.Components.Outtake;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Modes.FSMModes;

public class Outtake {
    private static Outtake instance = null;
    ReturnToInitPos returnToInitPos = ReturnToInitPos.OFF;

    public static synchronized Outtake getInstance() {
        if (instance == null) {
            instance = new Outtake();
        }
        return instance;
    }

    public void init() {
        Claw.getInstance().init();
        Extension.getInstance().init();
        Pivot.getInstance().init();
    }

    public void loop() {
        switch (returnToInitPos) {
            case OFF:
                if (Gamepad.getInstance().circle()) {
                    returnToInitPos = ReturnToInitPos.ON;
                }
                FSMModes.getInstance().FSM();
                break;
            case ON:
                InitPos.getInstance().return_to_init_pos();
                break;
        }
    }

    enum ReturnToInitPos {
        ON, OFF
    }
}
