package PrimeTech.OpModes.Utils.Tuners;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import PrimeTech.Global.Global;

/**
 * Drives the claw's servos to whatever position you type into FTC Dashboard.
 *
 * <p>This is how every servo constant in {@link PrimeTech.Components.Outtake.Claw} and
 * {@code PrimeTechV3.Components.Claw} was found: run this, nudge a position until the claw
 * looks right on the robot, then write that number into the component as a named constant.
 *
 * <p>Servo positions run 0.0 to 1.0. Move in small steps - a servo will happily try to drive
 * past a mechanical stop and strip its gears or bend the linkage.
 */
@Disabled
@Config
@TeleOp(name = "test servo", group = "InitializeForAssembly")
public class TestServo extends OpMode {
    public static final double GRIP_CLOSED = 0.0;
    public static final double CLAW_PIVOT_INIT = 0.5;
    public static final double WRIST_INIT = 0.25;

    // Edit these live from the dashboard.
    public static double gripServoPosition = GRIP_CLOSED;
    public static double wristServoPosition = WRIST_INIT;
    public static double clawPivotServoPosition = CLAW_PIVOT_INIT;

    Servo gripServo = null;
    Servo wristServo = null;
    Servo clawPivotServoLeft = null;
    Servo clawPivotServoRight = null;

    @Override
    public void init() {
        Global.hardwareMap = hardwareMap;
        Global.telemetry = telemetry;
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        gripServo = hardwareMap.get(Servo.class, "openingServo");
        gripServo.setPosition(GRIP_CLOSED);

        wristServo = hardwareMap.get(Servo.class, "rotationServo");
        wristServo.setPosition(WRIST_INIT);

        // The two claw pivot servos are mirrored, so one is reversed to make them agree.
        clawPivotServoLeft = hardwareMap.get(Servo.class, "frontBackServoLeft");
        clawPivotServoLeft.setDirection(Servo.Direction.REVERSE);
        clawPivotServoLeft.setPosition(CLAW_PIVOT_INIT);

        clawPivotServoRight = hardwareMap.get(Servo.class, "frontBackServoRight");
        clawPivotServoRight.setPosition(CLAW_PIVOT_INIT);
    }

    @Override
    public void loop() {
        gripServo.setPosition(gripServoPosition);
        clawPivotServoLeft.setPosition(clawPivotServoPosition);
        clawPivotServoRight.setPosition(clawPivotServoPosition);
        wristServo.setPosition(wristServoPosition);

        telemetry.addData("gripServoPosition: ", gripServoPosition);
        telemetry.addData("wristServoPosition: ", wristServoPosition);
        telemetry.addData("clawPivotServoPosition: ", clawPivotServoPosition);
        telemetry.update();
    }
}
