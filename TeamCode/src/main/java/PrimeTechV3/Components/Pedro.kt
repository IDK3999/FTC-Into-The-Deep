package PrimeTechV3.Components

import com.pedropathing.follower.Follower
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.PathChain
import com.pedropathing.util.Constants
import com.qualcomm.robotcore.hardware.HardwareMap
import pedroPathing.constants.FConstants
import pedroPathing.constants.LConstants

/**
 * Thin wrapper around [Pedro Pathing](https://pedropathing.com/), the library that drives
 * the robot along a path.
 *
 * Pedro's [Follower] does the hard work: it knows where the robot is (from the odometry
 * pod) and sets the four drive motors to chase a Bezier curve. All this wrapper adds is a
 * [isDone] flag in the same style as [Lift] and [Pivot], so an autonomous sequence can
 * treat "drive along this path" exactly like "raise the arm" and wait for both the same way.
 *
 * As with the other components, [update] has to be called every loop.
 */
object Pedro {
    // region Hardware
    // Instantiating these runs their static initialiser blocks, which is what actually
    // loads the tuned numbers (see pedroPathing/constants/). Constants.setConstants below
    // does the same thing, but FTC Dashboard also needs a real instance to edit live,
    // so the quickstart keeps both. Do not delete them.
    val fConstants: FConstants = FConstants()
    val lConstants: LConstants = LConstants()

    lateinit var follower: Follower
    // endregion Hardware

    // region Current state
    private var followState: PedroState = PedroState.IDLE
    // endregion Current state

    /**
     * Sets up the follower. Call once from the OpMode's `init`.
     *
     * @param startingPose where on the field the robot is placed before the match, in
     *   Pedro's field coordinates (inches, plus a heading in radians). Getting this wrong
     *   means every path afterwards is offset, so it must match how the robot is actually
     *   lined up against the wall.
     */
    fun init(hardwareMap: HardwareMap, startingPose: Pose) {
        Constants.setConstants(FConstants::class.java, LConstants::class.java)
        this.follower = Follower(hardwareMap)
        follower.setStartingPose(startingPose)
    }

    /** Call once from the OpMode's `start`. */
    fun start() {
        this.reset()
    }

    fun reset() {
        stopFollowing()
    }

    /** Abandons the current path and leaves the robot where it is. */
    fun stopFollowing() {
        follower.breakFollowing()
        followState = PedroState.IDLE
    }

    /** True once the robot has reached the end of its path. */
    fun isDone(): Boolean {
        return followState == PedroState.IDLE
    }

    /** Drives along [pathChain], holding the end position once it arrives. */
    fun followPath(pathChain: PathChain) {
        follower.followPath(pathChain, true)
        followState = PedroState.MOVING
    }

    /**
     * Drives along [pathChain] with the speed capped.
     *
     * @param maxPower 0.0 .. 1.0 ceiling on drive power. Lower it where accuracy matters
     *   more than speed, or where a full-speed run would tip the robot.
     */
    fun followPath(pathChain: PathChain, maxPower: Double) {
        follower.followPath(pathChain, maxPower, true)
        followState = PedroState.MOVING
    }

    /** Must be called every loop, so the follower can keep correcting the drive motors. */
    fun update() {
        follower.update()
        if (followState == PedroState.MOVING && !follower.isBusy)
            followState = PedroState.IDLE
    }

    /** Whether the robot is currently driving a path. */
    enum class PedroState {
        IDLE, MOVING
    }
}
