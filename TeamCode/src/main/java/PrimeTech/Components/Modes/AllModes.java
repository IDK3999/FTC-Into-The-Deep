package PrimeTech.Components.Modes;

import com.acmerobotics.dashboard.config.Config;

import PrimeTech.Components.Outtake.Claw;
import PrimeTech.Components.Outtake.Extension;
import PrimeTech.Components.Outtake.Pivot;

@Config
public class AllModes {
    public static double intakeSpecimenExtension = 0;
    public static double intakeSpecimenPivot = 375;

    public static double outtakeSpecimenExtension = 950;
    public static double outtakeSpecimenPivot = 2500;

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

    public static RetractCase retractCase = RetractCase.EXTENSION_RETRACT;

    public static void setRetractCase_to_EXTENSION_RETRACT(){
        retractCase = RetractCase.EXTENSION_RETRACT;
    }
    public static void run_to_pos_in_order(double pivotTarget, double extensionTarget, boolean intake_sample){
        switch(retractCase){
            case EXTENSION_RETRACT:
                if (Extension.extension_right.getCurrentPosition() > 100) {
                    Extension.getInstance().run_to_target(0);
                } else {
                    retractCase = RetractCase.PIVOT;
                }
                break;
            case PIVOT:
                if (Pivot.motorPivot.getCurrentPosition() > pivotTarget + 100 || Pivot.motorPivot.getCurrentPosition() < pivotTarget - 100 ) {
                    Pivot.getInstance().run_to_target(pivotTarget);
                } else {
                    retractCase = RetractCase.EXTENSION;
                }
                break;
            case EXTENSION:
                Pivot.getInstance().run_to_target(pivotTarget);
                if (Extension.extension_right.getCurrentPosition() > extensionTarget + 100 || Extension.extension_right.getCurrentPosition() < extensionTarget - 100) {
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
        Claw.getInstance().loop();
    }

    public static void outtake_sample_init(){
        Pivot.target = outtakeSamplePivot;
        setRetractCase_to_EXTENSION_RETRACT();
        Extension.target = outtakeSampleExtension;
    }

    public static void outtake_sample(){

        run_to_pos_in_order(outtakeSamplePivot,outtakeSampleExtension,false);

        if(retractCase == RetractCase.IDLE){
            Claw.getInstance().pivot(0.25);
            Claw.getInstance().rotate(0.25);
            Claw.getInstance().openState_method();
        }
    }

    public static void outtake_specimen_init(){
        setRetractCase_to_EXTENSION_RETRACT();
        Claw.getInstance().pivot(0.2);  
        Pivot.target = outtakeSpecimenPivot;
        Extension.target = outtakeSpecimenExtension;
    }

    public  static void outtake_specimen(){

        run_to_pos_in_order(outtakeSpecimenPivot,outtakeSpecimenExtension,false);

        if(retractCase == RetractCase.IDLE){
            Claw.getInstance().openState_method();
            Claw.getInstance().rotate(0.9);
            Claw.getInstance().frontBackState_method();
        }
    }

    public static void intake_specimen_init(){
        setRetractCase_to_EXTENSION_RETRACT();

        Pivot.target = intakeSpecimenPivot;
        Extension.target = intakeSpecimenExtension;
    }

    public static void intake_specimen() {
        run_to_pos_in_order(intakeSpecimenPivot,intakeSpecimenExtension,false);
        if(retractCase == RetractCase.IDLE){
            Claw.getInstance().openState_method();
            Claw.getInstance().rotate(0.25);
            Claw.getInstance().pivot(0.5 + Pivot.pivot_angle() / 180);
        }
    }

    public static void intake_sample_init(){
        setRetractCase_to_EXTENSION_RETRACT();
        Pivot.target = intakeSamplePivot;
        Claw.getInstance().rotate(0.25);
        Extension.target = intakeSampleExtension;
        Extension.getInstance().start();
        Claw.getInstance().change_to_OPEN_POS();
    }

    public static void intake_sample() {
        run_to_pos_in_order(intakeSamplePivot,intakeSampleExtension,true);
        if(retractCase == RetractCase.IDLE){
            Claw.getInstance().openState_method();
            //Claw.getInstance().ll_method();
            Claw.getInstance().pivot(Claw.FRONT_POS);
        }

    }

}

