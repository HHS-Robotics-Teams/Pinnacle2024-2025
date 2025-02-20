package org.firstinspires.ftc.teamcode.OpModes.Auto.SensingAuto;


import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_closed;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighBucketBackwardsAuto;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideTickThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucketBackwardsAuto;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucketLastSpeciemnAuto;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltTickThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSampleBucketScore;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
import static org.firstinspires.ftc.teamcode.Constants.Fields.isSlideIncrementing;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.colorSensor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intake_claw_servo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.resetEncoders;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants.DetectedColorAndDistance;
import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@Config
@Autonomous (name = " Sensing Basket side Specimen 1+3", group = "Comp Auto")
public class SensingAutoBasketSideSpecimen extends OpMode {

    double PoseXWhenCollect = 0;
    double PoseYWhenCollect = 0;
    double HeadingWhenCollect = 0;
    double TiltWhenCollect = 0;
    double SlideWhenCollect = 0;

    TrajectorySequence goFoward;
    TrajectorySequence goCollect;
    TrajectorySequence goBack;
    TrajectorySequence goScore;
    TrajectorySequence goCollectAgain;
    TrajectorySequence turnAgain;
    TrajectorySequence turnBacktoScore;

    TrajectorySequence turntoCollectLast;
    TrajectorySequence turntoScoreLast;
    TrajectorySequence park;

    SampleMecanumDrive drive;


    public enum AutoState {
        PRELOAD,
        DRIVE,
        DUNK,
        GO_BACK,
        COLLECT_ONE,
        EXTEND,
        MOVE_GRADUALLY,
        GRAB_SAMPLE,
        TURN_TO_SCORE,
        TILT_TO_SCORE,
        EXTEND_TO_SCORE,
        RESET_AND_CYCLE_TWO,
        EXTEND_TO_SECOND_SAMPLE,
        MOVE_GRADUALLY_SECOND_SAMPLE,
        LOWER_SLIDE_RESET,
        COLLECT_TWO,
        RESET_FOR_PATH_UPDATE,
        EXTEND_TO_SCORE_TWO,
        RESET_AND_CYCLE_THREE,
        TURN_TO_SCORE_LAST,
        RESET_TILT_FOR_LAST_CYCLE,
        EXTEND_TO_THIRD_SAMPLE,
        MOVE_GRADUALLY_THIRD_SAMPLE,
        GRAB_LAST_SAMPLE,
        RETRACT_LAST_SAMPLE,
        EXTEND_TO_SCORE_LAST_SAMPLE,
        DEPOSIT_LAST_SAMPLE,
        DRIVE_TO_PARK,
        FINISH
    }

    AutoState autoState = AutoState.PRELOAD;
    boolean beginLoweringArm = false;
    int cyclesDone = 0;

    ElapsedTime testTimer = new ElapsedTime();
    ElapsedTime dunkTimer = new ElapsedTime();
    ElapsedTime goBackTimer = new ElapsedTime();
    ElapsedTime grabTimer = new ElapsedTime();
    ElapsedTime depositTimer = new ElapsedTime();
    ElapsedTime extendToSecondSampleTimer = new ElapsedTime();
    ElapsedTime tiltTimer = new ElapsedTime();

    ElapsedTime LowerTileTimer = new ElapsedTime();

    public void init() {

        RobotHardware.init(hardwareMap);
        resetEncoders();

        intakeWristServo.setPosition(.75);
        intakeElbowServo.setPosition(ElbowRight);
        tiltMotor.setTargetPosition(TiltMinPosition);
        slideMotor.setTargetPosition(0);
        intake_claw_servo.setPosition(Claws_closed);

        applyPowers();


        drive = new SampleMecanumDrive(hardwareMap);

        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(0));
        drive.setPoseEstimate(startPose);


