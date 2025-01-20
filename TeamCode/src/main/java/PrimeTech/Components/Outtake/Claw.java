package PrimeTech.Components.Outtake;

import static PrimeTech.Global.Global.hardwareMap;
import static PrimeTech.Global.Global.telemetry;

import com.qualcomm.robotcore.hardware.Servo;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Limelight.Limelight;

public class Claw {
    private static Claw instance = null;
    public Servo openingServo = null;
    public Servo rotationServo = null;
    public Servo frontBackServo_left = null;
    public Servo frontBackServo_right = null;

    enum Rotation{
        ZERO, NINETIES
    }

    Rotation rotation = Rotation.ZERO;


    enum OpenState {
        OPEN, CLOSED
    }

    OpenState openState = OpenState.CLOSED;

    enum FrontBackState {
        FRONT, BACK
    }

    FrontBackState frontBackState = FrontBackState.FRONT;

    enum SquarePressed{
        YES, NO
    }

    SquarePressed squarePressed = SquarePressed.NO;

    boolean press = false;
    double last_pieceAngle = 0;
    // Servo positions
    // TODO: Adjust with actual positions
    public static final double OPEN_POS = 0.5;
    public static final double CLOSED_POS = 0.0;

    public static final double FRONT_POS = 0.95;
    public static final double MID_POS = 0.4;
    public static final double OUTTAKE_SAMPLE_PIVOT_POS = 0.25;
    public static final double BACK_POS = 0.0;

    public static final double ROTATION_INIT = 0.25;
    public static final double ROTATION_PERPENDICULAR = 0.5;


    public static synchronized Claw getInstance() {
        if (instance == null) {
            instance = new Claw();
        }
        return instance;
    }

    public void init() {


        openingServo = hardwareMap.get(Servo.class, "openingServo");


        rotationServo = hardwareMap.get(Servo.class, "rotationServo");


        frontBackServo_left = hardwareMap.get(Servo.class, "frontBackServoLeft");
        frontBackServo_left.setDirection(Servo.Direction.REVERSE);

        frontBackServo_right = hardwareMap.get(Servo.class, "frontBackServoRight");


    }
    public void start(){
        frontBackState = FrontBackState.BACK;
        openState = OpenState.CLOSED;
        rotation = Rotation.ZERO;


        openingServo.setPosition(CLOSED_POS);
        rotationServo.setPosition(ROTATION_INIT);


        frontBackServo_left.setPosition(BACK_POS);
        frontBackServo_right.setPosition(BACK_POS);
    }

    public void openState_method(){
        switch (openState) {
            case OPEN:
                if (Gamepad.getInstance().cross()) {
                    // Transition to CLOSED state
                    openingServo.setPosition(CLOSED_POS);
                    openState = OpenState.CLOSED;
                }
                break;
            case CLOSED:
                if (Gamepad.getInstance().cross()) {
                    // Transition to OPEN state
                    openingServo.setPosition(OPEN_POS);
                    openState = OpenState.OPEN;
                }
                break;
        }
    }

    public void intake_rotation(){
        switch(rotation){
            case ZERO:
                if(Gamepad.getInstance().triangle()){
                    rotate(ROTATION_PERPENDICULAR);
                    rotation = Rotation.NINETIES;
                }
                break;
            case NINETIES:
                if(Gamepad.getInstance().triangle()){
                    rotate(ROTATION_INIT);
                    rotation = Rotation.ZERO;
                }
                break;
        }
    }




    public void rotate(double angle){
        rotationServo.setPosition(angle);
    }

    public void pivot(double angle) {
        frontBackServo_right.setPosition(angle);
        frontBackServo_left.setPosition(angle);
    }

    public void change_to_OPEN_POS(){
        openingServo.setPosition(OPEN_POS);
        openState = OpenState.OPEN;
    }

    /* public void change_to_CLOSED_POS(){
        openingServo.setPosition(CLOSED_POS);
        openState = OpenState.CLOSED;
    }

    public void change_to_FRONT_POS(){
        frontBackState =FrontBackState.FRONT;
    } */
/* public void frontBackState_method(){
        switch (frontBackState) {
            case FRONT:
                if (Gamepad.getInstance().triangle()) {
                    // Transition to BACK state
                    frontBackServo_right.setPosition(BACK_POS);
                    frontBackServo_left.setPosition(BACK_POS);
                    frontBackState = FrontBackState.BACK;
                }
                break;
            case BACK:
                if (Gamepad.getInstance().triangle()) {
                    // Transition to FRONT state
                    frontBackServo_right.setPosition(FRONT_POS);
                    frontBackServo_left.setPosition(FRONT_POS);
                    frontBackState = FrontBackState.FRONT;
                }
                break;
        }
    }*/
    /*public void onSquarePress(){
           if(Gamepad.getInstance().square()){
               //rotationServo.setPosition(ROTATION_INIT);
               switch (squarePressed){
                   case YES:
                       squarePressed = SquarePressed.NO;
                       break;

                   case NO:
                       squarePressed = SquarePressed.YES;
                       break;
               }
           }
       }
       public void ll_method(){

           onSquarePress();

           double pieceAngle = Limelight.getInstance().getAngle()/360;
           telemetry.addData("Piece Angle", pieceAngle);
           if(squarePressed == SquarePressed.YES && Limelight.getInstance().foundPiece())
               rotationServo.setPosition(pieceAngle);


           telemetry.addData("Absolute Piece angle", Limelight.getInstance().getAngle());
           telemetry.addData("Piece angle", pieceAngle);
           telemetry.addData("Servo angle",rotationServo.getPosition());
           telemetry.addData("Press",squarePressed);

       }


       void move_to_ll_angle(double pieceAngle){
           double newAngle = rotationServo.getPosition() + pieceAngle - 0.25;
           rotationServo.setPosition(Math.max(0,Math.min(0.5,newAngle)));
       }*/


}
