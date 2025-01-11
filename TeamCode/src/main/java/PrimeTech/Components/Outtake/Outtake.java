package PrimeTech.Components.Outtake;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Modes.FSMModes;

public class Outtake {
    private static Outtake instance = null;
    WhatToRetract whatToRetract = WhatToRetract.IDLE;

    enum WhatToRetract {
        CLAW, EXTENSION, PIVOT, IDLE
    }
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
    public void start(){
        Claw_vechi.getInstance().start();
    }

    public void loop() {
        switch (whatToRetract) {
            case IDLE:
                FSMModes.getInstance().FSM();
                if (Gamepad.getInstance().circle()) {

                    Claw_vechi.getInstance().start();
                    Extension.getInstance().start();
                    Pivot.getInstance().start();

                    FSMModes.getInstance().change_to_general();

                    whatToRetract = WhatToRetract.EXTENSION;
                }
                break;
            case EXTENSION:
                if (Extension.extension_right.getCurrentPosition() > 100) {
                    Extension.getInstance().run_to_target(Extension.target);
                } else {
                    whatToRetract = WhatToRetract.PIVOT;
                }
                break;
            case PIVOT:
                if (Pivot.motorPivot.getCurrentPosition() > 100) {
                    Pivot.getInstance().run_to_target(Pivot.target);
                } else {
                    whatToRetract = WhatToRetract.IDLE;
                }
                break;
        }
    }

}
