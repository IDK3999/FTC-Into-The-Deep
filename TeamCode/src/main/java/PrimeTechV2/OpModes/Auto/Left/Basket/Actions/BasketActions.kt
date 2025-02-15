package PrimeTechV2.OpModes.Auto.Left.Basket.Actions

import PrimeTechV2.Components.ActionGroups.ActionGroups
import PrimeTechV2.OpModes.Auto.Left.Basket.Paths.BasketPaths
import com.rowanmcalpin.nextftc.core.command.Command
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup
import com.rowanmcalpin.nextftc.pedro.FollowPath

object BasketActions {
    val scorePreload: Command
        get() = SequentialGroup(
            FollowPath(BasketPaths.scorePreloadPath, true),
            ActionGroups.scoreBasket
        )

    val load1FromGround: Command
        get() = SequentialGroup(
            FollowPath(BasketPaths.load1FromGroundPath, true),
            ActionGroups.loadFromGround
        )

    val score1: Command
        get() = SequentialGroup(
            FollowPath(BasketPaths.score1Path, true),
            ActionGroups.scoreBasket
        )

    val load2FromGround: Command
        get() = SequentialGroup(
            FollowPath(BasketPaths.load2FromGroundPath, true),
            ActionGroups.loadFromGround
        )

    val score2: Command
        get() = SequentialGroup(
            FollowPath(BasketPaths.score2Path, true),
            ActionGroups.scoreBasket
        )

    val load3FromGround: Command
        get() = SequentialGroup(
            FollowPath(BasketPaths.load3FromGroundPath, true),
            ActionGroups.loadFromGround
        )

    val score3: Command
        get() = SequentialGroup(
            FollowPath(BasketPaths.score3Path, true),
            ActionGroups.scoreBasket
        )
}