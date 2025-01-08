package PrimeTech.OpModes.Utils.Tuners;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class ExtensionPIDTuner extends OpMode {
    public static double p = 0, i = 0, d = 0;
    public static double f = 0;
    public static double target = 0;
    public DcMotorEx extension_left = null;
    public DcMotorEx extension_right = null;
    public double ticks_in_degrees = (double) 8192 / 360;
    private PIDController controller;

    @Override
    public void init() {
        controller = new PIDController(p, i, d);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        extension_left = hardwareMap.get(DcMotorEx.class, "extensionLeft");
        extension_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        extension_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extension_left.setDirection(DcMotorSimple.Direction.REVERSE);

        extension_right = hardwareMap.get(DcMotorEx.class, "extensionRight");
        extension_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        extension_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void loop() {
        controller.setPID(p, i, d);
        int lift_pos = extension_right.getCurrentPosition();
        double pid = controller.calculate(lift_pos, target);
        double ff = Math.cos(Math.toRadians(lift_pos / ticks_in_degrees)) * f;
        double power = pid + ff;

        extension_right.setPower(power);
        extension_left.setPower(power);

        // Telemetry
        telemetry.addData("lift_pos: ", lift_pos);
        telemetry.addData("lift_target: ", target);
        telemetry.update();

    }
}
