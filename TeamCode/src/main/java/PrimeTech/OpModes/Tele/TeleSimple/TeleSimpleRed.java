package PrimeTech.OpModes.Tele.TeleSimple;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import PrimeTech.Components.Limelight.Limelight;

@TeleOp(name = "TeleSimpleRed", group = "TeleOp")
public class TeleSimpleRed extends TeleSimple {
    @Override
    public void init() {
        super.init();
        Limelight.getInstance().init_red();
    }
}