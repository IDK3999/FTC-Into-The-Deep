package PrimeTech.Components.Modes;

import PrimeTech.Components.Gamepad.Gamepad;

public class FSMModes {
    private static FSMModes instance = null;
    public Modes modes = Modes.GENERAL;
    public Init init = Init.NOT_OVER;

    public static synchronized FSMModes getInstance() {
        if (instance == null) {
            instance = new FSMModes();
        }
        return instance;
    }

    public void FSM() {
        switch (modes) {
            case GENERAL:
                if (Gamepad.getInstance().dpad_right()) {
                    modes = Modes.INTAKE_SPECIMEN;
                    init = Init.NOT_OVER;
                }
                if (Gamepad.getInstance().dpad_down()) {
                    modes = Modes.INTAKE_SAMPLE;
                }
                AllModes.general();
                break;
            case INTAKE_SAMPLE:
                        if (Gamepad.getInstance().dpad_down()) {
                            modes = Modes.GENERAL;
                        }
                        AllModes.intake_sample();
                break;
            case INTAKE_SPECIMEN:
                switch (init) {
                    case NOT_OVER:
                        AllModes.intake_specimen_init();
                        break;
                    case OVER:
                        if (Gamepad.getInstance().dpad_left()) {
                            modes = Modes.GENERAL;
                        }
                        AllModes.intake_specimen();
                        break;
                }
                break;
        }
    }

    public void init_over(){
        init = Init.OVER;
    }
    enum Modes {
        GENERAL, INTAKE_SAMPLE, INTAKE_SPECIMEN, OUTTAKE_SAMPLE
    }

    enum Init {
        OVER, NOT_OVER
    }
}
