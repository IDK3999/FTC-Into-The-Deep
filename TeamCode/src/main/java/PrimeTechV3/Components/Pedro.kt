package PrimeTechV3.Components

import com.pedropathing.follower.Follower
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.PathChain
import com.pedropathing.util.Constants
import com.qualcomm.robotcore.hardware.HardwareMap
import pedroPathing.constants.FConstants
import pedroPathing.constants.LConstants


object Pedro {
    // region Declare Components
    val fConstants: FConstants = FConstants()
    val lConstants: LConstants = LConstants()

    lateinit var follower: Follower

    // region Declare States
    private var state: PedroState = PedroState.IDLE
    // endregion Declare States

    fun init(hardwareMap: HardwareMap, startingPose: Pose) {
        Constants.setConstants(FConstants::class.java, LConstants::class.java)
        this.follower = Follower(hardwareMap)
        follower.setStartingPose(startingPose)
    }

    fun start() {
        this.reset()
    }

    fun reset() {
        stopFollowing()
    }

    fun stopFollowing() {
        follower.breakFollowing()
        state = PedroState.IDLE
    }

    fun isDone(): Boolean {
        return state == PedroState.IDLE
    }

    fun followPath(pathChain: PathChain) {
        follower.followPath(pathChain, true)
        state = PedroState.MOVING
    }

    fun followPath(pathChain: PathChain, maxPower: Double) {
        follower.followPath(pathChain, maxPower, true)
        state = PedroState.MOVING
    }

    fun update() {
        if (state == PedroState.MOVING) {
            if (follower.isBusy) {
                follower.update()
            } else {
                state = PedroState.IDLE
            }
        }
    }

    enum class PedroState {
        IDLE, MOVING
    }
}