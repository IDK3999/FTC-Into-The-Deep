package PrimeTech.OpModes.Utils.Tuners;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Global.Global;

@Disabled
@TeleOp(name = "Pivot PID Tuner", group = "Tuners")
@Config
public class PivotPIDTuner extends OpMode {
    public static double p = 0, i = 0, d = 0;
    public static double f = 0;
    public static double MAX_TICKS = 3000;
    public static double MIN_TICKS = 0;
    public static double increment = 50;
    public static double target = 0;
    public DcMotorEx motorPivot = null;
    public double ticks_in_degrees = (double) 8192 / 360;
    private PIDController controller;

    @Override
    public void init() {
        Global.gamepad1 = gamepad1;
        Gamepad.getInstance().init();

        controller = new PIDController(p, i, d);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        motorPivot = hardwareMap.get(DcMotorEx.class, "motorPivot");
        motorPivot.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motorPivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {
        Gamepad.getInstance().loop();

        if (Gamepad.getInstance().left_bumper() && target < MAX_TICKS) {
            target += increment;
        }
        if (Gamepad.getInstance().right_bumper() && target > MIN_TICKS) {
            target -= increment;
        }

        controller.setPID(p, i, d);
        int pivot_pos = motorPivot.getCurrentPosition();
        double pid = controller.calculate(pivot_pos, target);
        double ff = f * Math.cos(Math.toRadians(pivot_pos / ticks_in_degrees));
        double power = pid + ff;


        motorPivot.setPower(power);

        // Telemetry
        telemetry.addData("pivot_pos: ", pivot_pos);
        telemetry.addData("pivot_target: ", target);
        telemetry.update();

    }
}
