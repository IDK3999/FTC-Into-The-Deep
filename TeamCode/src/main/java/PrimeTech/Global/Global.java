package PrimeTech.Global;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * A holder for the four things the FTC SDK hands to an OpMode, so that any class can reach
 * them without being passed a reference.
 *
 * <p>Every OpMode in this package fills these in first thing in {@code init()}:
 *
 * <pre>
 * Global.hardwareMap = hardwareMap;
 * Global.telemetry = telemetry;
 * Global.gamepad1 = gamepad1;
 * Global.gamepad2 = gamepad2;
 * </pre>
 *
 * <p><b>This is not a pattern to copy.</b> It is global mutable state: the fields are null
 * until some OpMode happens to set them, nothing stops one OpMode's values leaking into the
 * next, and you cannot tell from a class's signature what it actually depends on. Forgetting
 * one line above gives a {@link NullPointerException} from somewhere deep in an unrelated
 * component.
 *
 * <p>{@code PrimeTechV3} avoids all of this by passing {@code hardwareMap} into each
 * component's {@code init} as a normal parameter. Do that instead.
 */
public class Global {
    public static HardwareMap hardwareMap;
    public static Telemetry telemetry;
    public static Gamepad gamepad1;
    public static Gamepad gamepad2;
}
