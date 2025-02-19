package PrimeTech.OpModes.Auto;

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

import PrimeTech.Global.Global;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Config
@Autonomous(name = "Net Pushbot", group = "Auto")
public class NetPushbot extends OpMode {
    private static final double x = 8.3;
    private static final double parkY = 124;
    private final Pose start = new Pose(x, 89);
    private final Pose parking = new Pose(x, parkY);

    private Follower follower;
    private int pathState;
    private Path park;
    private Timer timer;

    private void buildPaths() {
        park = new Path(new BezierLine(new Point(start), new Point(parking)));
        park.setConstantHeadingInterpolation(Math.toRadians(0));
    }

    private void autonomousPathUpdate() {
        double maxSec = 3;
        switch (pathState) {
            case 0:
                follower.followPath(park);
                setPathState(1);
                break;
            case 1:
                if (timer.getElapsedTimeSeconds() > maxSec) {
                    setPathState(-1);
                }
                break;
        }
    }

    public void setPathState(int state) {
        pathState = state;
        timer.resetTimer();
    }

    @Override
    public void init() {
        Global.hardwareMap = hardwareMap;
        timer = new Timer();
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(start);
        buildPaths();
        setPathState(0);
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();
    }
}
