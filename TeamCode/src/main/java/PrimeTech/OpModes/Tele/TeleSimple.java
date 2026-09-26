package PrimeTech.OpModes.Tele;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import PrimeTech.Components.Drivetrain.Drivetrain;
import PrimeTech.Components.Gamepad.GamepadTracker;
import PrimeTech.Components.Modes.FSMModes;
import PrimeTech.Components.Outtake.Outtake;
import PrimeTech.Global.Global;

/**
 * The TeleOp that was actually driven in the 2024-25 season. This is the entry point for
 * everything in {@code PrimeTech}.
 *
 * <h2>Controls</h2>
 *
 * <b>Gamepad 1 - arm</b>
 * <pre>
 *   d-pad up      basket mode: arm up over the basket
 *   d-pad down    floor mode: arm down to grab samples (press again to toggle claw shape)
 *   d-pad left    chamber mode: arm up to hang specimens
 *   d-pad right   wall mode: arm out to collect specimens
 *   cross         open / close the claw
 *   triangle      roll the wrist 90 degrees (floor mode)
 *   circle        re-initialise the arm: retract, then lower
 *   bumpers       raise / lower the arm, or snap the slide in/out inside a mode
 *   triggers      extend / retract the slide
 * </pre>
 *
 * <b>Gamepad 2 - driving</b>
 * <pre>
 *   left stick    drive and strafe
 *   right stick   turn
 * </pre>
 *
 * <h2>How an OpMode runs</h2>
 *
 * The SDK calls these in order: {@code init()} once when the driver selects this OpMode,
 * {@code start()} once when they hit play, then {@code loop()} over and over (about 50 times
 * a second) until stop. {@code loop()} must return quickly - never sleep or busy-wait in it.
 */
@TeleOp(name = "TeleSimple", group = "TeleOp")
public class TeleSimple extends OpMode {
    @Override
    public void init() {
        // MANUAL bulk caching: each hardware read normally costs a round trip over the wire
        // to the control hub, and this loop reads a lot of encoders. In MANUAL mode the first
        // read of a loop fetches everything at once and the rest come from that snapshot,
        // which is much faster - as long as the cache is cleared each loop (see loop()).
        for (LynxModule hub : hardwareMap.getAll(LynxModule.class)) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        // Publish the SDK's objects so the components can reach them. See Global's docs for
        // why this is not a pattern to copy.
        Global.hardwareMap = hardwareMap;
        Global.telemetry = telemetry;
        Global.gamepad1 = gamepad1;
        Global.gamepad2 = gamepad2;

        Drivetrain.getInstance().init();
        GamepadTracker.getInstance().init();
        Outtake.getInstance().init();
    }

    @Override
    public void start() {
        Outtake.getInstance().start();
        FSMModes.getInstance().start();
    }

    @Override
    public void loop() {
        Drivetrain.getInstance().loop();
        GamepadTracker.getInstance().loop();
        Outtake.getInstance().loop();

        // Throw away this loop's cached hardware values, so the next loop reads fresh ones.
        // Forget this and the encoders appear frozen at their first-ever values.
        for (LynxModule hub : hardwareMap.getAll(LynxModule.class)) {
            hub.clearBulkCache();
        }
    }
}
