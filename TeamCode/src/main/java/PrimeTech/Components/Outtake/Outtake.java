package PrimeTech.Components.Outtake;

public class Outtake {
    private static Outtake instance = null;

    public static synchronized Outtake getInstance() {
        if (instance == null) {
            instance = new Outtake();
        }
        return instance;
    }

    public void init() {
        Claw.getInstance().init();
        Extension.getInstance().init();
        Pivot.getInstance().init();
    }

    public void loop() {
        InitPos.getInstance().return_to_init_pos();
    }
}
