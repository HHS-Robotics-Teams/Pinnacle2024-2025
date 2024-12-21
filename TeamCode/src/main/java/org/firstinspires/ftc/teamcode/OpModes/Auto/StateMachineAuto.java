package org.firstinspires.ftc.teamcode.OpModes.Auto;

import static org.firstinspires.ftc.teamcode.Constants.Fields.AutoWasRan;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_closed;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSampleBucketScore;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intake_claw_servo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

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
    public void init(){

        RobotHardware.init(hardwareMap);
        applyPowers();
        intakeWristServo.setPosition(WristLeft);
        intakeElbowServo.setPosition(ElbowRight);
        tiltMotor.setTargetPosition(TiltMinPosition);
        slideMotor.setTargetPosition(0);
        intake_claw_servo.setPosition(Claws_closed);


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
                .splineToConstantHeading(new Vector2d(13,40),Math.toRadians(0))
                .build();
        goScore = drive.trajectorySequenceBuilder(new Pose2d(13,40, Math.toRadians(0)))
                .turn(Math.toRadians(-47))
                .build();
        turnAgain = drive.trajectorySequenceBuilder(new Pose2d(13, 40, Math.toRadians(-47)))
                .splineToLinearHeading(new Pose2d(13, 41, Math.toRadians(20)), Math.toRadians(-47))
                .build();
        turnBacktoScore = drive.trajectorySequenceBuilder(new Pose2d(13,41, Math.toRadians(20)))
                .turn(Math.toRadians(-70))
                .build();
        turntoCollectLast = drive.trajectorySequenceBuilder(new Pose2d(13, 41, Math.toRadians(-50)))
                .turn(Math.toRadians(94))
                .build();
        turntoScoreLast = drive.trajectorySequenceBuilder(new Pose2d(13,41, Math.toRadians(44)))
                //.splineToLinearHeading(new Pose2d(13, 39, Math.toRadians(-50)), Math.toRadians(42))
                .turn(Math.toRadians(-96))
                .build();
        park = drive.trajectorySequenceBuilder(new Pose2d(13,41, Math.toRadians(-52)))
                .splineToLinearHeading(new Pose2d(53, 20, Math.toRadians(-54)), Math.toRadians(-52))
                .build();
        /*goCollectAgain = drive.trajectorySequenceBuilder(new Pose2d(13, 39, Math.toRadians(0)))
                .splineToLinearHeading(new Pose2d(13.00001, 39.00001, Math.toRadians(-47)), Math.toRadians(-47))
                .build();*/

       drive.followTrajectorySequenceAsync(goFoward);
    }

    public void loop(){
        AutoWasRan = true;
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


        switch(autoState) {
            case PRELOAD:
                intakeElbowServo.setPosition(ElbowRight);
                intakeWristServo.setPosition(WristRight);
                testTimer.reset();
                autoState = AutoState.DRIVE;
                break;

            case DRIVE:
                slideMotor.setTargetPosition(SlideHighChamber);
                tiltMotor.setTargetPosition(TiltHighChamber + 400);
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
                    tiltMotor.setTargetPosition(540);
                    drive.followTrajectorySequenceAsync(goCollect);
                    autoState = AutoState.COLLECT_ONE;
                    break;
                }
                break;
            case COLLECT_ONE:
                intakeElbowServo.setPosition(ElbowLeft);
                intakeWristServo.setPosition(WristCenter);
                tiltMotor.setTargetPosition(540);
                if ((Math.abs(drive.getPoseEstimate().getX() - 14) <= 2) && (Math.abs(drive.getPoseEstimate().getY() - 40) <= 2)
                        && Math.abs(tiltMotor.getCurrentPosition() - 540) <= 20) {
                    slideMotor.setTargetPosition(940);
                    grabTimer.reset();
                    if ((Math.abs(slideMotor.getCurrentPosition() - 940) < 10)) {
                        // increase for more cycles, will NOT WORK
                        grabTimer.reset();
                        autoState = AutoState.EXTEND;
                        break;
                        }
                    }
                break;
            case EXTEND:
                intake_claw_servo.setPosition(Claws_closed);
                if (grabTimer.seconds() >= 0.8) {
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
                    tiltMotor.setTargetPosition(2230);
                    autoState = AutoState.EXTENDTOSCORE;
                    break;
                }
                break;
            case EXTENDTOSCORE:
                if (Math.abs(tiltMotor.getCurrentPosition() - 2230) <= 30){
                    slideMotor.setTargetPosition(1455);
                    if (Math.abs(slideMotor.getCurrentPosition() - 1455) < 5){
                        intakeElbowServo.setPosition(ElbowRight);
                        intakeWristServo.setPosition(WristSampleBucketScore);
                        depositTimer.reset();
                        autoState = AutoState.RESET_AND_CYCLE_TWO;
                        break;
                    }
                }
                break;
            case RESET_AND_CYCLE_TWO:
                if (depositTimer.seconds() > 0.3) {
                    intakeElbowServo.setPosition(ElbowRight);
                    intakeWristServo.setPosition(WristSampleBucketScore);
                    if (depositTimer.seconds() > 0.8) {

                        intake_claw_servo.setPosition(Claws_open);

                        extendToSecondSampleTimer.reset();
                        autoState = AutoState.EXTEND_TO_SECOND_SAMPLE;
                        break;
                    }
                }
                break;
            case EXTEND_TO_SECOND_SAMPLE:
                if (extendToSecondSampleTimer.seconds() > 0.5) {
                    intakeElbowServo.setPosition(ElbowLeft);
                    intakeWristServo.setPosition(WristCenter);
                    drive.update();
                    if ((Math.abs(drive.getPoseEstimate().getX() - 13) <= 2) && (Math.abs(drive.getPoseEstimate().getY() - 41) <= 2) &&
                            (extendToSecondSampleTimer.seconds() > 0.7)) {
                        slideMotor.setTargetPosition(0);
                        if (slideMotor.getCurrentPosition() <= 20){
                            tiltMotor.setTargetPosition(575);
                            drive.followTrajectorySequenceAsync(turnAgain);
                            autoState = AutoState.LOWER_SLIDE_RESET;
                            break;
                        }
                        }
                    }
                break;
            case LOWER_SLIDE_RESET:
                if (Math.abs(tiltMotor.getCurrentPosition() - 575) < 30 && extendToSecondSampleTimer.seconds() > 2) {
                    slideMotor.setTargetPosition(940);
                    if ((Math.abs(slideMotor.getCurrentPosition() - 940) < 20)) {
                        // increase for more cycles, will NOT WORK
                        grabTimer.reset();
                        intake_claw_servo.setPosition(Claws_closed);
                        autoState = AutoState.COLLECT_TWO;
                        break;
                    }
                }
                break;
            case COLLECT_TWO:
                    if (grabTimer.seconds() >= 0.5) {
                        drive.followTrajectorySequenceAsync(turnBacktoScore);
                        autoState = AutoState.RESET_FOR_PATH_UPDATE;
                        break;
                        }
                break;
            case RESET_FOR_PATH_UPDATE:
                slideMotor.setTargetPosition(0);
                if ((slideMotor.getCurrentPosition() < 20) && grabTimer.seconds() >= 2) {
                    tiltMotor.setTargetPosition(2230);
                    if (tiltMotor.getCurrentPosition() > 2250){
                        slideMotor.setPower(0);
                    }
                    if (Math.abs(tiltMotor.getCurrentPosition() - 2230) <= 30) {
                        slideMotor.setTargetPosition(1455);
                        autoState = AutoState.EXTENTOSCORE_TWO;
                        break;
                    }
                }
                break;
            case EXTENTOSCORE_TWO:
                if (Math.abs(slideMotor.getCurrentPosition() - 1455) < 5) {
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
                        intakeElbowServo.setPosition(ElbowLeft);
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
                }
                if (slideMotor.getCurrentPosition() <= 10) {
                    drive.followTrajectorySequenceAsync(turntoCollectLast);
                    autoState = AutoState.RESET_TILT_FOR_LAST_CYCLE;
                    break;
                }
                break;
            case RESET_TILT_FOR_LAST_CYCLE:
                if (slideMotor.getCurrentPosition() <= 10) {
                    tiltMotor.setTargetPosition(595);
                    tiltTimer.reset();
                    autoState = AutoState.EXTEND_TO_THIRD_SAMPLE;
                    break;
                }
                break;
            case EXTEND_TO_THIRD_SAMPLE:
                if ((Math.abs(tiltMotor.getCurrentPosition() - 600) <= 10) && (tiltTimer.seconds() >= 2)){
                    slideMotor.setTargetPosition(1370);
                    //elbow right nd wrist cneter
                    intakeElbowServo.setPosition(ElbowLeft);
                    intakeWristServo.setPosition(WristCenter);
                    autoState = AutoState.GRAB_LAST_SAMPLE;
                    break;
                }
                break;
            case GRAB_LAST_SAMPLE:
                if (Math.abs(slideMotor.getCurrentPosition() - 1370) < 10) {
                    intake_claw_servo.setPosition(Claws_closed);
                    grabTimer.reset();
                    autoState = AutoState.RETRACT_LAST_SAMPLE;
                    break;
                }
                break;
            case RETRACT_LAST_SAMPLE:
                if (grabTimer.seconds() >= 0.5){
                    slideMotor.setTargetPosition(0);
                    drive.followTrajectorySequenceAsync(turntoScoreLast);
                    if (slideMotor.getCurrentPosition() <= 20){
                        tiltMotor.setTargetPosition(2230);
                        autoState = AutoState.EXTEND_TO_SCORE_LAST_SAMPLE;
                        break;
                    }
                }
                break;
            case EXTEND_TO_SCORE_LAST_SAMPLE:
                if (Math.abs(tiltMotor.getCurrentPosition() - 2230) <= 10){
                    slideMotor.setTargetPosition(1455);
                    if (slideMotor.getCurrentPosition() > 1400){
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
                        intakeElbowServo.setPosition(ElbowLeft);
                        intakeWristServo.setPosition(WristCenter);
                        slideMotor.setTargetPosition(0);
                        if (slideMotor.getCurrentPosition() <= 200){
                            tiltMotor.setTargetPosition(1200);
                            drive.followTrajectorySequenceAsync(park);
                            autoState = AutoState.DRIVE_TO_PARK;
                            break;
                        }
                    }
                }
                break;
            case DRIVE_TO_PARK:
                if (tiltMotor.getCurrentPosition() > 1150){
                    intakeElbowServo.setPosition(ElbowRight);
                    intakeWristServo.setPosition(WristLeft);
                    slideMotor.setTargetPosition(1350);
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


