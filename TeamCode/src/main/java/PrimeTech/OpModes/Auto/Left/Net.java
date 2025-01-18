package PrimeTech.OpModes.Auto.Left;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import PrimeTech.Components.Outtake.Claw;
import PrimeTech.Global.Global;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Config
@Autonomous(name = "Net Zone", group = "Auto")
public class Net extends OpMode {
    // region Declare
    public static double startX = 8.3;
    public static double parkX = 11;
    public static double parkY = 124;

    public static double maxSec = 3;

    private final Pose start = new Pose(startX, 89);
    private final Pose parking = new Pose(parkX, parkY);

    private Follower follower;

    private int pathState;
    private Path park;

    private Timer pathTimer, opmodeTimer;
    // endregion Declare

    public void buildPaths() {
        park = new Path(new BezierLine(new Point(start), new Point(parking)));
        park.setConstantHeadingInterpolation(Math.toRadians(0));
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(park);
                setPathState(1);
                break;
            case 1:
                if (pathTimer.getElapsedTimeSeconds() > maxSec) {
                    setPathState(-1);
                }
                break;
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    @Override
    public void init() {
        Global.hardwareMap = hardwareMap;

        pathTimer = new Timer();

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(start);
        buildPaths();

        Claw.getInstance().init();
    }

    @Override
    public void start() {
        setPathState(0);
        Claw.getInstance().start();

    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }
}
