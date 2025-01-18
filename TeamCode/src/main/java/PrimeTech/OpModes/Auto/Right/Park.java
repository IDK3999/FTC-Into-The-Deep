package PrimeTech.OpModes.Auto.Right;

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

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name = "Park Right", group = "Auto")
public class Park extends OpMode {
    // region Declare
    public static double startX = 8.3;
    public static double parkY = 9.0;

    private final Pose start = new Pose(startX, 55);
    private final Pose parking = new Pose(startX, parkY);

    private Follower follower;

    private int pathState;
    private Path park;
    // endregion Declare

    public void buildPaths() {
        park = new Path(new BezierLine(new Point(start), new Point(parking)));
        park.setConstantHeadingInterpolation(Math.toRadians(0));
    }

    @Override
    public void init() {
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(start);
        buildPaths();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(park);
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy()) {
                    setPathState(-1);
                }
                break;
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
    }

    @Override
    public void start() {
        setPathState(0);
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
