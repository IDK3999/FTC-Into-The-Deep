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
        FRONT, BACK, MID
    }

    FrontBackState frontBackState = FrontBackState.FRONT;

    enum LL{
        ON, OFF, LOCK
    }

    LL ll = LL.OFF;

    boolean press=false;
    double last_pieceAngle=0;
    // Servo positions
    // TODO: Adjust with actual positions
    public static final double OPEN_POS = 0.25;
    public static final double CLOSED_POS = 0.0;

    public static final double FRONT_POS = 0.875;
    public static final double MID_POS = 0.3;
    public static final double BACK_POS = 0.0;

    public static final double ROTATION_INIT = 0.25;


    public static synchronized Claw_vechi getInstance() {
        if (instance == null) {
            instance = new Claw_vechi();
        }
        return instance;
    }

    public void init() {
        ll = LL.OFF;
        frontBackState = FrontBackState.FRONT;
        openState = OpenState.CLOSED;

        openingServo = hardwareMap.get(Servo.class, "openingServo");
        openingServo.setPosition(CLOSED_POS);

        rotationServo = hardwareMap.get(Servo.class, "rotationServo");
        rotationServo.setPosition(ROTATION_INIT);

        frontBackServo_left = hardwareMap.get(Servo.class, "frontBackServoLeft");
        frontBackServo_left.setDirection(Servo.Direction.REVERSE);
        frontBackServo_left.setPosition(FRONT_POS);

        frontBackServo_right = hardwareMap.get(Servo.class, "frontBackServoRight");
        frontBackServo_right.setPosition(FRONT_POS);


    }

    public void loop() {
        openState_method();
        frontBackState_method();
        ll_method();
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
       /* switch(ll){
            case OFF:
                if(Gamepad.getInstance().square()){
                    ll = LL.ON;
                }
                break;

            case ON:
                move_to_ll_postion();
                if(Gamepad.getInstance().square()){
                    ll = LL.LOCK;
                }
                break;
            case LOCK:
                if(Gamepad.getInstance().square()){
                    ll = LL.OFF;
                    rotationServo.setPosition(ROTATION_INIT);
                }
                break;
        }*/
        move_to_ll_postion();
    }



    void move_to_ll_postion(){
        /*
        double pieceAngle = Limelight.getInstance().getAngle()/360;
        if(Limelight.getInstance().foundPiece()){
            if(pieceAngle<0.21){
                rotationServo.setPosition(rotationServo.getPosition()-0.001);
            }
            else if(pieceAngle>0.29){
                rotationServo.setPosition(rotationServo.getPosition()+0.001);
            }
        }
        else{
            rotationServo.setPosition(0.25);
        }

         */
        double pieceAngle=Limelight.getInstance().getAngle()/360;
        if(Limelight.getInstance().foundPiece()){
            last_pieceAngle=pieceAngle;
        }
        //rotationServo.setPosition(rotationServo.getPosition()-0.25+ (double) Limelight.getInstance().getAngle() /360);
        if(Gamepad.getInstance().square()){
            press=!press;
            //pieceAngle = ;
            if(Limelight.getInstance().foundPiece()){
                double newAngle = rotationServo.getPosition()+last_pieceAngle-0.25;
                rotationServo.setPosition(Math.max(0,Math.min(0.5,newAngle)));

            }

        }
        if(!press){
            rotationServo.setPosition(0.25);
        }
        telemetry.addData("Absolute Piece angle", Limelight.getInstance().getAngle());
        telemetry.addData("Piece angle", pieceAngle);
        telemetry.addData("Servo angle",rotationServo.getPosition());
        telemetry.addData("Press",press);
    }
    public void rotate(double angle) {
        frontBackServo_right.setPosition(angle);
        frontBackServo_left.setPosition(angle);
    }

    public void change_to_OFF(){
        ll = LL.OFF;
    }
    public void change_to_CLOSED_POS(){
        openState = OpenState.CLOSED;
    }
    public void change_to_BACK_POS(){
        frontBackState =FrontBackState.FRONT;
    }

}
