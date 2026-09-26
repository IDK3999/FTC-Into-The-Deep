package PrimeTech.OpModes.Utils.Tuners;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Tunes the arm's pivot and extension PIDs at the same time, from FTC Dashboard.
 *
 * <p>Both axes interact - extending the slide changes how much torque the pivot needs, and
 * raising the arm changes how much the slide has to hold - so it is worth tuning them
 * together rather than one at a time. See {@link PivotPIDTuner} for the tuning procedure.
 *
 * <p>Targets are set from the dashboard here; there are no gamepad controls.
 */
@TeleOp(name = "Pivot & Extension PID Tuner", group = "Tuners")
@Config
public class PivotAndExtensionPIDTuner extends OpMode {
    /** Claw pivot servo position held throughout, to keep the claw out of the way. */
    public static final double CLAW_PIVOT_INIT = 0.5;

    public static final double TICKS_PER_DEGREE = (double) 8192 / 360;

    // region Pivot (arm rotation)
    public static double pivotProportionalGain = 0;
    public static double pivotIntegralGain = 0;
    public static double pivotDerivativeGain = 0.0;

    /** Gravity feedforward gain for the arm. */
    public static double pivotFeedforwardGain = 0;

    /** Arm position to drive to, in ticks. Set from the dashboard. */
    public static double pivotTarget = 0;
    // endregion Pivot

    // region Extension (slide)
    public static double extensionProportionalGain = 0;
    public static double extensionIntegralGain = 0;
    public static double extensionDerivativeGain = 0;

    /** Gravity feedforward gain for the slide. */
    public static double extensionFeedforwardGain = 0;

    /** Slide position to drive to, in ticks. Set from the dashboard. */
    public static double extensionTarget = 0;
    // endregion Extension

    public DcMotorEx pivotMotor = null;
    public DcMotorEx extensionMotorLeft = null;
    public DcMotorEx extensionMotorRight = null;

    Servo clawPivotServoLeft = null;
    Servo clawPivotServoRight = null;

    private PIDController pivotController;
    private PIDController extensionController;

    @Override
    public void init() {
        // MultipleTelemetry sends the same data to both the Driver Hub and the dashboard.
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        pivotController = new PIDController(
                pivotProportionalGain, pivotIntegralGain, pivotDerivativeGain);
        extensionController = new PIDController(
                extensionProportionalGain, extensionIntegralGain, extensionDerivativeGain);

        // Note: the encoders are deliberately NOT reset here, so you can restart this OpMode
        // without losing the zero you are tuning against.
        pivotMotor = hardwareMap.get(DcMotorEx.class, "motorPivot");
        pivotMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        extensionMotorLeft = hardwareMap.get(DcMotorEx.class, "extensionLeft");
        extensionMotorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        extensionMotorLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        extensionMotorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        extensionMotorRight = hardwareMap.get(DcMotorEx.class, "extensionRight");
        extensionMotorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        extensionMotorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // The two claw pivot servos are mirrored, so one is reversed to make them agree.
        clawPivotServoLeft = hardwareMap.get(Servo.class, "frontBackServoLeft");
        clawPivotServoLeft.setDirection(Servo.Direction.REVERSE);
        clawPivotServoLeft.setPosition(CLAW_PIVOT_INIT);

        clawPivotServoRight = hardwareMap.get(Servo.class, "frontBackServoRight");
        clawPivotServoRight.setPosition(CLAW_PIVOT_INIT);
    }

    @Override
    public void loop() {
        // Gains are re-applied every loop so dashboard edits take effect immediately.
        pivotController.setPID(pivotProportionalGain, pivotIntegralGain, pivotDerivativeGain);

        int pivotTicks = pivotMotor.getCurrentPosition();
        double pivotPidOutput = pivotController.calculate(pivotTicks, pivotTarget);

        // Scaled by the extension position: the further the slide is out, the more leverage
        // the arm's weight has, so the more holding power it needs.
        double pivotFeedforward = pivotFeedforwardGain
                * (1 + extensionMotorRight.getCurrentPosition() * 0.002)
                * Math.cos(Math.toRadians(pivotTicks / TICKS_PER_DEGREE));

        pivotMotor.setPower(pivotPidOutput + pivotFeedforward);

        extensionController.setPID(
                extensionProportionalGain, extensionIntegralGain, extensionDerivativeGain);

        int extensionTicks = extensionMotorRight.getCurrentPosition();
        double extensionPidOutput = extensionController.calculate(extensionTicks, extensionTarget);

        // The more the arm is raised, the more of the slide's weight the motors must hold.
        double extensionFeedforward = Math.sin(
                Math.toRadians(pivotMotor.getCurrentPosition() / TICKS_PER_DEGREE))
                * extensionFeedforwardGain;

        double extensionPower = extensionPidOutput + extensionFeedforward;
        extensionMotorRight.setPower(extensionPower);
        extensionMotorLeft.setPower(extensionPower);

        telemetry.addData("pivot_pos: ", pivotTicks);
        telemetry.addData("pivot_target: ", pivotTarget);
        telemetry.addData("extension_pos: ", extensionTicks);
        telemetry.addData("extension_target: ", extensionTarget);
        telemetry.update();
    }
}
