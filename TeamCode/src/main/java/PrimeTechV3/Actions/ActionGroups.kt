package PrimeTechV3.Actions

import PrimeTechV3.Components.Claw
import PrimeTechV3.Components.Lift
import PrimeTechV3.Components.Pivot
import com.qualcomm.robotcore.hardware.HardwareMap

object Actions {
    enum class PossibleActions {
        IDLE,
        BEFORE_SCORE_SPECIMEN,
        SCORE_SPECIMEN,
        BEFORE_SCORE_SAMPLE,
        SCORE_SAMPLE,
        GRAB_SPECIMEN,
        GRAB_SAMPLE,
        RESET_ALL
//        RESET_CLAW
//        RESET_LIFT,
//        RESET_EXTENSION
    }

    enum class ActionPhase {
        STARTING,
        MOVING_PIVOT,
        MOVING_LIFT,
        POSITIONING_CLAW,
        FINISHED
    }

    // region Declare States
    private var action: PossibleActions = PossibleActions.IDLE
    private var state: ActionPhase = ActionPhase.FINISHED
    // endregion Declare States

    fun init(hardwareMap: HardwareMap) {
        Claw.init(hardwareMap)
        Lift.init(hardwareMap)
        Pivot.init(hardwareMap)
    }

    fun start() {
        reset()
    }

    fun reset() {
        action = PossibleActions.IDLE
        state = ActionPhase.FINISHED
    }

    fun scoreSpecimen() {
        action = PossibleActions.SCORE_SPECIMEN
        state = ActionPhase.STARTING
    }

    fun scoreSample() {
        action = PossibleActions.SCORE_SAMPLE
        state = ActionPhase.STARTING
    }

    fun grabSpecimen() {
        action = PossibleActions.GRAB_SPECIMEN
        state = ActionPhase.STARTING
    }

    fun grabSample() {
        action = PossibleActions.GRAB_SAMPLE
        state = ActionPhase.STARTING
    }

    fun returnToReset() {
        action = PossibleActions.RESET_ALL
        state = ActionPhase.STARTING
    }

    fun update() {
        when (action) {
            PossibleActions.IDLE -> {
                // Do nothing
            }

            PossibleActions.BEFORE_SCORE_SPECIMEN -> {}

            PossibleActions.SCORE_SPECIMEN -> {
                when (state) {
                    ActionPhase.STARTING -> {
                        Pivot.setPivotPosition(Pivot.PivotPosition.SCORE_SPECIMEN)
                        state = ActionPhase.MOVING_PIVOT
                    }
                    ActionPhase.MOVING_PIVOT -> {
                        if (Pivot.isDone()) {
                            Lift.setLiftPosition(Lift.LiftPosition.SCORE_SPECIMEN)
                            state = ActionPhase.MOVING_LIFT
                        }
                    }
                    ActionPhase.MOVING_LIFT -> {
                        if (Lift.isDone()) {
                            Claw.setClawPivot(Claw.ClawPivotState.SCORE_SPECIMEN)
                            Claw.setClawVertical(true)
                            state = ActionPhase.POSITIONING_CLAW
                        }
                    }
                    ActionPhase.POSITIONING_CLAW -> {
                        if (Claw.isDone()) {
                            state = ActionPhase.FINISHED
                        }
                    }
                    else -> {}
                }
            }

            PossibleActions.BEFORE_SCORE_SAMPLE -> {}

            PossibleActions.SCORE_SAMPLE -> {
                when (state) {
                    ActionPhase.STARTING -> {
                        Pivot.setPivotPosition(Pivot.PivotPosition.SCORE_SAMPLE)
                        state = ActionPhase.MOVING_PIVOT
                    }
                    ActionPhase.MOVING_PIVOT -> {
                        if (Pivot.isDone()) {
                            Lift.setLiftPosition(Lift.LiftPosition.SCORE_SAMPLE)
                            state = ActionPhase.MOVING_LIFT
                        }
                    }
                    ActionPhase.MOVING_LIFT -> {
                        if (Lift.isDone()) {
                            Claw.setClawPivot(Claw.ClawPivotState.SCORE_SAMPLE)
                            Claw.setClawVertical(true)
                            state = ActionPhase.POSITIONING_CLAW
                        }
                    }
                    ActionPhase.POSITIONING_CLAW -> {
                        if (Claw.isDone()) {
                            state = ActionPhase.FINISHED
                        }
                    }
                    else -> {}
                }
            }

            PossibleActions.GRAB_SPECIMEN -> {
                when (state) {
                    ActionPhase.STARTING -> {
                        Pivot.setPivotPosition(Pivot.PivotPosition.GRAB_SPECIMEN)
                        state = ActionPhase.MOVING_PIVOT
                    }
                    ActionPhase.MOVING_PIVOT -> {
                        if (Pivot.isDone()) {
                            Lift.setLiftPosition(Lift.LiftPosition.LOAD_SPECIMEN)
                            state = ActionPhase.MOVING_LIFT
                        }
                    }
                    ActionPhase.MOVING_LIFT -> {
                        if (Lift.isDone()) {
                            Claw.setClawPivot(Claw.ClawPivotState.GRAB_SPECIMEN)
                            Claw.setClawVertical(false)
                            state = ActionPhase.POSITIONING_CLAW
                        }
                    }
                    ActionPhase.POSITIONING_CLAW -> {
                        if (Claw.isDone()) {
                            state = ActionPhase.FINISHED
                        }
                    }
                    else -> {}
                }
            }

            PossibleActions.GRAB_SAMPLE -> {
                when (state) {
                    ActionPhase.STARTING -> {
                        Pivot.setPivotPosition(Pivot.PivotPosition.LOW)
                        state = ActionPhase.MOVING_PIVOT
                    }
                    ActionPhase.MOVING_PIVOT -> {
                        if (Pivot.isDone()) {
                            Lift.setLiftPosition(Lift.LiftPosition.LOAD_SAMPLE)
                            state = ActionPhase.MOVING_LIFT
                        }
                    }
                    ActionPhase.MOVING_LIFT -> {
                        if (Lift.isDone()) {
                            Claw.setClawPivot(Claw.ClawPivotState.GRAB_SAMPLE)
                            Claw.setClawVertical(false)
                            state = ActionPhase.POSITIONING_CLAW
                        }
                    }
                    ActionPhase.POSITIONING_CLAW -> {
                        if (Claw.isDone()) {
                            state = ActionPhase.FINISHED
                        }
                    }
                    else -> {}
                }
            }

            PossibleActions.RESET_ALL -> {
                when (state) {
                    ActionPhase.STARTING -> {
                        Claw.closeClaw()
                        state = ActionPhase.POSITIONING_CLAW
                    }
                    ActionPhase.POSITIONING_CLAW -> {
                        if (Claw.isDone()) {
                            Lift.setLiftPosition(Lift.LiftPosition.LOW)
                            state = ActionPhase.MOVING_LIFT
                        }
                    }
                    ActionPhase.MOVING_LIFT -> {
                        if (Lift.isDone()) {
                            Pivot.setPivotPosition(Pivot.PivotPosition.LOW)
                            state = ActionPhase.MOVING_PIVOT
                        }
                    }
                    ActionPhase.MOVING_PIVOT -> {
                        if (Pivot.isDone()) {
                            state = ActionPhase.FINISHED
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun isDone(): Boolean {
        return state == ActionPhase.FINISHED
    }

    fun getCurrentAction(): PossibleActions {
        return action
    }

    fun getCurrentPhase(): ActionPhase {
        return state
    }
}