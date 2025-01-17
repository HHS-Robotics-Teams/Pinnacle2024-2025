package org.firstinspires.ftc.teamcode.OpModes.Auto.IlegalAuto;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_closed;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;

import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighBucketBackwardsAuto;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideTickThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucketBackwardsAuto;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltTickThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSampleBucketScore;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
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

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
@Config
@Autonomous (name = "Basket side Sample 1+3", group = "Comp Auto")
public class StateMachineAutoBasketSideSample extends OpMode {

    double PoseXWhenCollect = 0;
    double PoseYWhenCollect = 0;
    double HeadingWhenCollect = 0;
    double TiltWhenCollect = 0;
    double SlideWhenCollect = 0;

    TrajectorySequence goForward;
    TrajectorySequence goCollect;
    TrajectorySequence goBasket;
    TrajectorySequence goScore;
    TrajectorySequence goCollectAgain;
    TrajectorySequence turnAgain;
    TrajectorySequence turnBacktoScore;

    TrajectorySequence turntoCollectLast;
    TrajectorySequence turntoScoreLast;
    TrajectorySequence park;

    SampleMecanumDrive drive;

    public enum AutoState {
        PRELOAD_SAMPLE,
        DRIVE,
        EXTEND_TO_SCORE_PRELOAD,
        SCORE_PRELOAD,
        GO_BACK,
        COLLECT_ONE,
        EXTEND,
        GRAB_SAMPLE,
        TURN_TO_SCORE,
        TILT_TO_SCORE,
        EXTEND_TO_SCORE,
        RESET_AND_CYCLE_TWO,
        EXTEND_TO_SECOND_SAMPLE,
        LOWER_SLIDE_RESET,
        COLLECT_TWO,
        RESET_FOR_PATH_UPDATE,
        EXTEND_TO_SCORE_TWO,
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

    StateMachineAutoBasketSideSample.AutoState autoState = StateMachineAutoBasketSideSample.AutoState.PRELOAD_SAMPLE;
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


        goForward = drive.trajectorySequenceBuilder(new Pose2d(0,0, Math.toRadians(0)))
                .splineToConstantHeading(new Vector2d(6,0),Math.toRadians(0))
                .build();
        goBasket = drive.trajectorySequenceBuilder(new Pose2d(6,0, Math.toRadians(0)))
                .splineToLinearHeading(new Pose2d(13, 39, Math.toRadians(-47)), Math.toRadians(0))
                .build();
        goCollect = drive.trajectorySequenceBuilder(new Pose2d(13,39, Math.toRadians(-47)))
                .turn(Math.toRadians(47))
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

        drive.followTrajectorySequenceAsync(goForward);
    }


    public void loop() {
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
            case PRELOAD_SAMPLE:
                tiltMotor.setTargetPosition(TiltHighChamber + 400);
                intakeElbowServo.setPosition(ElbowRight);
                intakeWristServo.setPosition(WristRight);
                testTimer.reset();
                autoState = AutoState.DRIVE;
                break;

            case DRIVE:
                    drive.followTrajectorySequenceAsync(goBasket);
                if (testTimer.seconds() > 2) {
                    tiltMotor.setTargetPosition(TiltHighBucketBackwardsAuto);
                    intakeWristServo.setPosition(WristSampleBucketScore);
                    tiltTimer.reset();
                    autoState = AutoState.EXTEND_TO_SCORE_PRELOAD;
                    break;
                }
                break;

            case EXTEND_TO_SCORE_PRELOAD:
                if (tiltTimer.seconds() > 1.4) {
                    if (Math.abs(tiltMotor.getCurrentPosition() - TiltHighBucketBackwardsAuto) <= TiltTickThreshold) {
                        slideMotor.setTargetPosition(SlideHighBucketBackwardsAuto);
                        if (Math.abs(slideMotor.getCurrentPosition() - SlideHighBucketBackwardsAuto) < SlideTickThreshold) {
                            intakeElbowServo.setPosition(ElbowRight);
                            depositTimer.reset();
                            autoState = AutoState.SCORE_PRELOAD;
                            break;
                        }
                    }
                }
                break;
            case SCORE_PRELOAD:
                if (depositTimer.seconds() > 0.5) {
                    intake_claw_servo.setPosition(Claws_open);
                    intakeWristServo.setPosition(WristCenter);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50) {
                        //tiltMotor.setTargetPosition(605);
                        goBackTimer.reset();
                        autoState = AutoState.GO_BACK;
                        break;
                    }
                }
                break;
            case GO_BACK:
                if (goBackTimer.seconds() > 0.5) {
                    drive.followTrajectorySequenceAsync(goCollect);
                    autoState = AutoState.COLLECT_ONE;
                    break;
                }
                break;

           //From here down is the same code Don't mess with it

            case COLLECT_ONE:
                intakeElbowServo.setPosition(ElbowLeft);
                intakeWristServo.setPosition(WristCenter);
                tiltMotor.setTargetPosition(538);
                LowerTileTimer.reset();
                autoState = AutoState.EXTEND;
                break;
            case EXTEND:
                if ((Math.abs(drive.getPoseEstimate().getX() - 14) <= 2) && (Math.abs(drive.getPoseEstimate().getY() - 40) <= 2)
                        && Math.abs(tiltMotor.getCurrentPosition() - 538) <= 5 && LowerTileTimer.seconds() >= 2.5) {
                    slideMotor.setTargetPosition(1020);
                    grabTimer.reset();
                    if ((Math.abs(slideMotor.getCurrentPosition() - 1020) < SlideTickThreshold)) {
                        // increase for more cycles, will NOT WORK
                        grabTimer.reset();
                        tiltMotor.setPower(0);
                        PoseXWhenCollect = drive.getPoseEstimate().getX();
                        PoseYWhenCollect = drive.getPoseEstimate().getY();
                        HeadingWhenCollect = drive.getPoseEstimate().getHeading();
                        TiltWhenCollect = tiltMotor.getCurrentPosition();
                        SlideWhenCollect = slideMotor.getCurrentPosition();
                        autoState = AutoState.GRAB_SAMPLE;
                        break;
                    }
                }
                break;

