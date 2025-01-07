package PrimeTech.Components.Outtake;

import static PrimeTech.Global.Global.hardwareMap;
import static PrimeTech.Global.Global.telemetry;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import PrimeTech.Components.Gamepad.Gamepad;

public class Extension {
    // TODO: Edit with correct values
    public static final double MAX_TICKS = 2400;
    public static final double MIN_TICKS = 0.0;
    public static double p = 0.004, i = 0, d = 0;
    public static double f = 0;
    public static double target = 0;
    private static Extension instance = null;
    public final double increment = 50.0;

    public final double ticks_in_degrees = (double) 8192/360;
    public DcMotorEx extension_left = null;
    static public DcMotorEx extension_right = null;
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
        extension_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extension_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        extension_left.setDirection(DcMotorSimple.Direction.REVERSE);

        extension_right = hardwareMap.get(DcMotorEx.class, "extensionRight");
        extension_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        extension_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extension_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
        int lift_pos = extension_right.getCurrentPosition();
        double pid = controller.calculate(lift_pos, target);
        double ff = Math.cos(Math.toRadians(lift_pos/ticks_in_degrees))*f;
        double power = pid + ff;

        extension_right.setPower(power);
        extension_left.setPower(power);

        // Telemetry
        telemetry.addData("lift_pos: ", lift_pos);
        telemetry.addData("lift_target: ", target);
        telemetry.update();
    }

    public void change_liftState_to_MIN(){
        liftState = LiftState.MIN;
    }
    enum LiftState {
        MAX, INRANGE, MIN
    }
}
