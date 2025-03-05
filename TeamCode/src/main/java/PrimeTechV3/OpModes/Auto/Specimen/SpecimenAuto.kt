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
    enum class AutoState {
        INIT,
        SCORE_SPECIMEN,
        WAIT_FOR_SCORE_COMPLETE,
        DELAY1,
        RESET_MECHANISMS,
        WAIT_FOR_RESET_COMPLETE,
        FOLLOW_PATH,
        COMPLETE
    }

    private var currentState = AutoState.INIT

    private lateinit var actions: Actions
    private lateinit var pedro: Pedro

    private var pathStarted = false

    private lateinit var scorePreloadPath: PathChain

    override fun init() {
        actions = Actions
        actions.init(hardwareMap)

        pedro = Pedro
        pedro.init(hardwareMap, Pose(0.0, 0.0, Math.toRadians(0.0)))

        scorePreloadPath = pedro.follower.pathBuilder()
            .addPath(BezierLine(Point(Pose(0.0, 0.0)), Point(Pose(1.0, 0.0))))
            .setConstantHeadingInterpolation(Math.toRadians(0.0))
            .build()

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
        pedro.update()

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
                    Delay.start(1)
                    currentState = AutoState.DELAY1
                }
            }

            AutoState.DELAY1 -> {
                telemetry.addData("Action", "Delaying")

                if (Delay.isDone()) {
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
                    currentState = AutoState.FOLLOW_PATH
                }
            }

            AutoState.FOLLOW_PATH -> {
                telemetry.addData("Action", "Following path")

                // Track if we need to start the path with a boolean variable
                if (!pathStarted) {
                    pedro.reset()
                    pedro.followPath(scorePreloadPath)
                    pathStarted = true
                }
                // Check if path is complete
                else if (pedro.isDone()) {
                    telemetry.addData("Status", "Path following complete!")
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