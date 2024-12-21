package org.firstinspires.ftc.teamcode.OpModes.Auto.OldAuto;

import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;

import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHomePosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
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
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

/* Still A work in progress
Red observation side auto scores preload and moves one colored sample to observation zone
picks specimen of wall and scores it, then goes back to observation zone,
to pick another specimen and score it on high chamber
*/
@Disabled
@Autonomous(name = "RedObservationside Auto 1 + 2", group = "idk")
public class RedObservationside1_2 extends LinearOpMode {

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

            Pose2d startPose = new Pose2d(11, -61, Math.toRadians(90));
            drive.setPoseEstimate(startPose);

            TrajectorySequence forwardTrajectory = drive.trajectorySequenceBuilder(startPose)
                    // move preload to high chamber
                    .addTemporalMarker(() -> {
                        intakeElbowServo.setPosition(ElbowRight);
                        intakeWristServo.setPosition(WristSpecimenWallPickup);
                        slideMotor.setTargetPosition(SlideHighChamber);
                        tiltMotor.setTargetPosition(TiltHighChamber);
                    })
                    .forward(20)
                    // score preload
                    .addTemporalMarker(() -> {
                        slideMotor.setTargetPosition(SlideHighChamber);
                        intakeWristServo.setPosition(WristRight);
                    })
                    // move to 1st sample
                    .back(20)
                    .addTemporalMarker(() -> {
                        tiltMotor.setTargetPosition(TiltHomePosition);
                        intakeElbowServo.setPosition(ElbowRight);
                        slideMotor.setTargetPosition(SlideMinPosition);
                        intakeWristServo.setPosition(WristLeft);
                    })
                    .strafeRight(26)
                    .forward(50)
                    .strafeRight(11 )
                    .back(44)
                    .forward(44)
                    .strafeRight(10.5)
                    .back(44)
                    .forward(4)
                    .turn(Math.toRadians(153))
                    .addTemporalMarker(() -> {
                        intakeElbowServo.setPosition(ElbowRight);
                        intakeWristServo.setPosition(WristSpecimenWallPickup);
                    })
                    .waitSeconds(0.5)
                    .forward(5)
                    .addTemporalMarker(() -> {
                        tiltMotor.setTargetPosition(656);
                        slideMotor.setTargetPosition(280); //428
                      //  intakeCRServo.setPower(1);
                    })
                    .waitSeconds(1)
                    .addTemporalMarker(() -> {
                        tiltMotor.setTargetPosition(656);
                        slideMotor.setTargetPosition(290); //428
                      //  intakeCRServo.setPower(0);
                    })
                    .waitSeconds(1)
                    .addTemporalMarker(() -> {
                        tiltMotor.setTargetPosition(616);
                        slideMotor.setTargetPosition(SlideMinPosition);
                    })
                    .turn(Math.toRadians(-153))
                    .strafeLeft(48+12)
                    .addTemporalMarker(() -> {
                        tiltMotor.setTargetPosition(616);
                        slideMotor.setTargetPosition(SlideMinPosition);
                    })
                    .forward(18)
                    .addTemporalMarker(() -> {
                        intakeElbowServo.setPosition(ElbowRight);
                        intakeWristServo.setPosition(WristSpecimenWallPickup);
                        slideMotor.setTargetPosition(SlideHighChamber);
                        tiltMotor.setTargetPosition(TiltHighChamber);
                    })
                    .waitSeconds(.5)
                    .addTemporalMarker(() -> {
                        slideMotor.setTargetPosition(SlideHighChamber);
                        intakeWristServo.setPosition(WristRight);
                    })
                    // move to 1st sample
                    .back(20)
                    .addTemporalMarker(() -> {
                        tiltMotor.setTargetPosition(TiltHomePosition);
                        intakeElbowServo.setPosition(ElbowRight);
                        slideMotor.setTargetPosition(SlideMinPosition);
                        intakeWristServo.setPosition(WristLeft);
                    })
                    .build();
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


