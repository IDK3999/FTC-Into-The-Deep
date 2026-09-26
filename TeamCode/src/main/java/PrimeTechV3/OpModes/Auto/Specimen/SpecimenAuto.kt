package PrimeTechV3.OpModes.Auto.Specimen

import PrimeTechV3.Actions.Actions
import PrimeTechV3.Components.Claw
import PrimeTechV3.Components.Delay
import PrimeTechV3.Components.Lift
import PrimeTechV3.Components.Pedro
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode

/**
 * Autonomous for the specimen (right-hand) side of the field.
 *
 * The plan, in order:
 * 1. Hang the preloaded specimen on the chamber.
 * 2. Collect a specimen from the wall and hang that too.
 * 3. Push the three neutral samples out of the middle into our observation zone, so the
 *    human player can turn them into specimens for us.
 * 4. Collect and hang two more specimens from the wall.
 *
 * ## How the sequence works
 *
 * `loop()` is called repeatedly and must return immediately, so the run is written as a
 * list of numbered **steps** in a `when` block. [step] says which one we are on.
 *
 * Every step has the same shape - start something, then wait for it to finish - so that
 * shape lives in [runStep] instead of being written out 27 times. Read each step as:
 * "*begin* this, and move on once *isFinished*".
 */
@Autonomous(name = "Specimen")
class SpecimenAuto : OpMode() {
    /** Which step of the plan we are on. 0 means the run is over. */
    private var step = 0

    /** Whether the current step's `begin` block has already fired. See [runStep]. */
    private var stepStarted = false

    /** Short pause to let the robot stop rocking before the arm pushes the specimen on. */
    private val settleDelaySeconds = 0.05

    /** Pause at the wall, so the specimen is fully seated before the claw closes. */
    private val grabDelaySeconds = 0.2

    override fun init() {
        Actions.init(hardwareMap)
        Pedro.init(hardwareMap, SpecimenPaths.start)

        SpecimenPaths.build(Pedro.follower)
    }

    override fun start() {
        Pedro.start()
        Actions.start()

        step = 1
    }

