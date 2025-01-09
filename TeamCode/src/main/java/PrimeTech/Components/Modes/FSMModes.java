package PrimeTech.Components.Modes;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Outtake.Claw_vechi;

public class FSMModes {
    private static FSMModes instance = null;

    enum Modes {
        GENERAL, INTAKE_SAMPLE, INTAKE_SPECIMEN, OUTTAKE_SPECIMEN
    }
    public Modes modes = Modes.GENERAL;

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
                }
                if (Gamepad.getInstance().dpad_down()) {
                    modes = Modes.INTAKE_SAMPLE;
                }
                if (Gamepad.getInstance().dpad_up()) {
                    modes = Modes.OUTTAKE_SPECIMEN;
                }
                AllModes.general();
                break;
            case INTAKE_SAMPLE:
                        if (Gamepad.getInstance().dpad_right()) {
                             modes = Modes.INTAKE_SPECIMEN;
                        }
                        if (Gamepad.getInstance().dpad_up()) {
                            modes = Modes.OUTTAKE_SPECIMEN;
                        }
                        if (Gamepad.getInstance().dpad_down()) {
                            modes = Modes.GENERAL;
                        }
                        AllModes.intake_sample();
                break;
            case INTAKE_SPECIMEN:
                        if (Gamepad.getInstance().dpad_down()) {
                            modes = Modes.INTAKE_SAMPLE;
                        }
                        if (Gamepad.getInstance().dpad_up()) {
                            modes = Modes.OUTTAKE_SPECIMEN;
                        }
                        if (Gamepad.getInstance().dpad_right()) {
                            modes = Modes.GENERAL;
                        }
                        AllModes.intake_specimen();
                break;

            case OUTTAKE_SPECIMEN:
                    if (Gamepad.getInstance().dpad_right()) {
                        modes = Modes.INTAKE_SPECIMEN;
                    }
                    if (Gamepad.getInstance().dpad_down()) {
                        modes = Modes.INTAKE_SAMPLE;
                    }
                    if (Gamepad.getInstance().dpad_up()) {
                        modes = Modes.GENERAL;
                    }
                    AllModes.outtake_specimen();
                break;
        }
    }

    public void change_to_general(){
        modes = Modes.GENERAL;
    }

}
