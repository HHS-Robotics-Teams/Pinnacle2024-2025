package org.firstinspires.ftc.teamcode.drive.opmode;

import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowSpecimenScoring;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
@Autonomous (name = "Strafe left test", group = "drive")

public class StrafeLeftTest extends LinearOpMode {

    public static double DISTANCE = 60; // in

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        RobotHardware.init(hardwareMap);
        applyPowers();

        while (opModeInInit()) {
            intakeWristServo.setPosition(WristLeft);
            intakeElbowServo.setPosition(ElbowSpecimenScoring);
            tiltMotor.setTargetPosition(TiltMinPosition);
            slideMotor.setTargetPosition(0);

            SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

            Trajectory trajectory = drive.trajectoryBuilder(new Pose2d())
                    .strafeLeft(DISTANCE)
                    .build();

            waitForStart();

            if (isStopRequested()) return;

            drive.followTrajectory(trajectory);

            Pose2d poseEstimate = drive.getPoseEstimate();
            telemetry.addData("finalX", poseEstimate.getX());
            telemetry.addData("finalY", poseEstimate.getY());
            telemetry.addData("finalHeading", poseEstimate.getHeading());
            telemetry.update();

            while (!isStopRequested() && opModeIsActive()) ;
        }
    }
}

