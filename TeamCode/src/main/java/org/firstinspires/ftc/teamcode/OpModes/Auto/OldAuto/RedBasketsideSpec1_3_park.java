package org.firstinspires.ftc.teamcode.OpModes.Auto.OldAuto;


import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;

import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighBucket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHomePosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltLowChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristHorizontalPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSpecimenWallPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.batteryVoltageSensor;

import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;



/*
 Red basket side starts in F3 Square, it scores a preload specimen,
 then moves to yellow samples to score into high basket. it should be albe to score 2
 and park in assent zone 2, scoring 29 pts
 */
@Disabled
@Autonomous (name = "RedBasket side spec 1+2+Park", group = "Comp Auto")
public class RedBasketsideSpec1_3_park extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        RobotHardware.init(hardwareMap);
        applyPowers();

        while (opModeInInit()) {
            intakeWristServo.setPosition(WristLeft);
            intakeElbowServo.setPosition(ElbowRight);
            tiltMotor.setTargetPosition(TiltMinPosition);
            slideMotor.setTargetPosition(0);
        }
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        Pose2d startPose = new Pose2d(-8.25, -63.5, Math.toRadians(90));
        drive.setPoseEstimate(startPose);

        TrajectorySequence forwardTrajectory = drive.trajectorySequenceBuilder(startPose)
                // move preload to high chamber
                .addDisplacementMarker(() -> {
                    intakeElbowServo.setPosition(ElbowRight);
                    intakeWristServo.setPosition(WristSpecimenWallPickup);
                    slideMotor.setTargetPosition(SlideHighChamber);
                    tiltMotor.setTargetPosition(TiltHighChamber);
                })
                .forward(20)
                // sore preload
                .addDisplacementMarker(() -> {
                    slideMotor.setTargetPosition(SlideHighChamber);
                    intakeWristServo.setPosition(WristRight);
                })
                // move to 1st sample
                .back(8)
                .addDisplacementMarker(() -> {
                    tiltMotor.setTargetPosition(TiltHomePosition);
                    intakeElbowServo.setPosition(ElbowRight);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    intakeWristServo.setPosition(.58);
                })
                .lineTo(new Vector2d(-49.25,-36.5))
                //.strafeLeft(46)
                .waitSeconds(.25)
                // arm moving for 1st sample pickup
                .addDisplacementMarker(() -> {
                    tiltMotor.setTargetPosition(175);
                    intakeElbowServo.setPosition(ElbowRight);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    intakeWristServo.setPosition(WristHorizontalPickup);
                 //   intakeCRServo.setPower(-1);
                })
                .waitSeconds(1)
                // picked up 1st sample
                .addDisplacementMarker(()->{
                 //   intakeCRServo.setPower(0);
                })
               // move to bucket position
                .lineToLinearHeading(new Pose2d(-49, -63.5, Math.toRadians(200)))
                // move arm to high bucket
                .addDisplacementMarker(()->{
                    tiltMotor.setTargetPosition(TiltHighBucket);
                    intakeWristServo.setPosition(WristCenter);
                    slideMotor.setTargetPosition(SlideHighBucket);
                    intakeElbowServo.setPosition(ElbowCenter);
                })
                // score 1st sample outtake sample
                .addDisplacementMarker(()->{
                   // intakeCRServo.setPower(1);
                })
                .waitSeconds(.25)
                .addDisplacementMarker(()->{
                   // intakeCRServo.setPower(0);
                    tiltMotor.setTargetPosition(TiltHomePosition);
                    intakeElbowServo.setPosition(ElbowRight);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    intakeWristServo.setPosition(WristRight);
                })
                // move to 2nd sample
                .lineToLinearHeading(new Pose2d(-59, -63.5, Math.toRadians(90)))
                // arm moving for 2nd sample pickup
          /*      .addTemporalMarker(()->{
                    intakeWristServo.setPosition(WristRight);
                    intakeElbowServo.setPosition(ElbowSpecimenScoring);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    tiltMotor.setTargetPosition(280);
                    intakeCRServo.setPower(-1);
                })
                .waitSeconds(.25)
                // arm tilting down for 2nd sample pickup
                .addTemporalMarker(()->{
                    slideMotor.setTargetPosition(SlideMinPosition);
                    tiltMotor.setTargetPosition(240);
                    intakeCRServo.setPower(-1);
                })
                .waitSeconds(1)
                // picked up 2nd sample
                .addTemporalMarker(()->{
                    intakeCRServo.setPower(0);
                })
          */      .build();

        waitForStart();

        if (isStopRequested()) {
            return;
        }

        drive.followTrajectorySequence(forwardTrajectory);

        telemetry.addData("Status", "Autonomous Complete");
        telemetry.addData("slide ticks", slideMotor.getTargetPosition());
        telemetry.addData("slide pos", slideMotor.getCurrentPosition());
        telemetry.addData("tilt ticks", tiltMotor.getTargetPosition());
        telemetry.addData("tilt pos", tiltMotor.getCurrentPosition());
        telemetry.update();
    }
}
