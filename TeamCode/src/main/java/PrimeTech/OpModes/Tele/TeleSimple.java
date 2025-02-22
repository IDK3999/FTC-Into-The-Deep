package PrimeTech.OpModes.Tele;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Modes.FSMModes;
import PrimeTech.Components.Outtake.Outtake;
import PrimeTech.Global.Global;
import PrimeTech.OpModes.Tele.Drivetrain.Drivetrain;


@TeleOp(name = "TeleSimple", group = "TeleOp")
public class TeleSimple extends OpMode {
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
        FSMModes.getInstance().start();
    }

    @Override
    public void loop() {

        Drivetrain.getInstance().loop();
        Gamepad.getInstance().loop();
        Outtake.getInstance().loop();
        // Hang.getInstance().loop();
        // Limelight.getInstance().loop();
    }
}