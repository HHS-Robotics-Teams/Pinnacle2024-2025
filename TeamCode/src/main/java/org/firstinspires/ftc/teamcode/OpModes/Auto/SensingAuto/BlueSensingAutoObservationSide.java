package org.firstinspires.ftc.teamcode.OpModes.Auto.SensingAuto;


import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_closed;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlidePickupAdjust;



import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;

import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideTickThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltTickThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristHorizontalPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
import static org.firstinspires.ftc.teamcode.Constants.Fields.isSlideIncrementing;
import static org.firstinspires.ftc.teamcode.Constants.Fields.isTiltIncrementing;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.colorSensor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intake_claw_servo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.resetEncoders;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;
import static org.firstinspires.ftc.teamcode.drive.DriveConstants.MAX_ANG_VEL;
import static org.firstinspires.ftc.teamcode.drive.DriveConstants.MAX_VEL;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.constraints.AngularVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TranslationalVelocityConstraint;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants.DetectedColorAndDistance;
import org.firstinspires.ftc.teamcode.Constants.DetectedHueAndDistance;
import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import java.util.Arrays;


@Config
@Autonomous (name = "Blue Sensing Observation Side 1+2", group = "Comp Auto")
public class BlueSensingAutoObservationSide extends OpMode {

    TrajectorySequence goFoward;
    TrajectorySequence goPickUpFirst;
    TrajectorySequence goObsevationZone;
    TrajectorySequence goPickUpSecond;
    TrajectorySequence goBack;
    TrajectorySequence goScore;
    TrajectorySequence goDepositSecond;
    TrajectorySequence scoreFirstSpecimen;
    TrajectorySequence grabSecondSpecimen;
    TrajectorySequence scoreSecondSpecimen;

    TrajectorySequence goCollectAgain;
    TrajectorySequence turnAgain;
    TrajectorySequence park;

    SampleMecanumDrive drive;

    public enum AutoState {
        PRELOAD,
        DRIVE,
        DUNK,
        GO_BACK,
        PICK_UP_FIRST_SAMPLE,
        EXTEND,
        MOVE_GRADUALLY,
        GRAB_FIRST_SAMPLE,
        TURN_TO_OBSERVATION,
        EXTEND_TO_OBSERVATION,
        MOVE_GRADUALLY_SECOND_SAMPLE,
        GRAB_SECOND_SAMPLE,
        GO_OBSERVATION_AGAIN,
        DROP_SPECIMEN_AGAIN,
        GO_TO_CHAMBER,
        RETRACT,
        LIFT_SLOWLY,
        GRAB_FIRST_SPECIMEN,
        LIFT_FIRST_SPECIMEN,
        SCORE_FIRST_SPECIMEN,
        EXTEND_TO_SCORE_FIRST,
        LET_GO_OF_SPECIMEN,
        GO_TO_OBSERVATION_AGAIN,
        EXTEND_TO_SECOND_SPECIMEN,
        GRAB_SECOND_SPECIMEN,
        LIFT_SLOWLY_SECOND,
        DRIVE_TO_SCORE_SECOND_SPECIMEN,
        SCORE_SECOND_SPECIMEN,
        EXTEND_TO_SCORE_SECOND,
        RETRACT_SECOND,
        PARK,
        FINISH
    }

    AutoState autoState = AutoState.PRELOAD;
    boolean beginLoweringArm = false;
    int cyclesDone = 0;

    ElapsedTime testTimer = new ElapsedTime();
    ElapsedTime dunkTimer = new ElapsedTime();
    ElapsedTime dunkAgainTimer = new ElapsedTime();
    ElapsedTime goBackTimer = new ElapsedTime();
    ElapsedTime grabTimer = new ElapsedTime();
    ElapsedTime grabAgainTimer = new ElapsedTime();
    ElapsedTime extendTimer = new ElapsedTime();
    ElapsedTime depositTimer = new ElapsedTime();
    ElapsedTime LowerTileTimer = new ElapsedTime();

    @Override
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

