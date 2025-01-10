
package org.firstinspires.ftc.teamcode.OpModes.Auto;



import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_closed;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
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
@Autonomous (name = "Observation Side 1+2", group = "Comp Auto")
public class StateMachineAutoObservationSide extends OpMode {
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
    public int SlideHighBucketBacwards = 1445;
    double PoseXWhenCollect = 0;
    double PoseYWhenCollect = 0;
    double HeadingWhenCollect = 0;
    double TiltWhenCollect = 0;
    double SlideWhenCollect = 0;

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

    SampleMecanumDrive drive;

    public enum AutoState {
        PRELOAD,
        DRIVE,
        DUNK,
        GO_BACK,
        PICK_UP_FIRST_SAMPLE,
        EXTEND,
        GRAB_FIRST_SAMPLE,
        TURN_TO_OBSERVATION,
        EXTEND_TO_OBSERVATION,
        GRAB_SECOND_SAMPLE,
        GO_OBSERVATION_AGAIN,
        DROP_SPECIMEN_AGAIN,
        GO_TO_CHAMBER,
        GRAB_FIRST_SPECIMEN,
        LIFT_FIRST_SPECIMEN,
        SCORE_FIRST_SPECIMEN,
        SCORE_HIGH_CHAMBER,
        LET_GO_OF_SPECIMEN,
        GRAB_SECOND_SPECIMEN,
        DRIVE_TO_SCORE_SECOND_SPECIMEN,
        SCORE_SECOND_SPECIMEN,
        PARK,
        FINISH
    }

    StateMachineAutoObservationSide.AutoState autoState = StateMachineAutoObservationSide.AutoState.PRELOAD;
    boolean beginLoweringArm = false;
    int cyclesDone = 0;

    ElapsedTime testTimer = new ElapsedTime();
    ElapsedTime dunkTimer = new ElapsedTime();
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


        goFoward = drive.trajectorySequenceBuilder(new Pose2d(0, 0, Math.toRadians(0)))
                .splineToConstantHeading(new Vector2d(24, 4), Math.toRadians(0))
                .build();
        goBack = drive.trajectorySequenceBuilder(new Pose2d(24, 4, Math.toRadians(0)))
                .back(8)
                .build();
        goPickUpFirst = drive.trajectorySequenceBuilder(new Pose2d(16, 4, Math.toRadians(0)))
                .splineToConstantHeading(new Vector2d(20,-39), Math.toRadians(0))
                .build();
        goObsevationZone = drive.trajectorySequenceBuilder(new Pose2d(20,-39, Math.toRadians(0)))
                .turn(Math.toRadians(180))
                .build();
        goPickUpSecond = drive.trajectorySequenceBuilder(new Pose2d(20, -39, Math.toRadians(180)))
                .turn(Math.toRadians(157))
                .build();
        goDepositSecond = drive.trajectorySequenceBuilder(new Pose2d(20, -39, Math.toRadians(337)))
                .turn(Math.toRadians(-161))
                .build();
/*        goScore = drive.trajectorySequenceBuilder(new Pose2d(40,-48, Math.toRadians(180)))
                .splineToLinearHeading(new Pose2d(24,0),Math.toRadians(0))
                .build();*/
        scoreFirstSpecimen = drive.trajectorySequenceBuilder(new Pose2d(20, -39, Math.toRadians(176)))
                .splineToConstantHeading(new Vector2d(18, 3), Math.toRadians(177.5))
                .build();
        grabSecondSpecimen = drive.trajectorySequenceBuilder(new Pose2d(18, 3, Math.toRadians(177.5)))
                .splineToConstantHeading(new Vector2d(20,-39), Math.toRadians(177.5))
                .build();
        scoreSecondSpecimen = drive.trajectorySequenceBuilder(new Pose2d(20, -39, Math.toRadians(177.5)))
                .splineToConstantHeading(new Vector2d(18, 1), Math.toRadians(177.5))
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
//        park = drive.trajectorySequenceBuilder(new Pose2d(13,41, Math.toRadians(-50)))
//                .splineToLinearHeading(new Pose2d(53, 20, Math.toRadians(-54)), Math.toRadians(-52))
//                .build();
//        /*goCollectAgain = drive.trajectorySequenceBuilder(new Pose2d(13, 39, Math.toRadians(0)))
//                .splineToLinearHeading(new Pose2d(13.00001, 39.00001, Math.toRadians(-47)), Math.toRadians(-47))
//                .build();*/

