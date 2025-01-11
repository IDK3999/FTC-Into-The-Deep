package PrimeTech.Components.Modes;

import com.acmerobotics.dashboard.config.Config;

import PrimeTech.Components.Outtake.Claw_vechi;
import PrimeTech.Components.Outtake.Extension;
import PrimeTech.Components.Outtake.Pivot;

@Config
public class AllModes {
    static double intakeSpecimenExtension = 0;
    public static double intakeSpecimenPivot = 450;

    static double outtakeSpecimenExtension = 1000;
    static double outtakeSpecimenPivot = 1500;

    static double outtakeSamplePivot = 2250;

    public static AllModes instance = null;

    public static synchronized AllModes getInstance() {
        if (instance == null) {
            instance = new AllModes();
        }
        return instance;
    }

    public static void general() {
        Extension.getInstance().loop();
        Pivot.getInstance().loop();
        Claw_vechi.getInstance().loop();
    }

    public static void outtake_sample(){
        Pivot.target = outtakeSamplePivot;
        Extension.MAX_TICKS = 2500;

        Pivot.getInstance().run_to_target(outtakeSamplePivot);

        Claw_vechi.getInstance().loop();
        Extension.getInstance().loop();
    }

    public  static void outtake_specimen(){
        Pivot.target = outtakeSpecimenPivot;
        Extension.target = outtakeSpecimenExtension;

        Pivot.getInstance().run_to_target(outtakeSpecimenPivot);
        Extension.getInstance().run_to_target(outtakeSpecimenExtension);

        Claw_vechi.getInstance().openState_method();
        Claw_vechi.getInstance().rotate(0.3 + Pivot.pivot_angle() / 180);
    }

    public static void intake_specimen() {
        Pivot.target = intakeSpecimenPivot;
        Extension.target = intakeSpecimenExtension;

        Pivot.getInstance().run_to_target(intakeSpecimenPivot);
        Extension.getInstance().run_to_target(intakeSpecimenExtension);

        Claw_vechi.getInstance().openState_method();
        Claw_vechi.getInstance().ll_method();
        Claw_vechi.getInstance().rotate(0.3 + Pivot.pivot_angle() / 180);

    }

    public static void intake_sample() {
        Extension.getInstance().loop();
        Pivot.getInstance().loop();
        Claw_vechi.getInstance().openState_method();
        Claw_vechi.getInstance().ll_method();
        Claw_vechi.getInstance().rotate(0.75 + Pivot.pivot_angle() / 180);
    }
}