        Pose2d startPose = new Pose2d(0, -.75, Math.toRadians(0));
        drive.setPoseEstimate(startPose);
        TrajectoryVelocityConstraint toPreloadScore = new MinVelocityConstraint(Arrays.asList(
                new TranslationalVelocityConstraint(MAX_VEL * 0.80),
                new AngularVelocityConstraint(MAX_ANG_VEL * 0.80)
        ));
        TrajectoryVelocityConstraint toFirstPickupLimiter = new MinVelocityConstraint(Arrays.asList(
                new TranslationalVelocityConstraint(MAX_VEL * 0.30),
                new AngularVelocityConstraint(MAX_ANG_VEL * 0.30)
        ));


        goFoward = drive.trajectorySequenceBuilder(new Pose2d(0, 0, Math.toRadians(0)))
                .setVelConstraint(toPreloadScore)
                .lineToConstantHeading(new Vector2d(24,4))
                //.splineToConstantHeading(new Vector2d(24, 4), Math.toRadians(0))
                .build();
        goBack = drive.trajectorySequenceBuilder(new Pose2d(24, 4, Math.toRadians(0)))
                .back(10)
                .build();
        goPickUpFirst = drive.trajectorySequenceBuilder(new Pose2d(14, 4, Math.toRadians(0)))
                .setVelConstraint(toFirstPickupLimiter)
                .lineToLinearHeading(new Pose2d(20, -39, Math.toRadians(-1)))
                //.splineToConstantHeading(new Vector2d(20,-39), Math.toRadians(0))
                .build();
        goObsevationZone = drive.trajectorySequenceBuilder(new Pose2d(20,-39, Math.toRadians(-1)))
                .turn(Math.toRadians(180))
                .build();
        goPickUpSecond = drive.trajectorySequenceBuilder(new Pose2d(20, -39, Math.toRadians(180)))
                .turn(Math.toRadians(156))
                .build();
        goDepositSecond = drive.trajectorySequenceBuilder(new Pose2d(20, -39, Math.toRadians(336)))
                .turn(Math.toRadians(-161))
                .build();
/*        goScore = drive.trajectorySequenceBuilder(new Pose2d(40,-48, Math.toRadians(180)))
                .splineToLinearHeading(new Pose2d(24,0),Math.toRadians(0))
                .build();*/
        scoreFirstSpecimen = drive.trajectorySequenceBuilder(new Pose2d(20, -39, Math.toRadians(176)))
                .lineToLinearHeading(new Pose2d(24,1, Math.toRadians(0)))
                .build();
        grabSecondSpecimen = drive.trajectorySequenceBuilder(new Pose2d(24, 1, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(20,-39, Math.toRadians(178)))
                .build();
        scoreSecondSpecimen = drive.trajectorySequenceBuilder(new Pose2d(20, -39, Math.toRadians(178)))
                .lineToLinearHeading(new Pose2d(20,0, Math.toRadians(0)))
                //.splineToLinearHeading(new Pose2d(20, -2, Math.toRadians(0)), Math.toRadians(0))
                .build();

//        turnAgain = drive.trajectorySequenceBuilder(new Pose2d(13, 39, Math.toRadians(-47)))
//                .splineToLinearHeading(new Pose2d(13, 41, Math.toRadians(18)), Math.toRadians(-47))
//                .build();
//        turnBacktoScore = drive.trajectorySequenceBuilder(new Pose2d(13,41, Math.toRadians(18)))
//                .turn(Math.toRadians(-68))
//                .build();
//        turntoCollectLast = drive.trajectorySequenceBuilder(new Pose2d(13, 41, Math.toRadians(-46)))
//                .turn(Math.toRadians(92))
//                .build();
//        turntoScoreLast = drive.trajectorySequenceBuilder(new Pose2d(13,41, Math.toRadians(42)))
//                //.splineToLinearHeading(new Pose2d(13, 39, Math.toRadians(-50)), Math.toRadians(42))
//                .turn(Math.toRadians(-94))
//                .build();
        park = drive.trajectorySequenceBuilder(new Pose2d(20,-2, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(0,0, Math.toRadians(0)))
                .build();
//        /*goCollectAgain = drive.trajectorySequenceBuilder(new Pose2d(13, 39, Math.toRadians(0)))
//                .splineToLinearHeading(new Pose2d(13.00001, 39.00001, Math.toRadians(-47)), Math.toRadians(-47))
//                .build();*/

        drive.followTrajectorySequenceAsync(goFoward);
    }


    @Override
    public void loop() {
        DetectedHueAndDistance.updateColor(colorSensor);

        double detectedDistance = DetectedHueAndDistance.getDistance();

        String detectedHue = DetectedHueAndDistance.getColor();

        drive.update();
        telemetry.addData("state", autoState);
        telemetry.addData("isTiltIncrementing", isTiltIncrementing ? "True" : "False");
        telemetry.addData("Detected Hue", DetectedHueAndDistance.getColor());
        telemetry.addData("Distance (cm)", "%.2f", detectedDistance);
        telemetry.addData("Tilt power", tiltMotor.getPower());
        telemetry.addData("timer", testTimer.seconds());
        telemetry.addData("CURRENT X", drive.getPoseEstimate().getX());
        telemetry.addData("CURRENT Y", drive.getPoseEstimate().getY());
        telemetry.addData("tilt arm pos", tiltMotor.getCurrentPosition());
        telemetry.addData("slide pos", slideMotor.getCurrentPosition());

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
                if (testTimer.seconds() >= 2.0) {
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
                if (goBackTimer.seconds() > 0.3) {
                    tiltMotor.setTargetPosition(600);
                    slideMotor.setTargetPosition(0);
                    drive.followTrajectorySequenceAsync(goPickUpFirst);
                    autoState = AutoState.PICK_UP_FIRST_SAMPLE;
                    break;
                }
                break;
            case PICK_UP_FIRST_SAMPLE:
                intakeElbowServo.setPosition(ElbowLeft);
                intakeWristServo.setPosition(WristCenter);
                tiltMotor.setTargetPosition(420);
                LowerTileTimer.reset();
                autoState = AutoState.EXTEND;
                break;
            case EXTEND:
                intakeElbowServo.setPosition(ElbowLeft);
                if ((Math.abs(drive.getPoseEstimate().getX() - 20) <= 2) && (Math.abs(drive.getPoseEstimate().getY() - (-39)) <= 2)
                        && Math.abs(tiltMotor.getCurrentPosition() - 420) <= 5 && LowerTileTimer.seconds() >= 1.25) {
                    slideMotor.setTargetPosition(190);
                    grabTimer.reset();
                    autoState = AutoState.MOVE_GRADUALLY;
                    break;
                }
                break;
            case MOVE_GRADUALLY:
                if (slideMotor.getCurrentPosition() >= 190) {
                    if (detectedHue.equals("Blue")) {
                        grabTimer.reset();
                        tiltMotor.setPower(0);
                        autoState = AutoState.GRAB_FIRST_SAMPLE;
                        break;
                    } else if (isSlideIncrementing) {
                        slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + 20);
                        isSlideIncrementing = false; // Wait before the next increment
                    }
                    // Check if the motor reached the current target
                    else if (!slideMotor.isBusy()) {
                        isSlideIncrementing = true;

                        // Stop incrementing after reaching the desired total movement
                        if (slideMotor.getCurrentPosition() >= 230) {
                            grabTimer.reset();
                            tiltMotor.setPower(0);
                            autoState = AutoState.GRAB_FIRST_SAMPLE;
                            break;
                        }
                    }
                }
                break;
            case GRAB_FIRST_SAMPLE:
                if (grabTimer.seconds() >= 0.25) {
                    intake_claw_servo.setPosition(Claws_closed);
                    if (grabTimer.seconds() >= 0.4) {
                        slideMotor.setTargetPosition(0);
                        autoState = AutoState.TURN_TO_OBSERVATION;
                        break;
                    }
                }
                break;
            case TURN_TO_OBSERVATION:
                tiltMotor.setPower(1);
                tiltMotor.setTargetPosition(712);
                drive.followTrajectorySequenceAsync(goObsevationZone);
                drive.update();
                intakeWristServo.setPosition(WristRight); // check position
                intakeElbowServo.setPosition(ElbowLeft);
                grabTimer.reset();
                autoState = AutoState.EXTEND_TO_OBSERVATION;
                break;
            case EXTEND_TO_OBSERVATION:
                if ((Math.abs(drive.getPoseEstimate().getX() - 20) <= 3) &&
                        (Math.abs(drive.getPoseEstimate().getY() + 39) <= 3) && grabTimer.seconds() >= 1.5){
                    slideMotor.setTargetPosition(250);
                    tiltMotor.setTargetPosition(400);
                    if (slideMotor.getCurrentPosition() >= 240){
                        intake_claw_servo.setPosition(Claws_open);
                        tiltMotor.setTargetPosition(515);
                        intakeWristServo.setPosition(WristCenter);
                        testTimer.reset();
                        isSlideIncrementing = true;
                        drive.followTrajectorySequenceAsync(goPickUpSecond);
                        drive.update();
                        autoState = AutoState.MOVE_GRADUALLY_SECOND_SAMPLE;
                        break;
                    }
                }
                break;
            case MOVE_GRADUALLY_SECOND_SAMPLE:
                if ((Math.abs(tiltMotor.getCurrentPosition() - 515) <= TiltTickThreshold) && testTimer.seconds() >= 0.5) {
                    if (detectedHue.equals("Blue")) {
                        grabAgainTimer.reset();
                        tiltMotor.setPower(0);
                        autoState = AutoState.GRAB_SECOND_SAMPLE;
                        break;
                    } else if (isSlideIncrementing) {
                        slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + 15);
                        isSlideIncrementing = false; // Wait before the next increment
                    }
                    // Check if the motor reached the current target
                    else if (!slideMotor.isBusy()) {
                        isSlideIncrementing = true;

                        // Stop incrementing after reaching the desired total movement
                        if (slideMotor.getCurrentPosition() >= (300)) {
                            grabAgainTimer.reset();
                            tiltMotor.setPower(0);
                            autoState = AutoState.GRAB_SECOND_SAMPLE;
                            break;
                        }
                    }
                }
                break;
            case GRAB_SECOND_SAMPLE:
                if (grabTimer.seconds() > 0.3 ){
                    tiltMotor.setPower(0);
                    grabAgainTimer.reset();
                    autoState = AutoState.GO_OBSERVATION_AGAIN;
                }
                break;
            case GO_OBSERVATION_AGAIN:
                if (grabAgainTimer.seconds() >= 0.4) {
                    intake_claw_servo.setPosition(Claws_closed);
                    if (grabAgainTimer.seconds() >= 0.6) {
                        tiltMotor.setPower(1);
                        slideMotor.setTargetPosition(250);
                        tiltMotor.setTargetPosition(590);
                        drive.followTrajectorySequenceAsync(goDepositSecond);
                        drive.update();
                        grabTimer.reset();
                        autoState = AutoState.DROP_SPECIMEN_AGAIN;
                    }
                }
                break;
            case DROP_SPECIMEN_AGAIN:
                if (grabTimer.seconds() >= 0.8){
                    slideMotor.setTargetPosition(300);
                    if (slideMotor.getCurrentPosition() >= 280) {
                        intake_claw_servo.setPosition(Claws_open);
                        grabAgainTimer.reset();
                        autoState = AutoState.RETRACT;
                        break;
                    }
                }
                break;
            case RETRACT:
                tiltMotor.setTargetPosition(570);
                slideMotor.setTargetPosition(0);
                autoState = AutoState.LIFT_SLOWLY;
                break;
            case LIFT_SLOWLY:
                intakeWristServo.setPosition(WristRight);
                intakeElbowServo.setPosition(ElbowLeft);
                isTiltIncrementing = true;
                isSlideIncrementing = true;
                if (grabAgainTimer.seconds() >= 0.3 && Math.abs(tiltMotor.getCurrentPosition() - 570) <= TiltTickThreshold) {
                    slideMotor.setTargetPosition(447);
                    testTimer.reset();
                    autoState = AutoState.GRAB_FIRST_SPECIMEN;
                    break;
                }
                break;
            case GRAB_FIRST_SPECIMEN:
                if (testTimer.seconds() > 0.5) {
                    if ((detectedDistance <= 2.0) && (tiltMotor.getCurrentPosition() >= 580)) {
                        telemetry.speak("Grabbed");
                        intake_claw_servo.setPosition(Claws_closed);
                        grabTimer.reset();
                        autoState = AutoState.LIFT_FIRST_SPECIMEN;
                        break;
                    } else if (isTiltIncrementing && tiltMotor.getCurrentPosition() < 580){
                        tiltMotor.setTargetPosition(tiltMotor.getCurrentPosition() + 20);
                        isTiltIncrementing = false;
                    } else if (!tiltMotor.isBusy()){
                        isTiltIncrementing = true;
                        if (tiltMotor.getCurrentPosition() >= 590){
                            intake_claw_servo.setPosition(Claws_closed);
                            telemetry.speak("all the way up ");
                            grabTimer.reset();
                            autoState = AutoState.LIFT_FIRST_SPECIMEN;
                            break;
                        }
                    } else if (isSlideIncrementing && detectedDistance > 2.0) {
                        slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + 20);
                        isSlideIncrementing = false;
                    } else if (!slideMotor.isBusy()){
                        isSlideIncrementing = true;
                    }
                }
                break;
            case LIFT_FIRST_SPECIMEN:
                if (grabTimer.seconds() >= 0.4){
                    tiltMotor.setTargetPosition(715);
                    if (tiltMotor.getCurrentPosition() >= 705){
                        slideMotor.setTargetPosition(0);
                        drive.followTrajectorySequenceAsync(scoreFirstSpecimen);
                        dunkTimer.reset();
                        autoState = AutoState.EXTEND_TO_SCORE_FIRST;
                        break;
                    }
                }
                break;

