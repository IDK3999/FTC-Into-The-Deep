package PrimeTech.Components.Modes;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import PrimeTech.Components.Limelight.Limelight;
import PrimeTech.Components.Outtake.Claw;
import PrimeTech.Components.Outtake.Claw_vechi;
import PrimeTech.Components.Outtake.Extension;
import PrimeTech.Components.Outtake.Pivot;

public class AllModes {
    static double pivotTarget = Pivot.TICKS_FOR_PARALLEL;
    static double extensionTarget = Extension.MIN_TICKS;
    static double increment = 50.0;
  /*  static WhatToRetract_intake_sample_init whatToRetract_intake_sample_init = WhatToRetract_intake_sample_init.CLAW;
    static FoundAPiece foundAPiece = FoundAPiece.NO;
    static WhatToDo_intake_sample_loop whatToDo_intake_sample_loop = WhatToDo_intake_sample_loop.OPEN_CLAW;
*/
    public static void general() {
        Extension.getInstance().loop();
        Pivot.getInstance().loop();
        Claw_vechi.getInstance().loop();
    }

 /*   public static void intake_sample_init() {
        pivotTarget = Pivot.TICKS_FOR_PARALLEL;
        extensionTarget = Extension.MIN_TICKS;

        switch (whatToRetract_intake_sample_init) {
            case CLAW:
                Claw.getInstance().rotate(0.0);
                Claw.getInstance().move_to_angle(0.5);
                whatToRetract_intake_sample_init = WhatToRetract_intake_sample_init.EXTENSION;
                break;
            case EXTENSION:
                if (Extension.getInstance().extension_left.getCurrentPosition() != extensionTarget) {
                    Extension.getInstance().run_to_target(extensionTarget);
                } else {
                    whatToRetract_intake_sample_init = WhatToRetract_intake_sample_init.PIVOT;
                }
                break;
            case PIVOT:
                if (Pivot.getInstance().motorPivot.getCurrentPosition() != pivotTarget) {
                    Pivot.getInstance().run_to_target(pivotTarget);
                } else {
                    whatToRetract_intake_sample_init = WhatToRetract_intake_sample_init.OVER;
                }
                break;
            case OVER:
                FSMModes.getInstance().init = FSMModes.Init.OVER;
                break;
        }

        foundAPiece = FoundAPiece.NO;
    }

    public static void intake_sample_loop() {
        switch (foundAPiece) {
            case NO:
                Claw.getInstance().perpendicular_on_the_ground();
                Extension.getInstance().run_to_target(extensionTarget);
                extensionTarget += increment;

                if (Limelight.getInstance().foundPiece()) {
//                    foundAPiece = FoundAPiece.YES;
//                    whatToDo_intake_sample_loop = WhatToDo_intake_sample_loop.OPEN_CLAW;
                    telemetry.addData("Found a piece", "Yes");
                }
                break;
            case YES:
                switch (whatToDo_intake_sample_loop) {
                    case OPEN_CLAW:
                        Claw.getInstance().openingServo.setPosition(Claw.OPEN_POS);
                        whatToDo_intake_sample_loop = WhatToDo_intake_sample_loop.GET_PIVOT_DOWN;
                        break;
                    case GET_PIVOT_DOWN:
//                        Claw.getInstance().rotate( code for finding angle );
                        Pivot.getInstance().run_to_target(pivotTarget);
                        Claw.getInstance().perpendicular_on_the_ground();
                        pivotTarget -= increment;

//                        if ( code for finding distance to the piece ) {
//                            whatToDo_intake_sample_loop = WhatToDo_intake_sample_loop.CLOSE_CLAW;
//                        }
                        break;
                    case CLOSE_CLAW:
                        Claw.getInstance().openingServo.setPosition(Claw.CLOSED_POS);
                        whatToDo_intake_sample_loop = WhatToDo_intake_sample_loop.GET_PIVOT_UP;
                        break;
                    case GET_PIVOT_UP:
                        if (Pivot.getInstance().motorPivot.getCurrentPosition() != Pivot.TICKS_FOR_PARALLEL) {
                            Pivot.getInstance().run_to_target(Pivot.TICKS_FOR_PARALLEL);
                            Claw.getInstance().perpendicular_on_the_ground();
                        } else {
                            Claw.getInstance().rotate(0.0);
                            whatToDo_intake_sample_loop = WhatToDo_intake_sample_loop.RETRACT;
                        }
                        break;
                    case RETRACT:
                        if (Extension.getInstance().extension_left.getCurrentPosition() != Extension.MIN_TICKS) {
                            Extension.getInstance().run_to_target(Extension.MIN_TICKS);
                        } else {
                            whatToDo_intake_sample_loop = WhatToDo_intake_sample_loop.OVER;
                        }
                        break;
                    case OVER:
                        FSMModes.getInstance().modes = FSMModes.Modes.GENERAL;
                        break;
                }
                break;
        }
    }

    public static void outtake_sample_init() {
        Claw.getInstance().rotate(0.5);
    }

    public static void outtake_sample_loop() {
        Extension.getInstance().loop();
        Pivot.getInstance().loop();
        Claw.getInstance().parallel_to_the_ground_back();
        Claw.getInstance().open_close();
    }

    public static void intake_outtake_specimen_init() {
        Claw.getInstance().rotate(0.0);
    }

    public static void intake_outtake_specimen_loop() {
        Extension.getInstance().loop();
        Pivot.getInstance().loop();
        Claw.getInstance().parallel_to_the_ground_front();
        Claw.getInstance().open_close();
    }

    enum WhatToRetract_intake_sample_init {
        CLAW, EXTENSION, PIVOT, OVER
    }

    enum FoundAPiece {
        YES, NO
    }

    enum WhatToDo_intake_sample_loop {
        OPEN_CLAW, GET_PIVOT_DOWN, CLOSE_CLAW, GET_PIVOT_UP, RETRACT, OVER
    }*/
}
