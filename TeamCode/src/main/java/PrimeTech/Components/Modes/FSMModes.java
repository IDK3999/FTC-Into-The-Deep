package PrimeTech.Components.Modes;

import PrimeTech.Components.Gamepad.Gamepad;

public class FSMModes {
    private static FSMModes instance = null;
    private Modes modes = Modes.GENERAL;
    private IntakeSample intakeSample = IntakeSample.PARALLEL;

    public static synchronized FSMModes getInstance() {
        if (instance == null) {
            instance = new FSMModes();
        }
        return instance;
    }

    public void start() {
        modes = Modes.GENERAL;
    }

    public void FSM() {
        switch (modes) {
            case GENERAL:
                AllModes.general();
                break;
            case INTAKE_SAMPLE:
                AllModes.intake_sample();
                switch (intakeSample){
                    case PARALLEL:
                        if(Gamepad.getInstance().dpad_down()){
                            AllModes.intake_sample_perpendicular();
                            intakeSample = IntakeSample.PERPENDICULAR;
                        }
                        break;
                    case PERPENDICULAR:
                        if(Gamepad.getInstance().dpad_down()){
                            AllModes.intake_sample_parallel();
                            intakeSample = IntakeSample.PARALLEL;
                        }
                        break;
                }
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
            intakeSample = IntakeSample.PARALLEL;
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

    enum Modes {
        GENERAL, INTAKE_SAMPLE, INTAKE_SPECIMEN, OUTTAKE_SPECIMEN, OUTTAKE_SAMPLE
    }

    enum IntakeSample{
        PARALLEL, PERPENDICULAR
    }

}