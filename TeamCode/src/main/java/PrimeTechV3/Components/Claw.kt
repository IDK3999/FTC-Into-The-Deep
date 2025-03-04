package PrimeTechV3.Components

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

object Claw {
    // region Declare Components
    private lateinit var openingServo: Servo
    private lateinit var pivotServo: Servo
    private lateinit var rotationServo: Servo
    // endregion Declare Components

    // region Declare Positions
    private val openPosition = 0.9
    private val closedPosition = 0.1

    private val pivotFrontPosition = 0.0
    private val pivotBackPosition = 1.0
    private val pivotScoreSpecimenPosition = 0.65
    private val pivotGrabSpecimenPosition = 0.6
    private val pivotScoreSamplePosition = 0.9
    private val pivotGrabSamplePosition = 0.1

    private val rotationVertical = 0.5
    private val rotationHorizontal = 0.16
    // endregion Declare Positions

    // region Declare States
    var openState: ClawOpenState = ClawOpenState.CLOSED
        private set
    var rotationState: ClawRotationState = ClawRotationState.VERTICAL
        private set
    var pivotState: ClawPivotState = ClawPivotState.BACK
        private set
    // endregion Declare States

    fun init(hardwareMap: HardwareMap) {
        openingServo = hardwareMap.get(Servo::class.java, "openingServo")
        rotationServo = hardwareMap.get(Servo::class.java, "rotationServo")
        pivotServo = hardwareMap.get(Servo::class.java, "frontBackServoRight")
    }

    fun start() {
        this.reset()
    }

    fun reset() {
        openState = ClawOpenState.CLOSED
        rotationState = ClawRotationState.VERTICAL
        pivotState = ClawPivotState.BACK

        openingServo.position = closedPosition
        rotationServo.position = rotationVertical
        pivotServo.position = pivotBackPosition
    }

    fun setClawOpen(boolean: Boolean) {
        if (boolean) {
            openClaw()
        } else {
            closeClaw()
        }
    }

    fun isDone(): Boolean {
        return true;
    }

    fun setClawVertical(boolean: Boolean) {
        if (boolean) {
            rotateClawVertical()
        } else {
            rotateClawHorizontal()
        }
    }

    fun setClawPivot(pivotState: ClawPivotState) {
        when(pivotState) {
            ClawPivotState.FRONT -> pivotClawFront()
            ClawPivotState.BACK -> pivotClawBack()
            ClawPivotState.SCORE_SPECIMEN -> pivotClawScoreSpecimen()
            ClawPivotState.GRAB_SPECIMEN -> pivotClawGrabSpecimen()
            ClawPivotState.SCORE_SAMPLE -> pivotClawScoreSample()
            ClawPivotState.GRAB_SAMPLE -> pivotClawGrabSample()
        }
    }

    fun openClaw() {
        openingServo.position = openPosition
        openState = ClawOpenState.OPEN
    }

    fun closeClaw() {
        openingServo.position = closedPosition
        openState = ClawOpenState.CLOSED
    }

    fun rotateClawVertical() {
        rotationServo.position = rotationVertical
        rotationState = ClawRotationState.VERTICAL
    }

    fun rotateClawHorizontal() {
        rotationServo.position = rotationHorizontal
        rotationState = ClawRotationState.HORIZONTAL
    }

    fun pivotClawFront() {
        pivotServo.position = pivotFrontPosition
        pivotState = ClawPivotState.FRONT
    }

    fun pivotClawBack() {
        pivotServo.position = pivotBackPosition
        pivotState = ClawPivotState.BACK
    }

    fun pivotClawScoreSpecimen() {
        pivotServo.position = pivotScoreSpecimenPosition
        pivotState = ClawPivotState.SCORE_SPECIMEN
    }

    fun pivotClawGrabSpecimen() {
        pivotServo.position = pivotGrabSpecimenPosition
        pivotState = ClawPivotState.GRAB_SPECIMEN
    }

    fun pivotClawScoreSample() {
        pivotServo.position = pivotScoreSamplePosition
        pivotState = ClawPivotState.SCORE_SAMPLE
    }

    fun pivotClawGrabSample() {
        pivotServo.position = pivotGrabSamplePosition
        pivotState = ClawPivotState.GRAB_SAMPLE
    }

    enum class ClawRotationState {
        VERTICAL, HORIZONTAL
    }

    enum class ClawOpenState{
        OPEN, CLOSED
    }

    enum class ClawPivotState{
        FRONT, BACK, SCORE_SPECIMEN, GRAB_SPECIMEN, SCORE_SAMPLE, GRAB_SAMPLE
    }
}