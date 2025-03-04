package PrimeTechV3.OpModes.Auto.Specimen

import PrimeTechV3.Actions.Actions
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode

@Autonomous(name = "Specimen Auto")
class SpecimenAuto : OpMode() {
    enum class AutoState {
        INIT,
        SCORE_SPECIMEN,
        WAIT_FOR_SCORE_COMPLETE,
        RESET_MECHANISMS,
        WAIT_FOR_RESET_COMPLETE,
        COMPLETE
    }

    private var currentState = AutoState.INIT

    private lateinit var actions: Actions

    override fun init() {
        actions = Actions
        actions.init(hardwareMap)

        telemetry.addData("Status", "Initialized! Press play to start")
        telemetry.update()
    }

    override fun start() {
        actions.start()

        currentState = AutoState.SCORE_SPECIMEN
        telemetry.addData("Status", "Starting autonomous sequence")
        telemetry.update()
    }

    override fun loop() {
        actions.update()

        telemetry.addData("Current State", currentState)

        when (currentState) {
            AutoState.INIT -> {}

            AutoState.SCORE_SPECIMEN -> {
                telemetry.addData("Action", "Starting specimen scoring")

                actions.scoreSpecimen()

                currentState = AutoState.WAIT_FOR_SCORE_COMPLETE
            }

            AutoState.WAIT_FOR_SCORE_COMPLETE -> {
                telemetry.addData("Action", "Waiting for scoring to complete")

                if (actions.isDone()) {
                    telemetry.addData("Status", "Scoring complete!")
                    currentState = AutoState.RESET_MECHANISMS
                }
            }

            AutoState.RESET_MECHANISMS -> {
                telemetry.addData("Action", "Starting reset sequence")

                actions.returnToReset()

                currentState = AutoState.WAIT_FOR_RESET_COMPLETE
            }

            AutoState.WAIT_FOR_RESET_COMPLETE -> {
                telemetry.addData("Action", "Waiting for reset to complete")

                if (actions.isDone()) {
                    telemetry.addData("Status", "Reset complete!")
                    currentState = AutoState.COMPLETE
                }
            }

            AutoState.COMPLETE -> {
                telemetry.addData("Status", "All actions complete!")
            }
        }

        telemetry.addData("Current Action", actions.getCurrentAction())
        telemetry.addData("Current Phase", actions.getCurrentPhase())
        telemetry.addData("Is Done", actions.isDone())
        telemetry.update()
    }
}