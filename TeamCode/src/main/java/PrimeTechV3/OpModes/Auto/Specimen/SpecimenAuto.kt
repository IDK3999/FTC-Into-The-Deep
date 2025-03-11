package PrimeTechV3.OpModes.Auto.Specimen

import PrimeTechV3.Actions.Actions
import PrimeTechV3.Components.Delay
import PrimeTechV3.Components.Pedro
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode

@Autonomous(name = "Specimen V3")
class SpecimenAuto : OpMode() {
    private var state = 0

    private lateinit var actions: Actions
    private lateinit var pedro: Pedro

    private var actionStarted = false

    override fun init() {
        actions = Actions
        actions.init(hardwareMap)

        pedro = Pedro
        pedro.init(hardwareMap, SpecimenPaths.start)
//        pedro.init(hardwareMap, SpecimenPaths.load)

        SpecimenPaths.build(pedro.follower)
    }

    override fun start() {
        pedro.start()
        actions.start()

        state = 1
    }

    override fun loop() {
        actions.update()
        pedro.update()

        telemetry.addData("Current State", state)

        when (state) {
            0 -> {}

            1 -> {
                if (!actionStarted) {
                    pedro.followPath(SpecimenPaths.scorePreloadPath, 0.7)
                    actionStarted = true
                } else if (pedro.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            2 -> {
                if (!actionStarted) {
                    actions.setAction(Actions.PossibleActions.BEFORE_SCORE_SPECIMEN)
                    actionStarted = true
                } else if (actions.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            3 -> {
                if (!actionStarted) {
                    actions.setAction(Actions.PossibleActions.SCORE_SPECIMEN)
                    actionStarted = true
                } else if (actions.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            4 -> {
                if (!actionStarted) {
                    actions.setAction(Actions.PossibleActions.BEFORE_GRAB_SPECIMEN)
                    actionStarted = true
                } else if (actions.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            5 -> {
                if (!actionStarted) {
                    pedro.followPath(SpecimenPaths.load2Path)
                    actionStarted = true
                } else if (pedro.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            6 -> {
                if (!actionStarted) {
                    Delay.start(1.0)
                    actionStarted = true
                } else if (Delay.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            7 -> {
                if (!actionStarted) {
                    actions.setAction(Actions.PossibleActions.GRAB_SPECIMEN)
                    actionStarted = true
                } else if (actions.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            8 -> {
                if (!actionStarted) {
                    PrimeTechV3.Components.Lift.resetEncoders()
                    actionStarted = true
                } else if (PrimeTechV3.Components.Lift.isAtTarget()) {
                    actionStarted = false
                    state++
                }
            }

            9 -> {
                if (!actionStarted) {
                    pedro.followPath(SpecimenPaths.score2Path)
                    actionStarted = true
                } else if (pedro.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            10 -> {
                if (!actionStarted) {
                    actions.setAction(Actions.PossibleActions.BEFORE_SCORE_SPECIMEN)
                    actionStarted = true
                } else if (actions.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            11 -> {
                if (!actionStarted) {
                    actions.setAction(Actions.PossibleActions.SCORE_SPECIMEN)
                    actionStarted = true
                } else if (actions.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            12 -> {
                if (!actionStarted) {
                    actions.setAction(Actions.PossibleActions.RESET_ALL)
                    actionStarted = true
                } else if (actions.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            else -> {
                state = 0
            }
        }

        telemetry.update()
    }
}