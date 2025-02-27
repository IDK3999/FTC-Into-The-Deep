package PrimeTechV2.OpModes.Auto.Right.Specimen

import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierCurve
import com.pedropathing.pathgen.BezierLine
import com.pedropathing.pathgen.PathChain
import com.pedropathing.pathgen.Point
import com.rowanmcalpin.nextftc.pedro.FollowerNotInitializedException
import com.rowanmcalpin.nextftc.pedro.PedroData.follower

object SpecimenPaths {
    // region Poses
    private var startX = 8.4
    private var scoreX = 40.0
    private var giveX = 15.5
    private var scoreYStep = 1.5
    private var firstScoreY = 67.0
    private val secondScoreY = firstScoreY + scoreYStep
    private val thirdScoreY = secondScoreY + scoreYStep
    private val fourthScoreY = thirdScoreY + scoreYStep
    private val fifthScoreY = fourthScoreY + scoreYStep
    private val heading = Math.toRadians(180.0)

    val start = Pose(startX, 64.7, heading)
    private val scorePreload = Pose(scoreX, firstScoreY)
    private val get1 = Pose(57.0, 27.0)
    private val get1Control1 = Pose(6.0, 28.0)
    private val get1Control2 = Pose(74.0, 38.0)
    private val give1 = Pose(giveX, 23.0)
    private val get2 = Pose(52.0, 13.0)
    private val get2Control1 = Pose(72.0, 27.0)
    private val give2 = Pose(giveX, 13.0)
    private val get3 = Pose(52.0, 7.5)
    private val get3Control1 = Pose(72.0, 16.0)
    private val give3 = Pose(giveX, 7.5)
    private val load = Pose(15.5, 24.0)
    private val loadControl1 = Pose(33.0, 25.0)
    private val score2 = Pose(scoreX, secondScoreY)
    private val score3 = Pose(scoreX, thirdScoreY)
    private val score4 = Pose(scoreX, fourthScoreY)
    private val score5 = Pose(scoreX, fifthScoreY)
    private val park = Pose(14.0, 34.0)
    // endregion Poses

    // region Paths
    lateinit var scorePreloadPath: PathChain
    lateinit var get1Path: PathChain
    lateinit var give1Path: PathChain
    lateinit var get2Path: PathChain
    lateinit var give2Path: PathChain
    lateinit var get3Path: PathChain
    lateinit var give3Path: PathChain
    lateinit var parkPath: PathChain
    lateinit var load2Path: PathChain
    lateinit var score2Path: PathChain
    lateinit var load3Path: PathChain
    lateinit var score3Path: PathChain
    lateinit var load4Path: PathChain
    lateinit var score4Path: PathChain
    lateinit var load5Path: PathChain
    lateinit var score5Path: PathChain
    // endregion Paths

    fun buildObsZonePushbotPaths() {
        if (follower == null) {
            throw FollowerNotInitializedException()
        }

        scorePreloadPath = follower!!.pathBuilder()
            .addPath(BezierLine(Point(start), Point(scorePreload)))
            .setConstantHeadingInterpolation(heading)
            .build()

        get1Path = follower!!.pathBuilder()
            .addPath(
                BezierCurve(
                    Point(scorePreload),
                    Point(get1Control1),
                    Point(get1Control2),
                    Point(get1)
                )
            )
            .setConstantHeadingInterpolation(heading)
            .build()

        give1Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(get1), Point(give1)))
            .setConstantHeadingInterpolation(heading)
            .build()

        get2Path = follower!!.pathBuilder()
            .addPath(BezierCurve(Point(give1), Point(get2Control1), Point(get2)))
            .setConstantHeadingInterpolation(heading)
            .build()

        give2Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(get2), Point(give2)))
            .setConstantHeadingInterpolation(heading)
            .build()

        get3Path = follower!!.pathBuilder()
            .addPath(BezierCurve(Point(give2), Point(get3Control1), Point(get3)))
            .setConstantHeadingInterpolation(heading)
            .build()

        give3Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(get3), Point(give3)))
            .setConstantHeadingInterpolation(heading)
            .build()

        load2Path = follower!!.pathBuilder()
            .addPath(BezierCurve(Point(give3), Point(loadControl1), Point(load)))
            .setConstantHeadingInterpolation(heading)
            .build()

        score2Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(load), Point(score2)))
            .setConstantHeadingInterpolation(heading)
            .build()

        load3Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(score2), Point(load)))
            .setConstantHeadingInterpolation(heading)
            .build()

        score3Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(load), Point(score3)))
            .setConstantHeadingInterpolation(heading)
            .build()

        load4Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(score3), Point(load)))
            .setConstantHeadingInterpolation(heading)
            .build()

        score4Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(load), Point(score4)))
            .setConstantHeadingInterpolation(heading)
            .build()

        load5Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(score4), Point(load)))
            .setConstantHeadingInterpolation(heading)
            .build()

        score5Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(load), Point(score5)))
            .setConstantHeadingInterpolation(heading)
            .build()

        parkPath = follower!!.pathBuilder()
            .addPath(BezierLine(Point(score5), Point(park)))
            .setConstantHeadingInterpolation(heading)
            .build()
    }
}