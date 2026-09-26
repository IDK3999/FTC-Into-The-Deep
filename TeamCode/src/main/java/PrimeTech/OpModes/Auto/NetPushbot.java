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
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import PrimeTech.Global.Global;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

/**
 * The simplest possible autonomous: drive forward along the wall and park. Scores nothing.
 *
 * <p>A "pushbot" auto like this is the fallback when the real routine is not working - it at
 * least gets the parking points and stays out of the alliance partner's way. Useful as a
 * minimal example of driving one Pedro Pathing path.
 *
 * <p>Marked {@code @Disabled}, so it does not appear in the OpMode list on the Driver Hub.
 * Delete that annotation to use it.
 */
@Disabled
@Config
@Autonomous(name = "Net Pushbot", group = "Auto")
public class NetPushbot extends OpMode {
    /** x of the wall the robot drives along, in inches. It never leaves this line. */
    private static final double WALL_X = 8.3;

    /** y to park at, in inches. */
    private static final double PARK_Y = 124;

    /** How long to allow for the drive before giving up, in seconds. */
    private static final double MAX_DRIVE_SECONDS = 3;

    private final Pose start = new Pose(WALL_X, 89);
    private final Pose parking = new Pose(WALL_X, PARK_Y);

    private Follower follower;
    private int pathState;
    private Path park;
    private Timer timer;

    private void buildPaths() {
        park = new Path(new BezierLine(new Point(start), new Point(parking)));
        park.setConstantHeadingInterpolation(Math.toRadians(0));
    }

    /**
     * One step of the routine. Like every auto here, it cannot block, so it returns straight
     * away and is called again next loop.
     */
    private void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(park);
                setPathState(1);
                break;
            case 1:
                // Stop on a timer rather than on arrival: if the robot gets stuck against the
                // wall it would otherwise sit there spinning its wheels for the whole match.
                if (timer.getElapsedTimeSeconds() > MAX_DRIVE_SECONDS) {
                    setPathState(-1);
                }
                break;
        }
    }

    /** Moves to {@code state} and restarts the timer, so each state times out independently. */
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
