package PrimeTech.TeleOp;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Hang.Hang;
import PrimeTech.Components.Outtake.Outtake;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@TeleOp(name = "TeleOpPedro", group = "TeleOp")
public class TeleOpPedro extends OpMode {
    private final Pose startPose = new Pose(0, 0, 0);
    private Follower follower;

    @Override
    public void init() {
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);

        Gamepad.getInstance().init();
        Outtake.getInstance().init();
        Hang.getInstance().init();
//        Limelight.getInstance().init();
    }

    @Override
    public void start() {
        follower.startTeleopDrive();
    }

    @Override
    public void loop() {
        follower.setTeleOpMovementVectors(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x, true);
        follower.update();

        Gamepad.getInstance().loop();
        Outtake.getInstance().loop();
        Hang.getInstance().loop();
//        Limelight.getInstance().loop();
    }
}