        drive.followTrajectorySequenceAsync(goFoward);
    }


    @Override
    public void loop() {
        drive.update();
        telemetry.addData("state", autoState);
        telemetry.addData("timer", testTimer.seconds());
        telemetry.addData("CURRENT X", drive.getPoseEstimate().getX());
        telemetry.addData("CURRENT Y", drive.getPoseEstimate().getY());
        telemetry.addData("tilt arm pos", tiltMotor.getCurrentPosition());
        telemetry.addData("slide pos", slideMotor.getCurrentPosition());

        switch(autoState) {
            case PRELOAD:
                tiltMotor.setTargetPosition(TiltHighChamber + 400);
                intakeElbowServo.setPosition(ElbowRight);
                intakeWristServo.setPosition(WristRight);
                testTimer.reset();
                autoState =AutoState.DRIVE;
                break;

            case DRIVE:
                slideMotor.setTargetPosition(SlideHighChamber + 30);
                if (testTimer.seconds() >= 1.75) {
                    dunkTimer.reset();
                    autoState =AutoState.DUNK;
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
                    autoState = AutoState.GO_BACK;
                    break;
                }
                break;
            case GO_BACK:
                if (goBackTimer.seconds() > 0.5) {
                    tiltMotor.setTargetPosition(590);
                    drive.followTrajectorySequenceAsync(goPickUpFirst);
                    autoState = AutoState.PICK_UP_FIRST_SAMPLE;
                    break;
                }
                break;
            case PICK_UP_FIRST_SAMPLE:
                intakeElbowServo.setPosition(ElbowLeft);
                intakeWristServo.setPosition(WristCenter);
                tiltMotor.setTargetPosition(570);
                LowerTileTimer.reset();
                autoState = AutoState.EXTEND;
                break;

            case EXTEND:
                if ((Math.abs(drive.getPoseEstimate().getX() - 20) <= 2) && (Math.abs(drive.getPoseEstimate().getY() - (-39)) <= 2)
                        && Math.abs(tiltMotor.getCurrentPosition() - 570) <= 5 && LowerTileTimer.seconds() >= 2.5) {
                    slideMotor.setTargetPosition(535);
                    grabTimer.reset();
                    if ((Math.abs(slideMotor.getCurrentPosition() - 535) < SlideTickThreshold)) {
                        // increase for more cycles, will NOT WORK
                        grabTimer.reset();
                        tiltMotor.setPower(0);
                        //PoseXWhenCollect = drive.getPoseEstimate().getX();
                        //PoseYWhenCollect = drive.getPoseEstimate().getY();
                        //HeadingWhenCollect = drive.getPoseEstimate().getHeading();
                        //TiltWhenCollect = tiltMotor.getCurrentPosition();
                        //SlideWhenCollect = slideMotor.getCurrentPosition();
                        autoState = AutoState.GRAB_FIRST_SAMPLE;
                    }
                }
                break;

            case GRAB_FIRST_SAMPLE:
                if (grabTimer.seconds() >= 0.5) {
                    intake_claw_servo.setPosition(Claws_closed);
                    if (grabTimer.seconds() >= 0.8) {
                        slideMotor.setTargetPosition(0);
                        autoState = AutoState.TURN_TO_OBSERVATION;
                    }
                }
                break;
            case TURN_TO_OBSERVATION:
                tiltMotor.setPower(1);
                tiltMotor.setTargetPosition(600);
                drive.followTrajectorySequenceAsync(goObsevationZone);
                intakeWristServo.setPosition(WristRight); // check position
                intakeElbowServo.setPosition(ElbowLeft);
                grabTimer.reset();
                autoState = AutoState.EXTEND_TO_OBSERVATION;
                break;

            case EXTEND_TO_OBSERVATION:
                if ((Math.abs(drive.getPoseEstimate().getX() - 20) <= 4) &&
                        (Math.abs(drive.getPoseEstimate().getY() - -39) <= 4) && grabTimer.seconds() >= 1){
                    slideMotor.setTargetPosition(800);
                    tiltMotor.setTargetPosition(635);
                    if (slideMotor.getCurrentPosition() >= 775){
                        intake_claw_servo.setPosition(Claws_open);
                        grabTimer.reset();
                        tiltMotor.setTargetPosition(600);
                        intakeWristServo.setPosition(WristCenter);
                        drive.followTrajectorySequenceAsync(goPickUpSecond);
                        autoState = AutoState.GRAB_SECOND_SAMPLE;
                    }
                }
                break;
            case GRAB_SECOND_SAMPLE:
/*                slideMotor.setTargetPosition(950);
                tiltMotor.setTargetPosition(700);*/
                if (grabTimer.seconds() > 2){
                    slideMotor.setTargetPosition(850);
                    if (slideMotor.getCurrentPosition() >= 840) {
                        tiltMotor.setPower(0);
                        grabAgainTimer.reset();
                        autoState = AutoState.GO_OBSERVATION_AGAIN;
                    }
                }
                break;
            case GO_OBSERVATION_AGAIN:
                if (grabAgainTimer.seconds() >= 0.5) {
                    intake_claw_servo.setPosition(Claws_closed);
                    if (grabAgainTimer.seconds() >= 0.8) {
                        tiltMotor.setPower(1);
                        slideMotor.setTargetPosition(300);
                        tiltMotor.setTargetPosition(660);
                        drive.followTrajectorySequenceAsync(goDepositSecond);
                        grabTimer.reset();
                    /*tiltMotor.setTargetPosition(TiltHighChamber + 400);
                    intakeElbowServo.setPosition(ElbowLeft);
                    intakeWristServo.setPosition(WristRight);
                    slideMotor.setTargetPosition(SlideMinPosition);*/
                        //drive.followTrajectorySequenceAsync(goScore);
                        autoState = AutoState.DROP_SPECIMEN_AGAIN;
                    }
                }
                break;
            case DROP_SPECIMEN_AGAIN:
                if (grabTimer.seconds() >= 0.8){
                    slideMotor.setTargetPosition(800);
                    if (slideMotor.getCurrentPosition() >= 790) {
                        intake_claw_servo.setPosition(Claws_open);
                        grabAgainTimer.reset();
                        autoState = AutoState.GRAB_FIRST_SPECIMEN;
                    }
                }
                break;
            case GRAB_FIRST_SPECIMEN:
                intakeWristServo.setPosition(WristRight);
                intakeElbowServo.setPosition(ElbowLeft);
                if (grabAgainTimer.seconds() >= 0.5 && Math.abs(tiltMotor.getCurrentPosition() - 660) <= 10){
                    slideMotor.setTargetPosition(1140);
                    if (slideMotor.getCurrentPosition() >= 1140){
                        intake_claw_servo.setPosition(Claws_closed);
                        grabTimer.reset();
                        autoState = AutoState.LIFT_FIRST_SPECIMEN;
                    }
                }
                break;
            case LIFT_FIRST_SPECIMEN:
                if (grabTimer.seconds() >= 0.5){
                    tiltMotor.setTargetPosition(800);
                    if (tiltMotor.getCurrentPosition() >= 790){
                        intakeWristServo.setPosition(0.2);
                        slideMotor.setTargetPosition(0);
                        drive.followTrajectorySequenceAsync(scoreFirstSpecimen);
                        intakeElbowServo.setPosition(ElbowRight);
                        autoState = AutoState.SCORE_FIRST_SPECIMEN;
                        break;
                    }
                }
                break;
            case SCORE_FIRST_SPECIMEN:
                if ((Math.abs(drive.getPoseEstimate().getX() - 18) <= 2)
                        && (Math.abs(drive.getPoseEstimate().getY() - 3) <= 2)) {
                    tiltMotor.setTargetPosition(3300);
                    slideMotor.setTargetPosition(0);
                    drive.update();
                    if (tiltMotor.getCurrentPosition() >= 3250){
                        intake_claw_servo.setPosition(Claws_open);
                        grabTimer.reset();
                        drive.followTrajectorySequenceAsync(grabSecondSpecimen);
                        autoState = AutoState.LET_GO_OF_SPECIMEN;
                    }
                }
                break;
            case LET_GO_OF_SPECIMEN:
                if (grabTimer.seconds() >= 0.5) {
                    tiltMotor.setTargetPosition(665);
                    intakeWristServo.setPosition(WristCenter);
                    autoState = AutoState.GRAB_SECOND_SPECIMEN;
                }
                break;

            case GRAB_SECOND_SPECIMEN:
                if ((Math.abs(drive.getPoseEstimate().getX() - 20) <= 2)
                        && (Math.abs(drive.getPoseEstimate().getY() + 39) <= 2)
                            && Math.abs(tiltMotor.getCurrentPosition() - 665) <= 10){
                    intakeElbowServo.setPosition(ElbowLeft);
                    intakeWristServo.setPosition(WristRight);
                    slideMotor.setTargetPosition(1130);
                    if (slideMotor.getCurrentPosition() >= 1120){
                        intake_claw_servo.setPosition(Claws_closed);
                        tiltMotor.setTargetPosition(800);
                        grabTimer.reset();
                        autoState = AutoState.DRIVE_TO_SCORE_SECOND_SPECIMEN;
                    }
                }
                break;
            case DRIVE_TO_SCORE_SECOND_SPECIMEN:
                if (tiltMotor.getCurrentPosition() >= 780) {
                    intakeWristServo.setPosition(0.2);
                    intakeElbowServo.setPosition(ElbowRight);
                    drive.followTrajectorySequenceAsync(scoreSecondSpecimen);
                    autoState = AutoState.SCORE_SECOND_SPECIMEN;
                }
                break;
            case SCORE_SECOND_SPECIMEN:
                slideMotor.setTargetPosition(0);
                if ((Math.abs(drive.getPoseEstimate().getX() - 18) <= 2)
                        && (Math.abs(drive.getPoseEstimate().getY() - 2) <= 2)) {
                    tiltMotor.setTargetPosition(3300);
                    if (tiltMotor.getCurrentPosition() >= 3240) {
                        intake_claw_servo.setPosition(Claws_open);
                        grabTimer.reset();
                        autoState = AutoState.FINISH;
                    }
                }
                    break;

            case FINISH:
                drive.update();
                break;
        }
    }
}

