package PrimeTech.Components.Modes;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Outtake.Claw;
import PrimeTech.Components.Outtake.Extension;

public class FSMModes {
    private static FSMModes instance = null;

    enum Modes {
        GENERAL, INTAKE_SAMPLE, INTAKE_SPECIMEN, OUTTAKE_SPECIMEN, OUTTAKE_SAMPLE
    }
    private Modes modes = Modes.GENERAL;

    public static synchronized FSMModes getInstance() {
        if (instance == null) {
            instance = new FSMModes();
        }
        return instance;
    }

    public void start(){
        modes = Modes.GENERAL;
    }

    public void FSM() {
        switch (modes) {
            case GENERAL:
                AllModes.general();
                break;
            case INTAKE_SAMPLE:
                AllModes.intake_sample();
                break;
            case INTAKE_SPECIMEN:
                AllModes.intake_specimen();
                break;
            case OUTTAKE_SPECIMEN:
                AllModes.outtake_specimen();
                break;
            case OUTTAKE_SAMPLE:
                AllModes.outtake_sample();
                break;
        }
        if (Gamepad.getInstance().dpad_right() && modes != Modes.INTAKE_SPECIMEN) {
            modes = Modes.INTAKE_SPECIMEN;
            AllModes.intake_specimen_init();
        }
        if (Gamepad.getInstance().dpad_down() && modes != Modes.INTAKE_SAMPLE) {
            modes = Modes.INTAKE_SAMPLE;
            AllModes.intake_sample_init();
        }
        if (Gamepad.getInstance().dpad_up() && modes != Modes.OUTTAKE_SAMPLE) {
            modes = Modes.OUTTAKE_SAMPLE;
            AllModes.outtake_sample_init();
        }
        if (Gamepad.getInstance().dpad_left() && modes != Modes.OUTTAKE_SPECIMEN) {
            modes = Modes.OUTTAKE_SPECIMEN;
            AllModes.outtake_specimen_init();
        }

    }

}