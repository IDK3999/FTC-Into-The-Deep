package PrimeTech.Components.Modes;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.acmerobotics.dashboard.config.Config;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Limelight.Limelight;
import PrimeTech.Components.Outtake.Claw;
import PrimeTech.Components.Outtake.Claw_vechi;
import PrimeTech.Components.Outtake.Extension;
import PrimeTech.Components.Outtake.InitPos;
import PrimeTech.Components.Outtake.Pivot;

@Config
public class AllModes {
    static double intakeExtension = 0;
    public static double intakePivot = 400;

    static double outtakeExtension = 1000;
    static double outtakePivot = 1500;

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
        Pivot.target = outtakePivot;
        Extension.target = outtakeExtension;

        Pivot.getInstance().run_to_target(outtakePivot);
        Extension.getInstance().run_to_target(outtakeExtension);
        Claw_vechi.getInstance().openState_method();
        Claw_vechi.getInstance().rotate(0.3 + Pivot.pivot_angle() / 180);
    }

    public static void intake_specimen() {
       // Extension.getInstance().loop();
        //Pivot.getInstance().loop();
        Pivot.target = intakePivot;
        Extension.target = intakeExtension;

        Pivot.getInstance().run_to_target(intakePivot);
        Extension.getInstance().run_to_target(AllModes.intakeExtension);
        Claw_vechi.getInstance().openState_method();
        Claw_vechi.getInstance().ll_method();
        Claw_vechi.getInstance().rotate(0.3 + Pivot.pivot_angle() / 180);

    }


    public static void intake_sample() {
        Extension.getInstance().loop();
        Pivot.getInstance().loop();
        Claw_vechi.getInstance().openState_method();
        Claw_vechi.getInstance().ll_method();
        Claw_vechi.getInstance().rotate(0.75 +Pivot.pivot_angle() / 180);
    }
}

