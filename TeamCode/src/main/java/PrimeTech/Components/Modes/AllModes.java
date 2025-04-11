package PrimeTech.Components.Modes;

import com.acmerobotics.dashboard.config.Config;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Outtake.Claw;
import PrimeTech.Components.Outtake.Extension;
import PrimeTech.Components.Outtake.Pivot;

@Config
public class AllModes {
    public static double intakeSpecimenExtension = 0;
    public static double intakeSpecimenPivot = 450;

    public static double outtakeSpecimenExtension = 50;
    public static double outtakeSpecimenPivot = 2100;

    public static double outtakeSampleExtension = 900;
    public static double outtakeSamplePivot = 2050;

    public static double intakeSampleExtension = 0;
    public static double intakeSamplePivot = 0;

    public static AllModes instance = null;
    public static RetractCase retractCase = RetractCase.EXTENSION_RETRACT;

    public static synchronized AllModes getInstance() {
        if (instance == null) {
            instance = new AllModes();
        }
        return instance;
    }

    public static void setRetractCase_to_EXTENSION_RETRACT() {
        retractCase = RetractCase.EXTENSION_RETRACT;
    }

    /// RUN TO POSITION
    public static void run_to_pos_in_order(double pivotTarget, double extensionTarget, Mode mode) {
        switch (retractCase) {
            case EXTENSION_RETRACT:
                Pivot.getInstance().run_to_target(Pivot.target);
                if (Extension.extension_right.getCurrentPosition() > Extension.tolerance) {
                    Extension.getInstance().run_to_target(0);
                } else {
                    retractCase = RetractCase.PIVOT;
                }
                break;
            case PIVOT:
                Extension.getInstance().run_to_target(0);
                if (Pivot.motorPivot.getCurrentPosition() > pivotTarget + Pivot.tolerance || Pivot.motorPivot.getCurrentPosition() < pivotTarget - Pivot.tolerance) {
                    Pivot.getInstance().run_to_target(pivotTarget);
                } else {
                    retractCase = RetractCase.EXTENSION;
                }
                break;
            case EXTENSION:
                Pivot.getInstance().run_to_target(pivotTarget);
                if (Extension.extension_right.getCurrentPosition() > extensionTarget + Extension.tolerance || Extension.extension_right.getCurrentPosition() < extensionTarget - Extension.tolerance) {
                    Extension.getInstance().run_to_target(extensionTarget);
                } else {
                    retractCase = RetractCase.IDLE;
                }
                break;
            case IDLE:
                switch (mode) {
                    case INTAKE_SAMPLE:
                        intake_sample_loop(pivotTarget);
                        break;
                    case OUTTAKE_SAMPLE:
                        outtake_sample_loop(pivotTarget, extensionTarget);
                        break;
                    case INTAKE_SPECIMEN:
                        intake_specimen_loop(extensionTarget);
                        break;
                    case OUTTAKE_SPECIMEN:
                        outtake_specimen_loop(pivotTarget);
                        break;
                }
                break;
        }
    }

    /// GENERAL
    public static void general() {
        Extension.getInstance().loop();
        Claw.getInstance().openState_method();
        Pivot.getInstance().loop();
    }

    /// OUTTAKE SAMPLE
    public static void outtake_sample_init() {
        setRetractCase_to_EXTENSION_RETRACT();

        Extension.target = outtakeSampleExtension;

        Claw.getInstance().rotate(Claw.ROTATION_INIT);
        Claw.getInstance().pivot(Claw.OUTTAKE_SAMPLE_PIVOT_POS);
    }

    public static void outtake_sample() {
        run_to_pos_in_order(outtakeSamplePivot, outtakeSampleExtension, Mode.OUTTAKE_SAMPLE);
        Claw.getInstance().openState_method();
    }

    public static void outtake_sample_loop(double pivotTarget, double extensionTarget) {
        Pivot.target = outtakeSamplePivot;
        Pivot.getInstance().run_to_target(pivotTarget);
        Extension.getInstance().run_to_target(extensionTarget);
    }

    /// OUTTAKE SPECIMEN
    public static void outtake_specimen_init() {
        setRetractCase_to_EXTENSION_RETRACT();

        Extension.target = outtakeSpecimenExtension;
        Extension.getInstance().change_liftState_to_INRANGE();
        Extension.MAX_TICKS = Extension.LIMITED_MAX_TICKS;

        Claw.getInstance().pivot(Claw.OUTTAKE_SAMPLE_PIVOT_POS);
        Claw.getInstance().rotate(Claw.ROTATION_INIT);

    }

