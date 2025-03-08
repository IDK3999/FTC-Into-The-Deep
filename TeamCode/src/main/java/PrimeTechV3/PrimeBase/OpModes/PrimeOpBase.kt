package PrimeTechV3.PrimeBase.OpModes

import PrimeTechV3.Actions.Actions
import com.qualcomm.robotcore.eventloop.opmode.OpMode

abstract class PrimeOpBase : OpMode() {
    private val actions = Actions

    override fun init() {
        actions.init(hardwareMap)
        onInit()
    }

    abstract fun onInit()

    override fun start() {
        actions.start()
        onStart()
    }

    abstract fun onStart()

    override fun loop() {
        actions.update()
        onLoop()

        telemetry.addData("Current Action", actions.getCurrentAction())
        telemetry.addData("Current Phase", actions.getCurrentState())
        telemetry.update()
    }

    abstract fun onLoop()

    override fun stop() {
        onStop()
    }

    abstract fun onStop()

    protected fun waitForActionCompletion() = actions.isDone()
}