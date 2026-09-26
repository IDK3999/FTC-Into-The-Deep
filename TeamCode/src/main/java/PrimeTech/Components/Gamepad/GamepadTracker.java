package PrimeTech.Components.Gamepad;

import static PrimeTech.Global.Global.gamepad1;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Wraps gamepad 1 so code can ask "was this button <em>just</em> pressed?" instead of only
 * "is it down right now?".
 *
 * <p>This matters because {@code loop()} runs about 50 times a second. A human holds a
 * button for maybe a quarter of a second, so plain {@code gamepad1.cross} reads true on a
 * dozen consecutive loops. Toggling on that would flip the claw open and shut a dozen times.
 *
 * <p>The fix is to keep last loop's snapshot as well as this loop's, and report a press only
 * on the loop where the button changed from up to down - a "rising edge".
 *
 * <p>Method names say which behaviour you get, and the difference is not cosmetic:
 * <ul>
 *   <li>{@code ...Pressed()} - true for one single loop, when the button goes down.
 *       Use for toggles and mode changes.</li>
 *   <li>{@code ...Held()} - true on every loop while the button is down. Use when
 *       something should keep happening, like nudging a target up while held.</li>
 * </ul>
 *
 * <p>{@link #loop()} must be called once per OpMode loop, or these never update.
 *
 * <p>Note this only ever looks at gamepad 1, which drives the arm. The drivetrain reads
 * gamepad 2 directly in {@link PrimeTech.Components.Drivetrain.Drivetrain}.
 */
public class GamepadTracker {
    private static GamepadTracker instance = null;

    /** Snapshot of the buttons as of this loop. */
    private Gamepad currentState = null;

    /** Snapshot of the buttons as of the previous loop, for spotting changes. */
    private Gamepad previousState = null;

    public static synchronized GamepadTracker getInstance() {
        if (instance == null) {
            instance = new GamepadTracker();
        }
        return instance;
    }

    public void init() {
        // Copies, not references: gamepad1's fields are overwritten in place by the SDK, so
        // holding a reference would make "previous" and "current" always identical.
        currentState = new Gamepad();
        currentState.copy(gamepad1);

        previousState = new Gamepad();
    }

    /** Shifts this loop's snapshot into the previous slot and takes a fresh one. */
    public void loop() {
        previousState.copy(currentState);
        currentState.copy(gamepad1);
    }

    // region Just-pressed (true for one loop only)

    /** Used to swing the claw front/back. */
    public boolean trianglePressed() {
        return currentState.triangle && !previousState.triangle;
    }

    /** Used to re-initialise the arm mid-match. */
    public boolean circlePressed() {
        return currentState.circle && !previousState.circle;
    }

    /** Used to open/close the claw. */
    public boolean crossPressed() {
        return currentState.cross && !previousState.cross;
    }

    public boolean dpadUpPressed() {
        return currentState.dpad_up && !previousState.dpad_up;
    }

    public boolean dpadDownPressed() {
        return currentState.dpad_down && !previousState.dpad_down;
    }

    public boolean dpadLeftPressed() {
        return currentState.dpad_left && !previousState.dpad_left;
    }

    public boolean dpadRightPressed() {
        return currentState.dpad_right && !previousState.dpad_right;
    }

    public boolean leftBumperPressed() {
        return currentState.left_bumper && !previousState.left_bumper;
    }

    public boolean rightBumperPressed() {
        return currentState.right_bumper && !previousState.right_bumper;
    }

    // endregion Just-pressed

    // region Held (true every loop while down)

    public boolean leftBumperHeld() {
        return currentState.left_bumper;
    }

    public boolean rightBumperHeld() {
        return currentState.right_bumper;
    }

    /** Analog trigger, 0.0 released to 1.0 fully pulled. */
    public double rightTrigger() {
        return currentState.right_trigger;
    }

    /** Analog trigger, 0.0 released to 1.0 fully pulled. */
    public double leftTrigger() {
        return currentState.left_trigger;
    }

    // endregion Held
}
