package PrimeTech.Components.Limelight;

import static PrimeTech.Global.Global.hardwareMap;
import static PrimeTech.Global.Global.telemetry;


import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

public class Limelight {
    private static Limelight instance = null;
    public double[] pythonOutputs;
    private Limelight3A limelight;

    public static synchronized Limelight getInstance() {
        if (instance == null) {
            instance = new Limelight();
        }
        return instance;
    }

    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        limelight.setPollRateHz(100);
        telemetry.setMsTransmissionInterval(11);
        limelight.pipelineSwitch(0);
        limelight.start();
    }

    public void loop() {
        LLResult result = limelight.getLatestResult();
        if (result != null) {
            if (result.isValid()) {
                telemetry.addData("limelight pipeline", result.getPipelineIndex());

                pythonOutputs = result.getPythonOutput();
//                if (pythonOutputs != null && pythonOutputs.length > 0) {
//                    // Display the Python script outputs
//                    for (int i = 0; i < pythonOutputs.length; i++) {
//                        telemetry.addData("Python Output " + i, pythonOutputs[i]);
//                    }
//                } else {
//                    telemetry.addData("Python Output", "No data available");
//                }
            }
        }
    }

    public boolean foundPiece() {
        return pythonOutputs[0] == 1;
    }
}
