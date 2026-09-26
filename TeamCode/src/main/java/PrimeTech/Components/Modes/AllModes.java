package PrimeTech.Components.Modes;

import com.acmerobotics.dashboard.config.Config;

import PrimeTech.Components.Gamepad.GamepadTracker;
import PrimeTech.Components.Outtake.Claw;
import PrimeTech.Components.Outtake.Extension;
import PrimeTech.Components.Outtake.Pivot;

/**
 * What the arm does in each of the four scoring modes. {@link FSMModes} decides which mode
 * is active; this class holds the behaviour of each one.
 *
 * <p>Every mode has three methods:
 * <ul>
 *   <li>{@code init...} - run once when the driver selects the mode. Sets claw servos and
 *       kicks off the move into position.</li>
 *   <li>{@code update...} - run every loop while the mode is active. Drives the arm into
 *       position via {@link #runToPositionInOrder}, then hands over to {@code hold...}.</li>
 *   <li>{@code hold...} - run every loop once the arm has arrived. This is where the driver
 *       gets manual control back.</li>
 * </ul>
 *
 * <p>The {@code @Config} annotation exposes the public static fields below to
 * <a href="http://192.168.43.1:8080/dash">FTC Dashboard</a>, so target positions can be
 * edited live over wifi instead of rebuilding. Numbers changed there are lost on restart -
 * once a value is right, copy it back into this file.
 */
@Config
public class AllModes {
    // region Target positions, in encoder ticks
    public static double intakeSpecimenExtensionTicks = 0;
    public static double intakeSpecimenPivotTicks = 450;

    public static double outtakeSpecimenExtensionTicks = 50;
    public static double outtakeSpecimenPivotTicks = 2100;

    public static double outtakeSampleExtensionTicks = 900;
    public static double outtakeSamplePivotTicks = 2050;

    public static double intakeSampleExtensionTicks = 0;
    public static double intakeSamplePivotTicks = 0;
    // endregion Target positions

    /** Which stage of "get into position" we are in. See {@link #runToPositionInOrder}. */
    public static RetractStage retractStage = RetractStage.EXTENSION_RETRACT;

    /** Restarts the positioning sequence from the beginning. Called by every {@code init}. */
    public static void beginRetractSequence() {
        retractStage = RetractStage.EXTENSION_RETRACT;
    }

    /**
     * Moves the arm to {@code pivotTarget} / {@code extensionTarget} in a safe order, then
     * hands control to the mode's {@code hold} method.
     *
     * <p>The order matters. Swinging the arm while the slide is extended puts the claw a long
     * way from the robot, where it can hit the field, the submersible, or the floor. So the
     * sequence is always:
     *
     * <ol>
     *   <li>{@code EXTENSION_RETRACT} - pull the slide in, holding the arm where it is.</li>
     *   <li>{@code PIVOT} - slide is in, so now it is safe to swing the arm to its angle.</li>
     *   <li>{@code EXTENSION} - arm is at its angle, so extend out to the target.</li>
     *   <li>{@code IDLE} - in position; run the mode's {@code hold} behaviour from now on.</li>
     * </ol>
     *
     * <p>Each stage keeps driving the axis it is not waiting on, so nothing sags under gravity
     * while another axis moves.
     */
    public static void runToPositionInOrder(double pivotTarget, double extensionTarget, Mode mode) {
        switch (retractStage) {
            case EXTENSION_RETRACT:
                // Hold the arm at its existing target while the slide comes in.
                Pivot.getInstance().runToTarget(Pivot.targetTicks);
                if (Extension.extensionMotorRight.getCurrentPosition() > Extension.TOLERANCE_TICKS) {
                    Extension.getInstance().runToTarget(0);
                } else {
                    retractStage = RetractStage.PIVOT;
                }
                break;
            case PIVOT:
                // Keep the slide pinned in while the arm swings.
                Extension.getInstance().runToTarget(0);
                if (Pivot.pivotMotor.getCurrentPosition() > pivotTarget + Pivot.TOLERANCE_TICKS
                        || Pivot.pivotMotor.getCurrentPosition() < pivotTarget - Pivot.TOLERANCE_TICKS) {
                    Pivot.getInstance().runToTarget(pivotTarget);
                } else {
                    retractStage = RetractStage.EXTENSION;
                }
                break;
            case EXTENSION:
                // Hold the arm angle while the slide goes out.
                Pivot.getInstance().runToTarget(pivotTarget);
                if (Extension.extensionMotorRight.getCurrentPosition() > extensionTarget + Extension.TOLERANCE_TICKS
                        || Extension.extensionMotorRight.getCurrentPosition() < extensionTarget - Extension.TOLERANCE_TICKS) {
                    Extension.getInstance().runToTarget(extensionTarget);
                } else {
                    retractStage = RetractStage.IDLE;
                }
                break;
            case IDLE:
                switch (mode) {
                    case INTAKE_SAMPLE:
                        holdIntakeSample(pivotTarget);
                        break;
                    case OUTTAKE_SAMPLE:
                        holdOuttakeSample(pivotTarget, extensionTarget);
                        break;
                    case INTAKE_SPECIMEN:
                        holdIntakeSpecimen(extensionTarget);
                        break;
                    case OUTTAKE_SPECIMEN:
                        holdOuttakeSpecimen(pivotTarget);
                        break;
                }
                break;
        }
    }

