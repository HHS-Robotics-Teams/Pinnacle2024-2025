package org.firstinspires.ftc.teamcode.OpModes;

import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@Autonomous(name = "Test Auto", group = "idk")
public class TestAuto extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize the drive object
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        // Define the starting position
        Pose2d startPose = new Pose2d(0, 0, 0);
        drive.setPoseEstimate(startPose);

        // Build a simple trajectory
        Trajectory forwardTrajectory = drive.trajectoryBuilder(startPose)
                .addDisplacementMarker(() -> {
                    tiltMotor.setTargetPosition(TiltHighChamber);
                    slideMotor.setTargetPosition(SlideHighChamber);
        })
                .forward(24) // Move forward 24 inches
                .build();

        // Wait for the start signal
        waitForStart();

        if (isStopRequested()) return;

        // Follow the trajectory
        drive.followTrajectory(forwardTrajectory);

        // Optionally, report completion
        telemetry.addData("Status", "Autonomous Complete");
        telemetry.update();
    }
}