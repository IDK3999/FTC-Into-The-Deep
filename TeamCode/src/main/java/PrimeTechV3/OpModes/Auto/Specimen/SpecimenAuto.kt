package PrimeTechV3.OpModes.Auto.Specimen

import PrimeTechV3.Actions.Actions
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
                    pedro.followPath(SpecimenPaths.scorePreloadPath)
                    actionStarted = true
                } else if (pedro.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            2 -> {
                if (!actionStarted) {
                    actions.setAction(Actions.PossibleActions.SCORE_SPECIMEN)
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
                    pedro.followPath(SpecimenPaths.getGiveSamples)
                    actions.setAction(Actions.PossibleActions.BEFORE_GRAB_SPECIMEN)
                    actionStarted = true
                } else if (pedro.isDone() && actions.isDone()) {
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
                    actions.setAction(Actions.PossibleActions.GRAB_SPECIMEN)
                    actionStarted = true
                } else if (actions.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            7 -> {
                if (!actionStarted) {
                    pedro.followPath(SpecimenPaths.score2Path)
                    actionStarted = true
                } else if (pedro.isDone()) {
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