            case EXTEND_TO_SCORE_FIRST:
                if ((Math.abs(drive.getPoseEstimate().getX() - 24) <= 2.5) && (Math.abs(drive.getPoseEstimate().getY() - 1) <= 2.5)) {
                    intakeWristServo.setPosition(WristHorizontalPickup);
                    tiltMotor.setTargetPosition(1400);
                    if (dunkTimer.seconds() > 0.5) {
                        //slideMotor.setTargetPosition(60);
                        extendTimer.reset();
                        autoState = AutoState.SCORE_FIRST_SPECIMEN;
                        break;
                    }
                }
                break;
            case SCORE_FIRST_SPECIMEN:
                if (extendTimer.seconds() > 0.8) {
                    slideMotor.setTargetPosition( slideMotor.getCurrentPosition() + 195);
                    if (tiltMotor.getCurrentPosition() >= 1400) {
                        tiltMotor.setTargetPosition(850);
                        grabTimer.reset();
                        autoState = AutoState.LET_GO_OF_SPECIMEN;
                        break;
                    }
                }
                break;
            case LET_GO_OF_SPECIMEN:
                if (grabTimer.seconds() >= 1.2) {
                    intake_claw_servo.setPosition(Claws_open);
                    if (dunkTimer.seconds() >= .8) {
                        slideMotor.setTargetPosition(0);
                        tiltMotor.setTargetPosition(800);
                        intakeWristServo.setPosition(WristCenter);
                        autoState = AutoState.GO_TO_OBSERVATION_AGAIN;
                        break;
                    }
                }
                break;
            case GO_TO_OBSERVATION_AGAIN:
                drive.followTrajectorySequenceAsync(grabSecondSpecimen);
                drive.update();
                autoState =AutoState.LIFT_SLOWLY_SECOND;
                break;
            case LIFT_SLOWLY_SECOND:
                if ((Math.abs(drive.getPoseEstimate().getX() - 20) <= 2) && (Math.abs(drive.getPoseEstimate().getY() + 39) <= 2)
                        && Math.abs(tiltMotor.getCurrentPosition() - 800) <= 10){
                    intakeElbowServo.setPosition(ElbowLeft);
                    intakeWristServo.setPosition(WristRight);
                    tiltMotor.setTargetPosition(565);
                    testTimer.reset();

                    autoState = AutoState.EXTEND_TO_SECOND_SPECIMEN;
                    break;
                }
                break;
            case EXTEND_TO_SECOND_SPECIMEN:
                if (testTimer.seconds() >= 1) {
                    slideMotor.setTargetPosition(380);
                    isTiltIncrementing = true;
                    isSlideIncrementing = true;
                    autoState = AutoState.GRAB_SECOND_SPECIMEN;
                    break;
                }
                break;
            case GRAB_SECOND_SPECIMEN:
                if ((detectedDistance <= 2)) {
                    intake_claw_servo.setPosition(Claws_closed);
                    telemetry.speak("Grabbed");
                    grabTimer.reset();
                    autoState = AutoState.DRIVE_TO_SCORE_SECOND_SPECIMEN;
                    break;
                } else if (isTiltIncrementing && tiltMotor.getCurrentPosition() <= 570) {
                    tiltMotor.setTargetPosition(tiltMotor.getCurrentPosition() + 20);
                    isTiltIncrementing = false;
                } else if (!tiltMotor.isBusy()) {
                    isTiltIncrementing = true;
                    if (tiltMotor.getCurrentPosition() >= 590) {
                        intake_claw_servo.setPosition(Claws_closed);
                        telemetry.speak("all the way up");
                        grabTimer.reset();
                        autoState = AutoState.DRIVE_TO_SCORE_SECOND_SPECIMEN;
                        break;
                    }
                } else if (isSlideIncrementing && (detectedDistance >2)){
                    isSlideIncrementing = false;
                }else if (!slideMotor.isBusy()){
                    isSlideIncrementing = true;
                }
                break;
            case DRIVE_TO_SCORE_SECOND_SPECIMEN:
                if (grabTimer.seconds() >= .3 ) {
                    tiltMotor.setTargetPosition(800);
                    if (tiltMotor.getCurrentPosition() >= 780) {
                        slideMotor.setTargetPosition(0);
                        testTimer.reset();
                        drive.followTrajectorySequenceAsync(scoreSecondSpecimen);
                        autoState = AutoState.SCORE_SECOND_SPECIMEN;
                    }
                    break;
                }
                break;
            case SCORE_SECOND_SPECIMEN:
                intakeWristServo.setPosition(WristHorizontalPickup);
                if ((Math.abs(drive.getPoseEstimate().getX() - 20) <= 2) && (Math.abs(drive.getPoseEstimate().getY() - 0) <= 2)) {
                    tiltMotor.setTargetPosition(1070);
                    intakeElbowServo.setPosition(ElbowLeft);
                    drive.update();
                    if (testTimer.seconds() > 0.6){
                        slideMotor.setTargetPosition(320);
                        dunkAgainTimer.reset();
                        autoState = AutoState.RETRACT_SECOND;
                        break;
                    }
                }
                break;
            case RETRACT_SECOND:
                if (dunkAgainTimer.seconds() >= 0.4) {
                    tiltMotor.setTargetPosition(760);
                    if (dunkAgainTimer.seconds() >= 1) {
                        intake_claw_servo.setPosition(Claws_open);
                        autoState = AutoState.PARK;
                        break;
                    }
                }
                break;
            case PARK:
                slideMotor.setTargetPosition(0);
                drive.followTrajectorySequenceAsync(grabSecondSpecimen);
                drive.update();
                autoState = AutoState.FINISH;
                break;
            case FINISH:
                if ((Math.abs(drive.getPoseEstimate().getX() - 20) <= 2) && (Math.abs(drive.getPoseEstimate().getY() + 39) <= 2)) {
                    slideMotor.setTargetPosition(300);
                    intakeWristServo.setPosition(WristCenter);
                    drive.update();
                    break;
                }
                break;
        }
    }
}
