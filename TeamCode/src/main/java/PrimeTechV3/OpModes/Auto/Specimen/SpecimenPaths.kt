package PrimeTechV3.OpModes.Auto.Specimen

import com.pedropathing.follower.Follower
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierCurve
import com.pedropathing.pathgen.BezierLine
import com.pedropathing.pathgen.PathChain
import com.pedropathing.pathgen.Point

object SpecimenPaths {
    // region Poses
    private var startX = 8.4
    private var scoreX = 40.0
    private var scoreX2 = 36.0
    private var scoreX3 = 36.0
    private var giveX = 16.0
    private var scoreYStep = 3.5
    private var firstScoreY = 66.0
    private val secondScoreY = firstScoreY + scoreYStep
    private val thirdScoreY = secondScoreY + scoreYStep
    private val fourthScoreY = thirdScoreY + scoreYStep
    private val fifthScoreY = fourthScoreY + scoreYStep
    private val heading = Math.toRadians(180.0)

    val start = Pose(startX, 64.7, heading)
    private val scorePreload = Pose(scoreX, firstScoreY)
    private val get1 = Pose(60.0, 27.0)
    private val get1Control1 = Pose(4.0, 14.0)
    private val get1Control2 = Pose(60.0, 47.0)
    private val give1 = Pose(giveX, 23.0)
    private val get2 = Pose(52.0, 13.0)
    private val get2Control1 = Pose(72.0, 27.0)
    private val give2 = Pose(giveX, 13.0)
    private val get3 = Pose(52.0, 8.3)
    private val get3Control1 = Pose(72.0, 16.0)
    private val give3 = Pose(giveX, 8.0)
    private val load = Pose(15.0, 24.0, heading)
    private val loadControl1 = Pose(25.0, 13.0)
    private val loadControl2 = Pose(25.0, 24.0)
    private val loadControl = Pose(25.0, 30.0)
    private val score2 = Pose(scoreX2, secondScoreY)
    private val score2f = Pose(scoreX2 + 5, secondScoreY)
    private val score3 = Pose(scoreX3, thirdScoreY)
    private val score3f = Pose(scoreX3 + 5, thirdScoreY)
    private val score4 = Pose(scoreX3, fourthScoreY)
    private val score4f = Pose(scoreX3 + 5, fourthScoreY)
    private val score5 = Pose(scoreX2, fifthScoreY)
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
    lateinit var score2fPath: PathChain
    lateinit var load3Path: PathChain
    lateinit var score3Path: PathChain
    lateinit var score3fPath: PathChain
    lateinit var load4Path: PathChain
    lateinit var score4Path: PathChain
    lateinit var score4fPath: PathChain
    lateinit var load5Path: PathChain
    lateinit var score5Path: PathChain
    // endregion Paths

    fun build(follower: Follower) {
        scorePreloadPath = follower.pathBuilder()
            .addPath(BezierLine(Point(start), Point(scorePreload)))
            .setConstantHeadingInterpolation(heading)
            .build()

        get1Path = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    Point(score2f),
                    Point(get1Control1),
                    Point(get1Control2),
                    Point(get1)
                )
            )
            .setConstantHeadingInterpolation(heading)
            .build()

        give1Path = follower.pathBuilder()
            .addPath(BezierLine(Point(get1), Point(give1)))
            .setConstantHeadingInterpolation(heading)
            .build()

        get2Path = follower.pathBuilder()
            .addPath(BezierCurve(Point(give1), Point(get2Control1), Point(get2)))
            .setConstantHeadingInterpolation(heading)
            .build()

        give2Path = follower.pathBuilder()
            .addPath(BezierLine(Point(get2), Point(give2)))
            .setConstantHeadingInterpolation(heading)
            .build()

        get3Path = follower.pathBuilder()
            .addPath(BezierCurve(Point(give2), Point(get3Control1), Point(get3)))
            .setConstantHeadingInterpolation(heading)
            .build()

        give3Path = follower.pathBuilder()
            .addPath(BezierLine(Point(get3), Point(give3)))
            .setConstantHeadingInterpolation(heading)
            .build()

        load2Path = follower.pathBuilder()
            .addPath(BezierCurve(Point(scorePreload), Point(15.0, 67.0), Point(load)))
            .setConstantHeadingInterpolation(heading)
            .build()

        score2Path = follower.pathBuilder()
            .addPath(BezierCurve(Point(load), Point(16.0, 67.0), Point(score2)))
            .setConstantHeadingInterpolation(heading)
            .build()

        score2fPath = follower.pathBuilder()
            .addPath(BezierLine(Point(score2), Point(score2f)))
            .setConstantHeadingInterpolation(heading)
            .build()

        load3Path = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    Point(give2),
                    Point(loadControl1),
                    Point(loadControl2),
                    Point(load)
                )
            )
//            .addPath(BezierCurve(Point(score2), Point(loadControl), Point(load)))
            .setConstantHeadingInterpolation(heading)
            .build()

        score3Path = follower.pathBuilder()
            .addPath(BezierCurve(Point(load), Point(16.0, 67.0), Point(score3)))
            .setConstantHeadingInterpolation(heading)
            .build()

        score3fPath = follower.pathBuilder()
            .addPath(BezierLine(Point(score3), Point(score3f)))
            .setConstantHeadingInterpolation(heading)
            .build()

        load4Path = follower.pathBuilder()
            .addPath(BezierCurve(Point(score3f), Point(loadControl), Point(load)))
            .setConstantHeadingInterpolation(heading)
            .build()

        score4Path = follower.pathBuilder()
            .addPath(BezierCurve(Point(load), Point(16.0, 67.0), Point(score4)))
            .setConstantHeadingInterpolation(heading)
            .build()

        score4fPath = follower.pathBuilder()
            .addPath(BezierLine(Point(score4), Point(score4f)))
            .setConstantHeadingInterpolation(heading)
            .build()

        load5Path = follower.pathBuilder()
            .addPath(BezierCurve(Point(score4), Point(loadControl), Point(load)))
            .setConstantHeadingInterpolation(heading)
            .build()

        score5Path = follower.pathBuilder()
            .addPath(BezierLine(Point(load), Point(score5)))
            .setConstantHeadingInterpolation(heading)
            .build()

        parkPath = follower.pathBuilder()
            .addPath(BezierLine(Point(score5), Point(park)))
            .setConstantHeadingInterpolation(heading)
            .build()
    }
}