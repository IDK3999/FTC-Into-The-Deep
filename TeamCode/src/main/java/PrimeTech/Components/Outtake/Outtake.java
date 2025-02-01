package PrimeTech.Components.Outtake;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Modes.FSMModes;

public class Outtake {
    private static Outtake instance = null;
    WhatToRetract whatToRetract = WhatToRetract.IDLE;

    public static synchronized Outtake getInstance() {
        if (instance == null) {
            instance = new Outtake();
        }
        return instance;
    }

    public void init() {
        whatToRetract = WhatToRetract.IDLE;
        Claw.getInstance().init();
        Extension.getInstance().init();
        Pivot.getInstance().init();
    }

    public void start() {
        Claw.getInstance().start();
        Extension.getInstance().start();
        Pivot.getInstance().start();
    }

    public void loop() {
        switch (whatToRetract) {
            case IDLE:
                FSMModes.getInstance().FSM();
                if (Gamepad.getInstance().circle()) {

                    start();
                    FSMModes.getInstance().start();

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
                Extension.getInstance().run_to_target(25);
                if (Pivot.motorPivot.getCurrentPosition() > 100) {
                    Pivot.getInstance().run_to_target(Pivot.target);
                } else {
                    whatToRetract = WhatToRetract.IDLE;
                }
                break;
        }
    }

    enum WhatToRetract {
        EXTENSION, PIVOT, IDLE
    }

}
