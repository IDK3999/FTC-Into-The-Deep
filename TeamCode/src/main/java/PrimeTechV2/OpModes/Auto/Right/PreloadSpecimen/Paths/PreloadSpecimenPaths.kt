package PrimeTechV2.OpModes.Auto.Right.PreloadSpecimen.Paths

import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierCurve
import com.pedropathing.pathgen.BezierLine
import com.pedropathing.pathgen.PathChain
import com.pedropathing.pathgen.Point
import com.rowanmcalpin.nextftc.pedro.FollowerNotInitializedException
import com.rowanmcalpin.nextftc.pedro.PedroData.follower

object PreloadSpecimenPaths {
    // region Poses
    var startX = 8.9
    var scoreX = 40.0
    private var scoreY = 68.0
    var giveX = 17.9

    val start: Pose = Pose(startX, 64.6, Math.toRadians(180.0))
    val scorePreload: Pose = Pose(scoreX, scoreY, Math.toRadians(180.0))
    val get1: Pose = Pose(57.0, 23.0, Math.toRadians(180.0))
    val get1Control1: Pose = Pose(6.0, 32.0)
    val get1Control2: Pose = Pose(74.0, 37.0)
    val give1: Pose = Pose(giveX, 23.0, Math.toRadians(180.0))
    val get2: Pose = Pose(52.0, 13.0, Math.toRadians(180.0))
    val get2Control1: Pose = Pose(72.0, 30.0, Math.toRadians(180.0))
    val give2: Pose = Pose(giveX, 13.0, Math.toRadians(180.0))
    val get3: Pose = Pose(58.0, 8.6, Math.toRadians(180.0))
    val get3Control1: Pose = Pose(57.8, 15.5)
    val give3: Pose = Pose(giveX, 8.6, Math.toRadians(180.0))
    val park: Pose = Pose(12.2, 32.3, Math.toRadians(180.0))
    val parkControl1: Pose = Pose(33.7, 25.0, Math.toRadians(0.0))
    // endregion Poses

    // region Paths
    lateinit var scorePreloadPath: PathChain
    lateinit var get1Path: PathChain
    lateinit var give1Path: PathChain
    lateinit var get2Path: PathChain
    lateinit var give2Path: PathChain
    lateinit var get3Path: PathChain
    lateinit var give3Path: PathChain
    lateinit var parkFromScorePath: PathChain
    lateinit var parkPath: PathChain
    // endregion Paths

    fun buildObsZonePushbotPaths() {
        if (follower == null) {
            throw FollowerNotInitializedException()
        }

        scorePreloadPath = follower!!.pathBuilder()
            .addPath(BezierLine(Point(start), Point(scorePreload)))
//            .setLinearHeadingInterpolation(start.heading, scorePreload.heading)
            .setConstantHeadingInterpolation(start.heading)
            .build()

        get1Path = follower!!.pathBuilder()
            .addPath(BezierCurve(Point(scorePreload), Point(get1Control1), Point(get1Control2), Point(get1)))
//            .setLinearHeadingInterpolation(scorePreload.heading, get1.heading)
            .setConstantHeadingInterpolation(start.heading)
            .build()

        give1Path = follower!!.pathBuilder()
            .addPath(BezierLine(Point(get1), Point(give1)))
//            .setConstantHeadingInterpolation(give1.heading)
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

        parkFromScorePath = follower!!.pathBuilder()
            .addPath(BezierLine(Point(scorePreload), Point(park)))
            .setConstantHeadingInterpolation(Math.toRadians(180.0))
            .build()

        parkPath = follower!!.pathBuilder()
            .addPath(BezierCurve(Point(give3), Point(parkControl1), Point(park)))
            .setLinearHeadingInterpolation(give3.heading, park.heading)
            .build()
    }
}