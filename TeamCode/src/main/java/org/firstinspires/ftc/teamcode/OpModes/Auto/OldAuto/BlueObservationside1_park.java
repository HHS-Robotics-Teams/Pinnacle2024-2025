package org.firstinspires.ftc.teamcode.OpModes.Auto.OldAuto;

import static org.firstinspires.ftc.teamcode.Constants.Fields.BVM;
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

/* Blue observation side auto scores preload, and moves to colored samples to
observation zone for telop and parks, scoring 13 pts
*/
@Disabled

    @Autonomous(name = "BlueObservationside Auto 1 + Park", group = "Comp Auto")
    public class BlueObservationside1_park  extends LinearOpMode {

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

            Pose2d startPose = new Pose2d(-8.25, 63.5, Math.toRadians(270));
            drive.setPoseEstimate(startPose);

            TrajectorySequence forwardTrajectory = drive.trajectorySequenceBuilder(startPose)
                    // move preload to high chamber
                    .addTemporalMarker(() -> {
                        intakeElbowServo.setPosition(ElbowRight);
                        intakeWristServo.setPosition(WristSpecimenWallPickup);
                        slideMotor.setTargetPosition(SlideHighChamber);
                        tiltMotor.setTargetPosition(TiltHighChamber);
                    })
                    .forward(20.5)
                    // score preload
                    .addTemporalMarker(() -> {
                        slideMotor.setTargetPosition(SlideHighChamber);
                        intakeWristServo.setPosition(WristRight);
                    })
                    // move to 1st sample
                    .back(20.5)
                    .addTemporalMarker(() -> {
                        tiltMotor.setTargetPosition(TiltHomePosition);
                        intakeElbowServo.setPosition(ElbowRight);
                        slideMotor.setTargetPosition(SlideMinPosition);
                        intakeWristServo.setPosition(WristLeft);
                    })
                    .strafeRight(26)
                    .forward(52)
                    .strafeRight(11 )
                    // //moving 1st sample to obo zone
                    .back(45)
                    // move to 2nd sample
                    .forward(45)
                    .strafeRight(10.5)
                    // moving 2nd sample
                    .back(45)
                    // park

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
            telemetry.addData("BVM",BVM);
            telemetry.update();
        }

    }




