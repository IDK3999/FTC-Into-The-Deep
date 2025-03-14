package PrimeTechV3.OpModes.Auto.Sample

import PrimeTechV3.Actions.Actions
import PrimeTechV3.Components.Claw
import PrimeTechV3.Components.Pedro
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode

@Autonomous(name = "Sample")
class SampleAuto : OpMode() {
    private var state = 0

    private lateinit var actions: Actions
    private lateinit var pedro: Pedro

    private var actionStarted = false

    override fun init() {
        actions = Actions
        actions.init(hardwareMap)

        pedro = Pedro
        pedro.init(hardwareMap, SamplePaths.start)

        SamplePaths.build(pedro.follower)
    }

    override fun start() {
        pedro.start()
        actions.start()

        state = 1
    }

    override fun loop() {
        actions.update()
        pedro.update()

        when (state) {
            0 -> {}

            1 -> {
                if (!actionStarted) {
                    Claw.setClawOpen(false)
                    pedro.followPath(SamplePaths.scorePreloadPath, 0.7)
                    actionStarted = true
                } else if (pedro.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            2 -> {
                state++
            }

            3 -> {
                state++
            }

            4 -> {
                if (!actionStarted) {
                    Claw.setClawOpen(false)
                    pedro.followPath(SamplePaths.parkPath, 0.8)
                    actionStarted = true
                } else if (pedro.isDone()) {
                    actionStarted = false
                    state++
                }
            }

            else -> {
                state = 0
            }
        }

        telemetry.update()
    }
}