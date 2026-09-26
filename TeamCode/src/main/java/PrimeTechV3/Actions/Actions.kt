package PrimeTechV3.Actions

import PrimeTechV3.Components.Claw
import PrimeTechV3.Components.Lift
import PrimeTechV3.Components.Pivot
import com.qualcomm.robotcore.hardware.HardwareMap

/**
 * Sequences the arm components ([Claw], [Lift], [Pivot]) into whole jobs like
 * "score a specimen" or "pick a sample off the floor".
 *
 * ## Why it looks like this
 *
 * An FTC OpMode is not allowed to block. `loop()` is called over and over (roughly 50
 * times a second) and must return immediately each time, so you cannot write
 *
 * ```
 * raiseArm(); waitUntilArmIsUp(); openClaw();   // NOT possible here
 * ```
 *
 * Instead the job is cut into **steps**, and each call to [update] does one slice of work:
 * it looks at which step we are on, checks whether the hardware has caught up, and if so
 * starts the next step. This is a
 * [finite state machine](https://gm0.org/en/latest/docs/software/concepts/finite-state-machines.html).
 * The C++ analogue would be a coroutine written out by hand.
 *
 * Two variables track where we are:
 * - [currentAction] - which job we are doing (or [RobotAction.IDLE] for none).
 * - [currentStep] - how far into that job we are. [NOT_RUNNING] (0) means finished.
 *
 * ## Using it
 *
 * ```
 * Actions.startAction(Actions.RobotAction.SCORE_SPECIMEN)  // kick it off, returns at once
 * // ... then on every loop:
 * Actions.update()
 * if (Actions.isDone()) { /* the job finished */ }
 * ```
 */
object Actions {
    /** The jobs the arm knows how to do. "Before X" moves into position ready to do X. */
    enum class RobotAction {
        /** Nothing in progress; holds the arm in its safe carrying shape. */
        IDLE,

        /** Raise the arm and extend a little, ready to hang a specimen on the chamber. */
        BEFORE_SCORE_SPECIMEN,

        /** Extend onto the chamber bar and let go of the specimen. */
        SCORE_SPECIMEN,

        /** Swing over the basket, extend, and drop the sample in. */
        SCORE_SAMPLE,

        /** Reach down to the wall, ready to take a specimen off it. */
        BEFORE_GRAB_SPECIMEN,

        /** Close on the specimen at the wall and lift it clear. */
        GRAB_SPECIMEN,

        /** Turn the wrist sideways and pick a sample up off the floor. */
        GRAB_SAMPLE,

        /** Put everything back to its resting position. */
        RESET_ALL
    }

    /** The step number that means "no job in progress". See [isDone]. */
    private const val NOT_RUNNING = 0

    // region Current state
    /** The job currently being carried out. */
    var currentAction: RobotAction = RobotAction.IDLE
        private set

    /** How far into [currentAction] we are; [NOT_RUNNING] once it has finished. */
    var currentStep = NOT_RUNNING
        private set
    // endregion Current state

    /** Sets up every component. Call once from the OpMode's `init`. */
    fun init(hardwareMap: HardwareMap) {
        Claw.init(hardwareMap)
        Lift.init(hardwareMap)
        Pivot.init(hardwareMap)
    }

    /** Call once from the OpMode's `start`. */
    fun start() {
        reset()
    }

    fun reset() {
        currentAction = RobotAction.IDLE
        currentStep = NOT_RUNNING
    }

    /**
     * Begins [action] and returns immediately. The work happens in later [update] calls.
     *
     * This replaces whatever was running, mid-step - it does not queue behind it.
     */
    fun startAction(action: RobotAction) {
        currentAction = action
        currentStep = 1
    }

    /**
     * True when no job is in progress.
     *
     * **Careful:** this is also true *before* a job starts, because both states are
     * [NOT_RUNNING]. Calling [startAction] and testing [isDone] on the same loop will
     * report done immediately. The autonomous OpModes avoid this with their own
     * "have I started this step yet" flag - see `SpecimenAuto`.
     */
    fun isDone(): Boolean {
        return currentStep == NOT_RUNNING
    }

    /**
     * Runs one slice of the current job. Must be called every loop.
     *
     * Each numbered branch below is one step. A step either does something and moves on
     * straight away (`currentStep++`), or waits for a component to report `isDone()` first.
     * Setting `currentStep = NOT_RUNNING` ends the job.
     */
    fun update() {
        // The lift and pivot PIDs only run when these are called, so they come first and
        // happen on every loop regardless of which action (if any) is in progress.
        Lift.update()
        Pivot.update()

        when (currentAction) {
            RobotAction.IDLE -> {
                // Not a sequence: just keeps holding the safe carrying shape every loop.
                Claw.setClawPivot(Claw.ClawPivotState.BACK)
                Claw.rotateWristVertical()
                Claw.closeGrip()
            }

            RobotAction.BEFORE_SCORE_SPECIMEN -> when (currentStep) {
                // Point the claw at the chamber, holding the specimen.
                1 -> {
                    Claw.setClawPivot(Claw.ClawPivotState.SCORE_SPECIMEN)
                    Claw.rotateWristVertical()
                    Claw.closeGrip()
                    currentStep++
                }

                // Start extending. Deliberately does not wait for the claw servos first -
                // they can finish travelling while the lift is already on its way.
                2 -> {
                    Lift.setLiftPosition(Lift.LiftPosition.BEFORE_SCORE_SPECIMEN)
                    currentStep++
                }

                // Once extended, swing the arm up to the chamber.
                3 -> if (Lift.isDone()) {
                    Pivot.setPivotPosition(Pivot.PivotPosition.SCORE_SPECIMEN)
                    currentStep++
                }

                // Wait for the arm to arrive, then we are in position.
                4 -> if (Pivot.isDone()) currentStep = NOT_RUNNING
            }

            RobotAction.SCORE_SPECIMEN -> when (currentStep) {
                // Push the specimen onto the bar by extending further.
                1 -> {
                    Lift.setLiftPosition(Lift.LiftPosition.SCORE_SPECIMEN)
                    currentStep++
                }

                // Hooked on - let go.
                2 -> if (Lift.isDone()) {
                    Claw.openGrip()
                    currentStep++
                }

                // Give the grip time to open before the robot drives away.
                3 -> if (Claw.isDone()) currentStep = NOT_RUNNING
            }

            RobotAction.SCORE_SAMPLE -> when (currentStep) {
                // Tuck the claw forward, holding the sample.
                1 -> {
                    Claw.setClawPivot(Claw.ClawPivotState.FRONT)
                    Claw.rotateWristVertical()
                    Claw.closeGrip()
                    currentStep++
                }

                // Swing the arm up first, before extending - extending low would hit the basket.
                2 -> {
                    Pivot.setPivotPosition(Pivot.PivotPosition.SCORE_SAMPLE)
                    currentStep++
                }

                // Arm is up: now extend out over the basket.
                3 -> if (Pivot.isDone()) {
                    Lift.setLiftPosition(Lift.LiftPosition.SCORE_SAMPLE)
                    currentStep++
                }

                // Over the basket: tip the claw to point down into it.
                4 -> if (Lift.isDone()) {
                    Claw.setClawPivot(Claw.ClawPivotState.SCORE_SAMPLE)
                    currentStep++
                }

                // Drop the sample.
                5 -> if (Claw.isDone()) {
                    Claw.openGrip()
                    currentStep++
                }

                // Swing the empty claw back out of the basket.
                6 -> if (Claw.isDone()) {
                    Claw.setClawPivot(Claw.ClawPivotState.FRONT)
                    currentStep++
                }

                7 -> if (Claw.isDone()) currentStep = NOT_RUNNING
            }

            RobotAction.BEFORE_GRAB_SPECIMEN -> when (currentStep) {
                // Open the empty claw and aim it at the wall.
                1 -> {
                    Claw.setClawPivot(Claw.ClawPivotState.GRAB_SPECIMEN)
                    Claw.rotateWristVertical()
                    Claw.openGrip()
                    currentStep++
                }

                // Retract, again without waiting on the claw servos.
                2 -> {
                    Lift.setLiftPosition(Lift.LiftPosition.LOW)
                    currentStep++
                }

                // Retracted: drop the arm down to wall height.
                3 -> if (Lift.isDone()) {
                    Pivot.setPivotPosition(Pivot.PivotPosition.GRAB_SPECIMEN)
                    currentStep++
                }

                4 -> if (Pivot.isDone()) currentStep = NOT_RUNNING
            }

            RobotAction.GRAB_SPECIMEN -> when (currentStep) {
                // Reach in to the specimen sitting on the wall.
                1 -> {
                    Lift.setLiftPosition(Lift.LiftPosition.LOAD_SPECIMEN)
                    currentStep++
                }

                // In position - grab it.
                2 -> if (Lift.isDone()) {
                    Claw.closeGrip()
                    currentStep++
                }

                // Gripped: swing it back over the robot so it is carried safely.
                3 -> if (Claw.isDone()) {
                    Claw.setClawPivot(Claw.ClawPivotState.BACK)
                    currentStep++
                }

                // Pull the slide back in.
                4 -> if (Claw.isDone()) {
                    Lift.setLiftPosition(Lift.LiftPosition.LOW)
                    currentStep++
                }

                5 -> if (Lift.isDone()) currentStep = NOT_RUNNING
            }

            RobotAction.GRAB_SAMPLE -> when (currentStep) {
                // Open the claw and roll the wrist sideways to match a sample on the floor.
                1 -> {
                    Claw.setClawPivot(Claw.ClawPivotState.GRAB_SAMPLE)
                    Claw.rotateWristHorizontal()
                    Claw.openGrip()
                    currentStep++
                }

                // Lower the arm to the floor.
                2 -> if (Claw.isDone()) {
                    Pivot.setPivotPosition(Pivot.PivotPosition.LOW)
                    currentStep++
                }

                // Reach out over the sample.
                3 -> if (Pivot.isDone()) {
                    Lift.setLiftPosition(Lift.LiftPosition.LOAD_SAMPLE)
                    currentStep++
                }

                // KNOWN BUG, left as it was during the season: this step never advances.
                // Every other step ends with `currentStep++` or `currentStep = NOT_RUNNING`;
                // this one has neither, so GRAB_SAMPLE closes the grip and then hangs here
                // forever. No 2024-25 auto called GRAB_SAMPLE, so it never showed up at a
                // competition. To fix it, add a step 5 that waits for `Claw.isDone()` and
                // then sets `currentStep = NOT_RUNNING` - and test it on the robot.
                4 -> if (Lift.isDone()) {
                    Claw.closeGrip()
                }
            }

            RobotAction.RESET_ALL -> when (currentStep) {
                // Claw first: get it into its safe shape before anything big moves.
                1 -> {
                    Claw.reset()
                    currentStep++
                }

                // Then retract the slide.
                2 -> if (Claw.isDone()) {
                    Lift.setLiftPosition(Lift.LiftPosition.LOW)
                    currentStep++
                }

                // Only once retracted, lower the arm - dropping it extended would crash it.
                3 -> if (Lift.isDone()) {
                    Pivot.setPivotPosition(Pivot.PivotPosition.LOW)
                    currentStep++
                }

                4 -> if (Pivot.isDone()) currentStep = NOT_RUNNING
            }
        }
    }
}
