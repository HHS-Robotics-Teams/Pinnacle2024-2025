package org.firstinspires.ftc.teamcode.OpModes.Auto;

import static org.firstinspires.ftc.teamcode.Constants.Fields.BVM;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowSpecimenScoring;
import static org.firstinspires.ftc.teamcode.Constants.Fields.IDEALBATTERYV;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHomePosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSpecimenWallPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyBVM;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.batteryVoltageSensor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@Autonomous (name = "Spline testing ",group = "testing ")
public class SplineTesting extends LinearOpMode {


    @Override
    public void runOpMode() throws InterruptedException {
        RobotHardware.init(hardwareMap);
        applyPowers();

        while (opModeInInit()) {
            intakeWristServo.setPosition(WristLeft);
            intakeElbowServo.setPosition(ElbowSpecimenScoring);
            tiltMotor.setTargetPosition(TiltMinPosition);
            slideMotor.setTargetPosition(0);
        }
        while (opModeIsActive()){
            applyBVM();
        }

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        Pose2d startPose = new Pose2d(11, -61, Math.toRadians(270));
        drive.setPoseEstimate(startPose);

        TrajectorySequence forwardTrajectory = drive.trajectorySequenceBuilder(startPose)
                // move preload to high chamber
                .addDisplacementMarker(() -> {
                    intakeWristServo.setPosition(WristLeft);
                    intakeElbowServo.setPosition(ElbowSpecimenScoring);
                    tiltMotor.setTargetPosition(TiltMinPosition);
                    slideMotor.setTargetPosition(0);
                })
                .forward(14 * BVM)
                .waitSeconds(5)
                .splineToLinearHeading(new Pose2d(48, -12, Math.toRadians(90)), Math.toRadians(0))
                .waitSeconds(5)
                .build();
        waitForStart();

        if (isStopRequested()) {
            return;
        }

        drive.followTrajectorySequence(forwardTrajectory);

        telemetry.addData("Status", "Autonomous Complete");
        telemetry.update();
    }
}