            case GRAB_SAMPLE:
                intake_claw_servo.setPosition(Claws_closed);
                if (grabTimer.seconds() >= 0.8) {
                    tiltMotor.setPower(1);
                    autoState = AutoState.TURN_TO_SCORE;
                    break;
                }
                break;
            case TURN_TO_SCORE:
                slideMotor.setTargetPosition(0);
                drive.followTrajectorySequenceAsync(goScore);
                autoState = AutoState.TILT_TO_SCORE;
                break;

            case TILT_TO_SCORE:
                if (slideMotor.getCurrentPosition() <= 10) {
                    tiltMotor.setTargetPosition(TiltHighBucketBackwardsAuto + 4);
                    intakeElbowServo.setPosition(ElbowRight);
                    tiltTimer.reset();
                    autoState = AutoState.EXTEND_TO_SCORE;
                    break;
                }
                break;
            case EXTEND_TO_SCORE:
                if (tiltTimer.seconds() > 1.4) {
                    if (Math.abs(tiltMotor.getCurrentPosition() - TiltHighBucketBackwardsAuto) <= TiltTickThreshold) {
                        slideMotor.setTargetPosition(SlideHighBucketBackwardsAuto);
                        if (Math.abs(slideMotor.getCurrentPosition() - SlideHighBucketBackwardsAuto) < SlideTickThreshold) {
                            //intakeElbowServo.setPosition(ElbowRight);
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
                        if (slideMotor.getCurrentPosition() <= SlideTickThreshold) {
                            tiltMotor.setTargetPosition(583);
                            drive.followTrajectorySequenceAsync(turnAgain);
                            intakeElbowServo.setPosition(ElbowLeft);
                            autoState = AutoState.LOWER_SLIDE_RESET;
                            break;
                        }
                    }
                }
                break;
            case LOWER_SLIDE_RESET:
                if (Math.abs(tiltMotor.getCurrentPosition() - 583) < 25 && extendToSecondSampleTimer.seconds() > 2.5) {
                    slideMotor.setTargetPosition(1105);
                    if ((Math.abs(slideMotor.getCurrentPosition() - 1105) < SlideTickThreshold)) {
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
                if (grabTimer.seconds() >= 0.5) {
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
                    tiltMotor.setTargetPosition(TiltHighBucketBackwardsAuto - 26);
/*                    if (tiltMotor.getCurrentPosition() > 2300){
                        slideMotor.setPower(0);
                    }*/

                    if (Math.abs(tiltMotor.getCurrentPosition() - (TiltHighBucketBackwardsAuto - 26)) - 5  <= TiltTickThreshold) {
                        slideMotor.setTargetPosition(SlideHighBucketBackwardsAuto);
                        intakeElbowServo.setPosition(ElbowRight);
                        autoState = AutoState.EXTEND_TO_SCORE_TWO;
                        break;
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
                if (depositTimer.seconds() >= 0.5) {
                    intake_claw_servo.setPosition(Claws_open);
                    if (depositTimer.seconds() >= 1) {
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
                    tiltMotor.setTargetPosition(627);
                    tiltTimer.reset();
                    autoState = AutoState.EXTEND_TO_THIRD_SAMPLE;
                    break;
                }
                break;
            case EXTEND_TO_THIRD_SAMPLE:
                if ((Math.abs(tiltMotor.getCurrentPosition() - 627) <= 5) && (tiltTimer.seconds() >= 2)){
                    slideMotor.setTargetPosition(1420);
                    //elbow right nd wrist cneter
                    intakeElbowServo.setPosition(ElbowLeft);
                    intakeWristServo.setPosition(WristCenter);
                    autoState = AutoState.GRAB_LAST_SAMPLE;
                    break;
                }
                break;
            case GRAB_LAST_SAMPLE:
                if (Math.abs(slideMotor.getCurrentPosition() - 1420) < SlideTickThreshold) {
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
                            tiltMotor.setTargetPosition(TiltHighBucketBackwardsAuto - 42);
                            intakeElbowServo.setPosition(ElbowRight);
                            autoState = AutoState.EXTEND_TO_SCORE_LAST_SAMPLE;
                            break;
                        }
                    }
                }
                break;
            case EXTEND_TO_SCORE_LAST_SAMPLE:
                if (Math.abs(tiltMotor.getCurrentPosition() - (TiltHighBucketBackwardsAuto - 42)) <= TiltTickThreshold){
                    slideMotor.setTargetPosition(SlideHighBucketBackwardsAuto );
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideHighBucketBackwardsAuto) < SlideTickThreshold){
                        //intakeElbowServo.setPosition(ElbowRight);
                        intakeWristServo.setPosition(WristSampleBucketScore);
                        grabTimer.reset();
                        autoState = AutoState.DEPOSIT_LAST_SAMPLE;
                        break;
                    }
                }
                break;
            case DEPOSIT_LAST_SAMPLE:
                if (grabTimer.seconds() >= 0.5) {
                    intake_claw_servo.setPosition(Claws_open);
                    if (grabTimer.seconds() >= 1) {
                        //intakeElbowServo.setPosition(ElbowLeft);
                        intakeWristServo.setPosition(WristCenter);
                        slideMotor.setTargetPosition(0);
                        if (slideMotor.getCurrentPosition() <= 200) {
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
