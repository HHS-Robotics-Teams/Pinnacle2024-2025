package org.firstinspires.ftc.teamcode.OpModes.Auto;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.teamcode.Constants.Fields.BVM;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowSpecimenScoring;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowStarting;
import static org.firstinspires.ftc.teamcode.Constants.Fields.IDEALBATTERYV;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighBucket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHomePosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristHorizontalPickup;
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

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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
        FINISH
    }

    AutoState autoState = AutoState.PRELOAD;
    boolean beginLoweringArm = false;
    int cyclesDone = 0;

    ElapsedTime testTimer = new ElapsedTime();
    ElapsedTime dunkTimer = new ElapsedTime();
    ElapsedTime goBackTimer = new ElapsedTime();
    ElapsedTime lowerTiltArmMultiplierTimer = new ElapsedTime();
    ElapsedTime depositTimer = new ElapsedTime();



    public void init(){
        RobotHardware.init(hardwareMap);
        applyPowers();
        intakeWristServo.setPosition(WristLeft);
        intakeElbowServo.setPosition(ElbowSpecimenScoring);
        tiltMotor.setTargetPosition(TiltMinPosition);
        slideMotor.setTargetPosition(0);

        drive = new SampleMecanumDrive(hardwareMap);

        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(0));
        drive.setPoseEstimate(startPose);


        goFoward = drive.trajectorySequenceBuilder(new Pose2d(0,0, Math.toRadians(0)))
                .splineToConstantHeading(new Vector2d(21,0),Math.toRadians(0))
                .build();
        goBack = drive.trajectorySequenceBuilder(new Pose2d(21,0, Math.toRadians(0)))
                .back(8)
                .build();
        goCollect = drive.trajectorySequenceBuilder(new Pose2d(13,0, Math.toRadians(0)))
                .splineToConstantHeading(new Vector2d(13,39),Math.toRadians(0))
                .build();
        goScore = drive.trajectorySequenceBuilder(new Pose2d(13,39, Math.toRadians(0)))
                .turn(Math.toRadians(-47))
                .build();
        turnAgain = drive.trajectorySequenceBuilder(new Pose2d(13, 39, Math.toRadians(-47)))
                .turn(Math.toRadians(47))
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
        telemetry.addData("titl timer", lowerTiltArmMultiplierTimer.seconds());
        switch(autoState) {
            case PRELOAD:
                intakeElbowServo.setPosition(ElbowSpecimenScoring);
                intakeWristServo.setPosition(WristSpecimenWallPickup);
                testTimer.reset();
                autoState = AutoState.DRIVE;
                break;

            case DRIVE:
                slideMotor.setTargetPosition(SlideHighChamber);
                tiltMotor.setTargetPosition(TiltHighChamber);
                if (testTimer.seconds() >= 2) {
                    dunkTimer.reset();
                    autoState = AutoState.DUNK;
                }
                break;
            case DUNK:
                intakeWristServo.setPosition(WristRight);
                if (dunkTimer.seconds() >= 0.5){
                    tiltMotor.setTargetPosition(800);
                    slideMotor.setTargetPosition(0);
                    drive.followTrajectorySequenceAsync(goBack);
                    goBackTimer.reset();
                    autoState = AutoState.GOBACK;
                }
                break;
            case GOBACK:
                if (goBackTimer.seconds() > 2) {
                    drive.followTrajectorySequenceAsync(goCollect);
                    autoState = AutoState.COLLECT_ONE;
                }
                break;
            case COLLECT_ONE:
                intakeElbowServo.setPosition(ElbowSpecimenScoring);
                intakeWristServo.setPosition(WristHorizontalPickup);
                tiltMotor.setTargetPosition(800);
                if ((Math.abs(drive.getPoseEstimate().getX() - 14) <= 2) && (Math.abs(drive.getPoseEstimate().getY() - 38) <= 2)) {
                    slideMotor.setTargetPosition(1020);
                    intakeCRServo.setPower(1);
                    lowerTiltArmMultiplierTimer.reset();
                    if (cyclesDone >= 2){
                        autoState = AutoState.FINISH;
                        break;
                    }
                    else {
                        cyclesDone++;
                        autoState = AutoState.EXTEND;
                    }
                }
                break;
            case EXTEND:
                if (beginLoweringArm){
                    tiltMotor.setTargetPosition((int) (800-(100*lowerTiltArmMultiplierTimer.seconds())));
                }
                if ((Math.abs(slideMotor.getCurrentPosition() - 1020) < 10) && !beginLoweringArm){
                    lowerTiltArmMultiplierTimer.reset();
                    beginLoweringArm = true;
                }
                if (tiltMotor.getCurrentPosition() < 550) {
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
                    tiltMotor.setTargetPosition(2148);
                    autoState = AutoState.EXTENDTOSCORE;
                }
                break;
            case EXTENDTOSCORE:
                if (Math.abs(tiltMotor.getCurrentPosition() - 2148) <= 50){
                    slideMotor.setTargetPosition(950);
                    intakeElbowServo.setPosition(ElbowSpecimenScoring);
                    intakeWristServo.setPosition(.65);
                    if (Math.abs(slideMotor.getCurrentPosition() - 950) < 5){
                        intakeCRServo.setPower(-0.4);
                        depositTimer.reset();
                        autoState = AutoState.RESET_AND_CYCLE_TWO;
                    }
                }
                break;
            case RESET_AND_CYCLE_TWO:
                if (depositTimer.seconds() > 0.5){
                    slideMotor.setTargetPosition(0);
                    tiltMotor.setTargetPosition(650);
                    intakeElbowServo.setPosition(ElbowSpecimenScoring);
                    intakeWristServo.setPosition(WristHorizontalPickup);
                    drive.followTrajectorySequenceAsync(turnAgain);
                    autoState = AutoState.COLLECT_ONE;
                }
                break;
            case FINISH:
                drive.update();
                break;
        }
    }
}


