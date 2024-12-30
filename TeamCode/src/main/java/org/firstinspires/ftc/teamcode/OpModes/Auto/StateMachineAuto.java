package org.firstinspires.ftc.teamcode.OpModes.Auto;



import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_closed;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;

import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSampleBucketScore;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
import static org.firstinspires.ftc.teamcode.Constants.Fields.clawsClosed;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intake_claw_servo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.resetEncoders;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import android.transition.Slide;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;


@Config
@Autonomous (name = "StateMachineRedBasket side spec 1+2+Park", group = "Comp Auto")
public class StateMachineAuto extends OpMode {

    // power value is assigned to a variable which is referenced and changed
    public int ArmTicks = 100;
    public int TiltTickThreshold = 10;
    public int TiltStartPosition = 0;

    public int TiltMinPosition = 75;
    public int IntakeRotateThreshold = 250; //100
    public int TiltPickupPosition = 485;
    public int TiltLowChamber = 500;
    public int TiltHomePosition = 650;
    public int TiltUpThreshold = 1360;

    public  int TiltHighChamber = 1100;
    public  int TiltLowBucket = 1388;
    public  int TiltHighBucket = 1535;
    public int TiltMaxPosition = 1700;
    public int TiltSlowSlowPosition = 2000;
    public int TiltHighBucketBackwards = 2220;


    // slide motor positions
    public int SlideTickThreshold = 15;
    public int SlideTicks = 80;
    public int SlideStartPosition = 0;

    public int SlideMinPosition = 5;
    public int SlideLowBucket = 50;
    public int SlideLowChamber = 250;

    public int SlideHighChamber = 450;
//    public int SlideHighBucket = 1455;
//    public int SlideMaxPosition = 1455;
    public int SlideHighBucketBacwards = 1440;
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
        GOBACK,
        COLLECT_ONE,
        EXTEND,
        GRABSAMPLE,
        TURNTOSCORE,
        TILTTOSCORE,
        EXTENDTOSCORE,
        RESET_AND_CYCLE_TWO,
        EXTEND_TO_SECOND_SAMPLE,
        LOWER_SLIDE_RESET,
        COLLECT_TWO,
        RESET_FOR_PATH_UPDATE,
        EXTENTOSCORE_TWO,
        RESET_AND_CYCLE_THREE,
        TURN_TO_SCORE_LAST,
        RESET_TILT_FOR_LAST_CYCLE,
        EXTEND_TO_THIRD_SAMPLE,
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
    public void init(){

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


        goFoward = drive.trajectorySequenceBuilder(new Pose2d(0,0, Math.toRadians(0)))
                .splineToConstantHeading(new Vector2d(24,0),Math.toRadians(0))
                .build();
        goBack = drive.trajectorySequenceBuilder(new Pose2d(24,0, Math.toRadians(0)))
                .back(8)
                .build();
        goCollect = drive.trajectorySequenceBuilder(new Pose2d(16,0, Math.toRadians(0)))
                .splineToConstantHeading(new Vector2d(13,39),Math.toRadians(0))
                .build();
        goScore = drive.trajectorySequenceBuilder(new Pose2d(13,39, Math.toRadians(0)))
                .turn(Math.toRadians(-47))
                .build();
        turnAgain = drive.trajectorySequenceBuilder(new Pose2d(13, 39, Math.toRadians(-47)))
                .splineToLinearHeading(new Pose2d(13, 41, Math.toRadians(18)), Math.toRadians(-45))
                .build();
        turnBacktoScore = drive.trajectorySequenceBuilder(new Pose2d(13,41, Math.toRadians(17)))
                .turn(Math.toRadians(-68))
                .build();
        turntoCollectLast = drive.trajectorySequenceBuilder(new Pose2d(13, 41, Math.toRadians(-46)))
                .turn(Math.toRadians(91))
                .build();
        turntoScoreLast = drive.trajectorySequenceBuilder(new Pose2d(13,41, Math.toRadians(41)))
                //.splineToLinearHeading(new Pose2d(13, 39, Math.toRadians(-50)), Math.toRadians(42))
                .turn(Math.toRadians(-94))
                .build();
        park = drive.trajectorySequenceBuilder(new Pose2d(13,41, Math.toRadians(-50)))
                .splineToLinearHeading(new Pose2d(53, 20, Math.toRadians(-54)), Math.toRadians(-52))
                .build();
        /*goCollectAgain = drive.trajectorySequenceBuilder(new Pose2d(13, 39, Math.toRadians(0)))
                .splineToLinearHeading(new Pose2d(13.00001, 39.00001, Math.toRadians(-47)), Math.toRadians(-47))
                .build();*/

       drive.followTrajectorySequenceAsync(goFoward);
    }

