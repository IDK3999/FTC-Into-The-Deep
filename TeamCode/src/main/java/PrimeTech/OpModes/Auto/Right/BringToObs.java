package PrimeTech.OpModes.Auto.Right;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import PrimeTech.Components.Outtake.Claw;
import PrimeTech.Global.Global;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name = "Bring To Observation Zone", group = "Auto")
public class BringToObs extends OpMode {
    // region Declare
    public static double startX = 8.3;
    public static double parkY = 20.0;

    public static double maxSec = 5;

    private final Pose start = new Pose(startX, 55);

    private final Pose getFirstMiddle = new Pose(39.5, 35);
    private final Pose getFirst = new Pose(60, 34);
    private final Pose bringFirst = new Pose(20, 28);

    private final Pose getSecond = new Pose(57, 24);
    private final Pose bringSecond = new Pose(20, 30);

    private final Pose parking = new Pose(startX, parkY);

    private Follower follower;

    private int pathState;
    private Path park;
    private PathChain getBringFirst;

    private Timer pathTimer;
    // endregion Declare

    public void buildPaths() {
        getBringFirst = follower.pathBuilder()
                .addPath(new BezierLine(new Point(start), new Point(getFirstMiddle)))
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .addPath(new BezierLine(new Point(getFirstMiddle), new Point(getFirst)))
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(-90))
                .addPath(new BezierLine(new Point(getFirst), new Point(bringFirst)))
                .setConstantHeadingInterpolation(Math.toRadians(-90))
//                .addPath(new BezierLine(new Point(bringFirst), new Point(getSecond)))
//                .setConstantHeadingInterpolation(Math.toRadians(-90))
//                .addPath(new BezierLine(new Point(getSecond), new Point(bringSecond)))
//                .setConstantHeadingInterpolation(Math.toRadians(-90))
                .build();

        park = new Path(new BezierLine(new Point(start), new Point(parking)));
        park.setConstantHeadingInterpolation(Math.toRadians(0));
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(getBringFirst);
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
