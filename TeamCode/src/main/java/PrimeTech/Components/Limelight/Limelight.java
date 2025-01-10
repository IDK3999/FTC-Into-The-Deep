package PrimeTech.Components.Limelight;

import static PrimeTech.Global.Global.hardwareMap;
import static PrimeTech.Global.Global.telemetry;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

public class Limelight {

    //0 red  1 blue
    private static Limelight instance = null;
    public double[] pythonOutputs;
    private Limelight3A limelight;

    public static synchronized Limelight getInstance() {
        if (instance == null) {
            instance = new Limelight();
        }
        return instance;
    }

    public void init_blue() {
        //0 red  1 blue
        int color = 1;
        double[] pythonInputs={color,0,0,0,0,0,0,0};
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.updatePythonInputs(pythonInputs);
        limelight.setPollRateHz(100);
        telemetry.setMsTransmissionInterval(11);
        limelight.pipelineSwitch(0);
        limelight.start();
    }

    public void init_red() {
        //0 red  1 blue
        int color = 0;
        double[] pythonInputs={color,0,0,0,0,0,0,0};
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.updatePythonInputs(pythonInputs);
        limelight.setPollRateHz(100);
        telemetry.setMsTransmissionInterval(11);
        limelight.pipelineSwitch(0);
        limelight.start();
    }

    public void loop() {
        LLResult result = limelight.getLatestResult();
        if (result != null) {
                pythonOutputs = result.getPythonOutput();
                //telemetry.addData("limelight pipeline", result.getPipelineIndex());
                //telemetry.addData("Color", pythonOutputs[6]);
        }
    }

    public boolean foundPiece() {
        return pythonOutputs[0] == 1;
    }
    public double getAngle(){
        return pythonOutputs[5];
    }
}
