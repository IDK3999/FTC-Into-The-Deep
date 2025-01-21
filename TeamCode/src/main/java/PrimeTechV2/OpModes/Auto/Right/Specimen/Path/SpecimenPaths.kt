package PrimeTechV2.OpModes.Auto.Right.Specimen.Path

import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierCurve
import com.pedropathing.pathgen.BezierLine
import com.pedropathing.pathgen.PathChain
import com.pedropathing.pathgen.Point
import com.rowanmcalpin.nextftc.pedro.FollowerNotInitializedException
import com.rowanmcalpin.nextftc.pedro.PedroData.follower

object SpecimenPaths {
    // region Poses
    var scoreX: Double = 39.7
    private var scoreY = 58.0
    var scoreYStep: Double = 2.0
    var giveX: Double = 17.9

    val start: Pose = Pose(7.9, 55.0, Math.toRadians(0.0))
    val get1: Pose = Pose(37.0, 121.0, Math.toRadians(-90.0))
    val get1Control1: Pose = Pose(10.5, 33.4, Math.toRadians(-90.0))
    val get1Control2: Pose = Pose(74.3, 37.5, Math.toRadians(-90.0))
    val give1: Pose = Pose(giveX, 21.1, Math.toRadians(-90.0))
    val get2: Pose = Pose(52.0, 13.0, Math.toRadians(-90.0))
    val get2Control1: Pose = Pose(72.3, 30.6, Math.toRadians(-90.0))
    val give2: Pose = Pose(giveX, 13.0, Math.toRadians(-90.0))
    val get3: Pose = Pose(58.0, 8.6, Math.toRadians(-90.0))
    val get3Control1: Pose = Pose(61.5, 16.6, Math.toRadians(0.0))
    val give3: Pose = Pose(giveX, 8.6, Math.toRadians(-90.0))
    val give3ToSampleControl: Pose = Pose(33.7, 25.0, Math.toRadians(0.0))
    val Sample: Pose = Pose(12.2, 32.3, Math.toRadians(180.0))
    val score1: Pose = Pose(scoreX, scoreYStep.let { scoreY += it; scoreY }, Math.toRadians(0.0))
    val score2: Pose = Pose(scoreX, scoreYStep.let { scoreY += it; scoreY }, Math.toRadians(180.0))
    val score3: Pose = Pose(scoreX, scoreYStep.let { scoreY += it; scoreY }, Math.toRadians(180.0))
    val score4: Pose = Pose(scoreX, scoreYStep.let { scoreY += it; scoreY }, Math.toRadians(180.0))
    val score5: Pose = Pose(scoreX, scoreYStep.let { scoreY += it; scoreY }, Math.toRadians(180.0))
    // endregion Poses

    // region Paths
    lateinit var scorePreloadPath: PathChain
    lateinit var getAndGiveAllPath: PathChain
    lateinit var grabSample1Path: PathChain
    lateinit var scoreSample1Path: PathChain
    lateinit var grabSample2Path: PathChain
    lateinit var scoreSample2Path: PathChain
    lateinit var grabSample3Path: PathChain
    lateinit var scoreSample3Path: PathChain
    lateinit var grabSample4Path: PathChain
    lateinit var scoreSample4Path: PathChain
    lateinit var parkPath: PathChain
    // endregion Paths

    fun buildSpecimenPaths() {
        if (follower == null) {
            throw FollowerNotInitializedException()
        }

        scorePreloadPath = follower!!.pathBuilder()
            .addPath(BezierLine(Point(start), Point(score1)))
            .setConstantHeadingInterpolation(start.heading)
            .build()

        getAndGiveAllPath = follower!!.pathBuilder()
            .addPath(
                BezierCurve(
                    Point(score1),
                    Point(get1Control1),
                    Point(get1Control2),
                    Point(get1)
                )
            )
            .setLinearHeadingInterpolation(score1.heading, get1.heading)
            .addPath(BezierLine(Point(get1), Point(give1)))
            .setConstantHeadingInterpolation(give1.heading)
            .addPath(BezierCurve(Point(give1), Point(get2Control1), Point(get2)))
            .setConstantHeadingInterpolation(give1.heading)
            .addPath(BezierLine(Point(get2), Point(give2)))
            .setConstantHeadingInterpolation(give1.heading)
            .addPath(BezierCurve(Point(give2), Point(get3Control1), Point(get3)))
            .setConstantHeadingInterpolation(give1.heading)
            .addPath(BezierLine(Point(get3), Point(give3)))
            .setConstantHeadingInterpolation(give1.heading)
            .build()

        grabSample1Path = follower!!.pathBuilder()
            .addPath(BezierCurve(Point(give3), Point(give3ToSampleControl), Point(Sample)))
            .setLinearHeadingInterpolation(give3.heading, Sample.heading)
            .build()

        scoreSample1Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(Sample), Point(score2)))
            .setConstantHeadingInterpolation(score2.heading)
            .build()

        grabSample2Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(score2), Point(Sample)))
            .setConstantHeadingInterpolation(Sample.heading)
            .build()

        scoreSample2Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(Sample), Point(score3)))
            .setConstantHeadingInterpolation(score3.heading)
            .build()

        grabSample3Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(score3), Point(Sample)))
            .setConstantHeadingInterpolation(Sample.heading)
            .build()

        scoreSample3Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(Sample), Point(score4)))
            .setConstantHeadingInterpolation(score4.heading)
            .build()

        grabSample4Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(score4), Point(Sample)))
            .setConstantHeadingInterpolation(Sample.heading)
            .build()

        scoreSample4Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(Sample), Point(score5)))
            .setConstantHeadingInterpolation(score5.heading)
            .build()

        parkPath = follower!!.pathBuilder()
            .addPath(BezierLine(Point(score5), Point(Sample)))
            .setConstantHeadingInterpolation(Sample.heading)
            .build()
    }
}