    public static void outtake_specimen() {
        run_to_pos_in_order(outtakeSpecimenPivot, outtakeSpecimenExtension, Mode.OUTTAKE_SPECIMEN);
        Claw.getInstance().openState_method();
    }

    public static void outtake_specimen_loop(double pivotTarget) {
        if (Gamepad.getInstance().left_bumper_pressed()) {
            Extension.target = outtakeSpecimenExtension;
        }
        if (Gamepad.getInstance().right_bumper_pressed()) {
            Extension.target = Extension.MAX_TICKS;
        }


        Pivot.target = outtakeSpecimenPivot;

        Pivot.getInstance().run_to_target(pivotTarget);
        Extension.getInstance().loop();
    }

    /// INTAKE SPECIMEN
    public static void intake_specimen_init() {
        setRetractCase_to_EXTENSION_RETRACT();

        Extension.target = intakeSpecimenExtension;

        Claw.getInstance().change_to_OPEN_POS();
        Claw.getInstance().pivot(Claw.MID_POS);
        Claw.getInstance().rotate(Claw.ROTATION_INIT);
    }

    public static void intake_specimen() {
        run_to_pos_in_order(intakeSpecimenPivot, intakeSpecimenExtension, Mode.INTAKE_SPECIMEN);
        Claw.getInstance().openState_method();
    }

    public static void intake_specimen_loop(double extensionTarget) {
        Pivot.target = intakeSpecimenPivot;
        Claw.getInstance().pivot(Claw.MID_POS - Pivot.pivot_angle() / 180);

        Pivot.getInstance().loop();
        Extension.getInstance().run_to_target(extensionTarget);
    }

    /// INTAKE SAMPLE
    public static void intake_sample_init() {
        setRetractCase_to_EXTENSION_RETRACT();

        Extension.target = intakeSampleExtension;
        Extension.getInstance().start();
        Extension.MAX_TICKS = Extension.FINAL_MAX_TICKS;

        Claw.getInstance().change_to_CLOSE_POS();
        Claw.getInstance().change_to_rotation_ZERO();
        Claw.getInstance().pivot(Claw.MID_POS);
    }

    public static void intake_sample_parallel() {
        Claw.getInstance().change_to_CLOSE_POS();
        Claw.getInstance().change_to_rotation_ZERO();
        Claw.getInstance().pivot(Claw.MID_POS);
    }

    public static void intake_sample_perpendicular() {
        Claw.getInstance().change_to_OPEN_POS();
        Claw.getInstance().change_to_rotation_ZERO();
        Claw.getInstance().pivot(Claw.FRONT_POS);
    }

    public static void intake_sample() {
        run_to_pos_in_order(intakeSamplePivot, intakeSampleExtension, Mode.INTAKE_SAMPLE);
        Claw.getInstance().openState_method();
    }

    public static void intake_sample_loop(double pivotTarget) {
        if (Gamepad.getInstance().left_bumper_pressed()) {
            FSMModes.getInstance().changeIntakeSampleToParallel();

            Claw.getInstance().change_to_rotation_ZERO();
            Claw.getInstance().pivot(Claw.MID_POS);

            Extension.getInstance().change_LiftState_to_MIN();
        }
        if (Gamepad.getInstance().right_bumper_pressed()) {
            FSMModes.getInstance().changeIntakeSampleToParallel();

            Claw.getInstance().change_to_rotation_ZERO();
            Claw.getInstance().pivot(Claw.MID_POS);

            Extension.getInstance().change_LiftState_to_MAX();
        }

        Pivot.target = intakeSamplePivot;
        Claw.getInstance().intake_rotation();

        Pivot.getInstance().run_to_target(pivotTarget);
        Extension.getInstance().loop();
    }

    /// ENUMS
    enum Mode {
        INTAKE_SAMPLE, INTAKE_SPECIMEN, OUTTAKE_SAMPLE, OUTTAKE_SPECIMEN
    }

    public enum RetractCase {
        EXTENSION_RETRACT, EXTENSION, PIVOT, IDLE
    }


}

