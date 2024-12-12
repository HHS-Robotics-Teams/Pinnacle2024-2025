package org.firstinspires.ftc.teamcode.OpModes.Auto;

import static org.firstinspires.ftc.teamcode.Constants.Fields.BVM;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowSpecimenScoring;
import static org.firstinspires.ftc.teamcode.Constants.Fields.IDEALBATTERYV;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHomePosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSpecimenWallPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.batteryVoltageSensor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeCRServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;



// Still a work in progress
@Autonomous (name = "Basket side 1+3+Park", group = "idk")
public class Basketside1_3_park extends LinearOpMode {
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

        Pose2d startPose = new Pose2d(-11, -61, Math.toRadians(270));
        drive.setPoseEstimate(startPose);

        TrajectorySequence forwardTrajectory = drive.trajectorySequenceBuilder(startPose)
                // move preload to high chamber
                .addTemporalMarker(() -> {
                    intakeElbowServo.setPosition(ElbowSpecimenScoring);
                    intakeWristServo.setPosition(WristSpecimenWallPickup);
                    slideMotor.setTargetPosition(SlideHighChamber);
                    tiltMotor.setTargetPosition(TiltHighChamber);
                })
                .forward(18)
                // sore preload
                .addTemporalMarker(() -> {
                    slideMotor.setTargetPosition(SlideHighChamber);
                    intakeWristServo.setPosition(WristRight);
                })
                // move to 1st sample
                .back(8)
                .addTemporalMarker(() -> {
                    tiltMotor.setTargetPosition(TiltHomePosition);
                    intakeElbowServo.setPosition(ElbowSpecimenScoring);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    intakeWristServo.setPosition(WristRight);
                })
                .strafeLeft(46)
                .waitSeconds(.5)
                // arm moving for sample pickup
                .addTemporalMarker(()->{
                    intakeWristServo.setPosition(WristRight);
                    intakeElbowServo.setPosition(ElbowSpecimenScoring);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    tiltMotor.setTargetPosition(280);
                    intakeCRServo.setPower(-1);
                })
                .waitSeconds(.5)
                // arm tilting down for sample pickup
                .addTemporalMarker(()->{
                    slideMotor.setTargetPosition(SlideMinPosition);
                    tiltMotor.setTargetPosition(240);
                    intakeCRServo.setPower(-1);
                })
                .waitSeconds(1)
                .addTemporalMarker(()->{
                    intakeCRServo.setPower(0);
                })
                .waitSeconds(4)








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
