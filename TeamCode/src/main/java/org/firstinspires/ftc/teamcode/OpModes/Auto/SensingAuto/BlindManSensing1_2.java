package org.firstinspires.ftc.teamcode.OpModes.Auto.SensingAuto;

import static org.firstinspires.ftc.teamcode.Constants.DetectedColorAndDistance.distance;

import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_closed;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.OldDetectedDistance;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideTickThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltTickThreshold;

import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristHorizontalPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlidePickupAdjust;
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

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.constraints.AngularVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TranslationalVelocityConstraint;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants.DetectedColorAndDistance;
import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import java.util.Arrays;


@Config
@Autonomous(name = "BLUE Blind Man Observation Side 1+2", group = "Comp Auto")
public class BlindManSensing1_2  extends OpMode {
    double targetHeading = Math.toRadians(0);
    double targetHeading_TWO = Math.toRadians(336);


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
        CHECK_DISTANCE_FIRST,
        MOVE_GRADUALLY,
        GRAB_FIRST_SAMPLE,
        TURN_TO_OBSERVATION,
        EXTEND_TO_OBSERVATION,
        CHECK_DISTANCE_SECOND,
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

    BlindManSensing1_2.AutoState autoState = BlindManSensing1_2.AutoState.PRELOAD;
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

        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(0));
        drive.setPoseEstimate(startPose);

        TrajectoryVelocityConstraint toFirstPickupLimiter = new MinVelocityConstraint(Arrays.asList(
           new TranslationalVelocityConstraint(MAX_VEL * 0.32),
           new AngularVelocityConstraint(MAX_ANG_VEL * 0.32)
        ));
        TrajectoryVelocityConstraint toScoring = new MinVelocityConstraint(Arrays.asList(
                new TranslationalVelocityConstraint(MAX_VEL * 0.6),
                new AngularVelocityConstraint(MAX_ANG_VEL * 0.6)
        ));

        goFoward = drive.trajectorySequenceBuilder(new Pose2d(0, 0, Math.toRadians(0)))
                .setVelConstraint(toScoring)
                .lineToConstantHeading(new Vector2d(24,4))
                //.splineToConstantHeading(new Vector2d(24, 4), Math.toRadians(0))
                .build();
        goBack = drive.trajectorySequenceBuilder(new Pose2d(24, 4, Math.toRadians(0)))
                .back(10)
                .build();

        goPickUpFirst = drive.trajectorySequenceBuilder(new Pose2d(14, 4, Math.toRadians(0)))
                .setVelConstraint(toFirstPickupLimiter)
                //.lineToConstantHeading(new Vector2d(20,-39))
                //.lineToLinearHeading(new Pose2d(20, -39, Math.toRadians(0)))
                .splineToConstantHeading(new Vector2d(20,-39), Math.toRadians(0))
                .build();
        goObsevationZone = drive.trajectorySequenceBuilder(new Pose2d(20,-39, Math.toRadians(0)))
                .resetConstraints()
                .turn(Math.toRadians(180))
                .build();
        goPickUpSecond = drive.trajectorySequenceBuilder(new Pose2d(20, -39, Math.toRadians(180)))
                .turn(Math.toRadians(156))
                .build();
        goDepositSecond = drive.trajectorySequenceBuilder(new Pose2d(20, -39, Math.toRadians(336)))
                .turn(Math.toRadians(-160))
                .build();
        scoreFirstSpecimen = drive.trajectorySequenceBuilder(new Pose2d(20, -39, Math.toRadians(175)))
                .setVelConstraint(toScoring)
                .lineToLinearHeading(new Pose2d(24,3.5, Math.toRadians(0)))
                .build();
        grabSecondSpecimen = drive.trajectorySequenceBuilder(new Pose2d(24, 3.5, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(20,-39, Math.toRadians(178)))
                .build();
        scoreSecondSpecimen = drive.trajectorySequenceBuilder(new Pose2d(20, -39, Math.toRadians(178)))
                .setVelConstraint(toScoring)
                .lineToLinearHeading(new Pose2d(24,-1, Math.toRadians(0)))
                //.splineToLinearHeading(new Pose2d(20, -2, Math.toRadians(0)), Math.toRadians(0))
                .build();
        park = drive.trajectorySequenceBuilder(new Pose2d(20,-2, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(0,0, Math.toRadians(0)))
                .build();

        drive.followTrajectorySequenceAsync(goFoward);
    }


    @Override
    public void loop() {
        DetectedColorAndDistance.updateColor(colorSensor);

        double detectedDistance = DetectedColorAndDistance.getDistance();

        String detectedColor = DetectedColorAndDistance.getColor();

        drive.update();
        telemetry.addData("state", autoState);
        telemetry.addData("Distance (cm)", "%.2f", detectedDistance);
        telemetry.addData("Old Distance " ,"%.2f", OldDetectedDistance);
        telemetry.addData("Tilt power", tiltMotor.getPower());
        telemetry.addData("Test timer", testTimer.seconds());
        telemetry.addData("Extend Timer " , extendTimer.seconds());
        telemetry.addData("Heading", drive.getPoseEstimate().getHeading());
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
                if (testTimer.seconds() >= 1.5) {
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
                    tiltMotor.setTargetPosition(525);
                    slideMotor.setTargetPosition(0);
                    drive.followTrajectorySequenceAsync(goPickUpFirst);
                    autoState = AutoState.PICK_UP_FIRST_SAMPLE;
                    break;
                }
                break;
            case PICK_UP_FIRST_SAMPLE:
                intakeElbowServo.setPosition(ElbowLeft);
                intakeWristServo.setPosition(WristCenter);
                tiltMotor.setTargetPosition(430);
                LowerTileTimer.reset();
                autoState = AutoState.EXTEND;
                break;
            case EXTEND:
                if (((Math.abs(drive.getPoseEstimate().getX() - 20) <= 2.5) && (Math.abs(drive.getPoseEstimate().getY() + 39) <= 2.5)
                        && Math.abs(tiltMotor.getCurrentPosition() - 430) <= 5 && LowerTileTimer.seconds() >= 2.25)) {
                    slideMotor.setTargetPosition(200);
                    OldDetectedDistance = detectedDistance;
                    extendTimer.reset();
                    autoState = AutoState.MOVE_GRADUALLY;
                    break;
                }
                break;

            case MOVE_GRADUALLY:
                if (Math.abs(tiltMotor.getCurrentPosition()- 430) <= TiltTickThreshold) {
                    if ((detectedDistance < 5.5))  {
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
                        if (slideMotor.getCurrentPosition() >= 225) {
                            grabTimer.reset();
                            tiltMotor.setPower(0);
                            autoState = AutoState.GRAB_FIRST_SAMPLE;
                            break;
                        }
                    }
                }
                break;
            case GRAB_FIRST_SAMPLE:
                if (grabTimer.seconds() >= 0.35) {
                    intake_claw_servo.setPosition(Claws_closed);
                    if (grabTimer.seconds() >= 0.5) {
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
                    if ((slideMotor.getCurrentPosition() >= 240)){
                        intake_claw_servo.setPosition(Claws_open);
                        tiltMotor.setTargetPosition(515);
                        intakeWristServo.setPosition(WristCenter);
                        testTimer.reset();
                        isSlideIncrementing = true;
                        drive.followTrajectorySequenceAsync(goPickUpSecond);
                        drive.update();
                        OldDetectedDistance = detectedDistance;
                        autoState = AutoState.MOVE_GRADUALLY_SECOND_SAMPLE;
                        break;
                    }
                }
                break;
            case MOVE_GRADUALLY_SECOND_SAMPLE:
                if ((Math.abs(tiltMotor.getCurrentPosition() - 515) <= TiltTickThreshold) && (Math.abs(drive.getPoseEstimate().getHeading() - targetHeading_TWO) < Math.toRadians(7))) {
                    if (detectedDistance < 5.5 ) {
                        grabAgainTimer.reset();
                        tiltMotor.setPower(0);
                        autoState = AutoState.GRAB_SECOND_SAMPLE;
                        break;
                    } else if (isSlideIncrementing) {
                        slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + 20);
                        isSlideIncrementing = false; // Wait before the next increment
                    }
                    // Check if the motor reached the current target
                    else if (!slideMotor.isBusy()) {
                        isSlideIncrementing = true;

                        // Stop incrementing after reaching the desired total movement
                        if (slideMotor.getCurrentPosition() >= 290) {
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
                    if (grabAgainTimer.seconds() >= 0.5) {
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
                if (grabAgainTimer.seconds() >= 0.3 && Math.abs(tiltMotor.getCurrentPosition() - 560) <= TiltTickThreshold) {
                    slideMotor.setTargetPosition(460);
                    isSlideIncrementing = true;
                    testTimer.reset();
                    autoState = AutoState.GRAB_FIRST_SPECIMEN;
                    break;
                }
                break;
            case GRAB_FIRST_SPECIMEN:
                if (testTimer.seconds() > .5) {
                        if ((distance < 2.0) && tiltMotor.getCurrentPosition() >= 575) {
                            intake_claw_servo.setPosition(Claws_closed);
                            grabTimer.reset();
                            autoState = AutoState.LIFT_FIRST_SPECIMEN;
                            break;
                        } else if (isTiltIncrementing) {
                            tiltMotor.setTargetPosition(tiltMotor.getCurrentPosition() + 20);
                            isTiltIncrementing = false;

                        } else if (!tiltMotor.isBusy()) {
                            isTiltIncrementing = true;
                            if (detectedDistance >= 2.0) {
                                slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + 20);
                            }
                            if (tiltMotor.getCurrentPosition() >= 590) {
                                intake_claw_servo.setPosition(Claws_closed);
                                telemetry.speak("all the way up ");
                                grabTimer.reset();
                                autoState = AutoState.LIFT_FIRST_SPECIMEN;
                                break;
                            }
                        }
                }
                break;
            case LIFT_FIRST_SPECIMEN:
                if (grabTimer.seconds() >= 0.3){
                    tiltMotor.setTargetPosition(715);
                    if (tiltMotor.getCurrentPosition() >= 705){
                        slideMotor.setTargetPosition(0);
                        drive.followTrajectorySequenceAsync(scoreFirstSpecimen);
                        dunkTimer.reset();
                        autoState = AutoState.EXTEND_TO_SCORE_SECOND;
                        break;
                    }
                }
                break;

            case EXTEND_TO_SCORE_FIRST:
                    if ((Math.abs(drive.getPoseEstimate().getX() - 24) <= 2.5) && (Math.abs(drive.getPoseEstimate().getY() - 3.5) <= 2.5)
                            && (Math.abs(drive.getPoseEstimate().getHeading() - targetHeading) < Math.toRadians(7))) {
                        intakeWristServo.setPosition(WristHorizontalPickup);
                        if (dunkTimer.seconds() > 0.5) {
                            tiltMotor.setTargetPosition(1010);
                            extendTimer.reset();
                            autoState = AutoState.SCORE_FIRST_SPECIMEN;
                            break;
                    }
                }
                break;
            case SCORE_FIRST_SPECIMEN:

            if ((extendTimer.seconds() > 0.6 ) && (tiltMotor.getCurrentPosition() >= 1010)) {
                slideMotor.setTargetPosition(245);
                if (extendTimer.seconds() > 1) {
                    tiltMotor.setTargetPosition(825);
                    grabTimer.reset();
                    autoState = AutoState.LET_GO_OF_SPECIMEN;
                    break;
                }
            }
            break;

            case LET_GO_OF_SPECIMEN:
                if (grabTimer.seconds() >= 0.3) {
                    intake_claw_servo.setPosition(Claws_open);
                    if (grabTimer.seconds() >= 0.4) {
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
                    tiltMotor.setTargetPosition(540);
                    testTimer.reset();
                    isTiltIncrementing = true;
                    autoState = AutoState.EXTEND_TO_SECOND_SPECIMEN;
                    break;
                }
                break;
            case EXTEND_TO_SECOND_SPECIMEN:
                if (testTimer.seconds() >= 1) {
                    extendTimer.reset();
                    slideMotor.setTargetPosition(360);
                    autoState = AutoState.GRAB_SECOND_SPECIMEN;
                    break;
                }
                break;
            case GRAB_SECOND_SPECIMEN:

                    if ((distance < 1.8) && (tiltMotor.getCurrentPosition() >= 545)) {
                        intake_claw_servo.setPosition(Claws_closed);
                        grabTimer.reset();
                        autoState = AutoState.DRIVE_TO_SCORE_SECOND_SPECIMEN;
                        break;
                    } else if (isTiltIncrementing) {
                        tiltMotor.setTargetPosition(tiltMotor.getCurrentPosition() + 20);
                        isTiltIncrementing = false;
                    } else if (!tiltMotor.isBusy()) {
                        isTiltIncrementing = true;
                        if (distance > 1.8){
                            slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + 20);
                        }
                        if ((tiltMotor.getCurrentPosition() >= 560) || (extendTimer.seconds() > 2.0 )){
                            intake_claw_servo.setPosition(Claws_closed);
                            grabTimer.reset();
                            autoState = AutoState.DRIVE_TO_SCORE_SECOND_SPECIMEN;
                            break;
                        }
                    }
                break;
            case DRIVE_TO_SCORE_SECOND_SPECIMEN:
                if (grabTimer.seconds() >= 0.3 ) {
                    tiltMotor.setTargetPosition(800);
                    if (tiltMotor.getCurrentPosition() >= 780) {
                        slideMotor.setTargetPosition(0);
                        intakeWristServo.setPosition(WristRight);
                        drive.followTrajectorySequenceAsync(scoreSecondSpecimen);
                        dunkTimer.reset();
                        autoState = AutoState.EXTEND_TO_SCORE_SECOND;
                    }
                    break;
                }
                break;
            case EXTEND_TO_SCORE_SECOND:
                    if ((Math.abs(drive.getPoseEstimate().getX() - 24) <= 2.5) && (Math.abs(drive.getPoseEstimate().getY() + 1) <= 2.5)
                            && (Math.abs(drive.getPoseEstimate().getHeading() - targetHeading) < Math.toRadians(8))) {
                        intakeWristServo.setPosition(WristHorizontalPickup);
                        if (dunkTimer.seconds() > 0.6) {
                            tiltMotor.setTargetPosition(1020);
                            slideMotor.setTargetPosition(210);
                            intakeElbowServo.setPosition(ElbowLeft);
                            extendTimer.reset();
                            autoState = AutoState.EXTEND_TO_SCORE_SECOND;
                            break;
                        }
                    }

                break;

            case SCORE_SECOND_SPECIMEN:
            if ((extendTimer.seconds() > 0.6 ) && (Math.abs(tiltMotor.getCurrentPosition() - 1020) <= TiltTickThreshold)) {
                slideMotor.setTargetPosition(220);
                if (extendTimer.seconds() > 0.8) {
                    tiltMotor.setTargetPosition(825);
                    grabTimer.reset();
                    autoState = AutoState.RETRACT_SECOND;
                    break;
                }
            }
            break;

            case RETRACT_SECOND:
//                if (dunkAgainTimer.seconds() >=.3) {
//                    tiltMotor.setTargetPosition(760);
                    if (dunkAgainTimer.seconds() >= 0.8) {
                        intake_claw_servo.setPosition(Claws_open);
                        autoState = AutoState.PARK;
                        break;
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
                    slideMotor.setTargetPosition(SlideHighChamber + 50);
                    intakeWristServo.setPosition(WristCenter);
                    drive.update();
                    break;
                }
                break;
        }
    }
}


