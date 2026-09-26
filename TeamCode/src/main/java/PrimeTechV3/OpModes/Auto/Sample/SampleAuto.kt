package PrimeTechV3.OpModes.Auto.Sample

import PrimeTechV3.Actions.Actions
import PrimeTechV3.Components.Claw
import PrimeTechV3.Components.Pedro
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode

/**
 * Autonomous for the basket (left-hand) side of the field.
 *
 * **This one was never finished.** It drives up to the basket and then parks, without
 * scoring anything - steps 2 and 3 are empty placeholders where the scoring would go.
 * It is kept because the drive paths are tuned and working, which is most of the job;
 * see [PrimeTechV3.OpModes.Auto.Specimen.SpecimenAuto] for a complete routine to copy
 * the scoring steps from.
 *
 * The step structure is the same as `SpecimenAuto` - see [runStep].
 */
@Autonomous(name = "Sample")
class SampleAuto : OpMode() {
    /** Which step of the plan we are on. 0 means the run is over. */
    private var step = 0

    /** Whether the current step's `begin` block has already fired. */
    private var stepStarted = false

    /** Capped speed on the way to the basket - slower, because this one has to be accurate. */
    private val approachSpeed = 0.7

    /** Capped speed on the way to park; accuracy matters less, so it can go faster. */
    private val parkSpeed = 0.8

    override fun init() {
        Actions.init(hardwareMap)
        Pedro.init(hardwareMap, SamplePaths.start)

        SamplePaths.build(Pedro.follower)
    }

    override fun start() {
        Pedro.start()
        Actions.start()

        step = 1
    }

    override fun loop() {
        Actions.update()
        Pedro.update()

        telemetry.addData("Auto step", step)

        when (step) {
            0 -> {} // Run finished.

            // Drive from the wall up to the basket, holding onto the preloaded sample.
            1 -> runStep(
                begin = {
                    Claw.closeGrip()
                    Pedro.followPath(SamplePaths.scorePreloadPath, approachSpeed)
                },
                isFinished = { Pedro.isDone() }
            )

            // Placeholders: this is where raising the arm and dropping the sample into the
            // basket belongs. They do nothing but fall through to the next step.
            2, 3 -> step++

            // Park out by the submersible.
            4 -> runStep(
                begin = {
                    Claw.closeGrip()
                    Pedro.followPath(SamplePaths.parkPath, parkSpeed)
                },
                isFinished = { Pedro.isDone() }
            )

            else -> step = 0
        }

        telemetry.update()
    }

    /**
     * Runs one step of the plan, spread over as many loops as it takes: [begin] fires on
     * the first loop only, then we wait until [isFinished] says we can move on.
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
