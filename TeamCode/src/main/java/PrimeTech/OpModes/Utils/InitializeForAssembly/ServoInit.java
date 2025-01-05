package PrimeTech.OpModes.Utils.InitializeForAssembly;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Servo To Init", group = "InitializeForAssembly")
public class ServoInit extends OpMode {
    Servo servo = null;
    private final double pose = 1.0;
    @Override
    public void init() {
        servo = hardwareMap.get(Servo.class, "servo");
        servo.setPosition(pose);
    }

    @Override
    public void loop() {
        servo.setPosition(pose);
    }
}