        goFoward = drive.trajectorySequenceBuilder(new Pose2d(0, 0, Math.toRadians(0)))
                .splineToConstantHeading(new Vector2d(24, 0), Math.toRadians(0))
                .build();
        goBack = drive.trajectorySequenceBuilder(new Pose2d(24, 0, Math.toRadians(0)))
                .back(7.5)
                .build();
        goCollect = drive.trajectorySequenceBuilder(new Pose2d(16, 0, Math.toRadians(0)))
                .splineToConstantHeading(new Vector2d(13, 39), Math.toRadians(-14))
                .build();
        goScore = drive.trajectorySequenceBuilder(new Pose2d(13, 39, Math.toRadians(-14)))
                .turn(Math.toRadians(-40))
                .back(8.5)
                .build();
        turnAgain = drive.trajectorySequenceBuilder(new Pose2d(6, 46, Math.toRadians(-40)))
                .splineToLinearHeading(new Pose2d(13, 49, Math.toRadians(0)), Math.toRadians(-47))
                .build();
        turnBacktoScore = drive.trajectorySequenceBuilder(new Pose2d(13, 41, Math.toRadians(19)))
                .turn(Math.toRadians(-45))
                .build();
        turntoCollectLast = drive.trajectorySequenceBuilder(new Pose2d(13, 41, Math.toRadians(-45)))
                .turn(Math.toRadians(73))
                .build();
        turntoScoreLast = drive.trajectorySequenceBuilder(new Pose2d(13, 41, Math.toRadians(22)))
                //.splineToLinearHeading(new Pose2d(13, 39, Math.toRadians(-50)), Math.toRadians(42))
                .turn(Math.toRadians(-54))
                .build();
        park = drive.trajectorySequenceBuilder(new Pose2d(13, 41, Math.toRadians(-10)))
                .splineToLinearHeading(new Pose2d(53, 20, Math.toRadians(-54)), Math.toRadians(-52))
                .build();
        /*goCollectAgain = drive.trajectorySequenceBuilder(new Pose2d(13, 39, Math.toRadians(0)))
                .splineToLinearHeading(new Pose2d(13.00001, 39.00001, Math.toRadians(-47)), Math.toRadians(-47))
                .build();*/

