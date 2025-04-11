package PrimeTechV3.OpModes.Auto.Sample

import com.pedropathing.follower.Follower
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierCurve
import com.pedropathing.pathgen.PathChain
import com.pedropathing.pathgen.Point

object SamplePaths {
    // region Poses
    private var startX = 7.35
    private var startY = 112.5
    private var scoreY = 127.0
    private val heading = Math.toRadians(0.0)

    val start = Pose(startX, startY, heading)
    private val scorePreload = Pose(18.0, 135.0)
    private val park = Pose(64.0, 95.0)
    private val parkControl1 = Pose(28.0, 96.0)
    private val parkC2 = Pose(68.0, 118.0)
    // endregion Poses

    // region Paths
    lateinit var scorePreloadPath: PathChain
    lateinit var parkPath: PathChain
    // endregion Paths

    fun build(follower: Follower) {
        scorePreloadPath = follower.pathBuilder()
            .addPath(BezierCurve(Point(start), Point(29.0, 115.0), Point(scorePreload)))
            .setConstantHeadingInterpolation(heading)
            .build()

        parkPath = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    Point(scorePreload),
                    Point(parkControl1),
                    Point(parkC2),
                    Point(park)
                )
            )
            .setConstantHeadingInterpolation(heading)
            .build()
    }
}