    /**
     * The default mode: full manual control. Triggers extend the slide, bumpers raise the
     * arm, cross toggles the claw. No automatic positioning at all.
     */
    public static void updateGeneral() {
        Extension.getInstance().loop();
        Claw.getInstance().updateGripToggle();
        Pivot.getInstance().loop();
    }

    // region Outtake sample - drop a sample into the basket

    public static void initOuttakeSample() {
        beginRetractSequence();

        Extension.targetTicks = outtakeSampleExtensionTicks;

        Claw.getInstance().setWrist(Claw.WRIST_STRAIGHT);
        Claw.getInstance().setClawPivot(Claw.CLAW_PIVOT_SCORE_SAMPLE);
    }

    public static void updateOuttakeSample() {
        runToPositionInOrder(outtakeSamplePivotTicks, outtakeSampleExtensionTicks, Mode.OUTTAKE_SAMPLE);
        Claw.getInstance().updateGripToggle();
    }

    /** In position over the basket: just hold both axes there so the driver can release. */
    public static void holdOuttakeSample(double pivotTarget, double extensionTarget) {
        Pivot.targetTicks = outtakeSamplePivotTicks;
        Pivot.getInstance().runToTarget(pivotTarget);
        Extension.getInstance().runToTarget(extensionTarget);
    }

    // endregion Outtake sample

    // region Outtake specimen - hang a specimen on the chamber

    public static void initOuttakeSpecimen() {
        beginRetractSequence();

        Extension.targetTicks = outtakeSpecimenExtensionTicks;
        Extension.getInstance().setLimitStateInRange();
        // Cap the slide shorter than usual: at chamber height, full extension would put the
        // claw outside the robot's legal footprint.
        Extension.maxTicks = Extension.SPECIMEN_MAX_TICKS;

        Claw.getInstance().setClawPivot(Claw.CLAW_PIVOT_SCORE_SAMPLE);
        Claw.getInstance().setWrist(Claw.WRIST_STRAIGHT);
    }

    public static void updateOuttakeSpecimen() {
        runToPositionInOrder(outtakeSpecimenPivotTicks, outtakeSpecimenExtensionTicks, Mode.OUTTAKE_SPECIMEN);
        Claw.getInstance().updateGripToggle();
    }

    /**
     * In position at the chamber. The bumpers snap the slide between fully in and fully out,
     * which is the motion that hooks the specimen onto the bar.
     */
    public static void holdOuttakeSpecimen(double pivotTarget) {
        if (GamepadTracker.getInstance().leftBumperPressed()) {
            Extension.targetTicks = outtakeSpecimenExtensionTicks;
        }
        if (GamepadTracker.getInstance().rightBumperPressed()) {
            Extension.targetTicks = Extension.maxTicks;
        }

        Pivot.targetTicks = outtakeSpecimenPivotTicks;

        Pivot.getInstance().runToTarget(pivotTarget);
        Extension.getInstance().loop();
    }

    // endregion Outtake specimen

    // region Intake specimen - take a specimen off the wall

