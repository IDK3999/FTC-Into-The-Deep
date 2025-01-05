package PrimeTech.OpModes.Tele.TeleSimple;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import PrimeTech.Global.Global;
import PrimeTech.OpModes.Tele.TeleSimple.Drivetrain.Drivetrain;

@TeleOp(name = "TeleDriveOnly", group = "TeleOp")
public class TeleDriveOnly extends OpMode {
    @Override
    public void init() {
        Global.hardwareMap = hardwareMap;
        Global.telemetry = telemetry;
        Global.gamepad1 = gamepad1;
        Drivetrain.getInstance().init();
    }

    @Override
    public void loop() {
        Drivetrain.getInstance().loop();
    }
}
