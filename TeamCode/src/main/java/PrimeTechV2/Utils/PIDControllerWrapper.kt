package PrimeTechV2.Utils

import com.arcrobotics.ftclib.controller.PIDController
import com.rowanmcalpin.nextftc.core.control.controllers.Controller

class PIDControllerWrapper(
    val ftcLibController: PIDController,
    override var setPointTolerance: Double
) : Controller {
    override var target: Double
        get() = ftcLibController.setPoint
        set(value) {
            ftcLibController.setPoint = value
        }

    override fun calculate(reference: Double): Double {
        return ftcLibController.calculate(reference)
    }

    override fun reset() {
        ftcLibController.reset()
    }
}