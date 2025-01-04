package PrimeTech.TeleOp.TeleOpSimple;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Hang.Hang;
import PrimeTech.Components.Outtake.Outtake;
import PrimeTech.TeleOp.TeleOpSimple.Drivetrain.Drivetrain;

@TeleOp(name = "TeleOpSimple", group = "TeleOp")
public class TeleOpSimple extends OpMode {
    @Override
    public void init() {
        Drivetrain.getInstance().init();
        Gamepad.getInstance().init();
        Outtake.getInstance().init();
        Hang.getInstance().init();
//        Limelight.getInstance().init();
    }

    @Override
    public void loop() {
        Drivetrain.getInstance().loop();
        Gamepad.getInstance().loop();
        Outtake.getInstance().loop();
        Hang.getInstance().loop();
//        Limelight.getInstance().loop();
    }
}
