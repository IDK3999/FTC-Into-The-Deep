package PrimeTech.OpModes.Utils.Tuners;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import PrimeTech.Components.Limelight.Limelight;
import PrimeTech.Global.Global;

@TeleOp(name = "test servo", group = "InitializeForAssembly")
public class TestServo extends OpMode{
    public static final double OPEN_POS = 0.0;
    public static final double CLOSED_POS = 0.0;

    public static final double FRONT_BACK_INIT = 0.0;

    public static final double ROTATION_INIT = 0.25;
    Servo openingServo = null;
    Servo rotationServo = null;
    Servo frontBackServo_left = null;
    Servo frontBackServo_right = null;

    double openingServo_pos=CLOSED_POS;
    double rotationServo_pos=ROTATION_INIT;
    double frontBackServoRight_pos=FRONT_BACK_INIT;

    @Override
    public void init() {

        Global.hardwareMap = hardwareMap;
        Global.telemetry = telemetry;
        Limelight.getInstance().init();
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
        Global.gamepad1 = gamepad1;
    }

    @Override
    public void loop() {
        Limelight.getInstance().loop();

        double pieceAngle = Limelight.getInstance().getAngle()/360;
        openingServo.setPosition(0.2);
        //rotationServo.setPosition(0.5);
        telemetry.addData("Piece angle", Limelight.getInstance().getAngle());
        telemetry.addData("Servo angle",rotationServo.getPosition()-0.25+ (double) Limelight.getInstance().getAngle() /360);
        //rotationServo.setPosition(rotationServo.getPosition()-0.25+ (double) Limelight.getInstance().getAngle() /360);

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
        frontBackServo_right.setPosition(0.25);
        frontBackServo_left.setPosition(0.25);


//        telemetry.addData("openingServo_pos: ", openingServo_pos);
//        telemetry.addData("rotationServo_pos: ", rotationServo_pos);
//        telemetry.addData("frontBackServoRight_pos: ", frontBackServoRight_pos);
        telemetry.update();
    }

    void move_to_ll_angle(){
        rotationServo.setPosition(Limelight.getInstance().getAngle()/360);
    }
}
