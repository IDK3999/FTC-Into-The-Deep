package PrimeTechV3.OpModes.Auto.Specimen

import PrimeTechV3.Actions.Actions
import PrimeTechV3.Components.Delay
import PrimeTechV3.Components.Pedro
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode

@Autonomous(name = "Specimen Auto")
class SpecimenAuto : OpMode() {
    private var state = 0

    private lateinit var actions: Actions
    private lateinit var pedro: Pedro

    private var pathStarted = false

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
            0 -> {} // INIT

            1 -> { // FOLLOW_PATH
                if (!pathStarted) {
                    pedro.reset()
                    pedro.followPath(SpecimenPaths.scorePreloadPath)
                    pathStarted = true
                } else if (pedro.isDone()) {
                    state++
                }
            }

            2 -> { // SCORE_SPECIMEN
                actions.scoreSpecimen()
                state++
            }

            3 -> { // WAIT_FOR_SCORE_COMPLETE
                if (actions.isDone()) {
                    Delay.start(1)
                    state++
                }
            }

            4 -> { // DELAY1
                if (Delay.isDone()) {
                    state++
                }
            }

            5 -> { // RESET_MECHANISMS
                actions.returnToReset()
                state++
            }

            6 -> { // WAIT_FOR_RESET_COMPLETE
                if (actions.isDone()) {
                    state++
                }
            }

            7 -> { // COMPLETE
            }
        }

        telemetry.update()
    }
}