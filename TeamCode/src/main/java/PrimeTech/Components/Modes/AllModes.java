package PrimeTech.Components.Modes;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Limelight.Limelight;
import PrimeTech.Components.Outtake.Claw;
import PrimeTech.Components.Outtake.Claw_vechi;
import PrimeTech.Components.Outtake.Extension;
import PrimeTech.Components.Outtake.InitPos;
import PrimeTech.Components.Outtake.Pivot;

public class AllModes {
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


    public static void intake_specimen() {
        Extension.getInstance().loop();
        Pivot.getInstance().loop();
        Claw_vechi.getInstance().openState_method();
        Claw_vechi.getInstance().rotate(0.3 + (Pivot.motorPivot.getCurrentPosition() / Pivot.ticks_in_degrees) / 180);
    }


    public static void intake_sample() {
        Extension.getInstance().loop();
        Pivot.getInstance().loop();
        Claw_vechi.getInstance().openState_method();
        Claw_vechi.getInstance().ll_method();
        Claw_vechi.getInstance().rotate(0.75 + Pivot.motorPivot.getCurrentPosition() / Pivot.ticks_in_degrees / 180);
    }
}