    public static void initIntakeSpecimen() {
        beginRetractSequence();

        Extension.targetTicks = intakeSpecimenExtensionTicks;

        Claw.getInstance().open();
        Claw.getInstance().setClawPivot(Claw.CLAW_PIVOT_MID);
        Claw.getInstance().setWrist(Claw.WRIST_STRAIGHT);
    }

    public static void updateIntakeSpecimen() {
        runToPositionInOrder(intakeSpecimenPivotTicks, intakeSpecimenExtensionTicks, Mode.INTAKE_SPECIMEN);
        Claw.getInstance().updateGripToggle();
    }

    /**
     * At the wall. The arm stays under bumper control here, and the claw pivot is adjusted as
     * the arm moves so the jaws stay pointing the same way in space rather than tilting with
     * the arm - dividing the arm angle by 180 converts degrees into roughly the right amount
     * of servo travel to cancel it out.
     */
    public static void holdIntakeSpecimen(double extensionTarget) {
        Pivot.targetTicks = intakeSpecimenPivotTicks;
        Claw.getInstance().setClawPivot(Claw.CLAW_PIVOT_MID - Pivot.getPivotAngleDegrees() / 180);

        Pivot.getInstance().loop();
        Extension.getInstance().runToTarget(extensionTarget);
    }

    // endregion Intake specimen

    // region Intake sample - pick a sample up off the floor

    public static void initIntakeSample() {
        beginRetractSequence();

        Extension.targetTicks = intakeSampleExtensionTicks;
        Extension.getInstance().start();
        Extension.maxTicks = Extension.FULL_RANGE_MAX_TICKS;

        Claw.getInstance().close();
        Claw.getInstance().straightenWrist();
        Claw.getInstance().setClawPivot(Claw.CLAW_PIVOT_MID);
    }

    /** Claw tucked and closed - the shape for driving around, or for a sample lying lengthways. */
    public static void setSampleGrabParallel() {
        Claw.getInstance().close();
        Claw.getInstance().straightenWrist();
        Claw.getInstance().setClawPivot(Claw.CLAW_PIVOT_MID);
    }

    /** Claw open and swung right forward, to drop over a sample lying across the robot. */
    public static void setSampleGrabPerpendicular() {
        Claw.getInstance().open();
        Claw.getInstance().straightenWrist();
        Claw.getInstance().setClawPivot(Claw.CLAW_PIVOT_FRONT);
    }

    public static void updateIntakeSample() {
        runToPositionInOrder(intakeSamplePivotTicks, intakeSampleExtensionTicks, Mode.INTAKE_SAMPLE);
        Claw.getInstance().updateGripToggle();
    }

    /**
     * Down at floor level. The bumpers snap the slide fully in or fully out for reaching
     * samples at different distances, and triangle rolls the wrist to match a sample's angle.
     */
    public static void holdIntakeSample(double pivotTarget) {
        if (GamepadTracker.getInstance().leftBumperPressed()) {
            FSMModes.getInstance().resetSampleGrabOrientation();

            Claw.getInstance().straightenWrist();
            Claw.getInstance().setClawPivot(Claw.CLAW_PIVOT_MID);

            Extension.getInstance().clampToMin();
        }
        if (GamepadTracker.getInstance().rightBumperPressed()) {
            FSMModes.getInstance().resetSampleGrabOrientation();

            Claw.getInstance().straightenWrist();
            Claw.getInstance().setClawPivot(Claw.CLAW_PIVOT_MID);

            Extension.getInstance().clampToMax();
        }

        Pivot.targetTicks = intakeSamplePivotTicks;
        Claw.getInstance().updateWristToggle();

        Pivot.getInstance().runToTarget(pivotTarget);
        Extension.getInstance().loop();
    }

    // endregion Intake sample

    /**
     * The four modes that have a fixed arm position to drive to.
     *
     * <p>{@link FSMModes.RobotMode} is the same list plus GENERAL. The duplication is a wart -
     * GENERAL has no position to drive to, so it never reaches {@link #runToPositionInOrder}.
     */
    enum Mode {
        INTAKE_SAMPLE, INTAKE_SPECIMEN, OUTTAKE_SAMPLE, OUTTAKE_SPECIMEN
    }

    /** Stage of the safe positioning sequence in {@link #runToPositionInOrder}. */
    public enum RetractStage {
        EXTENSION_RETRACT, EXTENSION, PIVOT, IDLE
    }
}
