package PrimeTech.OpModes.Utils.InitializeForAssembly;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import PrimeTech.Global.Global;

@Config
@TeleOp(name = "Servos To Init", group = "InitializeForAssembly")
public class ServoInit extends OpMode {
    public static double openingServo_pos = 1.0;
    public static double rotationServo_pos = 0.0;
    public static double frontBackServo_pos = 0.0;

    Servo openingServo = null;
    Servo rotationServo = null;
    Servo frontBackServo_left = null;
    Servo frontBackServo_right = null;

    @Override
    public void init() {
        Global.hardwareMap = hardwareMap;
        Global.telemetry = telemetry;
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        openingServo = hardwareMap.get(Servo.class, "openingServo");
        rotationServo = hardwareMap.get(Servo.class, "rotationServo");
        frontBackServo_left = hardwareMap.get(Servo.class, "frontBackServoLeft");
        frontBackServo_right = hardwareMap.get(Servo.class, "frontBackServoRight");
    }

    @Override
    public void loop() {
        openingServo.setPosition(openingServo_pos);
        frontBackServo_left.setPosition(frontBackServo_pos);
        frontBackServo_right.setPosition(frontBackServo_pos);
        rotationServo.setPosition(rotationServo_pos);

        telemetry.addData("openingServo: ", openingServo_pos);
        telemetry.addData("rotationServo: ", rotationServo_pos);
        telemetry.addData("frontBackServo: ", frontBackServo_pos);
        telemetry.update();
    }
}
