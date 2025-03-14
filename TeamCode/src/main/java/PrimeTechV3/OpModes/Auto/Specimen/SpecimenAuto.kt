package PrimeTechV3.OpModes.Auto.Specimen

import PrimeTechV3.Actions.Actions
import PrimeTechV3.Components.Claw
import PrimeTechV3.Components.Delay
import PrimeTechV3.Components.Pedro
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode

@Autonomous(name = "Specimen")
class SpecimenAuto : OpMode() {
    private var state = 0

    private lateinit var actions: Actions
    private lateinit var pedro: Pedro

    private var actionStarted = false

    private val grabDelay = 0.2

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
                    Claw.setClawOpen(false)
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
                    Delay.start(0.2)
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
                    actions.setAction(Actions.PossibleActions.BEFORE_GRAB_SPECIMEN)
                    pedro.followPath(SpecimenPaths.get1Path)
                    actionStarted = true
                } else if (actions.isDone() && pedro.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            13 -> {
                if (!actionStarted) {
                    pedro.followPath(SpecimenPaths.give1Path)
                    actionStarted = true
                } else if (pedro.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            14 -> {
                if (!actionStarted) {
                    pedro.followPath(SpecimenPaths.get2Path)
                    actionStarted = true
                } else if (pedro.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            15 -> {
                if (!actionStarted) {
                    pedro.followPath(SpecimenPaths.give2Path)
                    actionStarted = true
                } else if (pedro.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            16 -> {
                if (!actionStarted) {
                    pedro.followPath(SpecimenPaths.load3Path)
                    actionStarted = true
                } else if (pedro.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            17 -> {
                if (!actionStarted) {
                    Delay.start(0.2)
                    actionStarted = true
                } else if (Delay.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            18 -> {
                if (!actionStarted) {
                    actions.setAction(Actions.PossibleActions.GRAB_SPECIMEN)
                    actionStarted = true
                } else if (actions.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            19 -> {
                if (!actionStarted) {
                    PrimeTechV3.Components.Lift.resetEncoders()
                    actionStarted = true
                } else if (PrimeTechV3.Components.Lift.isAtTarget()) {
                    actionStarted = false
                    state++
                }
            }

            20 -> {
                if (!actionStarted) {
                    pedro.followPath(SpecimenPaths.score3Path)
                    actionStarted = true
                } else if (pedro.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            21 -> {
                if (!actionStarted) {
                    actions.setAction(Actions.PossibleActions.BEFORE_SCORE_SPECIMEN)
                    actionStarted = true
                } else if (actions.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            22 -> {
                if (!actionStarted) {
                    actions.setAction(Actions.PossibleActions.SCORE_SPECIMEN)
                    actionStarted = true
                } else if (actions.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            23 -> {
                if (!actionStarted) {
                    actions.setAction(Actions.PossibleActions.BEFORE_GRAB_SPECIMEN)
                    pedro.followPath(SpecimenPaths.load4Path)
                    actionStarted = true
                } else if (actions.isDone() && pedro.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            24 -> {
                if (!actionStarted) {
                    Delay.start(0.2)
                    actionStarted = true
                } else if (Delay.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            25 -> {
                if (!actionStarted) {
                    actions.setAction(Actions.PossibleActions.GRAB_SPECIMEN)
                    actionStarted = true
                } else if (actions.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            26 -> {
                if (!actionStarted) {
                    PrimeTechV3.Components.Lift.resetEncoders()
                    actionStarted = true
                } else if (PrimeTechV3.Components.Lift.isAtTarget()) {
                    actionStarted = false
                    state++
                }
            }

            27 -> {
                if (!actionStarted) {
                    pedro.followPath(SpecimenPaths.score4Path)
                    actionStarted = true
                } else if (pedro.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            28 -> {
                if (!actionStarted) {
                    actions.setAction(Actions.PossibleActions.BEFORE_SCORE_SPECIMEN)
                    actionStarted = true
                } else if (actions.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            29 -> {
                if (!actionStarted) {
                    actions.setAction(Actions.PossibleActions.SCORE_SPECIMEN)
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