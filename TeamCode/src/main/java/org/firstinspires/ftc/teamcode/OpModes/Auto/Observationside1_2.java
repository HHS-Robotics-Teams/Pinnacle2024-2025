package org.firstinspires.ftc.teamcode.OpModes.Auto;


import static org.firstinspires.ftc.teamcode.Constants.Fields.BVM;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowSpecimenScoring;
import static org.firstinspires.ftc.teamcode.Constants.Fields.IDEALBATTERYV;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideLowChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHomePosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltPickupPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSpecimenWallPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyBVM;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.batteryVoltageSensor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeCRServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@Autonomous (name = "Observationside Auto 1 + 2", group = "idk")
public class Observationside1_2 extends LinearOpMode {



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

        Pose2d startPose = new Pose2d(11, -61, Math.toRadians(-90));
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
                // score preload
                .addTemporalMarker(() -> {
                    slideMotor.setTargetPosition(SlideHighChamber);
                    intakeWristServo.setPosition(WristRight);
                })
                // move to 1st sample
                .back(18)
                .addTemporalMarker(() -> {
                    tiltMotor.setTargetPosition(TiltHomePosition);
                    intakeElbowServo.setPosition(ElbowSpecimenScoring);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    intakeWristServo.setPosition(WristLeft);
                })
                .strafeRight(26)
                .forward(44)
                .strafeRight(11 )
                .back(33)
                .forward(33)
                .strafeRight(11)
                .back(33 )
                .forward(4)
                .turn(Math.toRadians(153))
                .addTemporalMarker(() -> {
                    intakeElbowServo.setPosition(ElbowSpecimenScoring);
                    intakeWristServo.setPosition(WristSpecimenWallPickup);
                })
                .waitSeconds(0.5)
                .forward(5)
                .addTemporalMarker(() -> {
                    tiltMotor.setTargetPosition(656);
                    slideMotor.setTargetPosition(280); //428
                    intakeCRServo.setPower(1);
                })
                .waitSeconds(1)
                .addTemporalMarker(() -> {
                    tiltMotor.setTargetPosition(656);
                    slideMotor.setTargetPosition(290); //428
                    intakeCRServo.setPower(0);
                })
                .waitSeconds(1)
                .addTemporalMarker(() -> {
                    tiltMotor.setTargetPosition(656);
                    slideMotor.setTargetPosition(SlideMinPosition);
                })
                .turn(Math.toRadians(-153))
                .strafeLeft(48)
                .addTemporalMarker(() -> {
                    tiltMotor.setTargetPosition(656);
                    slideMotor.setTargetPosition(SlideMinPosition);
                })



                /*.splineToLinearHeading(new Pose2d(48, -12, Math.toRadians(-90)), Math.toRadians(0))
                .forward(52) //1st sample in observation zone
                .back(52) //move to 2nd sample
                .strafeLeft(10)
                // move to 2nd sample to observation zone
                .addTemporalMarker(()-> {
                    tiltMotor.setTargetPosition(TiltPickupPosition);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    intakeElbowServo.setPosition(ElbowSpecimenScoring);
                    intakeWristServo.setPosition(WristSpecimenWallPickup);
                })
                .forward(52)
                // pick up 1st specimen
*/







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
