package pedroPathing.constants;

import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.localization.Localizers;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class FConstants {
    static {
        FollowerConstants.localizers = Localizers.PINPOINT;

        FollowerConstants.leftFrontMotorName = "leftFront";
        FollowerConstants.leftRearMotorName = "leftBack";
        FollowerConstants.rightFrontMotorName = "rightFront";
        FollowerConstants.rightRearMotorName = "rightBack";

        FollowerConstants.leftFrontMotorDirection = DcMotorSimple.Direction.REVERSE;
        FollowerConstants.leftRearMotorDirection = DcMotorSimple.Direction.REVERSE;
        FollowerConstants.rightFrontMotorDirection = DcMotorSimple.Direction.FORWARD;
        FollowerConstants.rightRearMotorDirection = DcMotorSimple.Direction.FORWARD;

        FollowerConstants.mass = 12;

        FollowerConstants.xMovement = 79.714;
        FollowerConstants.yMovement = 62.515;

        FollowerConstants.forwardZeroPowerAcceleration = -38.5081;
        FollowerConstants.lateralZeroPowerAcceleration = -73.2039;

        FollowerConstants.translationalPIDFCoefficients.setCoefficients(0.2, 0, 0.015, 0);
        FollowerConstants.useSecondaryTranslationalPID = false;
        FollowerConstants.secondaryTranslationalPIDFCoefficients.setCoefficients(0.1, 0, 0.01, 0); // Not being used, @see useSecondaryTranslationalPID

        FollowerConstants.headingPIDFCoefficients.setCoefficients(1.85, 0, 0.16, 0);
        FollowerConstants.useSecondaryHeadingPID = false;
        FollowerConstants.secondaryHeadingPIDFCoefficients.setCoefficients(2, 0, 0.1, 0); // Not being used, @see useSecondaryHeadingPID

        FollowerConstants.drivePIDFCoefficients.setCoefficients(0.03, 0, 0.002, 0.6, 0);
        FollowerConstants.useSecondaryDrivePID = false;
        FollowerConstants.secondaryDrivePIDFCoefficients.setCoefficients(0.1, 0, 0, 0.6, 0); // Not being used, @see useSecondaryDrivePID

        FollowerConstants.zeroPowerAccelerationMultiplier = 4;
        FollowerConstants.centripetalScaling = 0.0005;

        FollowerConstants.pathEndTimeoutConstraint = 100;
        FollowerConstants.pathEndTValueConstraint = 0.995;
        FollowerConstants.pathEndVelocityConstraint = 0.1;
        FollowerConstants.pathEndTranslationalConstraint = 0.1;
        FollowerConstants.pathEndHeadingConstraint = 0.007;
    }
}
