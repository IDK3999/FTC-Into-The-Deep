package PrimeTech.OpModes.Tele.TeleSimple;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Limelight.Limelight;
import PrimeTech.Components.Outtake.Extension;
import PrimeTech.Components.Outtake.Outtake;
import PrimeTech.Components.Outtake.Pivot;
import PrimeTech.Global.Global;
import PrimeTech.OpModes.Tele.TeleSimple.Drivetrain.Drivetrain;

@Config
public abstract class TeleSimple extends OpMode {
    @Override
    public void init() {
        Global.hardwareMap = hardwareMap;
        Global.telemetry = telemetry;
        Global.gamepad1 = gamepad1;
        Global.gamepad2 = gamepad2;

        Drivetrain.getInstance().init();
        Gamepad.getInstance().init();
        Outtake.getInstance().init();
        //  Hang.getInstance().init();
    }

    @Override
    public void start() {
        Outtake.getInstance().start();
        Extension.getInstance().start();
        Pivot.getInstance().start();
    }

    @Override
    public void loop() {
        Limelight.getInstance().loop();
        Drivetrain.getInstance().loop();
        Gamepad.getInstance().loop();
        Outtake.getInstance().loop();
        // Hang.getInstance().loop();
    }
}