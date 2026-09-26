package PrimeTechV3.OpModes.Auto.Specimen

import com.pedropathing.follower.Follower
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierCurve
import com.pedropathing.pathgen.BezierLine
import com.pedropathing.pathgen.PathChain
import com.pedropathing.pathgen.Point

/**
 * Every path driven by [SpecimenAuto], and the field positions they join up.
 *
 * ## Reading the coordinates
 *
 * Pedro Pathing measures the field in **inches** from one corner, so both x and y run
 * 0 to 144, and headings are in **radians**. A [Pose] is a position plus a heading;
 * a [Point] is just a position.
 *
 * Every path here holds a constant heading of 180 degrees ([HEADING]) - the robot never
 * turns during this routine, it only slides around, because the claw scores over the back
 * of the robot. That is why larger x means "further onto the chamber" below.
 *
 * ## Curves and control points
 *
 * A [BezierLine] is a straight line between two points. A [BezierCurve] bends: it takes a
 * start, one or more **control points**, and an end. The curve is pulled towards the control
 * points without passing through them, which is how a path is steered around the
 * submersible instead of driving through it. Names ending in `Control` are only there to
 * shape a curve - the robot never visits them.
 *
 * ## Editing these
 *
 * Positions were found by driving the robot and reading its pose off FTC Dashboard, then
 * nudging the numbers until the run worked. If a path is off, change the number here rather
 * than adding a correction elsewhere - and re-test, because later paths start where earlier
 * ones end.
 */
object SpecimenPaths {
    /** Constant robot heading for the whole routine: 180 degrees, i.e. claw facing the chamber. */
    private val HEADING = Math.toRadians(180.0)

    // region Field positions
    /** x of the wall the robot starts against, and returns to for specimens. */
    private const val START_X = 8.4

    /** x of the chamber for the preloaded specimen. */
    private const val PRELOAD_SCORE_X = 40.0

    /** x of the chamber for every later specimen - slightly closer, they seat better. */
    private const val SCORE_X = 36.0

    /** x the samples get pushed back to, inside the observation zone. */
    private const val PUSH_TO_X = 16.0

    /**
     * How far apart along y the specimens are hung.
     *
     * Each one goes beside the last rather than on top of it, so they do not knock each
     * other off the bar.
     */
    private const val SCORE_Y_STEP = 3.5

    private const val FIRST_SCORE_Y = 66.0
    private const val SECOND_SCORE_Y = FIRST_SCORE_Y + SCORE_Y_STEP
    private const val THIRD_SCORE_Y = SECOND_SCORE_Y + SCORE_Y_STEP
    private const val FOURTH_SCORE_Y = THIRD_SCORE_Y + SCORE_Y_STEP

    /**
     * How much further the robot pushes once it is at the chamber, to seat the specimen.
     *
     * Done as its own short path so the robot is already square to the bar before it pushes.
     */
    private const val SCORE_PUSH_DISTANCE = 5.0

    /** Where the robot is placed before the match starts. */
    val start = Pose(START_X, 64.7, HEADING)

    private val scorePreload = Pose(PRELOAD_SCORE_X, FIRST_SCORE_Y)

    // Approach the chamber, then push forward onto the bar.
    private val scoreSpecimen2 = Pose(SCORE_X, SECOND_SCORE_Y)
    private val scoreSpecimen2Push = Pose(SCORE_X + SCORE_PUSH_DISTANCE, SECOND_SCORE_Y)
    private val scoreSpecimen3 = Pose(SCORE_X, THIRD_SCORE_Y)
    private val scoreSpecimen3Push = Pose(SCORE_X + SCORE_PUSH_DISTANCE, THIRD_SCORE_Y)
    private val scoreSpecimen4 = Pose(SCORE_X, FOURTH_SCORE_Y)
    private val scoreSpecimen4Push = Pose(SCORE_X + SCORE_PUSH_DISTANCE, FOURTH_SCORE_Y)

    // The three neutral samples sitting in the middle of the field. For each one the robot
    // drives out past it ("behindSample") then shoves it back to the wall ("pushSample").
    private val behindSample1 = Pose(60.0, 24.0)
    private val behindSample1Control1 = Pose(4.0, 14.0)
    private val behindSample1Control2 = Pose(60.0, 47.0)
    private val pushSample1 = Pose(PUSH_TO_X, 24.0)

    private val behindSample2 = Pose(58.0, 13.0)
    private val behindSample2Control1 = Pose(70.0, 27.0)
    private val pushSample2 = Pose(PUSH_TO_X, 13.0)

    private val behindSample3 = Pose(52.0, 8.3)
    private val behindSample3Control1 = Pose(72.0, 16.0)
    private val pushSample3 = Pose(PUSH_TO_X, 8.0)

    /** Where the robot waits at the wall for the human player to hand over a specimen. */
    private val wallPickup = Pose(15.0, 24.0, HEADING)
    private val wallPickupControl1 = Pose(25.0, 13.0)
    private val wallPickupControl2 = Pose(25.0, 24.0)
    private val wallPickupControl3 = Pose(25.0, 30.0)

    /** Control point shared by the approaches to the chamber. */
    private val chamberApproachControl = Pose(16.0, 67.0)
    // endregion Field positions

    // region Paths
    // `lateinit` means "assigned later, trust me" - these get their values in build(),
    // which needs the Follower and so cannot run until the OpMode has one. Reading one of
    // these before build() throws UninitializedPropertyAccessException.
    lateinit var scorePreloadPath: PathChain

