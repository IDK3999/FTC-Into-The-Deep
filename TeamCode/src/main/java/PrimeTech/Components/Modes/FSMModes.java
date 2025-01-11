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
        if (Gamepad.getInstance().dpad_right()) {
            change_mode(Modes.INTAKE_SPECIMEN);
        }
        if (Gamepad.getInstance().dpad_down()) {
            change_mode(Modes.INTAKE_SAMPLE);
        }
        if (Gamepad.getInstance().dpad_up()) {
            change_mode(Modes.OUTTAKE_SPECIMEN);
        }
        if (Gamepad.getInstance().dpad_left()) {
            Extension.getInstance().change_liftState_to_INRANGE();
            change_mode(Modes.OUTTAKE_SAMPLE);
        }
    }

    public void change_to_general(){
        modes = Modes.GENERAL;
    }

    public void change_mode(Modes mode){
        if(modes == Modes.OUTTAKE_SAMPLE){
            Extension.MAX_TICKS = 1000;
        }
        if(modes == mode){
            modes = Modes.GENERAL;
        }
        else{
            modes = mode;
        }

    }

}