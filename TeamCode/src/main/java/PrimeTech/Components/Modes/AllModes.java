package PrimeTech.Components.Modes;

import com.acmerobotics.dashboard.config.Config;

import PrimeTech.Components.Outtake.Claw_vechi;
import PrimeTech.Components.Outtake.Extension;
import PrimeTech.Components.Outtake.Outtake;
import PrimeTech.Components.Outtake.Pivot;

@Config
public class AllModes {
    public static double intakeSpecimenExtension = 0;
    public static double intakeSpecimenPivot = 450;

    public static double outtakeSpecimenExtension = 1000;
    public static double outtakeSpecimenPivot = 1500;

    public static double outtakeSamplePivot = 2250;
    public static double outtakeSampleExtension = 2600;

    public static double intakeSamplePivot = 0;
    public static double intakeSampleExtension = 0;

    public static AllModes instance = null;

    public static synchronized AllModes getInstance() {
        if (instance == null) {
            instance = new AllModes();
        }
        return instance;
    }

    public enum RetractCase{
        EXTENSION_RETRACT, EXTENSION, PIVOT, IDLE
    }

    public RetractCase retractCase = RetractCase.EXTENSION_RETRACT;

    public void setRetractCase_to_EXTENSION_RETRACT(){
        retractCase = RetractCase.EXTENSION_RETRACT;
    }
    public void run_to_pos_in_order(double pivotTarget, double extensionTarget, boolean intake_sample){
        switch(retractCase){
            case EXTENSION_RETRACT:
                if (Extension.extension_right.getCurrentPosition() > 100) {
                    Extension.getInstance().run_to_target(Extension.target);
                } else {
                    retractCase = RetractCase.PIVOT;
                }
                break;
            case PIVOT:
                if (Pivot.motorPivot.getCurrentPosition() > pivotTarget + 50 || Pivot.motorPivot.getCurrentPosition() < pivotTarget - 50 ) {
                    Pivot.getInstance().run_to_target(pivotTarget);
                } else {
                    retractCase = RetractCase.EXTENSION;
                }
                break;
            case EXTENSION:
                Pivot.getInstance().run_to_target(pivotTarget);
                if (Extension.extension_right.getCurrentPosition() > extensionTarget + 50 || Extension.extension_right.getCurrentPosition() < extensionTarget - 50) {
                    Extension.getInstance().run_to_target(extensionTarget);
                } else {
                    retractCase = RetractCase.IDLE;
                }
                break;
            case IDLE:
                if(intake_sample){
                    Extension.getInstance().loop();
                    Pivot.getInstance().run_to_target(pivotTarget);
                }
                else{
                    Pivot.getInstance().run_to_target(pivotTarget);
                    Extension.getInstance().run_to_target(extensionTarget);
                }

                break;
        }
    }

    public static void general() {
        Extension.getInstance().loop();
        Pivot.getInstance().loop();
        Claw_vechi.getInstance().loop();
    }

    public static void outtake_sample_init(){
        Pivot.target = outtakeSamplePivot;
        AllModes.getInstance().setRetractCase_to_EXTENSION_RETRACT();
        Extension.target = outtakeSampleExtension;
    }

    public static void outtake_sample(){
        Claw_vechi.getInstance().loop();

        AllModes.getInstance().run_to_pos_in_order(outtakeSamplePivot,outtakeSampleExtension,false);
    }

    public static void outtake_specimen_init(){
        AllModes.getInstance().setRetractCase_to_EXTENSION_RETRACT();
        Pivot.target = outtakeSpecimenPivot;
        Extension.target = outtakeSpecimenExtension;
    }

    public  static void outtake_specimen(){
        Claw_vechi.getInstance().openState_method();
        Claw_vechi.getInstance().rotate(0.3 + Pivot.pivot_angle() / 180);

        AllModes.getInstance().run_to_pos_in_order(outtakeSpecimenPivot,outtakeSpecimenExtension,false);
    }

    public static void intake_specimen_init(){
        AllModes.getInstance().setRetractCase_to_EXTENSION_RETRACT();
        Pivot.target = intakeSpecimenPivot;
        Extension.target = intakeSpecimenExtension;
    }

    public static void intake_specimen() {
        Claw_vechi.getInstance().openState_method();
        Claw_vechi.getInstance().rotate(0.3 + Pivot.pivot_angle() / 180);

        AllModes.getInstance().run_to_pos_in_order(intakeSpecimenPivot,intakeSpecimenExtension,false);
    }

    public static void intake_sample_init(){
        AllModes.getInstance().setRetractCase_to_EXTENSION_RETRACT();
        Pivot.target = intakeSamplePivot;
        Extension.target = intakeSampleExtension;
        Claw_vechi.getInstance().change_to_OPEN_POS();
    }

    public static void intake_sample() {

        Claw_vechi.getInstance().openState_method();
        Claw_vechi.getInstance().ll_method();
        Claw_vechi.getInstance().rotate(0.75 + Pivot.pivot_angle() / 180);

        AllModes.getInstance().run_to_pos_in_order(intakeSamplePivot,outtakeSpecimenExtension,true);}
    }

