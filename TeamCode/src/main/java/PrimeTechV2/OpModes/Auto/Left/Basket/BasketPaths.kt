package PrimeTechV2.OpModes.Auto.Left.Basket

import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierLine
import com.pedropathing.pathgen.PathChain
import com.pedropathing.pathgen.Point
import com.rowanmcalpin.nextftc.pedro.FollowerNotInitializedException
import com.rowanmcalpin.nextftc.pedro.PedroData.follower

object BasketPaths {
    // region Poses
    private var scoreX = 16.0
    private var scoreY = 128.0
    private var scoreHeading = Math.toRadians(-45.0)
    private var loadFromGroundX = 35.0
    private var loadFromGroundYStep = -10.5
    private var load1FromGroundY = 120.75
    private val load2FromGroundY = load1FromGroundY + loadFromGroundYStep

    val start = Pose(7.5, 88.6)
    private val score = Pose(scoreX, scoreY, scoreHeading)
    private val load1FromGround = Pose(loadFromGroundX, load1FromGroundY)
    private val load2FromGround = Pose(loadFromGroundX, load2FromGroundY)
    private val load3FromGround = Pose(45.5, 131.0, Math.toRadians(90.0))
    // endregion Poses

    // region Paths
    lateinit var scorePreloadPath: PathChain
    lateinit var load1FromGroundPath: PathChain
    lateinit var score1Path: PathChain
    lateinit var load2FromGroundPath: PathChain
    lateinit var score2Path: PathChain
    lateinit var load3FromGroundPath: PathChain
    lateinit var score3Path: PathChain
    // endregion Paths

    fun buildBasketPaths() {
        if (follower == null) {
            throw FollowerNotInitializedException()
        }

        scorePreloadPath = follower!!.pathBuilder()
            .addPath(BezierLine(Point(start), Point(score)))
            .setLinearHeadingInterpolation(start.heading, scoreHeading)
            .build()

        load1FromGroundPath = follower!!.pathBuilder()
            .addPath(BezierLine(Point(start), Point(load1FromGround)))
            .setLinearHeadingInterpolation(scoreHeading, load1FromGround.heading)
            .build()

        score1Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(load1FromGround), Point(score)))
            .setLinearHeadingInterpolation(load1FromGround.heading, scoreHeading)
            .build()

        load2FromGroundPath = follower!!.pathBuilder()
            .addPath(BezierLine(Point(score), Point(load2FromGround)))
            .setLinearHeadingInterpolation(scoreHeading, load2FromGround.heading)
            .build()

        score2Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(load2FromGround), Point(score)))
            .setLinearHeadingInterpolation(load2FromGround.heading, scoreHeading)
            .build()

        load3FromGroundPath = follower!!.pathBuilder()
            .addPath(BezierLine(Point(score), Point(load3FromGround)))
            .setLinearHeadingInterpolation(scoreHeading, load3FromGround.heading)
            .build()

        score3Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(load3FromGround), Point(score)))
            .setLinearHeadingInterpolation(load3FromGround.heading, scoreHeading)
            .build()
    }
}