    public void loop(){

        drive.update();
        telemetry.addData("state", autoState);
        telemetry.addData("timer", testTimer.seconds());
        telemetry.addData("CURRENT X", drive.getPoseEstimate().getX());
        telemetry.addData("CURRENT Y", drive.getPoseEstimate().getY());
        telemetry.addData("tilt arm pos", tiltMotor.getCurrentPosition());
        telemetry.addData("heading", drive.getPoseEstimate().getHeading());
        telemetry.addData("heading in radians", Math.toRadians(drive.getPoseEstimate().getHeading()));
        telemetry.addData("heading converted", Math.toRadians(Math.abs(((drive.getPoseEstimate().getHeading()*180)/3.14159))));
        telemetry.addData("heading error, should be sub 2 to pass", (Math.abs(((drive.getPoseEstimate().getHeading()*180)/3.14159) - 23) <= 2));

        telemetry.addData("\nPoseXWhenCollect: ", PoseXWhenCollect);
        telemetry.addData("PoseYWhenCollect: ", PoseYWhenCollect);
        telemetry.addData("HeadingWhenCollect: ", HeadingWhenCollect);
        telemetry.addData("TiltWhenCollect: ", TiltWhenCollect);
        telemetry.addData("SlideWhenCollect: ", SlideWhenCollect);

        switch(autoState) {
            case PRELOAD:
                tiltMotor.setTargetPosition(TiltHighChamber + 400);
                intakeElbowServo.setPosition(ElbowRight);
                intakeWristServo.setPosition(WristRight);
                testTimer.reset();
                autoState = AutoState.DRIVE;
                break;

            case DRIVE:
                slideMotor.setTargetPosition(SlideHighChamber + 30);
                if (testTimer.seconds() >= 1.75) {
                    dunkTimer.reset();
                    autoState = AutoState.DUNK;
                    break;
                }
                break;
            case DUNK:
                intakeWristServo.setPosition(WristRight);
                tiltMotor.setTargetPosition(1000);
                if (dunkTimer.seconds() >= 0.4){
                    intake_claw_servo.setPosition(Claws_open);
                    slideMotor.setTargetPosition(0);
                    drive.followTrajectorySequenceAsync(goBack);
                    goBackTimer.reset();
                    autoState = AutoState.GOBACK;
                    break;
                }
                break;
            case GOBACK:
                if (goBackTimer.seconds() > 0.5) {
                    tiltMotor.setTargetPosition(590);
                    drive.followTrajectorySequenceAsync(goCollect);
                    autoState = AutoState.COLLECT_ONE;
                    break;
                }
                break;
            case COLLECT_ONE:
                intakeElbowServo.setPosition(ElbowLeft);
                intakeWristServo.setPosition(WristCenter);
                tiltMotor.setTargetPosition(605);
                LowerTileTimer.reset();
                autoState = AutoState.EXTEND;
                break;
            case EXTEND:
                if ((Math.abs(drive.getPoseEstimate().getX() - 14) <= 2) && (Math.abs(drive.getPoseEstimate().getY() - 40) <= 2)
                        && Math.abs(tiltMotor.getCurrentPosition() - 605) <= 5 && LowerTileTimer.seconds() >= 2.5) {
                    slideMotor.setTargetPosition(985);
                    grabTimer.reset();
                    if ((Math.abs(slideMotor.getCurrentPosition() - 985) < SlideTickThreshold)) {
                        // increase for more cycles, will NOT WORK
                        grabTimer.reset();
                        tiltMotor.setPower(0);
                        PoseXWhenCollect = drive.getPoseEstimate().getX();
                        PoseYWhenCollect = drive.getPoseEstimate().getY();
                        HeadingWhenCollect = drive.getPoseEstimate().getHeading();
                        TiltWhenCollect = tiltMotor.getCurrentPosition();
                        SlideWhenCollect = slideMotor.getCurrentPosition();
                        autoState = AutoState.GRABSAMPLE;
                        break;
                    }
                }
                break;

            case GRABSAMPLE:
                intake_claw_servo.setPosition(Claws_closed);
                if (grabTimer.seconds() >= 0.8) {
                    tiltMotor.setPower(1);
                    autoState = AutoState.TURNTOSCORE;
                    break;
                }
                break;
            case TURNTOSCORE:
                slideMotor.setTargetPosition(0);
                drive.followTrajectorySequenceAsync(goScore);
                autoState = AutoState.TILTTOSCORE;
                break;

            case TILTTOSCORE:
                if (slideMotor.getCurrentPosition() <= 10){
                    tiltMotor.setTargetPosition(TiltHighBucketBackwards);
                    tiltTimer.reset();
                    autoState = AutoState.EXTENDTOSCORE;
                    break;
                }
                break;
            case EXTENDTOSCORE:
                if (tiltTimer.seconds() > 1.4) {
                    if (Math.abs(tiltMotor.getCurrentPosition() - TiltHighBucketBackwards) <= TiltTickThreshold) {
                        slideMotor.setTargetPosition(SlideHighBucketBacwards);
                        if (Math.abs(slideMotor.getCurrentPosition() - SlideHighBucketBacwards) < SlideTickThreshold) {
                            intakeElbowServo.setPosition(ElbowRight);
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
                    if (depositTimer.seconds() > 0.7) {

                        intake_claw_servo.setPosition(Claws_open);

                        extendToSecondSampleTimer.reset();
                        autoState = AutoState.EXTEND_TO_SECOND_SAMPLE;
                        break;
                    }
                }
                break;
            case EXTEND_TO_SECOND_SAMPLE:
                if (extendToSecondSampleTimer.seconds() > 0.5) {
                    //intakeElbowServo.setPosition(ElbowLeft);
                    intakeWristServo.setPosition(WristCenter);
                    drive.update();
                    if ((Math.abs(drive.getPoseEstimate().getX() - 13) <= 2) && (Math.abs(drive.getPoseEstimate().getY() - 41) <= 2) &&
                            (extendToSecondSampleTimer.seconds() > 0.7)) {
                        slideMotor.setTargetPosition(0);
                        if (slideMotor.getCurrentPosition() <= SlideTickThreshold){
                            tiltMotor.setTargetPosition(655);
                            drive.followTrajectorySequenceAsync(turnAgain);
                            intakeElbowServo.setPosition(ElbowLeft);
                            autoState = AutoState.LOWER_SLIDE_RESET;
                            break;
                        }
                        }
                    }
                break;
            case LOWER_SLIDE_RESET:
                if (Math.abs(tiltMotor.getCurrentPosition() - 655) < 5 && extendToSecondSampleTimer.seconds() > 2.5) {
                    slideMotor.setTargetPosition(1080);
                    if ((Math.abs(slideMotor.getCurrentPosition() - 1080) < SlideTickThreshold)) {
                        // increase for more cycles, will NOT WORK
                        tiltMotor.setPower(0);
                        PoseXWhenCollect = drive.getPoseEstimate().getX();
                        PoseYWhenCollect = drive.getPoseEstimate().getY();
                        HeadingWhenCollect = drive.getPoseEstimate().getHeading();
                        TiltWhenCollect = tiltMotor.getCurrentPosition();
                        SlideWhenCollect = slideMotor.getCurrentPosition();
                        grabTimer.reset();
                        autoState = AutoState.COLLECT_TWO;
                        break;
                    }
                }
                break;
            case COLLECT_TWO:
                if (grabTimer.seconds()>= 0.5) {
                    intake_claw_servo.setPosition(Claws_closed);
                    if (grabTimer.seconds() >= 1) {
                        drive.followTrajectorySequenceAsync(turnBacktoScore);
                        tiltMotor.setPower(1);
                        autoState = AutoState.RESET_FOR_PATH_UPDATE;
                        break;
                    }
                }
                break;
            case RESET_FOR_PATH_UPDATE:
                slideMotor.setTargetPosition(0);
                if ((slideMotor.getCurrentPosition() < SlideTickThreshold) && grabTimer.seconds() >= 2) {
                    tiltMotor.setTargetPosition(TiltHighBucketBackwards - 5);
                    if (tiltMotor.getCurrentPosition() > 2300){
                        slideMotor.setPower(0);
                    }

                    if (Math.abs(tiltMotor.getCurrentPosition() - TiltHighBucketBackwards) -5  <= TiltTickThreshold) {
                        slideMotor.setTargetPosition(SlideHighBucketBacwards);
                        autoState = AutoState.EXTENTOSCORE_TWO;
                        break;
                    }
                }
                break;
            case EXTENTOSCORE_TWO:
                slideMotor.setPower(1);
                if (Math.abs(slideMotor.getCurrentPosition() - SlideHighBucketBacwards) < SlideTickThreshold) {
                    intakeElbowServo.setPosition(ElbowRight);
                    intakeWristServo.setPosition(WristSampleBucketScore);
                    depositTimer.reset();
                    autoState = AutoState.RESET_AND_CYCLE_THREE;
                    break;
                }
                break;
            case RESET_AND_CYCLE_THREE:
                if (depositTimer.seconds() >= 0.5) {
                    intake_claw_servo.setPosition(Claws_open);
                    if (depositTimer.seconds() >= 1){
                        //intakeElbowServo.setPosition(ElbowLeft);
                        intakeWristServo.setPosition(WristCenter);
                        if (depositTimer.seconds() >= 1.5) {
                            slideMotor.setTargetPosition(0);
                            autoState = AutoState.TURN_TO_SCORE_LAST;
                            break;

                        }
                    }
                }
                break;
            case TURN_TO_SCORE_LAST:
                if (slideMotor.getCurrentPosition() < 300){
                    tiltMotor.setTargetPosition(1000);
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
                    tiltMotor.setTargetPosition(705);
                    tiltTimer.reset();
                    autoState = AutoState.EXTEND_TO_THIRD_SAMPLE;
                    break;
                }
                break;
            case EXTEND_TO_THIRD_SAMPLE:
                if ((Math.abs(tiltMotor.getCurrentPosition() - 705) <= 5) && (tiltTimer.seconds() >= 2)){
                    slideMotor.setTargetPosition(1475);
                    //elbow right nd wrist cneter
                    intakeElbowServo.setPosition(ElbowLeft);
                    intakeWristServo.setPosition(WristCenter);
                    autoState = AutoState.GRAB_LAST_SAMPLE;
                    break;
                }
                break;
            case GRAB_LAST_SAMPLE:
                if (Math.abs(slideMotor.getCurrentPosition() - 1475) < SlideTickThreshold) {
                    tiltMotor.setPower(0);
                    PoseXWhenCollect = drive.getPoseEstimate().getX();
                    PoseYWhenCollect = drive.getPoseEstimate().getY();
                    HeadingWhenCollect = drive.getPoseEstimate().getHeading();
                    TiltWhenCollect = tiltMotor.getCurrentPosition();
                    SlideWhenCollect = slideMotor.getCurrentPosition();
                    grabTimer.reset();
                    autoState = AutoState.RETRACT_LAST_SAMPLE;
                    break;
                }
                break;
            case RETRACT_LAST_SAMPLE:
                if (grabTimer.seconds() >= 0.5) {
                    intake_claw_servo.setPosition(Claws_closed);
                    if (grabTimer.seconds() >= 1) {
                        tiltMotor.setPower(1);
                        slideMotor.setTargetPosition(0);
                        drive.followTrajectorySequenceAsync(turntoScoreLast);
                        if (slideMotor.getCurrentPosition() <= SlideTickThreshold) {
                            tiltMotor.setTargetPosition(TiltHighBucketBackwards - 5);
                            autoState = AutoState.EXTEND_TO_SCORE_LAST_SAMPLE;
                            break;
                        }
                    }
                }
                break;
            case EXTEND_TO_SCORE_LAST_SAMPLE:
                if (Math.abs(tiltMotor.getCurrentPosition() - TiltHighBucketBackwards ) <= TiltTickThreshold){
                    slideMotor.setTargetPosition(SlideHighBucketBacwards );
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideHighBucketBacwards) < SlideTickThreshold){
                        intakeElbowServo.setPosition(ElbowRight);
                        intakeWristServo.setPosition(WristSampleBucketScore);
                        grabTimer.reset();
                        autoState = AutoState.DEPOSIT_LAST_SAMPLE;
                        break;
                    }
                }
                break;
            case DEPOSIT_LAST_SAMPLE:
                if (grabTimer.seconds() >= 0.5){
                    intake_claw_servo.setPosition(Claws_open);
                    if (grabTimer.seconds() >= 1){
                        //intakeElbowServo.setPosition(ElbowLeft);
                        intakeWristServo.setPosition(WristCenter);
                        slideMotor.setTargetPosition(0);
                        if (slideMotor.getCurrentPosition() <= 200){
                            intakeElbowServo.setPosition(ElbowLeft);
                            //tiltMotor.setTargetPosition(1200);
                            //drive.followTrajectorySequenceAsync(park);
                            autoState = AutoState.FINISH;
                            break;
                        }
                    }
                }
                break;
//            case DRIVE_TO_PARK:
//                if (tiltMotor.getCurrentPosition() > 1150){
//                    intakeElbowServo.setPosition(ElbowRight);
//                    intakeWristServo.setPosition(WristLeft);
//                    slideMotor.setTargetPosition(1350);
//                    autoState = AutoState.FINISH;
//                    break;
//                }
//                break;
            case FINISH:

                drive.update();

                break;
        }
    }
}


