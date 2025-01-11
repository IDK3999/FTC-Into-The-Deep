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
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Global.Global;

@Disabled
@TeleOp(name = "Pivot & Extension PID Tuner", group = "Tuners")
@Config
public class PivotAndExtensionPIDTuner extends OpMode {
    public static final double MAX_TICKS = 2600;
    public static final double MIN_TICKS = 0;
    public static double p_pivot = 0.002, i_pivot = 0.05, d_pivot = 0.00025;
    public static double f_pivot = 0.26;
    public static double increment_pivot = 50;
    public static double target_pivot = 0;
    public static double p_extension = 0.004, i_extension = 0, d_extension = 0;
    public static double f_extension = 0;
    public static double increment_extension = 50;
    public static double target_extension = 0;
    public DcMotorEx motorPivot = null;
    public DcMotorEx extension_left = null;
    public DcMotorEx extension_right = null;
    public double ticks_in_degrees = (double) 8192 / 360;
    private PIDController controller_pivot;
    private PIDController controller_extension;

    @Override
    public void init() {
        Global.gamepad1 = gamepad1;
        Gamepad.getInstance().init();
        controller_pivot = new PIDController(p_pivot, i_pivot, d_pivot);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        motorPivot = hardwareMap.get(DcMotorEx.class, "motorPivot");
        motorPivot.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motorPivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        controller_extension = new PIDController(p_extension, i_extension, d_extension);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        extension_left = hardwareMap.get(DcMotorEx.class, "extensionLeft");
        extension_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        extension_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extension_left.setDirection(DcMotorSimple.Direction.REVERSE);
        extension_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        extension_right = hardwareMap.get(DcMotorEx.class, "extensionRight");
        extension_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        extension_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extension_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {
        Gamepad.getInstance().loop();

        if (Gamepad.getInstance().left_bumper() && target_pivot < MAX_TICKS) {
            target_pivot += increment_pivot;
        }
        if (Gamepad.getInstance().right_bumper() && target_pivot > MIN_TICKS) {
            target_pivot -= increment_pivot;
        }
        controller_pivot.setPID(p_pivot, i_pivot, d_pivot);
        int pivot_pos = motorPivot.getCurrentPosition();
        double pid_pivot = controller_pivot.calculate(pivot_pos, target_pivot);
        double ff_pivot = f_pivot * (1 + extension_right.getCurrentPosition() * 0.027 / 28) * Math.cos(Math.toRadians(pivot_pos / ticks_in_degrees));
        double power_pivot = pid_pivot + ff_pivot;

        motorPivot.setPower(power_pivot);

        // Telemetry
        telemetry.addData("pivot_pos: ", pivot_pos);
        telemetry.addData("pivot_target: ", target_pivot);
        telemetry.addData("chestia",1 + extension_right.getCurrentPosition() * 0.027 / 28);
        telemetry.update();

        if (target_extension < MAX_TICKS) {
            target_extension += increment_extension * Gamepad.getInstance().right_trigger();
        }
        if (target_extension > MIN_TICKS) {
            target_extension -= increment_extension * Gamepad.getInstance().left_trigger();
        }
        controller_extension.setPID(p_extension, i_extension, d_extension);
        int lift_pos = extension_right.getCurrentPosition();
        double pid_extension = controller_extension.calculate(lift_pos, target_extension);
        double ff_extension = Math.cos(Math.toRadians(lift_pos / ticks_in_degrees)) * f_extension;
        double power_extension = pid_extension + ff_extension;

        extension_right.setPower(power_extension);
        extension_left.setPower(power_extension);

        // Telemetry
        telemetry.addData("lift_pos: ", lift_pos);
        telemetry.addData("lift_target: ", target_extension);
        telemetry.update();


    }

}
