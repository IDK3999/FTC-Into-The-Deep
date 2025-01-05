package PrimeTech.OpModes.Tele.TeleSimple;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "TeleExtensionAndPivotOnly", group = "TeleOp")
public class TeleExtensionAndPivotOnly extends OpMode {
    public DcMotorEx extension_left = null;
    public DcMotorEx extension_right = null;
    public DcMotorEx motorPivot = null;

    @Override
    public void init() {
        extension_left = hardwareMap.get(DcMotorEx.class, "extensionLeft");
        extension_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        extension_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extension_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        extension_left.setDirection(DcMotorSimple.Direction.REVERSE);

        extension_right = hardwareMap.get(DcMotorEx.class, "extensionRight");
        extension_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        extension_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extension_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorPivot = hardwareMap.get(DcMotorEx.class, "motorPivot");
        motorPivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorPivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorPivot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    @Override
    public void loop() {
        extension_left.setPower(gamepad1.left_stick_y);
        extension_right.setPower(gamepad1.left_stick_y);
        motorPivot.setPower(gamepad1.right_stick_y);
    }
}