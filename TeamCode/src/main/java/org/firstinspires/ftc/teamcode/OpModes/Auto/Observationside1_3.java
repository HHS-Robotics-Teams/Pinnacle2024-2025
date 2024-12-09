package org.firstinspires.ftc.teamcode.OpModes.Auto;


import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowSpecimenScoring;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltPickupPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSpecimenWallPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
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

@Autonomous
public class Observationside1_3 extends LinearOpMode {


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
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        Pose2d startPose = new Pose2d(11, 61, Math.toRadians(90));
        drive.setPoseEstimate(startPose);

        TrajectorySequence forwardTrajectory = drive.trajectorySequenceBuilder(startPose)
                .addTemporalMarker(() -> {
                    intakeElbowServo.setPosition(ElbowSpecimenScoring);
                    intakeWristServo.setPosition(WristSpecimenWallPickup);
                    slideMotor.setTargetPosition(SlideHighChamber);
                    tiltMotor.setTargetPosition(TiltHighChamber);
                    })
                .forward(17)
                .addTemporalMarker(() -> {
                    slideMotor.setTargetPosition(SlideHighChamber);
                    intakeWristServo.setPosition(WristRight);
                })
                .back(14)
                .addTemporalMarker(() -> {
                    tiltMotor.setTargetPosition(TiltMinPosition);
                    intakeElbowServo.setPosition(ElbowSpecimenScoring);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    intakeWristServo.setPosition(WristLeft);
                })
                .splineToLinearHeading(new Pose2d(40, 54, Math.toRadians(-90)), Math.toRadians(0))
                .forward(20)
                .back(20)
                .strafeLeft(10)
                .addTemporalMarker(()-> {
                    tiltMotor.setTargetPosition(TiltPickupPosition);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    intakeElbowServo.setPosition(ElbowSpecimenScoring);
                    intakeWristServo.setPosition(WristSpecimenWallPickup);
                })
                .forward(20)
                







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
