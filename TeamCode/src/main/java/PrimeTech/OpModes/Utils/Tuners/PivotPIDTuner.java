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

import PrimeTech.Components.Gamepad.GamepadTracker;
import PrimeTech.Global.Global;

/**
 * Tuning OpMode for the arm's PID gains.
 *
 * <h2>How to use it</h2>
 *
 * <ol>
 *   <li>Connect a laptop to the robot's wifi and open FTC Dashboard at
 *       <a href="http://192.168.43.1:8080/dash">192.168.43.1:8080/dash</a>.</li>
 *   <li>Run this OpMode. Every {@code public static} field below appears in the dashboard and
 *       can be edited live, without rebuilding.</li>
 *   <li>Set a {@code target}, then raise {@code proportionalGain} until the arm reaches it
 *       briskly. If it overshoots and wobbles, add {@code derivativeGain} to damp it. If it
 *       stops short and never quite arrives, add a little {@code integralGain}.</li>
 *   <li>Watch the graph of {@code pivot_pos} against {@code pivot_target} to see what is
 *       happening - it is far more informative than watching the robot.</li>
 *   <li>When it behaves, copy the numbers into {@link PrimeTech.Components.Outtake.Pivot}.
 *       Dashboard edits are lost when the OpMode stops.</li>
 * </ol>
 *
 * <p>Marked {@code @Disabled}, so it does not clutter the Driver Hub's OpMode list. Remove
 * that annotation when you need it.
 */
@Disabled
@TeleOp(name = "Pivot PID Tuner", group = "Tuners")
@Config
public class PivotPIDTuner extends OpMode {
    public static double proportionalGain = 0;
    public static double integralGain = 0;
    public static double derivativeGain = 0;

    /** Gravity feedforward gain; extra power to hold the arm up against its own weight. */
    public static double feedforwardGain = 0;

    public static double MAX_TICKS = 3000;
    public static double MIN_TICKS = 0;

    /** How far each loop of a held bumper moves the target, in ticks. */
    public static double ticksPerLoop = 50;

    /** The position being driven to, in ticks. Set this from the dashboard. */
    public static double target = 0;

    public static final double TICKS_PER_DEGREE = (double) 8192 / 360;

    public DcMotorEx pivotMotor = null;

    private PIDController controller;

    @Override
    public void init() {
        Global.gamepad1 = gamepad1;
        GamepadTracker.getInstance().init();

        controller = new PIDController(proportionalGain, integralGain, derivativeGain);

        // MultipleTelemetry sends the same data to both the Driver Hub and the dashboard.
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        pivotMotor = hardwareMap.get(DcMotorEx.class, "motorPivot");
        pivotMotor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        pivotMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {
        GamepadTracker.getInstance().loop();

        // Note these are the opposite way round to the real TeleOp: here left raises the
        // target and right lowers it.
        if (GamepadTracker.getInstance().leftBumperHeld() && target < MAX_TICKS) {
            target += ticksPerLoop;
        }
        if (GamepadTracker.getInstance().rightBumperHeld() && target > MIN_TICKS) {
            target -= ticksPerLoop;
        }

        // Re-applied every loop so edits made in the dashboard take effect immediately.
        controller.setPID(proportionalGain, integralGain, derivativeGain);

        int currentTicks = pivotMotor.getCurrentPosition();
        double pidOutput = controller.calculate(currentTicks, target);
        double feedforward =
                feedforwardGain * Math.cos(Math.toRadians(currentTicks / TICKS_PER_DEGREE));

        pivotMotor.setPower(pidOutput + feedforward);

        telemetry.addData("pivot_pos: ", currentTicks);
        telemetry.addData("pivot_target: ", target);
        telemetry.update();
    }
}
