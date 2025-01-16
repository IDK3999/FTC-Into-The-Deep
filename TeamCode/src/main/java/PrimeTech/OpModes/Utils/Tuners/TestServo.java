package PrimeTech.OpModes.Utils.Tuners;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import PrimeTech.Components.Gamepad.Gamepad;
import PrimeTech.Components.Limelight.Limelight;
import PrimeTech.Global.Global;

//@Disabled
@Config
@TeleOp(name = "test servo", group = "InitializeForAssembly")
public class TestServo extends OpMode{
    public static final double OPEN_POS = 0.0;
    public static final double CLOSED_POS = 0.0;

    public static final double FRONT_BACK_INIT = 0.5;

    public static final double ROTATION_INIT = 0.25;
    Servo openingServo = null;
    Servo rotationServo = null;
    Servo frontBackServo_left = null;
    Servo frontBackServo_right = null;

    public static double openingServo_pos=CLOSED_POS;
    public static double rotationServo_pos=ROTATION_INIT;
    public static double frontBackServoRight_pos=FRONT_BACK_INIT;

    @Override
    public void init() {
       // Global.gamepad1 = gamepad1;
        //Gamepad.getInstance().init();
        Global.hardwareMap = hardwareMap;
        Global.telemetry = telemetry;
        //Limelight.getInstance().init_blue();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        openingServo = hardwareMap.get(Servo.class, "openingServo");
        //openingServo.setDirection(Servo.Direction.REVERSE);
        openingServo.setPosition(CLOSED_POS);

        rotationServo = hardwareMap.get(Servo.class, "rotationServo");
        rotationServo.setPosition(ROTATION_INIT);

        frontBackServo_left = hardwareMap.get(Servo.class, "frontBackServoLeft");
        frontBackServo_left.setDirection(Servo.Direction.REVERSE);
        frontBackServo_left.setPosition(FRONT_BACK_INIT);


        frontBackServo_right = hardwareMap.get(Servo.class, "frontBackServoRight");
        frontBackServo_right.setPosition(FRONT_BACK_INIT);

    }

    @Override
    public void loop() {
        //Limelight.getInstance().loop();
        //Gamepad.getInstance().loop();


        openingServo.setPosition(openingServo_pos);
        frontBackServo_left.setPosition(frontBackServoRight_pos);
        frontBackServo_right.setPosition(frontBackServoRight_pos);
        rotationServo.setPosition(rotationServo_pos);
        //rotationServo.setPosition(0.5);
       // double angle = Limelight.getInstance().getAngle()/360;

        //rotationServo.setPosition(angle);

        //double pieceAngle=0;
        //rotationServo.setPosition(rotationServo.getPosition()-0.25+ (double) Limelight.getInstance().getAngle() /360);

        //telemetry.addData("Piece angle", angle);
        //telemetry.addData("Servo angle",rotationServo.getPosition());
        //telemetry.addData("Press",press);
        //if(Limelight.getInstance().foundPiece()){

            /*
        if(pieceAngle<0.21){
            rotationServo.setPosition(rotationServo.getPosition()-0.001);
        }
        else if(pieceAngle>0.29){
            rotationServo.setPosition(rotationServo.getPosition()+0.001);
        }
        }
        else{
            rotationServo.setPosition(0.25);
        */
       // }
       // frontBackServo_right.setPosition(1);
        //frontBackServo_left.setPosition(1);
        //sleep();


        telemetry.addData("openingServo_pos: ", openingServo_pos);
        telemetry.addData("rotationServo_pos: ", rotationServo_pos);
        telemetry.addData("frontBackServoRight_pos: ", frontBackServoRight_pos);
        telemetry.update();

    }

    void move_to_ll_angle(){
        rotationServo.setPosition(Limelight.getInstance().getAngle()/360);
    }
}
