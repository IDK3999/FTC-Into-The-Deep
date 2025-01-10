package PrimeTech.Components.Modes;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Outtake.Claw_vechi;
import PrimeTech.Components.Outtake.Extension;

public class FSMModes {
    private static FSMModes instance = null;

    enum Modes {
        GENERAL, INTAKE_SAMPLE, INTAKE_SPECIMEN, OUTTAKE_SPECIMEN, OUTTAKE_SAMPLE
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
                AllModes.general();
                if (Gamepad.getInstance().dpad_right()) {
                    modes = Modes.INTAKE_SPECIMEN;
                }
                if (Gamepad.getInstance().dpad_down()) {
                    modes = Modes.INTAKE_SAMPLE;
                }
                if (Gamepad.getInstance().dpad_up()) {
                    modes = Modes.OUTTAKE_SPECIMEN;
                }
                if (Gamepad.getInstance().dpad_left()) {
                    Extension.getInstance().change_liftState_to_INRANGE();
                    modes = Modes.OUTTAKE_SAMPLE;
                }
                break;
            case INTAKE_SAMPLE:
                        AllModes.intake_sample();
                        if (Gamepad.getInstance().dpad_right()) {
                             modes = Modes.INTAKE_SPECIMEN;
                        }
                        if (Gamepad.getInstance().dpad_up()) {
                            modes = Modes.OUTTAKE_SPECIMEN;
                        }
                        if (Gamepad.getInstance().dpad_down()) {
                            modes = Modes.GENERAL;
                        }
                if (Gamepad.getInstance().dpad_left()) {
                    Extension.getInstance().change_liftState_to_INRANGE();
                    modes = Modes.OUTTAKE_SAMPLE;
                }
                break;
            case INTAKE_SPECIMEN:
                        AllModes.intake_specimen();
                        if (Gamepad.getInstance().dpad_down()) {
                            modes = Modes.INTAKE_SAMPLE;
                        }
                        if (Gamepad.getInstance().dpad_up()) {
                            modes = Modes.OUTTAKE_SPECIMEN;
                        }
                        if (Gamepad.getInstance().dpad_right()) {
                            modes = Modes.GENERAL;
                        }
                if (Gamepad.getInstance().dpad_left()) {
                    Extension.getInstance().change_liftState_to_INRANGE();
                    modes = Modes.OUTTAKE_SAMPLE;
                }
                break;

            case OUTTAKE_SPECIMEN:
                    AllModes.outtake_specimen();
                    if (Gamepad.getInstance().dpad_right()) {
                        modes = Modes.INTAKE_SPECIMEN;
                    }
                    if (Gamepad.getInstance().dpad_down()) {
                        modes = Modes.INTAKE_SAMPLE;
                    }
                    if (Gamepad.getInstance().dpad_up()) {
                        modes = Modes.GENERAL;
                    }
                if (Gamepad.getInstance().dpad_left()) {
                    Extension.getInstance().change_liftState_to_INRANGE();
                    modes = Modes.OUTTAKE_SAMPLE;
                }
                break;
            case OUTTAKE_SAMPLE:
                AllModes.outtake_sample();
                if (Gamepad.getInstance().dpad_right()) {
                    Extension.MAX_TICKS = 1000;
                    modes = Modes.INTAKE_SPECIMEN;
                }
                if (Gamepad.getInstance().dpad_down()) {
                    Extension.MAX_TICKS = 1000;
                    modes = Modes.INTAKE_SAMPLE;
                }
                if (Gamepad.getInstance().dpad_up()) {
                    Extension.MAX_TICKS = 1000;
                    modes = Modes.OUTTAKE_SPECIMEN;
                }
                if (Gamepad.getInstance().dpad_left()) {
                    Extension.MAX_TICKS = 1000;
                    modes = Modes.GENERAL;
                }
                break;
        }
    }

    public void change_to_general(){
        modes = Modes.GENERAL;
    }

}