    override fun loop() {
        // The components only move when these are called, so they run every loop no matter
        // which step we are on.
        Actions.update()
        Pedro.update()

        telemetry.addData("Auto step", step)
        telemetry.addData("Action", Actions.currentAction)
        telemetry.addData("Action step", Actions.currentStep)

        when (step) {
            0 -> {} // Run finished; nothing left to do.

            // --- Preloaded specimen ---------------------------------------------------
            // Drive to the chamber while raising the arm, so both finish at the same time.
            1 -> runStep(
                begin = {
                    Claw.closeGrip()
                    Actions.startAction(Actions.RobotAction.BEFORE_SCORE_SPECIMEN)
                    Pedro.followPath(SpecimenPaths.scorePreloadPath)
                },
                isFinished = { Actions.isDone() && Pedro.isDone() }
            )

            2 -> runStep(
                begin = { Delay.start(settleDelaySeconds) },
                isFinished = { Delay.isDone() }
            )

            3 -> runStep(
                begin = { Actions.startAction(Actions.RobotAction.SCORE_SPECIMEN) },
                isFinished = { Actions.isDone() }
            )

            // --- Second specimen, off the wall ----------------------------------------
            4 -> runStep(
                begin = { Actions.startAction(Actions.RobotAction.BEFORE_GRAB_SPECIMEN) },
                isFinished = { Actions.isDone() }
            )

            5 -> runStep(
                begin = { Pedro.followPath(SpecimenPaths.loadSpecimen2Path) },
                isFinished = { Pedro.isDone() }
            )

            6 -> runStep(
                begin = { Delay.start(grabDelaySeconds) },
                isFinished = { Delay.isDone() }
            )

            7 -> runStep(
                begin = { Actions.startAction(Actions.RobotAction.GRAB_SPECIMEN) },
                isFinished = { Actions.isDone() }
            )

            // Re-zero the lift against its hard stop. Encoder counts drift as the match
            // goes on, and every later height is measured from this zero.
            8 -> runStep(
                begin = { Lift.resetEncoders() },
                isFinished = { Lift.isAtTarget() }
            )

            9 -> runStep(
                begin = {
                    Actions.startAction(Actions.RobotAction.BEFORE_SCORE_SPECIMEN)
                    Pedro.followPath(SpecimenPaths.scoreSpecimen2Path)
                },
                isFinished = { Pedro.isDone() }
            )

            // A short nudge straight into the chamber, separate from the approach path so
            // the robot arrives square to the bar before pushing.
            10 -> runStep(
                begin = { Pedro.followPath(SpecimenPaths.scoreSpecimen2PushPath) },
                isFinished = { Actions.isDone() && Pedro.isDone() }
            )

            11 -> runStep(
                begin = { Delay.start(settleDelaySeconds) },
                isFinished = { Delay.isDone() }
            )

            12 -> runStep(
                begin = { Actions.startAction(Actions.RobotAction.SCORE_SPECIMEN) },
                isFinished = { Actions.isDone() }
            )

            // --- Push the three neutral samples into the observation zone --------------
            // "driveBehind" gets the robot on the far side of a sample; "push" then shoves
            // it back towards the wall. The arm drops to grabbing height at the same time,
            // ready for the next wall pickup.
            13 -> runStep(
                begin = {
                    Actions.startAction(Actions.RobotAction.BEFORE_GRAB_SPECIMEN)
                    Pedro.followPath(SpecimenPaths.driveBehindSample1Path)
                },
                isFinished = { Actions.isDone() && Pedro.isDone() }
            )

            14 -> runStep(
                begin = { Pedro.followPath(SpecimenPaths.pushSample1Path) },
                isFinished = { Pedro.isDone() }
            )

            15 -> runStep(
                begin = { Pedro.followPath(SpecimenPaths.driveBehindSample2Path) },
                isFinished = { Pedro.isDone() }
            )

            16 -> runStep(
                begin = { Pedro.followPath(SpecimenPaths.pushSample2Path) },
                isFinished = { Pedro.isDone() }
            )

            // --- Third specimen -------------------------------------------------------
            17 -> runStep(
                begin = { Pedro.followPath(SpecimenPaths.loadSpecimen3Path) },
                isFinished = { Pedro.isDone() }
            )

            18 -> runStep(
                begin = { Delay.start(grabDelaySeconds) },
                isFinished = { Delay.isDone() }
            )

            19 -> runStep(
                begin = { Actions.startAction(Actions.RobotAction.GRAB_SPECIMEN) },
                isFinished = { Actions.isDone() }
            )

            20 -> runStep(
                begin = { Lift.resetEncoders() },
                isFinished = { Lift.isAtTarget() }
            )

            21 -> runStep(
                begin = {
                    Actions.startAction(Actions.RobotAction.BEFORE_SCORE_SPECIMEN)
                    Pedro.followPath(SpecimenPaths.scoreSpecimen3Path)
                },
                isFinished = { Pedro.isDone() }
            )

            22 -> runStep(
                begin = { Pedro.followPath(SpecimenPaths.scoreSpecimen3PushPath) },
                isFinished = { Actions.isDone() && Pedro.isDone() }
            )

            23 -> runStep(
                begin = { Delay.start(settleDelaySeconds) },
                isFinished = { Delay.isDone() }
            )

            24 -> runStep(
                begin = { Actions.startAction(Actions.RobotAction.SCORE_SPECIMEN) },
                isFinished = { Actions.isDone() }
            )

            // --- Fourth specimen ------------------------------------------------------
            // Collected, but never scored: the run did not fit in the 30 second autonomous
            // period. The paths to hang it (scoreSpecimen4Path, scoreSpecimen4PushPath) are
            // still built and tuned in SpecimenPaths, so finishing this off means adding
            // steps 28-31 in the same shape as steps 20-24 above.
            25 -> runStep(
                begin = {
                    Actions.startAction(Actions.RobotAction.BEFORE_GRAB_SPECIMEN)
                    Pedro.followPath(SpecimenPaths.loadSpecimen4Path)
                },
                isFinished = { Actions.isDone() && Pedro.isDone() }
            )

            26 -> runStep(
                begin = { Delay.start(grabDelaySeconds) },
                isFinished = { Delay.isDone() }
            )

            27 -> runStep(
                begin = { Actions.startAction(Actions.RobotAction.GRAB_SPECIMEN) },
                isFinished = { Actions.isDone() }
            )

            else -> step = 0 // Ran off the end of the list: stop.
        }

        telemetry.update()
    }

    /**
     * Runs one step of the plan, spread over as many loops as it takes.
     *
     * On the first loop of a step, [begin] fires - it starts a path or an action and
     * returns straight away. On every loop after that we ask [isFinished]; when it says
     * yes we move on to the next step.
     *
     * [begin] is deliberately not called again while we wait, which is what [stepStarted]
     * is for. Calling `followPath` every loop would restart the path from the beginning
     * over and over and the robot would never get anywhere.
     */
    private fun runStep(begin: () -> Unit, isFinished: () -> Boolean) {
        if (!stepStarted) {
            begin()
            stepStarted = true
        } else if (isFinished()) {
            stepStarted = false
            step++
        }
    }
}
