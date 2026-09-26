package PrimeTech.Components.Outtake;

import PrimeTech.Components.Gamepad.GamepadTracker;
import PrimeTech.Components.Modes.FSMModes;

/**
 * Top level of the arm. Normally it just runs the mode state machine
 * ({@link FSMModes}); its own job is the mid-match re-initialise on circle.
 *
 * <p>Re-initialising has to happen in a safe order. Dropping the arm while the slide is
 * still extended would swing the claw into the floor or the submersible, so the slide is
 * retracted <em>first</em> and only then is the arm lowered. That two-stage wait is what
 * {@link #whatToRetract} tracks.
 */
public class Outtake {
    private static Outtake instance = null;

    /** Which part of the retract sequence we are waiting on, or IDLE for normal operation. */
    WhatToRetract whatToRetract = WhatToRetract.IDLE;

    public static synchronized Outtake getInstance() {
        if (instance == null) {
            instance = new Outtake();
        }
        return instance;
    }

    public void init() {
        whatToRetract = WhatToRetract.IDLE;
        Claw.getInstance().init();
        Extension.getInstance().init();
        Pivot.getInstance().init();
    }

    public void start() {
        Claw.getInstance().start();
        Extension.getInstance().start();
        Pivot.getInstance().start();
    }

    /** Call once per loop. */
    public void loop() {
        switch (whatToRetract) {
            case IDLE:
                // Normal operation: hand over to the mode state machine.
                FSMModes.getInstance().update();

                // Circle restarts everything, e.g. after the arm has been knocked out of
                // position or the driver has lost track of which mode it is in.
                if (GamepadTracker.getInstance().circlePressed()) {
                    start();
                    FSMModes.getInstance().start();

                    whatToRetract = WhatToRetract.EXTENSION;
                }
                break;
            case EXTENSION:
                // Wait for the slide to come in before touching the arm angle.
                if (Extension.extensionMotorRight.getCurrentPosition() > Extension.TOLERANCE_TICKS) {
                    Extension.getInstance().runToTarget(Extension.targetTicks);
                } else {
                    whatToRetract = WhatToRetract.PIVOT;
                }
                break;
            case PIVOT:
                // Slide is in; hold it there while the arm comes down.
                Extension.getInstance().runToTarget(0);
                if (Pivot.pivotMotor.getCurrentPosition() > Pivot.TOLERANCE_TICKS) {
                    Pivot.getInstance().runToTarget(Pivot.targetTicks);
                } else {
                    whatToRetract = WhatToRetract.IDLE;
                }
                break;
        }
    }

    /** Stage of the "retract everything safely" sequence. */
    enum WhatToRetract {
        EXTENSION, PIVOT, IDLE
    }
}
