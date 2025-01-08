package PrimeTech.OpModes.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Autonomous(name = "Squircle", group = "Auto")
public class Squircle extends OpMode {
    private Telemetry telemetryA;
    private Follower follower;
    private PathChain generatedPath;

    @Override
    public void init() {
        follower = new Follower(hardwareMap);

        PathBuilder builder = new PathBuilder();

        builder
                .addPath(
                        new BezierCurve(
                                new Point(24.000, 72.000, Point.CARTESIAN),
                                new Point(24.000, 120.000, Point.CARTESIAN),
                                new Point(72.000, 120.000, Point.CARTESIAN)
                        )
                )
                .setTangentHeadingInterpolation()
                .addPath(
                        new BezierCurve(
                                new Point(72.000, 120.000, Point.CARTESIAN),
                                new Point(120.000, 120.000, Point.CARTESIAN),
                                new Point(120.000, 72.000, Point.CARTESIAN)
                        )
                )
                .setTangentHeadingInterpolation()
                .addPath(
                        new BezierCurve(
                                new Point(120.000, 72.000, Point.CARTESIAN),
                                new Point(120.000, 24.000, Point.CARTESIAN),
                                new Point(72.000, 24.000, Point.CARTESIAN)
                        )
                )
                .setTangentHeadingInterpolation()
                .addPath(
                        new BezierCurve(
                                new Point(72.000, 24.000, Point.CARTESIAN),
                                new Point(24.000, 24.000, Point.CARTESIAN),
                                new Point(24.000, 72.000, Point.CARTESIAN)
                        )
                )
                .setTangentHeadingInterpolation();

        generatedPath = builder.build();

        follower.followPath(generatedPath);

        telemetryA = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        telemetryA.addLine("Running Generated Path Auto");
        telemetryA.update();
    }

    @Override
    public void loop() {
        follower.update();
        if (follower.atParametricEnd()) {
            follower.followPath(generatedPath);
        }

        follower.telemetryDebug(telemetryA);
    }
}
