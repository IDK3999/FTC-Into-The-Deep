package PrimeTechV3.OpModes.Auto.Sample

import com.pedropathing.follower.Follower
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierCurve
import com.pedropathing.pathgen.PathChain
import com.pedropathing.pathgen.Point

/**
 * Paths for [SampleAuto], the basket (left-hand) side of the field.
 *
 * See the comments at the top of
 * [PrimeTechV3.OpModes.Auto.Specimen.SpecimenPaths] for how Pedro's coordinates, poses
 * and Bezier control points work - the same applies here.
 *
 * This side of the field was never finished. There are only two paths: drive up to the
 * basket, and park. Nothing is actually scored.
 */
object SamplePaths {
    /** Constant robot heading for this routine: 0 degrees. */
    private val HEADING = Math.toRadians(0.0)

    // region Field positions
    private const val START_X = 7.35
    private const val START_Y = 112.5

    /** Where the robot is placed before the match starts. */
    val start = Pose(START_X, START_Y, HEADING)

    /** In front of the basket, where a sample would be dropped in. */
    private val scorePreload = Pose(18.0, 135.0)

    /** Control point that swings the robot out around the basket on the way in. */
    private val scorePreloadControl = Pose(29.0, 115.0)

    /** Out near the submersible, to be touching it when the match ends. */
    private val park = Pose(64.0, 95.0)
    private val parkControl1 = Pose(28.0, 96.0)
    private val parkControl2 = Pose(68.0, 118.0)
    // endregion Field positions

    // region Paths
    lateinit var scorePreloadPath: PathChain
    lateinit var parkPath: PathChain
    // endregion Paths

    /** Creates the paths. Call once from the OpMode's `init`, before following any of them. */
    fun build(follower: Follower) {
        scorePreloadPath = follower.pathBuilder()
            .addPath(BezierCurve(Point(start), Point(scorePreloadControl), Point(scorePreload)))
            .setConstantHeadingInterpolation(HEADING)
            .build()

        parkPath = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    Point(scorePreload),
                    Point(parkControl1),
                    Point(parkControl2),
                    Point(park)
                )
            )
            .setConstantHeadingInterpolation(HEADING)
            .build()
    }
}
