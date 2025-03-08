package PrimeTechV3.OpModes.Auto.Specimen

import PrimeTechV3.Actions.Actions
import PrimeTechV3.Components.Delay
import PrimeTechV3.Components.Pedro
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierLine
import com.pedropathing.pathgen.PathChain
import com.pedropathing.pathgen.Point
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

        telemetry.addData("Status", "Initialized! Press play to start")
        telemetry.update()
    }

    override fun start() {
        pedro.start()
        actions.start()

        state = 1
        telemetry.addData("Status", "Starting autonomous sequence")
        telemetry.update()
    }

    override fun loop() {
        actions.update()
        pedro.update()

        telemetry.addData("Current State", state)

        when (state) {
            0 -> {} // INIT

            1 -> { // FOLLOW_PATH
                telemetry.addData("Action", "Following path")

                if (!pathStarted) {
                    pedro.reset()
                    pedro.followPath(SpecimenPaths.scorePreloadPath)
                    pathStarted = true
                } else if (pedro.isDone()) {
                    telemetry.addData("Status", "Path following complete!")
                    state++
                }
            }

            2 -> { // SCORE_SPECIMEN
                telemetry.addData("Action", "Starting specimen scoring")
                actions.scoreSpecimen()
                state++
            }

            3 -> { // WAIT_FOR_SCORE_COMPLETE
                telemetry.addData("Action", "Waiting for scoring to complete")

                if (actions.isDone()) {
                    telemetry.addData("Status", "Scoring complete!")
                    Delay.start(1)
                    state++
                }
            }

            4 -> { // DELAY1
                telemetry.addData("Action", "Delaying")

                if (Delay.isDone()) {
                    state++
                }
            }

            5 -> { // RESET_MECHANISMS
                telemetry.addData("Action", "Starting reset sequence")
                actions.returnToReset()
                state++
            }

            6 -> { // WAIT_FOR_RESET_COMPLETE
                telemetry.addData("Action", "Waiting for reset to complete")

                if (actions.isDone()) {
                    telemetry.addData("Status", "Reset complete!")
                    state++
                }
            }

            7 -> { // COMPLETE
                telemetry.addData("Status", "All actions complete!")
            }
        }

        telemetry.addData("Current Action", actions.getCurrentAction())
        telemetry.addData("Current Phase", actions.getCurrentPhase())
        telemetry.addData("Is Done", actions.isDone())
        telemetry.update()
    }
}