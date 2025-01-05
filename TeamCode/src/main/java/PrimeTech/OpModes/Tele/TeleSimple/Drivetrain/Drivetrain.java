package PrimeTech.OpModes.Tele.TeleSimple.Drivetrain;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static PrimeTech.Global.Global.gamepad1;
import static PrimeTech.Global.Global.hardwareMap;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class Drivetrain {
    private static Drivetrain instance = null;
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


        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftFront.setZeroPowerBehavior(BRAKE);


        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightBack.setZeroPowerBehavior(BRAKE);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);

        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightFront.setZeroPowerBehavior(BRAKE);
        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void loop() {
        // Gamepad control
        double y = smoothControl(-gamepad1.left_stick_y);
        double x = smoothControl(gamepad1.left_stick_x);
        double rx = smoothControl(gamepad1.right_stick_x);

        leftFront.setPower(y + x - rx);
        leftBack.setPower(y - x - rx);
        rightFront.setPower(y - x + rx);
        rightBack.setPower(y + x + rx);
    }

    private double smoothControl(double val) {
        return 0.5 * Math.tan(1.12 * val);
    }
}
