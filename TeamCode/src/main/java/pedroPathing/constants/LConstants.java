package pedroPathing.constants;

import com.pedropathing.localization.Encoder;
import com.pedropathing.localization.constants.ThreeWheelConstants;
import com.pedropathing.localization.constants.TwoWheelConstants;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

public class LConstants {
    static {
        TwoWheelConstants.forwardTicksToInches = 0.0029518;
        TwoWheelConstants.strafeTicksToInches =  0.0029488;
        TwoWheelConstants.forwardY = 3.9;
        TwoWheelConstants.strafeX = -5.7;
        TwoWheelConstants.forwardEncoder_HardwareMapName = "leftFront";
        TwoWheelConstants.strafeEncoder_HardwareMapName = "leftBack";
        TwoWheelConstants.forwardEncoderDirection = Encoder.REVERSE;
        TwoWheelConstants.strafeEncoderDirection = Encoder.REVERSE;
        TwoWheelConstants.IMU_HardwareMapName = "imu";
        TwoWheelConstants.IMU_Orientation = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.RIGHT, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
    }
}

// TODO: Edit with correct values