package PrimeTechV2.OpModes.Auto.Right.Specimen.Paths

import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierCurve
import com.pedropathing.pathgen.BezierLine
import com.pedropathing.pathgen.PathChain
import com.pedropathing.pathgen.Point
import com.rowanmcalpin.nextftc.pedro.FollowerNotInitializedException
import com.rowanmcalpin.nextftc.pedro.PedroData.follower

object SpecimenPaths {
    // region Poses
    private var startX = 8.9
    private var scoreX = 40.4
    private var giveX = 14.5
    private var scoreYStep = 0.7
    private var firstScoreY = 66.0
    private val secondScoreY = firstScoreY + scoreYStep
    private val thirdScoreY = secondScoreY + scoreYStep
    private val fourthScoreY = thirdScoreY + scoreYStep
    private val fifthScoreY = fourthScoreY + scoreYStep

    val start: Pose = Pose(startX, 65.0, Math.toRadians(180.0))
    private val scorePreload: Pose = Pose(scoreX, firstScoreY, Math.toRadians(180.0))
    private val get1: Pose = Pose(57.0, 27.0, Math.toRadians(180.0))
    private val get1Control1: Pose = Pose(6.0, 28.0)
    private val get1Control2: Pose = Pose(74.0, 38.0)
    private val give1: Pose = Pose(giveX, 23.0, Math.toRadians(180.0))
    private val get2: Pose = Pose(52.0, 13.0, Math.toRadians(180.0))
    private val get2Control1: Pose = Pose(72.0, 30.0, Math.toRadians(180.0))
    private val give2: Pose = Pose(giveX, 13.0, Math.toRadians(180.0))
    private val get3: Pose = Pose(58.0, 11.0, Math.toRadians(180.0))
    private val get3Control1: Pose = Pose(57.8, 15.5)
    private val give3: Pose = Pose(giveX, 8.6, Math.toRadians(180.0))
    private val load: Pose = Pose(13.0, 32.3, Math.toRadians(180.0))
    private val loadControl1: Pose = Pose(33.7, 25.0, Math.toRadians(0.0))
    private val score2: Pose = Pose(scoreX, secondScoreY, Math.toRadians(180.0))
    private val score3: Pose = Pose(scoreX, thirdScoreY, Math.toRadians(180.0))
    private val score4: Pose = Pose(scoreX, fourthScoreY, Math.toRadians(180.0))
    private val score5: Pose = Pose(scoreX, fifthScoreY, Math.toRadians(180.0))
    private val park: Pose = Pose(12.2, 32.3, Math.toRadians(180.0))
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
            .setConstantHeadingInterpolation(start.heading)
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
            .setConstantHeadingInterpolation(start.heading)
            .build()

        give1Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(get1), Point(give1)))
            .setConstantHeadingInterpolation(start.heading)
            .build()

        get2Path = follower!!.pathBuilder()
            .addPath(BezierCurve(Point(give1), Point(get2Control1), Point(get2)))
            .setConstantHeadingInterpolation(get2.heading)
            .build()

        give2Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(get2), Point(give2)))
            .setConstantHeadingInterpolation(give2.heading)
            .build()

        get3Path = follower!!.pathBuilder()
            .addPath(BezierCurve(Point(give2), Point(get3Control1), Point(get3)))
            .setConstantHeadingInterpolation(get3.heading)
            .build()

        give3Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(get3), Point(give3)))
            .setConstantHeadingInterpolation(give3.heading)
            .build()

        load2Path = follower!!.pathBuilder()
            .addPath(BezierCurve(Point(give3), Point(loadControl1), Point(load)))
            .setConstantHeadingInterpolation(give3.heading)
            .build()

        score2Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(load), Point(score2)))
            .setConstantHeadingInterpolation(score2.heading)
            .build()

        load3Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(score2), Point(load)))
            .setConstantHeadingInterpolation(load.heading)
            .build()

        score3Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(load), Point(score3)))
            .setConstantHeadingInterpolation(score3.heading)
            .build()

        load4Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(score3), Point(load)))
            .setConstantHeadingInterpolation(load.heading)
            .build()

        score4Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(load), Point(score4)))
            .setConstantHeadingInterpolation(score4.heading)
            .build()

        load5Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(score4), Point(load)))
            .setConstantHeadingInterpolation(load.heading)
            .build()

        score5Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(load), Point(score5)))
            .setConstantHeadingInterpolation(score5.heading)
            .build()

        parkPath = follower!!.pathBuilder()
            .addPath(BezierLine(Point(score5), Point(park)))
            .setConstantHeadingInterpolation(park.heading)
            .build()
    }
}