    lateinit var loadSpecimen2Path: PathChain
    lateinit var scoreSpecimen2Path: PathChain
    lateinit var scoreSpecimen2PushPath: PathChain

    lateinit var driveBehindSample1Path: PathChain
    lateinit var pushSample1Path: PathChain
    lateinit var driveBehindSample2Path: PathChain
    lateinit var pushSample2Path: PathChain
    lateinit var driveBehindSample3Path: PathChain
    lateinit var pushSample3Path: PathChain

    lateinit var loadSpecimen3Path: PathChain
    lateinit var scoreSpecimen3Path: PathChain
    lateinit var scoreSpecimen3PushPath: PathChain

    lateinit var loadSpecimen4Path: PathChain
    lateinit var scoreSpecimen4Path: PathChain
    lateinit var scoreSpecimen4PushPath: PathChain
    // endregion Paths

    /**
     * Creates every path. Must be called once, from the OpMode's `init`, before any path
     * is followed.
     *
     * Note that each path starts where the previous one ended, so the whole run is chained
     * together - moving one position shifts everything after it.
     */
    fun build(follower: Follower) {
        // Straight out from the wall to the chamber, carrying the preloaded specimen.
        scorePreloadPath = follower.pathBuilder()
            .addPath(BezierLine(Point(start), Point(scorePreload)))
            .setConstantHeadingInterpolation(HEADING)
            .build()

        // Back to the wall for the second specimen.
        loadSpecimen2Path = follower.pathBuilder()
            .addPath(BezierCurve(Point(scorePreload), Point(15.0, 67.0), Point(wallPickup)))
            .setConstantHeadingInterpolation(HEADING)
            .build()

        scoreSpecimen2Path = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    Point(wallPickup),
                    Point(chamberApproachControl),
                    Point(scoreSpecimen2)
                )
            )
            .setConstantHeadingInterpolation(HEADING)
            .build()

        scoreSpecimen2PushPath = follower.pathBuilder()
            .addPath(BezierLine(Point(scoreSpecimen2), Point(scoreSpecimen2Push)))
            .setConstantHeadingInterpolation(HEADING)
            .build()

        // Loop out around the first sample. Starts from where the robot finished scoring.
        driveBehindSample1Path = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    Point(scoreSpecimen2Push),
                    Point(behindSample1Control1),
                    Point(behindSample1Control2),
                    Point(behindSample1)
                )
            )
            .setConstantHeadingInterpolation(HEADING)
            .build()

        // Shove the sample to the wall. The high acceleration multiplier makes the robot
        // stop harder at the end, so it does not coast into the wall behind the sample.
        pushSample1Path = follower.pathBuilder()
            .addPath(BezierLine(Point(behindSample1), Point(pushSample1)))
            .setConstantHeadingInterpolation(HEADING)
            .setZeroPowerAccelerationMultiplier(4.0)
            .build()

        driveBehindSample2Path = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    Point(pushSample1),
                    Point(behindSample2Control1),
                    Point(behindSample2)
                )
            )
            .setConstantHeadingInterpolation(HEADING)
            .build()

        pushSample2Path = follower.pathBuilder()
            .addPath(BezierLine(Point(behindSample2), Point(pushSample2)))
            .setConstantHeadingInterpolation(HEADING)
            .setZeroPowerAccelerationMultiplier(4.0)
            .build()

        // The third sample is built but SpecimenAuto does not use it - the run ran out of
        // time. Two samples pushed, then straight back to collecting specimens.
        driveBehindSample3Path = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    Point(pushSample2),
                    Point(behindSample3Control1),
                    Point(behindSample3)
                )
            )
            .setConstantHeadingInterpolation(HEADING)
            .build()

        pushSample3Path = follower.pathBuilder()
            .addPath(BezierLine(Point(behindSample3), Point(pushSample3)))
            .setConstantHeadingInterpolation(HEADING)
            .build()

        // From the second sample straight back to the wall for the third specimen.
        loadSpecimen3Path = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    Point(pushSample2),
                    Point(wallPickupControl1),
                    Point(wallPickupControl2),
                    Point(wallPickup)
                )
            )
            .setConstantHeadingInterpolation(HEADING)
            .build()

        scoreSpecimen3Path = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    Point(wallPickup),
                    Point(chamberApproachControl),
                    Point(scoreSpecimen3)
                )
            )
            .setConstantHeadingInterpolation(HEADING)
            .build()

        scoreSpecimen3PushPath = follower.pathBuilder()
            .addPath(BezierLine(Point(scoreSpecimen3), Point(scoreSpecimen3Push)))
            .setConstantHeadingInterpolation(HEADING)
            .build()

        loadSpecimen4Path = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    Point(scoreSpecimen3Push),
                    Point(wallPickupControl3),
                    Point(wallPickup)
                )
            )
            .setConstantHeadingInterpolation(HEADING)
            .build()

        scoreSpecimen4Path = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    Point(wallPickup),
                    Point(chamberApproachControl),
                    Point(scoreSpecimen4)
                )
            )
            .setConstantHeadingInterpolation(HEADING)
            .build()

        scoreSpecimen4PushPath = follower.pathBuilder()
            .addPath(BezierLine(Point(scoreSpecimen4), Point(scoreSpecimen4Push)))
            .setConstantHeadingInterpolation(HEADING)
            .build()
    }
}
