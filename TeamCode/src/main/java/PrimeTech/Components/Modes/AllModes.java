package PrimeTech.Components.Modes;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Limelight.Limelight;
import PrimeTech.Components.Outtake.Claw;
import PrimeTech.Components.Outtake.Claw_vechi;
import PrimeTech.Components.Outtake.Extension;
import PrimeTech.Components.Outtake.InitPos;
import PrimeTech.Components.Outtake.Pivot;

public class AllModes {
    /*    static double pivotTarget = Pivot.TICKS_FOR_PARALLEL;
        static double extensionTarget = Extension.MIN_TICKS;
        static double increment = .0;
      /*  static WhatToRetract_intake_sample_init whatToRetract_intake_sample_init = WhatToRetract_intake_sample_init.CLAW;
        static FoundAPiece foundAPiece = FoundAPiece.NO;
        static WhatToDo_intake_sample_loop whatToDo_intake_sample_loop = WhatToDo_intake_sample_loop.OPEN_CLAW;
    */
    public static AllModes instance = null;
     public static synchronized  AllModes getInstance(){
         if(instance == null){
             instance = new AllModes();
         }
         return instance;
     }


    enum WhatToMove {
        CLAW, EXTENSION_RETRACT, PIVOT, EXTENSION, STOP
    }

    WhatToMove whatToMove = WhatToMove.CLAW;

    public static void general() {
        Extension.getInstance().loop();
        Pivot.getInstance().loop();
        Claw_vechi.getInstance().loop();
    }




    public static void intake_specimen_init(){
       Claw_vechi.getInstance().change_to_OFF();
       Claw_vechi.getInstance().change_to_CLOSED_POS();
       Claw_vechi.getInstance().change_to_BACK_POS();
       AllModes.getInstance().move_to_position(0, 3500);
       Claw_vechi.getInstance().rotate((Pivot.motorPivot.getCurrentPosition()*Pivot.ticks_in_degrees-90)/360);
    }
    public static void intake_specimen() {
        Claw_vechi.getInstance().openState_method();

    }



    public static void intake_sample(){
        Extension.getInstance().loop();
        Pivot.getInstance().loop();
        Claw_vechi.getInstance().openState_method();
        Claw_vechi.getInstance().ll_method();
        Claw_vechi.getInstance().rotate(0.0/*trebuie schimbat*/+Pivot.motorPivot.getCurrentPosition()*Pivot.ticks_in_degrees/360);
    }

     void move_to_position(double extension_target, double pivot_target) {
        Extension.target = extension_target;
        Pivot.target = pivot_target;
        switch (whatToMove) {
            case CLAW:
                Claw_vechi.getInstance().openingServo.setPosition(Claw_vechi.CLOSED_POS);
                Claw_vechi.getInstance().rotationServo.setPosition(Claw_vechi.ROTATION_INIT);
                Claw_vechi.getInstance().frontBackServo_left.setPosition(Claw_vechi.BACK_POS);
                Claw_vechi.getInstance().frontBackServo_right.setPosition(Claw_vechi.BACK_POS);
                whatToMove = WhatToMove.EXTENSION_RETRACT;
                break;
            case EXTENSION_RETRACT:
                if (Extension.extension_right.getCurrentPosition() > 50) {
                    Extension.getInstance().run_to_target(0);
                } else {
                    whatToMove = WhatToMove.PIVOT;
                }
                break;
            case PIVOT:
                if (Pivot.getInstance().motorPivot.getCurrentPosition() > pivot_target + 50 || Pivot.getInstance().motorPivot.getCurrentPosition() < pivot_target - 50) {
                    Pivot.getInstance().run_to_target(pivot_target);
                } else {
                    FSMModes.getInstance().init_over();
                }
                break;
        }
    }
}

 /*public static void intake_sample_init() {

    }

    public static void intake_sample_loop() {

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
//}
