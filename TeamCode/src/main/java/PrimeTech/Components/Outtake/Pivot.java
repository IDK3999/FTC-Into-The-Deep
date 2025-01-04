package PrimeTech.Components.Outtake;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import PrimeTech.Components.Gamepad.Gamepad;

public class Pivot {
    // TODO: Edit with correct values
    public static final double MAX_TICKS = 0;
    public static final double MIN_TICKS = 0;
    public static final double TICKS_FOR_PARALLEL = 0;
    public static double p = 0, i = 0, d = 0;
    public static double f = 0;
    public static double target = 0;
    private static Pivot instance = null;
    public final double increment = 0;
    public DcMotorEx motorPivot = null;
    LiftState liftState = LiftState.MIN;
    private PIDController controller;

    public static synchronized Pivot getInstance() {
        if (instance == null) {
            instance = new Pivot();
        }
        return instance;
    }

    public void init() {
        controller = new PIDController(p, i, d);

        motorPivot = hardwareMap.get(DcMotorEx.class, "motorPivot");
        motorPivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorPivot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public void loop() {
        double local_target = fsm();
        run_to_target(local_target);
    }

    public double fsm() {
        switch (liftState) {
            case MIN:
                if (Gamepad.getInstance().right_bumper()) {
                    target += increment;
                    liftState = LiftState.INRANGE;
                }
                break;
            case MAX:
                if (Gamepad.getInstance().left_bumper()) {
                    target -= increment;
                    liftState = LiftState.INRANGE;
                }
                break;
            case INRANGE:
                if (Gamepad.getInstance().right_bumper()) {
                    target += increment;
                }
                if (Gamepad.getInstance().left_bumper()) {
                    target -= increment;
                }
                if (target > MAX_TICKS) {
                    liftState = LiftState.MAX;
                    target = MAX_TICKS;
                }
                if (target < MIN_TICKS) {
                    liftState = LiftState.MIN;
                    target = MIN_TICKS;
                }
                break;
        }
        return target;
    }

    public void run_to_target(double target) {
        controller.setPID(p, i, d);
        int pivot_pos = motorPivot.getCurrentPosition();
        double pid = controller.calculate(pivot_pos, target);
        double power = pid + f;

        motorPivot.setPower(power);

        // Telemetry
        telemetry.addData("pivot_pos: ", pivot_pos);
        telemetry.addData("pivot_target: ", target);
        telemetry.update();
    }

    enum LiftState {
        MAX, INRANGE, MIN
    }
}
