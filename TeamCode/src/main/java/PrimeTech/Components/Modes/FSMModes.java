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
                    modes = Modes.INTAKE_OUTTAKE_SPECIMEN;
                    AllModes.intake_outtake_specimen_init();
                }
                if (Gamepad.getInstance().dpad_left()) {
                    modes = Modes.OUTTAKE_SAMPLE;
                    AllModes.outtake_sample_init();
                }
                if (Gamepad.getInstance().dpad_down()) {
                    modes = Modes.INTAKE_SAMPLE;
                    init = Init.NOT_OVER;
                }
                AllModes.general();
                break;
            case INTAKE_SAMPLE:
                switch (init) {
                    case NOT_OVER:
                        AllModes.intake_sample_init();
                        break;
                    case OVER:
                        if (Gamepad.getInstance().dpad_down()) {
                            modes = Modes.GENERAL;
                        }
                        AllModes.intake_sample_loop();
                        break;
                }
                break;
            case INTAKE_OUTTAKE_SPECIMEN:
                if (Gamepad.getInstance().dpad_right()) {
                    modes = Modes.GENERAL;
                }
                AllModes.intake_outtake_specimen_loop();
                break;
            case OUTTAKE_SAMPLE:
                if (Gamepad.getInstance().dpad_left()) {
                    modes = Modes.GENERAL;
                }
                AllModes.outtake_sample_loop();
                break;
        }
    }

    enum Modes {
        GENERAL, INTAKE_SAMPLE, INTAKE_OUTTAKE_SPECIMEN, OUTTAKE_SAMPLE
    }

    enum Init {
        OVER, NOT_OVER
    }
}
