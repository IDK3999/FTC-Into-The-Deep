package PrimeTech.OpModes.Utils.InitializeForAssembly;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import PrimeTech.Global.Global;

/**
 * Holds the claw servos at fixed positions while the robot is being assembled.
 *
 * <p>A servo horn can only be bolted on in one of a limited number of angles, so before you
 * attach one you run this to drive the servo to a known position - then the mechanism's range
 * of motion lines up with the servo's. Without it you can easily assemble a claw that runs out
 * of servo travel before it is fully open.
 *
 * <p>Very close to {@link PrimeTech.OpModes.Utils.Tuners.TestServo}; the difference is intent.
 * This one is for building the robot, that one is for finding position constants.
 */
@Disabled
@Config
@TeleOp(name = "Servos To Init", group = "InitializeForAssembly")
public class ServoInit extends OpMode {
    // Edit these live from FTC Dashboard.
    public static double gripServoPosition = 1.0;
    public static double wristServoPosition = 0.0;
    public static double clawPivotServoPosition = 0.0;

    Servo gripServo = null;
    Servo wristServo = null;
    Servo clawPivotServoLeft = null;
    Servo clawPivotServoRight = null;

    @Override
    public void init() {
        Global.hardwareMap = hardwareMap;
        Global.telemetry = telemetry;
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        gripServo = hardwareMap.get(Servo.class, "openingServo");
        wristServo = hardwareMap.get(Servo.class, "rotationServo");
        clawPivotServoLeft = hardwareMap.get(Servo.class, "frontBackServoLeft");
        clawPivotServoRight = hardwareMap.get(Servo.class, "frontBackServoRight");
    }

    @Override
    public void loop() {
        gripServo.setPosition(gripServoPosition);
        clawPivotServoLeft.setPosition(clawPivotServoPosition);
        clawPivotServoRight.setPosition(clawPivotServoPosition);
        wristServo.setPosition(wristServoPosition);

        telemetry.addData("gripServoPosition: ", gripServoPosition);
        telemetry.addData("wristServoPosition: ", wristServoPosition);
        telemetry.addData("clawPivotServoPosition: ", clawPivotServoPosition);
        telemetry.update();
    }
}
