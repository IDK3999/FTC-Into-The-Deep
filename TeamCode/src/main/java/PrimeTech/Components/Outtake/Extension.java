package PrimeTech.Components.Outtake;

import static PrimeTech.Global.Global.hardwareMap;
import static PrimeTech.Global.Global.telemetry;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import PrimeTech.Components.Gamepad.Gamepad;

public class Extension {

    public static final double MIN_TICKS = 0.0;

    public static double MAX_TICKS = 500;
    public static double FINAL_MAX_TICKS = 500;
    public static double LIMITED_MAX_TICKS = 300;
    public static double p = 0.012, i = 0.12, d = 0.000287;
    public static double f = 0.02;
    public static double target = 0;
    static public DcMotorEx extension_right = null;
    private static Extension instance = null;
    public final double increment = 25.0;

    public DcMotorEx extension_left = null;
    LiftState liftState = LiftState.MIN;
    private PIDController controller;

    public static synchronized Extension getInstance() {
        if (instance == null) {
            instance = new Extension();
        }
        return instance;
    }

    public void init() {
        controller = new PIDController(p, i, d);

        extension_left = hardwareMap.get(DcMotorEx.class, "extensionLeft");
        extension_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        extension_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        extension_left.setDirection(DcMotorSimple.Direction.REVERSE);

        extension_right = hardwareMap.get(DcMotorEx.class, "extensionRight");
        extension_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        extension_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void start() {
        target = 0;
        liftState = LiftState.MIN;
    }

    public void loop() {
        double local_target = fsm();
        run_to_target(local_target);
    }

    public double fsm() {
        double last_target = target;
        switch (liftState) {
            case MIN:
                target += increment * Gamepad.getInstance().right_trigger();
                if (last_target != target) {
                    liftState = LiftState.INRANGE;
                }
                break;
            case MAX:
                target -= increment * Gamepad.getInstance().left_trigger();
                if (last_target != target) {
                    liftState = LiftState.INRANGE;
                }
                break;
            case INRANGE:
                target += increment * Gamepad.getInstance().right_trigger() - increment * Gamepad.getInstance().left_trigger();
                if (target > MAX_TICKS) {
                    change_LiftState_to_MAX();
                }
                if (target < MIN_TICKS) {
                    change_LiftState_to_MIN();
                }
                break;
        }
        return target;
    }

    public void change_LiftState_to_MIN() {
        liftState = LiftState.MIN;
        target = MIN_TICKS;
    }

    public void change_LiftState_to_MAX() {
        liftState = LiftState.MAX;
        target = MAX_TICKS;
    }


    public void run_to_target(double target) {
        controller.setPID(p, i, d);
        int lift_pos = extension_right.getCurrentPosition();
        double pid = controller.calculate(lift_pos, target);
        double ff = Math.sin(Math.toRadians(Pivot.pivot_angle())) * f;
        double power = pid + ff;

        extension_right.setPower(power);
        extension_left.setPower(power);
        //Telemetry
        telemetry.addData("lift_pos: ", lift_pos);
        telemetry.addData("lift_target: ", target);
        telemetry.update();
    }

    public void change_liftState_to_INRANGE() {
        liftState = LiftState.INRANGE;
    }

    enum LiftState {
        MAX, INRANGE, MIN
    }


}
