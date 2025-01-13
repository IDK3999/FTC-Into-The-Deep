package PrimeTech.OpModes.Tele.TeleSimple;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import PrimeTech.Components.Limelight.Limelight;

@TeleOp(name = "TeleSimpleBlue", group = "TeleOp")
public class TeleSimpleBlue extends TeleSimple {
    @Override
    public void init() {
        super.init();
        Limelight.getInstance().init_blue();
    }
}