        drive.followTrajectorySequenceAsync(goFoward);
    }

    public void loop() {

        DetectedColorAndDistance.updateColor(colorSensor);

        String detectedColor = DetectedColorAndDistance.getColor();

        drive.update();
        telemetry.addData("Detected Color", DetectedColorAndDistance.getColor());
        telemetry.addData("state", autoState);
        telemetry.addData("timer", testTimer.seconds());
        telemetry.addData("CURRENT X", drive.getPoseEstimate().getX());
        telemetry.addData("CURRENT Y", drive.getPoseEstimate().getY());
        telemetry.addData("tilt arm pos", tiltMotor.getCurrentPosition());
        telemetry.addData("slide ticks", slideMotor.getCurrentPosition());
//        telemetry.addData("heading", drive.getPoseEstimate().getHeading());
//        telemetry.addData("heading in radians", Math.toRadians(drive.getPoseEstimate().getHeading()));
//        telemetry.addData("heading converted", Math.toRadians(Math.abs(((drive.getPoseEstimate().getHeading() * 180) / 3.14159))));
      //  telemetry.addData("heading error, should be sub 2 to pass", (Math.abs(((drive.getPoseEstimate().getHeading() * 180) / 3.14159) - 23) <= 2));

//        telemetry.addData("\nPoseXWhenCollect: ", PoseXWhenCollect);
//        telemetry.addData("PoseYWhenCollect: ", PoseYWhenCollect);
//        telemetry.addData("HeadingWhenCollect: ", HeadingWhenCollect);
//        telemetry.addData("TiltWhenCollect: ", TiltWhenCollect);
//        telemetry.addData("SlideWhenCollect: ", SlideWhenCollect);

        switch (autoState) {
            case PRELOAD:
                tiltMotor.setTargetPosition(TiltHighChamber + 400);
                intakeElbowServo.setPosition(ElbowRight);
                intakeWristServo.setPosition(WristRight);
                testTimer.reset();
                autoState = AutoState.DRIVE;
                break;

            case DRIVE:
                slideMotor.setTargetPosition(SlideHighChamber + 12);
                if (testTimer.seconds() >= 1.65) {
                    dunkTimer.reset();
                    autoState = AutoState.DUNK;
                    break;
                }
                break;
            case DUNK:
                intakeWristServo.setPosition(WristRight);
                tiltMotor.setTargetPosition(1000);
                if (dunkTimer.seconds() >= 0.3) {
                    intake_claw_servo.setPosition(Claws_open);
                    slideMotor.setTargetPosition(0);
                    drive.followTrajectorySequenceAsync(goBack);
                    goBackTimer.reset();
                    autoState = AutoState.GO_BACK;
                    break;
                }
                break;
            case GO_BACK:
                if (goBackTimer.seconds() > 0.4) {
                    tiltMotor.setTargetPosition(525);
                    drive.followTrajectorySequenceAsync(goCollect);
                    autoState = AutoState.COLLECT_ONE;
                    break;
                }
                break;
            case COLLECT_ONE:
                intakeElbowServo.setPosition(ElbowLeft);
                intakeWristServo.setPosition(WristCenter);
                tiltMotor.setTargetPosition(525);
                LowerTileTimer.reset();
                autoState = AutoState.EXTEND;
                break;
            case EXTEND:

                if ((Math.abs(drive.getPoseEstimate().getX() - 14) <= 2) && (Math.abs(drive.getPoseEstimate().getY() - 40) <= 2)
                        && Math.abs(tiltMotor.getCurrentPosition() - 525) <= 5 && LowerTileTimer.seconds() >= 2) {
                    slideMotor.setTargetPosition(370);
                    grabTimer.reset();
                    autoState = AutoState.MOVE_GRADUALLY;
                    break;
                    }
//
                break;
            case  MOVE_GRADUALLY:
                if (slideMotor.getCurrentPosition() >= 370){
                    if (detectedColor.equals("Yellow")){
                        grabTimer.reset();
                        tiltMotor.setPower(0);
                        autoState = AutoState.GRAB_SAMPLE;
                        break;
                    } else if (isSlideIncrementing){
                        slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + 20);
                        isSlideIncrementing = false;
                    }
                    else if (!slideMotor.isBusy()){
                        isSlideIncrementing = true;
                        if (slideMotor.getCurrentPosition() >= (395)){
                            grabTimer.reset();
                            tiltMotor.setPower(0);
                            autoState = AutoState.GRAB_SAMPLE;
                            break;
                        }
                    }
                }
                break;

            case GRAB_SAMPLE:
                if (grabTimer.seconds() >= 0.5) {
                    intake_claw_servo.setPosition(Claws_closed);
                    if (grabTimer.seconds() >= 0.9) {
                        tiltMotor.setPower(1);
                        autoState = AutoState.TURN_TO_SCORE;
                        break;
                    }
                }
                break;
            case TURN_TO_SCORE:
                slideMotor.setTargetPosition(0);
                drive.followTrajectorySequenceAsync(goScore);
                autoState = AutoState.TILT_TO_SCORE;
                break;

            case TILT_TO_SCORE:
                if (slideMotor.getCurrentPosition() <= 10) {
                    tiltMotor.setTargetPosition(TiltHighBucketBackwardsAuto - 5);
                    intakeElbowServo.setPosition(ElbowRight);
                    tiltTimer.reset();
                    autoState = AutoState.EXTEND_TO_SCORE;
                    break;
                }
                break;
            case EXTEND_TO_SCORE:
                if (tiltTimer.seconds() > 2) {
                    if (Math.abs(tiltMotor.getCurrentPosition() - TiltHighBucketBackwardsAuto - 5) <= TiltTickThreshold) {
                        slideMotor.setTargetPosition(SlideHighBucketBackwardsAuto);
                        intakeWristServo.setPosition(.3);
                        if (Math.abs(slideMotor.getCurrentPosition() - SlideHighBucketBackwardsAuto) <= SlideTickThreshold) {
                            //intakeElbowServo.setPosition(ElbowRight);
                            intakeWristServo.setPosition(WristSampleBucketScore);
                            depositTimer.reset();
                            autoState = AutoState.RESET_AND_CYCLE_TWO;
                            break;
                        }
                        if (tiltTimer.seconds() > 4 ) {
                            intakeWristServo.setPosition(WristSampleBucketScore);
                            depositTimer.reset();
                            autoState = AutoState.RESET_AND_CYCLE_TWO;
                            break;
                        }
                    }
                }
                break;
            case RESET_AND_CYCLE_TWO:
                if (depositTimer.seconds() > 0.2) {
                    intakeElbowServo.setPosition(ElbowRight);
                    intakeWristServo.setPosition(WristSampleBucketScore);
                    if (depositTimer.seconds() > 0.6) {
                        intake_claw_servo.setPosition(Claws_open);
                        extendToSecondSampleTimer.reset();
                        autoState = AutoState.EXTEND_TO_SECOND_SAMPLE;
                        break;
                    }
                }
                break;
            case EXTEND_TO_SECOND_SAMPLE:
                if (extendToSecondSampleTimer.seconds() > 0.4) {
                    //intakeElbowServo.setPosition(ElbowLeft);
                    intakeWristServo.setPosition(WristCenter);
                    drive.update();
                    if ((Math.abs(drive.getPoseEstimate().getX() - 8) <= 2) && (Math.abs(drive.getPoseEstimate().getY() - 45) <= 2) &&
                            (extendToSecondSampleTimer.seconds() > 0.7)) {
                        slideMotor.setTargetPosition(0);
                        if (slideMotor.getCurrentPosition() <= SlideTickThreshold) {
                            tiltMotor.setTargetPosition(533);
                            drive.followTrajectorySequenceAsync(turnAgain);
                            intakeElbowServo.setPosition(ElbowLeft);
                            autoState = AutoState.LOWER_SLIDE_RESET;
                            break;
                        }
                    }
                }
                break;
            case LOWER_SLIDE_RESET:
                if (Math.abs(tiltMotor.getCurrentPosition() - 533) < 25 && extendToSecondSampleTimer.seconds() > 2.0) {
                    slideMotor.setTargetPosition(370);
                    grabTimer.reset();
                    isSlideIncrementing = true;
                    autoState = AutoState.MOVE_GRADUALLY_SECOND_SAMPLE;
                    break;
                    }
                break;

            case MOVE_GRADUALLY_SECOND_SAMPLE:
                if (slideMotor.getCurrentPosition() >= 370){
                if (detectedColor.equals("Yellow")){
                    grabTimer.reset();
                    tiltMotor.setPower(0);
                    autoState = AutoState.COLLECT_TWO;
                    break;
                } else if (isSlideIncrementing){
                    slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + 20);
                    isSlideIncrementing = false;
                }
                else if (!slideMotor.isBusy()){
                    isSlideIncrementing = true;
                    if (slideMotor.getCurrentPosition() >= (391)){
                        grabTimer.reset();
                        tiltMotor.setPower(0);
                        autoState =AutoState.COLLECT_TWO;
                        break;
                    }
                }
            }
                break;

            case COLLECT_TWO:
                if (grabTimer.seconds() >= 0.5) {
                    intake_claw_servo.setPosition(Claws_closed);
                    if (grabTimer.seconds() >= 1.1) {
                        drive.followTrajectorySequenceAsync(turnBacktoScore);
                        testTimer.reset();
                        tiltMotor.setPower(1);
                        autoState = AutoState.RESET_FOR_PATH_UPDATE;
                        break;
                    }
                }
                break;
            case RESET_FOR_PATH_UPDATE:
                slideMotor.setTargetPosition(0);
                if ((slideMotor.getCurrentPosition() < SlideTickThreshold) && grabTimer.seconds() >= 2.5) {
                    tiltMotor.setTargetPosition(TiltHighBucketBackwardsAuto);
                    intakeElbowServo.setPosition(ElbowRight);

                    if (testTimer.seconds() > 0.6) {
                        if (Math.abs(tiltMotor.getCurrentPosition() - (TiltHighBucketBackwardsAuto)) <= TiltTickThreshold) {
                            slideMotor.setTargetPosition(SlideHighBucketBackwardsAuto);
                            intakeWristServo.setPosition(0.3);
                            autoState = AutoState.EXTEND_TO_SCORE_TWO;
                            break;
                        }
                    }
                }
                break;
            case EXTEND_TO_SCORE_TWO:
                slideMotor.setPower(1);
                if (Math.abs(slideMotor.getCurrentPosition() - SlideHighBucketBackwardsAuto) < SlideTickThreshold) {
                    //intakeElbowServo.setPosition(ElbowRight);
                    intakeWristServo.setPosition(WristSampleBucketScore);
                    depositTimer.reset();
                    autoState = AutoState.RESET_AND_CYCLE_THREE;
                    break;
                }
                break;
            case RESET_AND_CYCLE_THREE:
                if (depositTimer.seconds() >= 0.4) {
                    intake_claw_servo.setPosition(Claws_open);
                    if (depositTimer.seconds() >= 0.9) {
                        //intakeElbowServo.setPosition(ElbowLeft);
                        intakeWristServo.setPosition(WristCenter);
                        if (depositTimer.seconds() >= 1.4) {
                            slideMotor.setTargetPosition(0);
                            autoState = AutoState.TURN_TO_SCORE_LAST;
                            break;

                        }
                    }
                }
                break;
            case TURN_TO_SCORE_LAST:
                if (slideMotor.getCurrentPosition() <= 300) {
                    tiltMotor.setTargetPosition(890);
                    intakeElbowServo.setPosition(ElbowLeft);
                }
                if (slideMotor.getCurrentPosition() <= SlideTickThreshold) {
                    drive.followTrajectorySequenceAsync(turntoCollectLast);
                    autoState = AutoState.RESET_TILT_FOR_LAST_CYCLE;
                    break;
                }
                break;
            case RESET_TILT_FOR_LAST_CYCLE:
                if (slideMotor.getCurrentPosition() <= SlideTickThreshold) {
                    tiltMotor.setTargetPosition(535);
                    tiltTimer.reset();
                    autoState = AutoState.EXTEND_TO_THIRD_SAMPLE;
                    break;
                }
                break;
            case EXTEND_TO_THIRD_SAMPLE:
                if (((tiltMotor.getCurrentPosition() - 535) <= 10) && (tiltTimer.seconds() >= .9)) {
                    slideMotor.setTargetPosition(420);
                    intakeElbowServo.setPosition(ElbowLeft);
                    intakeWristServo.setPosition(WristCenter);
                    isSlideIncrementing = true;
                    autoState = AutoState.MOVE_GRADUALLY_THIRD_SAMPLE;
                    break;
                }
                break;
            case MOVE_GRADUALLY_THIRD_SAMPLE:
                    if (slideMotor.getCurrentPosition() >= 420) {
                        if (detectedColor.equals("Yellow")) {
                            grabTimer.reset();
                            tiltMotor.setPower(0);
                            autoState = AutoState.GRAB_LAST_SAMPLE;
                            break;
                        } else if (isSlideIncrementing){
                            slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + 20);
                            isSlideIncrementing = false;
                        }
                        else if (!slideMotor.isBusy()){
                            isSlideIncrementing = true;
                            if (slideMotor.getCurrentPosition() >= (441)){
                                grabTimer.reset();
                                tiltMotor.setPower(0);
                                autoState = AutoState.GRAB_LAST_SAMPLE;
                                break;
                            }
                        }
                    }

                break;

            case GRAB_LAST_SAMPLE:
                    tiltMotor.setPower(0);
                    PoseXWhenCollect = drive.getPoseEstimate().getX();
                    PoseYWhenCollect = drive.getPoseEstimate().getY();
                    HeadingWhenCollect = drive.getPoseEstimate().getHeading();
                    TiltWhenCollect = tiltMotor.getCurrentPosition();
                    SlideWhenCollect = slideMotor.getCurrentPosition();
                    grabTimer.reset();
                    autoState = AutoState.RETRACT_LAST_SAMPLE;
                    break;

            case RETRACT_LAST_SAMPLE:
                if (grabTimer.seconds() >= 0.4) {
                    intake_claw_servo.setPosition(Claws_closed);
                    if (grabTimer.seconds() >= .9) {
                        tiltMotor.setPower(1);
                        slideMotor.setTargetPosition(0);
                        drive.followTrajectorySequenceAsync(turntoScoreLast);
                        if (slideMotor.getCurrentPosition() <= SlideTickThreshold) {
                            tiltMotor.setTargetPosition(TiltHighBucketLastSpeciemnAuto);
                            intakeWristServo.setPosition(0.3);
                            intakeElbowServo.setPosition(ElbowRight);
                            testTimer.reset();
                            autoState = AutoState.EXTEND_TO_SCORE_LAST_SAMPLE;
                            break;
                        }
                    }
                }
                break;
            case EXTEND_TO_SCORE_LAST_SAMPLE:
                if (testTimer.seconds() >= .8 ) {
                    if (Math.abs(tiltMotor.getCurrentPosition() - TiltHighBucketLastSpeciemnAuto) <= TiltTickThreshold) {
                        slideMotor.setTargetPosition(SlideHighBucketBackwardsAuto);
                        if (Math.abs(slideMotor.getCurrentPosition() - SlideHighBucketBackwardsAuto) <= SlideTickThreshold) {
                            //intakeElbowServo.setPosition(ElbowRight);
                            intakeWristServo.setPosition(WristSampleBucketScore);
                            grabTimer.reset();
                            autoState = AutoState.DEPOSIT_LAST_SAMPLE;
                            break;
                        }
                    }
                }
                break;
            case DEPOSIT_LAST_SAMPLE:
                if (grabTimer.seconds() >= 0.4) {
                    intake_claw_servo.setPosition(Claws_open);
                    if (grabTimer.seconds() >= 0.9) {
                        //intakeElbowServo.setPosition(ElbowLeft);
                        intakeWristServo.setPosition(WristCenter);
                        slideMotor.setTargetPosition(0);
                        if (slideMotor.getCurrentPosition() <= 75) {
                            intakeElbowServo.setPosition(ElbowLeft);
                            //tiltMotor.setTargetPosition(1150);
                            //drive.followTrajectorySequenceAsync(park);
                            autoState = AutoState.FINISH;
                            break;
                        }
                    }
                }
                break;
            case DRIVE_TO_PARK:
                if (tiltMotor.getCurrentPosition() > 1100){
                    intakeElbowServo.setPosition(ElbowRight);
                    intakeWristServo.setPosition(WristLeft);
                    slideMotor.setTargetPosition(SlideHighBucketBackwardsAuto);
                    autoState = AutoState.FINISH;
                    break;
                }
                break;
            case FINISH:

                drive.update();

                break;
        }
    }
}


