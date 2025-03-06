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
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "Pivot & Extension PID Tuner", group = "Tuners")
@Config
public class    PivotAndExtensionPIDTuner extends OpMode {
    public static final double MAX_TICKS = 2600;
    public static final double MIN_TICKS = 0;
    public static final double FRONT_BACK_INIT = 0.5;
    public static double pivot_p = 0, pivot_i = 0, pivot_d = 0.0;
    public static double pivot_f = 0;
    public static double increment_pivot = 50;
    public static double target_pivot = 0;
    public static double extension_p = 0, extension_i = 0, extension_d = 0;
    public static double extension_f = 0;
    public static double increment_extension = 50;
    public static double extension_target = 0;
    public static double frontBackServoRight_pos = FRONT_BACK_INIT;
    public DcMotorEx motorPivot = null;
    public DcMotorEx extension_left = null;
    public DcMotorEx extension_right = null;
    public double ticks_in_degrees = (double) 8192 / 360;
    Servo frontBackServo_left = null;
    Servo frontBackServo_right = null;
    private PIDController controller_pivot;
    private PIDController controller_extension;

    @Override
    public void init() {
        //Global.gamepad1 = gamepad1;
        //Gamepad.getInstance().init();
        controller_pivot = new PIDController(pivot_p, pivot_i, pivot_d);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        motorPivot = hardwareMap.get(DcMotorEx.class, "motorPivot");
        //motorPivot.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motorPivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        controller_extension = new PIDController(extension_p, extension_i, extension_d);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        extension_left = hardwareMap.get(DcMotorEx.class, "extensionLeft");
        extension_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        // extension_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extension_left.setDirection(DcMotorSimple.Direction.REVERSE);
        extension_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        extension_right = hardwareMap.get(DcMotorEx.class, "extensionRight");
        extension_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        // extension_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extension_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontBackServo_left = hardwareMap.get(Servo.class, "frontBackServoLeft");
        frontBackServo_left.setDirection(Servo.Direction.REVERSE);
        frontBackServo_left.setPosition(FRONT_BACK_INIT);


        frontBackServo_right = hardwareMap.get(Servo.class, "frontBackServoRight");
        frontBackServo_right.setPosition(FRONT_BACK_INIT);
    }

    @Override
    public void loop() {
        /*Gamepad.getInstance().loop();

        if (Gamepad.getInstance().left_bumper() && target_pivot < MAX_TICKS) {
            target_pivot += increment_pivot;
        }
        if (Gamepad.getInstance().right_bumper() && target_pivot > MIN_TICKS) {
            target_pivot -= increment_pivot;
        }*/
        controller_pivot.setPID(pivot_p, pivot_i, pivot_d);
        int pivot_pos = motorPivot.getCurrentPosition();
        double pid_pivot = controller_pivot.calculate(pivot_pos, target_pivot);
        double ff_pivot = pivot_f * (1 + extension_right.getCurrentPosition() * 0.002) * Math.cos(Math.toRadians(pivot_pos / ticks_in_degrees));
        double power_pivot = pid_pivot + ff_pivot;

        motorPivot.setPower(power_pivot);

        // Telemetry


        /*if (target_extension < MAX_TICKS) {
            target_extension += increment_extension * Gamepad.getInstance().right_trigger();
        }
        if (target_extension > MIN_TICKS) {
            target_extension -= increment_extension * Gamepad.getInstance().left_trigger();
        }*/


        controller_extension.setPID(extension_p, extension_i, extension_d);
        int lift_pos = extension_right.getCurrentPosition();
        double pid_extension = controller_extension.calculate(lift_pos, extension_target);
        double ff_extension = Math.sin(Math.toRadians(motorPivot.getCurrentPosition() / ticks_in_degrees)) * extension_f;
        double power_extension = pid_extension + ff_extension;

        extension_right.setPower(power_extension);
        extension_left.setPower(power_extension);

        // Telemetry
        //
        telemetry.addData("pivot_pos: ", pivot_pos);
        telemetry.addData("pivot_target: ", target_pivot);
        telemetry.addData("lift_pos: ", lift_pos);
        telemetry.addData("lift_target: ", extension_target);
        telemetry.update();


    }

}
