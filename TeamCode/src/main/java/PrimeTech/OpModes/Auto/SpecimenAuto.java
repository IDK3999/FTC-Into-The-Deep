package PrimeTech.OpModes.Auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import PrimeTech.Components.Modes.AllModes;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name = "Specimen Auto", group = "Auto")
public class SpecimenAuto extends OpMode {
    // region Declare Poses
    // Score coords
    private final double scoreX = 39.9;
    private final double scoreYStep = 2;
    // Give coords
    private final double giveX = 17.9;
    // Poses
    private final Pose start = new Pose(7.9, 55, Math.toRadians(0));
    private final Pose get1 = new Pose(37, 121, Math.toRadians(-90));
    private final Pose get1Control1 = new Pose(10.5, 33.4, Math.toRadians(0));
    private final Pose get1Control2 = new Pose(74.3, 37.5, Math.toRadians(0));
    private final Pose give1 = new Pose(giveX, 21.1, Math.toRadians(-90));
    private final Pose get2 = new Pose(52, 13, Math.toRadians(-90));
    private final Pose get2Control1 = new Pose(72.3, 30.6, Math.toRadians(0));
    private final Pose give2 = new Pose(giveX, 13, Math.toRadians(-90));
    private final Pose get3 = new Pose(58, 8.6, Math.toRadians(-90));
    private final Pose get3Control1 = new Pose(61.5, 16.6, Math.toRadians(0));
    private final Pose give3 = new Pose(giveX, 8.6, Math.toRadians(-90));
    private final Pose give3ToSampleControl = new Pose(33.7, 25, Math.toRadians(0));
    private final Pose Sample = new Pose(12.2, 32.3, Math.toRadians(180));
    private double scoreY = 58;
    private final Pose score1 = new Pose(scoreX, scoreY += scoreYStep, Math.toRadians(0));
    private final Pose score2 = new Pose(scoreX, scoreY += 2, Math.toRadians(180));

    // another Sample

    private final Pose score3 = new Pose(scoreX, scoreY += scoreYStep, Math.toRadians(180));

    // another Sample

    private final Pose score4 = new Pose(scoreX, scoreY += scoreYStep, Math.toRadians(180));

    // another Sample

    private final Pose score5 = new Pose(scoreX, scoreY += scoreYStep, Math.toRadians(180));

    // go to Sample for park
    // endregion Declare Poses

    // region Declare Utils
    private Follower follower;
    private Timer pathTimer, opmodeTimer;

    private int pathState;
    private Path scorePreload, park;
    private PathChain getAndGiveAll, grabSample1, scoreSample1, grabSample2, scoreSample2, grabSample3, scoreSample3, grabSample4, scoreSample4;

    // endregion Declare Utils

    public void buildPaths() {
        scorePreload = new Path(new BezierLine(new Point(start), new Point(score1)));
        scorePreload.setConstantHeadingInterpolation(start.getHeading());

        getAndGiveAll = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(score1), new Point(get1Control1), new Point(get1Control2), new Point(get1)))
                .setLinearHeadingInterpolation(score1.getHeading(), get1.getHeading())
                .addPath(new BezierLine(new Point(get1), new Point(give1)))
                .setConstantHeadingInterpolation(give1.getHeading())
                .addPath(new BezierCurve(new Point(give1), new Point(get2Control1), new Point(get2)))
                .addPath(new BezierLine(new Point(get2), new Point(give2)))
                .addPath(new BezierCurve(new Point(give2), new Point(get3Control1), new Point(get3)))
                .addPath(new BezierLine(new Point(get3), new Point(give3)))
                .build();

        grabSample1 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(give3), new Point(give3ToSampleControl), new Point(Sample)))
                .setLinearHeadingInterpolation(give3.getHeading(), Sample.getHeading())
                .build();

        scoreSample1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(Sample), new Point(score2)))
                .setConstantHeadingInterpolation(score2.getHeading())
                .build();

        grabSample2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score2), new Point(Sample)))
                .setConstantHeadingInterpolation(Sample.getHeading())
                .build();

        scoreSample2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(Sample), new Point(score3)))
                .setConstantHeadingInterpolation(score3.getHeading())
                .build();

        grabSample3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score3), new Point(Sample)))
                .setConstantHeadingInterpolation(Sample.getHeading())
                .build();

        scoreSample3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(Sample), new Point(score4)))
                .setConstantHeadingInterpolation(score4.getHeading())
                .build();

        grabSample4 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score4), new Point(Sample)))
                .setConstantHeadingInterpolation(Sample.getHeading())
                .build();

        scoreSample4 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(Sample), new Point(score5)))
                .setConstantHeadingInterpolation(score5.getHeading())
                .build();

        park = new Path(new BezierLine(new Point(score5), new Point(Sample)));
        park.setConstantHeadingInterpolation(Sample.getHeading());
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(scorePreload);
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy()) {
                    // Score Preload
                    /*while(AllModes.getInstance().retractCase != AllModes.RetractCase.IDLE){
                        AllModes.getInstance().run_to_pos_in_order(AllModes.outtakeSpecimenPivot, AllModes.outtakeSpecimenExtension, false);
                    }
*/


                    follower.followPath(getAndGiveAll, false);
                    setPathState(2);
                }
                break;
            case 2:
                if (!follower.isBusy()) {
                    follower.followPath(grabSample1, true);
                    setPathState(3);
                }
                break;
            case 3:
                if (!follower.isBusy()) {
                    // Grab Sample

                    follower.followPath(scoreSample1, false);
                    setPathState(4);
                }
                break;
            case 4:
                if (!follower.isBusy()) {
                    // Score Sample

                    follower.followPath(grabSample2, true);
                    setPathState(5);
                }
                break;
            case 5:
                if (!follower.isBusy()) {
                    // Grab Sample

                    follower.followPath(scoreSample2, false);
                    setPathState(6);
                }
                break;
            case 6:
                if (!follower.isBusy()) {
                    // Score Sample

                    follower.followPath(grabSample3, true);
                    setPathState(7);
                }
                break;
            case 7:
                if (!follower.isBusy()) {
                    // Grab Sample

                    follower.followPath(scoreSample3, false);
                    setPathState(8);
                }
                break;
            case 8:
                if (!follower.isBusy()) {
                    // Score Sample

                    follower.followPath(grabSample4, true);
                    setPathState(9);
                }
                break;
            case 9:
                if (!follower.isBusy()) {
                    // Grab Sample

                    follower.followPath(scoreSample4, false);
                    setPathState(10);
                }
                break;
            case 10:
                if (!follower.isBusy()) {
                    // Score Sample

                    follower.followPath(park);
                    setPathState(11);
                }
                break;
            case 11:
                if (!follower.isBusy()) {
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
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(start);
        buildPaths();
    }

    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }
}

