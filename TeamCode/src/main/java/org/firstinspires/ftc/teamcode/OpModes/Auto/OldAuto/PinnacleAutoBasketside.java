//package org.firstinspires.ftc.teamcode.OpModes.Auto.OldAuto;
//
////import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
////import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
////
////
////import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
////import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
////import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
////import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHomePosition;
////import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
////import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
////import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
////import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
////import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSpecimenWallPickup;
////import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
////
////import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
////import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
////import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
////import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;
////
////import com.acmerobotics.roadrunner.geometry.Pose2d;
////import com.acmerobotics.roadrunner.trajectory.Trajectory;
////import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
////import com.qualcomm.robotcore.eventloop.opmode.Disabled;
////import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
////import com.qualcomm.robotcore.hardware.DcMotorSimple;
////
////import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
////import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
////import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
//
//@Disabled
//@Autonomous(name = "Pinnacle Basketside Auto", group = "idk")
//public class PinnacleAutoBasketside extends LinearOpMode {
//
//    @Override
//    public void runOpMode() throws InterruptedException {
//
//        RobotHardware.init(hardwareMap);
//        applyPowers();
//
//        while (opModeInInit()) {
//            intakeWristServo.setPosition(WristLeft);
//            intakeElbowServo.setPosition(ElbowRight);
//            tiltMotor.setTargetPosition(TiltMinPosition);
//            slideMotor.setTargetPosition(0);
//        }
//        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
//
//        Pose2d startPose = new Pose2d(11, 61, Math.toRadians(-90));
//        drive.setPoseEstimate(startPose);
//
//        TrajectorySequence forwardTrajectory = drive.trajectorySequenceBuilder(startPose)
//                .addDisplacementMarker(() -> {
//                    intakeElbowServo.setPosition(ElbowRight);
//                })
//                .addDisplacementMarker(() -> {
//                    intakeWristServo.setPosition(WristSpecimenWallPickup);
//                })
//                .addDisplacementMarker(() -> {
//                    slideMotor.setTargetPosition(SlideHighChamber);
//                })
//                .addDisplacementMarker(() -> {
//                    tiltMotor.setTargetPosition(TiltHighChamber);
//                })
//                .forward(19-.15)
//                .addDisplacementMarker(() -> {
//                    slideMotor.setTargetPosition(SlideHighChamber);
//                })
//                .addDisplacementMarker(() -> {
//                    intakeWristServo.setPosition(WristRight);
//                })
//                .back(20)
//                .addDisplacementMarker(() -> {
//                    slideMotor.setTargetPosition(SlideMinPosition);
//                })
//                .addDisplacementMarker(() -> {
//                    tiltMotor.setTargetPosition(350);
//                })
//                .addDisplacementMarker(() -> {
//                    intakeElbowServo.setPosition(ElbowRight);
//                })
//                .addDisplacementMarker(() -> {
//                    intakeWristServo.setPosition(WristSpecimenWallPickup);
//                })
//                .turn(Math.toRadians(90))
//                .back(50)
//                .addDisplacementMarker(() -> {
//                    intakeWristServo.setPosition(WristLeft);
//                })
//                .addDisplacementMarker(() -> {
//                    intakeElbowServo.setPosition(ElbowRight);
//                })
//                .addDisplacementMarker(() -> {
//                    slideMotor.setTargetPosition(SlideMinPosition);
//                })
//                .addDisplacementMarker(() -> {
//                    tiltMotor.setTargetPosition(TiltMinPosition);
//                })
//                .build();
//
//        waitForStart();
//
//        if (isStopRequested()) {
//            return;
//        }
//
//        drive.followTrajectorySequence(forwardTrajectory);
//
//        telemetry.addData("Status", "Autonomous Complete");
//        telemetry.update();
//    }
//}
