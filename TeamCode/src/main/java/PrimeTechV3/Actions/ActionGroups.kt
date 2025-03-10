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
        BEFORE_GRAB_SPECIMEN,
        GRAB_SPECIMEN,
        GRAB_SAMPLE,
        RESET_ALL
//        RESET_CLAW
//        RESET_LIFT,
//        RESET_EXTENSION
    }

    // region Declare States
    private var action: PossibleActions = PossibleActions.IDLE
    private var state = 0
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
        state = 0
    }

    fun setAction(setToAction: PossibleActions) {
        action = setToAction
        state = 1
    }

    fun update() {
        Lift.update()
        Pivot.update()

        when (action) {
            PossibleActions.IDLE -> {
                Claw.setClawPivot(Claw.ClawPivotState.BACK)
                Claw.setClawVertical(true)
                Claw.setClawOpen(false)
            }

            PossibleActions.BEFORE_SCORE_SPECIMEN -> {
                when (state) {
                    1 -> {
                        Claw.setClawPivot(Claw.ClawPivotState.SCORE_SPECIMEN)
                        Claw.setClawVertical(true)
                        Claw.setClawOpen(false)
                        state++
                    }

                    2 -> { // POSITIONING_CLAW
                        if (Claw.isDone()) {
//                            Lift.setLiftPosition(Lift.LiftPosition.BEFORE_SCORE_SPECIMEN)
                            Lift.setCustomLiftPosition(100)
                            state++
                        }
                    }

                    3 -> { // MOVING_LIFT_BEFORE
                        if (Lift.isDone()) {
                            Pivot.setPivotPosition(Pivot.PivotPosition.SCORE_SPECIMEN)
                            state++
                        }
                    }

                    4 -> { // MOVING_PIVOT
                        if (Pivot.isDone()) {
                            state = 0
                        }
                    }
                }
            }

            PossibleActions.SCORE_SPECIMEN -> {
                when (state) {
                    1 -> {
                        Lift.setLiftPosition(Lift.LiftPosition.SCORE_SPECIMEN)
                        state++
                    }

                    2 -> { // MOVING_LIFT
                        if (Lift.isDone()) {
                            Claw.setClawOpen(true)
                            state++
                        }
                    }

                    3 -> { // RESETTING_CLAW
                        if (Claw.isDone()) {
                            state = 0
                        }
                    }
                }
            }

            PossibleActions.BEFORE_SCORE_SAMPLE -> {}

            PossibleActions.SCORE_SAMPLE -> {
                when (state) {
                    1 -> { // STARTING
                        Claw.setClawPivot(Claw.ClawPivotState.SCORE_SAMPLE)
                        Claw.setClawVertical(true)
                        Claw.setClawOpen(false)
                        state = 2
                    }

                    2 -> { // POSITIONING_CLAW
                        if (Claw.isDone()) {
                            Pivot.setPivotPosition(Pivot.PivotPosition.SCORE_SAMPLE)
                            state = 3
                        }
                    }

                    3 -> { // MOVING_PIVOT
                        if (Pivot.isDone()) {
                            Lift.setLiftPosition(Lift.LiftPosition.SCORE_SAMPLE)
                            state = 4
                        }
                    }

                    4 -> { // MOVING_LIFT
                        if (Lift.isDone()) {
                            state = 0
                        }
                    }
                }
            }

            PossibleActions.BEFORE_GRAB_SPECIMEN -> {
                when (state) {
                    1 -> { // STARTING
                        Claw.setClawPivot(Claw.ClawPivotState.GRAB_SPECIMEN)
                        Claw.setClawVertical(true)
                        Claw.setClawOpen(true)
                        state++
                    }

                    2 -> { // POSITIONING_CLAW
                        if (Claw.isDone()) {
                            Lift.setLiftPosition(Lift.LiftPosition.LOW)
                            state++
                        }
                    }

                    3 -> { // MOVING_LIFT
                        if (Lift.isDone()) {
                            Pivot.setPivotPosition(Pivot.PivotPosition.GRAB_SPECIMEN)
                            state++
                        }
                    }

                    4 -> { // MOVING_PIVOT
                        if (Pivot.isDone()) {
                            state = 0
                        }
                    }
                }
            }

            PossibleActions.GRAB_SPECIMEN -> {
                when (state) {
                    1 -> { // STARTING
                        Lift.setLiftPosition(Lift.LiftPosition.LOAD_SPECIMEN)
                        state++
                    }

                    2 -> { // MOVING_LIFT
                        if (Lift.isDone()) {
                            Claw.closeClaw()
                            state++
                        }
                    }

                    3 -> { // POSITIONING_CLAW
                        if (Claw.isDone()) {
                            Claw.setClawPivot(Claw.ClawPivotState.BACK)
                            state++
                        }
                    }

                    4 -> {
                        if (Claw.isDone()) {
                            Lift.setLiftPosition(Lift.LiftPosition.LOW)
                            state++
                        }
                    }

                    5 -> { // POSITIONING_CLAW
                        if (Lift.isDone()) {
                            state = 0
                        }
                    }
                }
            }

            PossibleActions.GRAB_SAMPLE -> {
                when (state) {
                    1 -> { // STARTING
                        Claw.setClawPivot(Claw.ClawPivotState.GRAB_SAMPLE)
                        Claw.setClawVertical(false)
                        Claw.setClawOpen(true)
                        state++
                    }

                    2 -> { // POSITIONING_CLAW
                        if (Claw.isDone()) {
                            Pivot.setPivotPosition(Pivot.PivotPosition.LOW)
                            state++
                        }
                    }

                    3 -> { // MOVING_PIVOT
                        if (Pivot.isDone()) {
                            Lift.setLiftPosition(Lift.LiftPosition.LOAD_SAMPLE)
                            state++
                        }
                    }

                    4 -> { // MOVING_LIFT
                        if (Lift.isDone()) {
                            Claw.setClawOpen(false)
                        }
                    }

                    5 -> { // CLOSING_CLAW
                        if (Claw.isDone()) {
                            state = 0
                        }
                    }
                }
            }

            PossibleActions.RESET_ALL -> {
                when (state) {
                    1 -> { // STARTING
                        Claw.reset()
                        state++
                    }

                    2 -> { // POSITIONING_CLAW
                        if (Claw.isDone()) {
                            Lift.setLiftPosition(Lift.LiftPosition.LOW)
                            state++
                        }
                    }

                    3 -> { // MOVING_LIFT
                        if (Lift.isDone()) {
                            Pivot.setPivotPosition(Pivot.PivotPosition.LOW)
                            state++
                        }
                    }

                    4 -> { // MOVING_PIVOT
                        if (Pivot.isDone()) {
                            state = 0
                        }
                    }
                }
            }
        }
    }

    fun isDone(): Boolean {
        return state == 0
    }

    fun getCurrentAction(): PossibleActions {
        return action
    }

    fun getCurrentState(): Int {
        return state
    }
}