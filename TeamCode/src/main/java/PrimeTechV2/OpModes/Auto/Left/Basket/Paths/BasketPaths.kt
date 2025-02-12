package PrimeTechV2.OpModes.Auto.Left.Basket.Paths

import com.pedropathing.localization.Pose
import com.rowanmcalpin.nextftc.pedro.FollowerNotInitializedException
import com.rowanmcalpin.nextftc.pedro.PedroData.follower

object BasketPaths {
    // region Poses
    // endregion Poses

    // region Paths
    // endregion Paths

    fun buildBasketPaths() {
        if (follower == null) {
            throw FollowerNotInitializedException()
        }
    }
}