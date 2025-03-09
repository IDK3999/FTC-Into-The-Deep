package PrimeTechV3.Components

import com.arcrobotics.ftclib.controller.PIDController
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

object Lift {
    // region Declare Components
    private lateinit var liftMotorLeft: DcMotorEx
    private lateinit var liftMotorRight: DcMotorEx

    private val p = 0.012
    private val i = 0.12
    private val d = 0.000287
    private val tolerance = 35.0
    private lateinit var controller: PIDController

    private var target = 0.0
    // endregion Declare Components

    private val positions = mapOf(
        LiftPosition.LOW to 0.0,
        LiftPosition.BEFORE_SCORE_SPECIMEN to 270.0,
        LiftPosition.SCORE_SPECIMEN to 330.0,
        LiftPosition.SCORE_SAMPLE to 600.0,
        LiftPosition.LOAD_SPECIMEN to 100.0,
        LiftPosition.LOAD_SAMPLE to 100.0
    )

    // region Declare States
    private var position: LiftPosition = LiftPosition.LOW
    private var state: LiftState = LiftState.IDLE
    // endregion Declare States

    fun init(hardwareMap: HardwareMap) {
        controller = PIDController(p, i, d)

        liftMotorLeft = hardwareMap.get(DcMotorEx::class.java, "extensionLeft")
        liftMotorRight = hardwareMap.get(DcMotorEx::class.java, "extensionRight")

        liftMotorLeft.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        liftMotorRight.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT

        liftMotorLeft.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        liftMotorRight.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        liftMotorLeft.direction = DcMotorSimple.Direction.REVERSE

        controller.setTolerance(tolerance)
    }

    fun start() {
        this.reset()
    }

    fun reset() {
        setLiftPosition(LiftPosition.LOW)
    }

    fun isDone(): Boolean {
        return this.isAtTarget()
    }

    fun setLiftPosition(position: LiftPosition) {
        this.position = position
        target = positions[position] ?: 0.0
        controller.setPoint = target
        state = LiftState.MOVING
    }

    fun setCustomLiftPosition(position: Int) {
        this.position = LiftPosition.CUSTOM
        target = position.toDouble()
        controller.setPoint = target
        state = LiftState.MOVING
    }

    fun update() {
        val currentPosition = liftMotorRight.currentPosition.toDouble()
        val power = controller.calculate(currentPosition)
        val clampedPower = power.coerceIn(-1.0, 1.0)

        liftMotorLeft.power = clampedPower
        liftMotorRight.power = clampedPower

        if (state == LiftState.MOVING && controller.atSetPoint())
            state = LiftState.IDLE
    }

    fun isAtTarget(): Boolean {
        return state == LiftState.IDLE
    }

    enum class LiftPosition {
        LOW,
        BEFORE_SCORE_SPECIMEN,
        SCORE_SPECIMEN,
        SCORE_SAMPLE,
        LOAD_SPECIMEN,
        LOAD_SAMPLE,
        CUSTOM
    }

    enum class LiftState {
        IDLE, MOVING
    }
}