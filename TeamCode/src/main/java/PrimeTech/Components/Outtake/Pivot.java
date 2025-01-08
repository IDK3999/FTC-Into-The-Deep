package PrimeTech.Components.Outtake;

import static PrimeTech.Global.Global.hardwareMap;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import PrimeTech.Components.Gamepad.Gamepad;

public class Pivot {
    // TODO: Edit with correct values
    public static final double MAX_TICKS = 3600;
    public static final double MIN_TICKS = 0.0;
    public static final double TICKS_FOR_PARALLEL = 0;
    public static double p = 0.002, i = 0.03, d = 0.0002;
    public static double f = 0.3;
    public static double target = 0;
    private static Pivot instance = null;
    public final double increment = 50;

    public static final double ticks_in_degrees = (double) 8192 / 360;
    public static DcMotorEx motorPivot = null;
    LiftState liftState = LiftState.MIN;
    private PIDController controller;

    public static synchronized Pivot getInstance() {
        if (instance == null) {
            instance = new Pivot();
        }
        return instance;
    }

    public void init() {
        target = 0;
        liftState = LiftState.MIN;

        controller = new PIDController(p, i, d);

        motorPivot = hardwareMap.get(DcMotorEx.class, "motorPivot");
        motorPivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorPivot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motorPivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void loop() {
        double local_target = fsm();
        run_to_target(local_target);
    }

    public double fsm() {
        switch (liftState) {
            case MIN:
                if (Gamepad.getInstance().left_bumper()) {
                    target += increment;
                    liftState = LiftState.INRANGE;
                }
                break;
            case MAX:
                if (Gamepad.getInstance().right_bumper()) {
                    target -= increment;
                    liftState = LiftState.INRANGE;
                }
                break;
            case INRANGE:
                if (Gamepad.getInstance().left_bumper()) {
                    target += increment;
                }
                if (Gamepad.getInstance().right_bumper()) {
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
        double ff = Math.cos(Math.toRadians(pivot_pos / ticks_in_degrees)) * f * (1 + Extension.extension_right.getCurrentPosition() * 0.027 / 28);
        double power = pid + ff;

        motorPivot.setPower(power);

        // Telemetry
        //telemetry.addData("pivot_pos: ", pivot_pos);
        //telemetry.addData("pivot_target: ", target);
        //telemetry.update();
    }

    public void change_liftState_to_MIN() {
        liftState = LiftState.MIN;
    }

    enum LiftState {
        MAX, INRANGE, MIN
    }
}
