package PrimeTech.OpModes.Utils.InitializeForAssembly;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import PrimeTech.Global.Global;

@TeleOp(name = "Servo To Init", group = "InitializeForAssembly")
public class ServoInit extends OpMode {
    public static double pose = 0.0;
    Servo servoL = null;
    Servo servoR = null;

    @Override
    public void init() {
        servoL = hardwareMap.get(Servo.class, "servo_left");
        servoL.setPosition(pose);
        servoR = hardwareMap.get(Servo.class, "servo_right");
        servoR.setDirection(Servo.Direction.REVERSE);
        servoR.setPosition(pose);
        Global.gamepad1 = gamepad1;
    }

    @Override
    public void loop() {
        pose = Math.max(0.3, Math.min(-gamepad1.left_stick_x, 1));
        telemetry.addData("Servo Position", pose);
        servoL.setPosition(pose);
        servoR.setPosition(pose);
    }
}
