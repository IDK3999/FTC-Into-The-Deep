package PrimeTech.Components.Outtake;

import static PrimeTech.Global.Global.hardwareMap;
import static PrimeTech.Global.Global.telemetry;

import com.qualcomm.robotcore.hardware.Servo;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Limelight.Limelight;

public class Claw_vechi {
    private static Claw_vechi instance = null;
    public Servo openingServo = null;
    public Servo rotationServo = null;
    public Servo frontBackServo_left = null;
    public Servo frontBackServo_right = null;

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
    public static final double OPEN_POS = 0.25;
    public static final double CLOSED_POS = 0.0;

    public static final double FRONT_POS = 0.875;
    public static final double BACK_POS = 0.0;

    public static final double ROTATION_INIT = 0.25;


    public static synchronized Claw_vechi getInstance() {
        if (instance == null) {
            instance = new Claw_vechi();
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
        squarePressed = SquarePressed.NO;
        frontBackState = FrontBackState.FRONT;
        openState = OpenState.CLOSED;

        openingServo.setPosition(CLOSED_POS);
        rotationServo.setPosition(ROTATION_INIT);

        frontBackServo_left.setPosition(FRONT_POS);
        frontBackServo_right.setPosition(FRONT_POS);
    }
    public void loop() {
        openState_method();
        frontBackState_method();
        //ll_method();
        onSquarePress();
    }

    public void onSquarePress(){
        if(Gamepad.getInstance().square()){
            rotationServo.setPosition(ROTATION_INIT);
        }
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
    public void frontBackState_method(){
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
    }

    public void ll_method(){

        double pieceAngle = Limelight.getInstance().getAngle()/360;

        switch(squarePressed){
            case NO:
                rotationServo.setPosition(0.25);
                if(Gamepad.getInstance().square() && Limelight.getInstance().foundPiece()){

                    move_to_ll_angle(pieceAngle);

                    squarePressed = SquarePressed.YES;
                }
                break;
            case YES:
                if(Gamepad.getInstance().square() && Limelight.getInstance().foundPiece()){
                    move_to_ll_angle(pieceAngle);
                }
                else if(Gamepad.getInstance().square() && !Limelight.getInstance().foundPiece()){
                    squarePressed = SquarePressed.NO;
                }
                break;
        }


/*
        if(Limelight.getInstance().foundPiece()){
            last_pieceAngle = pieceAngle;
        }

        if(Gamepad.getInstance().square()){
            press = !press;
            if(Limelight.getInstance().foundPiece()){
                double newAngle = rotationServo.getPosition() + last_pieceAngle - 0.25;
                rotationServo.setPosition(Math.max(0,Math.min(0.5,newAngle)));

            }

        }

        if(!press){
            rotationServo.setPosition(0.25);
        }

*/
        telemetry.addData("Absolute Piece angle", Limelight.getInstance().getAngle());
        telemetry.addData("Piece angle", pieceAngle);
        telemetry.addData("Servo angle",rotationServo.getPosition());
        telemetry.addData("Press",press);

    }

    void move_to_ll_angle(double pieceAngle){
        double newAngle = rotationServo.getPosition() + pieceAngle - 0.25;
        rotationServo.setPosition(Math.max(0,Math.min(0.5,newAngle)));
    }
    public void rotate(double angle) {
        frontBackServo_right.setPosition(angle);
        frontBackServo_left.setPosition(angle);
    }

    public void change_to_OPEN_POS(){
        openingServo.setPosition(OPEN_POS);
        openState = OpenState.OPEN;
    }
    public void change_to_FRONT_POS(){
        frontBackState =FrontBackState.FRONT;
    }


}
