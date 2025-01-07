package PrimeTech.Components.Outtake;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Modes.FSMModes;

public class InitPos {
    private static InitPos instance = null;
    WhatToRetract whatToRetract = WhatToRetract.IDLE;

    public static synchronized InitPos getInstance() {
        if (instance == null) {
            instance = new InitPos();
        }
        return instance;
    }

    public void return_to_init_pos() {
        switch (whatToRetract) {
            case IDLE:
                FSMModes.getInstance().FSM();
                if (Gamepad.getInstance().circle()) {
                    Extension.target = 0.0;
                    Extension.getInstance().change_liftState_to_MIN();
                    Pivot.target = 0.0;
                    Pivot.getInstance().change_liftState_to_MIN();
                    whatToRetract = WhatToRetract.CLAW;
                }
                break;
            case CLAW:
               /* Claw.getInstance().openingServo.setPosition(Claw.CLOSED_POS);
                Claw.getInstance().rotationServo.setPosition(Claw.ROTATION_INIT);
                Claw.getInstance().frontBackServo_left.setPosition(Claw.FRONT_BACK_INIT);
                Claw.getInstance().frontBackServo_right.setPosition(Claw.FRONT_BACK_INIT);*/
                whatToRetract = WhatToRetract.EXTENSION;
                break;
            case EXTENSION:

                if (Extension.extension_right.getCurrentPosition() > 100) {
                    Extension.getInstance().run_to_target(Extension.target);
                } else {
                    whatToRetract = WhatToRetract.PIVOT;
                }
                break;
            case PIVOT:
                Pivot.target = 0;
                if (Pivot.getInstance().motorPivot.getCurrentPosition() > 100) {
                    Pivot.getInstance().run_to_target(Pivot.target);
                } else {
                    whatToRetract = WhatToRetract.IDLE;
                }
                break;
        }
    }

    enum WhatToRetract {
        CLAW, EXTENSION, PIVOT, IDLE
    }
}
