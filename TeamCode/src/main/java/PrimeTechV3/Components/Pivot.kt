package PrimeTechV3.Components

import com.arcrobotics.ftclib.controller.PIDController
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap

object Pivot {
    // region Declare Components
    private lateinit var pivotMotor: DcMotorEx

    private val p = 0.0018
    private val i = 0.035
    private val d = 0.0002
    private val tolerance = 20.0
    private lateinit var controller: PIDController

    private var target = 0.0
    // endregion Declare Components

    private val positions = mapOf(
        PivotPosition.LOW to 0.0,
        PivotPosition.SCORE_SPECIMEN to 2050.0,
        PivotPosition.SCORE_SAMPLE to 2100.0,
        PivotPosition.GRAB_SPECIMEN to 250.0
    )

    // region Declare States
    private var position: PivotPosition = PivotPosition.LOW
    private var state: PivotState = PivotState.IDLE
    // endregion Declare States

    fun init(hardwareMap: HardwareMap) {
        controller = PIDController(p, i, d)

        pivotMotor = hardwareMap.get(DcMotorEx::class.java, "motorPivot")

        pivotMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT

        pivotMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

        pivotMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        controller.setTolerance(tolerance)
    }

    fun start() {
        this.reset()
    }

    fun reset() {
        setPivotPosition(PivotPosition.LOW)
    }

    fun isDone(): Boolean {
        return this.isAtTarget()
    }

    fun setPivotPosition(position: PivotPosition) {
        this.position = position
        target = positions[position] ?: 0.0
        controller.setPoint = target
        state = PivotState.MOVING
    }

    fun setCustomPivotPosition(position: Int) {
        this.position = PivotPosition.CUSTOM
        target = position.toDouble()
        controller.setPoint = target
        state = PivotState.MOVING
    }

    fun update() {
        val currentPosition = pivotMotor.currentPosition.toDouble()
        val power = controller.calculate(currentPosition)
        val clampedPower = power.coerceIn(-1.0, 1.0)

        pivotMotor.power = clampedPower

        if (state == PivotState.MOVING && controller.atSetPoint())
            state = PivotState.IDLE
    }

    fun isAtTarget(): Boolean {
        return state == PivotState.IDLE
    }

    enum class PivotPosition {
        LOW,
        SCORE_SPECIMEN,
        SCORE_SAMPLE,
        GRAB_SPECIMEN,
        CUSTOM
    }

    enum class PivotState {
        IDLE, MOVING
    }
}