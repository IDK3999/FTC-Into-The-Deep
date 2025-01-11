package PrimeTech.OpModes.Tele.TeleSimple;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Hang.Hang;
import PrimeTech.Components.Limelight.Limelight;
import PrimeTech.Components.Outtake.Outtake;
import PrimeTech.Global.Global;
import PrimeTech.OpModes.Tele.TeleSimple.Drivetrain.Drivetrain;

@TeleOp(name = "TeleSimpleBlue", group = "TeleOp")
@Config
public class TeleSimpleBlue extends OpMode {
    @Override
    public void init() {
        Global.hardwareMap = hardwareMap;
        Global.telemetry = telemetry;
        Global.gamepad1 = gamepad1;
        Limelight.getInstance().init_blue();

        Drivetrain.getInstance().init();
        Gamepad.getInstance().init();
        Outtake.getInstance().init();
        //  Hang.getInstance().init();
    }

    @Override
    public void start() {
        Outtake.getInstance().start();
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
