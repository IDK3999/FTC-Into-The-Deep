package PrimeTechV3.OpModes.Auto.Specimen

import PrimeTechV3.Actions.Actions
import PrimeTechV3.Components.Pedro
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode

@Autonomous(name = "Specimen V3")
class SpecimenAuto : OpMode() {
    private var state = 0

    private lateinit var actions: Actions
    private lateinit var pedro: Pedro

    private var actionStarted = false

    override fun init() {
        actions = Actions
        actions.init(hardwareMap)

        pedro = Pedro
        pedro.init(hardwareMap, SpecimenPaths.start)

        SpecimenPaths.build(pedro.follower)
    }

    override fun start() {
        pedro.start()
        actions.start()

        state = 1
    }

    override fun loop() {
        actions.update()
        pedro.update()

        telemetry.addData("Current State", state)

        when (state) {
            0 -> {}

            1 -> {
                if (!actionStarted) {
                    pedro.followPath(SpecimenPaths.scorePreloadPath)
                    actionStarted = true
                } else if (pedro.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            2 -> {
                if (!actionStarted) {
                    actions.beforeScoreSpecimen()
                    actionStarted = true
                } else if (actions.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            3 -> {
                if (!actionStarted) {
                    actions.scoreSpecimen()
                    actionStarted = true
                } else if (actions.isDone()) {
                    actionStarted = false
                    state++
                }
            }
        }

        telemetry.update()
    }
}