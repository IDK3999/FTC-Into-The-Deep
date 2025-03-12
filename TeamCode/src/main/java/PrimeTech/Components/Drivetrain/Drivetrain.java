package PrimeTech.Components.Drivetrain;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static PrimeTech.Global.Global.gamepad2;
import static PrimeTech.Global.Global.hardwareMap;
import static PrimeTech.Global.Global.telemetry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import PrimeTech.Components.Outtake.Extension;
import PrimeTech.Components.Outtake.Pivot;

public class Drivetrain {
    private static Drivetrain instance = null;
    final double baterry_saver = 1;
    DcMotor leftBack = null;
    DcMotor leftFront = null;
    DcMotor rightBack = null;
    DcMotor rightFront = null;

    public static synchronized Drivetrain getInstance() {
        if (instance == null) {
            instance = new Drivetrain();
        }
        return instance;
    }

    public void init() {
        // TODO: Check if FLOAT is better than BRAKE for movement


        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        leftBack.setZeroPowerBehavior(BRAKE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);


        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftFront.setZeroPowerBehavior(BRAKE);
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);

        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightBack.setZeroPowerBehavior(BRAKE);

        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightFront.setZeroPowerBehavior(BRAKE);

    }

    public void loop() {
        // Gamepad control
        double y = smoothControl(-gamepad2.left_stick_y);
        double x = smoothControl(gamepad2.left_stick_x);
        double rx = smoothControl(gamepad2.right_stick_x);

        leftFront.setPower((y + x + rx) * baterry_saver);
        leftBack.setPower((y - x + rx) * baterry_saver);
        rightFront.setPower((y - x - rx) * baterry_saver);
        rightBack.setPower((y + x - rx) * baterry_saver);


        telemetry.addData("lift_pos: ", Extension.extension_right.getCurrentPosition()
        );
        telemetry.addData("lift_target: ", Extension.target);
        telemetry.addData("pivot_pos: ", Pivot.motorPivot.getCurrentPosition());
        telemetry.addData("pivot_target: ", Pivot.target);
        telemetry.update();
    }

    private double smoothControl(double val) {
        return 0.5 * Math.tan(1.12 * val);
    }
}
