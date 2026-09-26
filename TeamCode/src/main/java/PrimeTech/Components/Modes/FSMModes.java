package PrimeTech.Components.Modes;

import PrimeTech.Components.Gamepad.GamepadTracker;

/**
 * Tracks which scoring mode the arm is in, and switches between them on the d-pad.
 *
 * <p>The four scoring modes each put the arm somewhere specific:
 *
 * <pre>
 *   d-pad up     OUTTAKE_SAMPLE     arm up over the basket
 *   d-pad down   INTAKE_SAMPLE      arm down at the floor
 *   d-pad left   OUTTAKE_SPECIMEN   arm up at the chamber
 *   d-pad right  INTAKE_SPECIMEN    arm out at the wall
 * </pre>
 *
 * <p>plus {@code GENERAL}, the manual mode it starts in. The behaviour of each lives in
 * {@link AllModes}.
 *
 * <p>Note the ordering inside {@link #update()}: the current mode runs <em>first</em>, and the
 * d-pad is checked afterwards. So the loop where a mode change happens still runs one tick of
 * the old mode. That is deliberate - it means a mode's {@code init} is the last thing to touch
 * the hardware on that loop, and does not get immediately overwritten.
 */
public class FSMModes {
    private static FSMModes instance = null;

    /** The mode the arm is currently in. */
    private RobotMode currentMode = RobotMode.GENERAL;

    /** Which claw shape the sample intake is using; only meaningful in INTAKE_SAMPLE. */
    private SampleGrabOrientation sampleGrabOrientation = SampleGrabOrientation.PARALLEL;

    public static synchronized FSMModes getInstance() {
        if (instance == null) {
            instance = new FSMModes();
        }
        return instance;
    }

    public void start() {
        currentMode = RobotMode.GENERAL;
    }

    /** Runs the current mode, then handles mode changes. Call once per loop. */
    public void update() {
        switch (currentMode) {
            case GENERAL:
                AllModes.updateGeneral();
                break;
            case INTAKE_SAMPLE:
                AllModes.updateIntakeSample();

                // While grabbing samples, d-pad down doubles as a toggle between the two claw
                // shapes, instead of re-entering the mode it is already in.
                switch (sampleGrabOrientation) {
                    case PARALLEL:
                        if (GamepadTracker.getInstance().dpadDownPressed()) {
                            AllModes.setSampleGrabPerpendicular();
                            sampleGrabOrientation = SampleGrabOrientation.PERPENDICULAR;
                        }
                        break;
                    case PERPENDICULAR:
                        if (GamepadTracker.getInstance().dpadDownPressed()) {
                            AllModes.setSampleGrabParallel();
                            sampleGrabOrientation = SampleGrabOrientation.PARALLEL;
                        }
                        break;
                }
                break;
            case INTAKE_SPECIMEN:
                AllModes.updateIntakeSpecimen();
                break;
            case OUTTAKE_SPECIMEN:
                AllModes.updateOuttakeSpecimen();
                break;
            case OUTTAKE_SAMPLE:
                AllModes.updateOuttakeSample();
                break;
        }

        // Each check ignores the button if we are already in that mode, so pressing it twice
        // does not restart the positioning sequence from scratch.
        if (GamepadTracker.getInstance().dpadRightPressed() && currentMode != RobotMode.INTAKE_SPECIMEN) {
            currentMode = RobotMode.INTAKE_SPECIMEN;
            AllModes.initIntakeSpecimen();
        }
        if (GamepadTracker.getInstance().dpadDownPressed() && currentMode != RobotMode.INTAKE_SAMPLE) {
            currentMode = RobotMode.INTAKE_SAMPLE;
            sampleGrabOrientation = SampleGrabOrientation.PARALLEL;
            AllModes.initIntakeSample();
        }
        if (GamepadTracker.getInstance().dpadUpPressed() && currentMode != RobotMode.OUTTAKE_SAMPLE) {
            currentMode = RobotMode.OUTTAKE_SAMPLE;
            AllModes.initOuttakeSample();
        }
        if (GamepadTracker.getInstance().dpadLeftPressed() && currentMode != RobotMode.OUTTAKE_SPECIMEN) {
            currentMode = RobotMode.OUTTAKE_SPECIMEN;
            AllModes.initOuttakeSpecimen();
        }
    }

    /** Puts the sample-grab toggle back to its default, after something else moved the claw. */
    public void resetSampleGrabOrientation() {
        sampleGrabOrientation = SampleGrabOrientation.PARALLEL;
    }

    /** The modes the arm can be in. GENERAL is full manual control. */
    enum RobotMode {
        GENERAL, INTAKE_SAMPLE, INTAKE_SPECIMEN, OUTTAKE_SPECIMEN, OUTTAKE_SAMPLE
    }

    /** The two claw shapes used for picking samples off the floor. */
    enum SampleGrabOrientation {
        PARALLEL, PERPENDICULAR
    }
}
