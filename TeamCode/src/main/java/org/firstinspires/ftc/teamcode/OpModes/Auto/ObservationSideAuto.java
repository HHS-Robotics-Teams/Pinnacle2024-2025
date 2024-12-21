
package org.firstinspires.ftc.teamcode.OpModes.Auto;

import static org.firstinspires.ftc.teamcode.Constants.Fields.AutoWasRan;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_closed;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMaxPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideStartPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltPickupPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
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
@Autonomous (name = "Observation Side ", group = "Comp Auto")
public class ObservationSideAuto extends OpMode {
    TrajectorySequence goFoward;
    TrajectorySequence goPickUpFirst;
    TrajectorySequence goObsevationZone;
    TrajectorySequence goBack;
    TrajectorySequence goScore;
    TrajectorySequence goCollectAgain;
    TrajectorySequence turnAgain;

    SampleMecanumDrive drive;

    public enum AutoState {
        PRELOAD,
        DRIVE,
        DUNK,
        GO_BACK,
        PICK_UP_FIRST_SAMPLE,
        GRAB_FIRST_SAMPLE,
        EXTEND,
        GRAB_FIRST_SPECIMEN,
        GRAB_SECOND_SPECIMEN,
        TURN_TO_SCORE,
        TILT_TO_SCORE,
        EXTEND_TO_SCORE,
        RESET_AND_CYCLE_TWO,
        FINISH
    }

    ObservationSideAuto.AutoState autoState = ObservationSideAuto.AutoState.PRELOAD;
    boolean beginLoweringArm = false;
    int cyclesDone = 0;

    ElapsedTime testTimer = new ElapsedTime();
    ElapsedTime dunkTimer = new ElapsedTime();
    ElapsedTime goBackTimer = new ElapsedTime();
    ElapsedTime grabTimer = new ElapsedTime();
    ElapsedTime extendTimer = new ElapsedTime();
    ElapsedTime depositTimer = new ElapsedTime();

    @Override
    public void init() {
        RobotHardware.init(hardwareMap);

        HardwareSettings.init(hardwareMap);

        applyPowers();

        intakeWristServo.setPosition(WristLeft);
        intakeElbowServo.setPosition(ElbowRight);
        tiltMotor.setTargetPosition(TiltMinPosition);
        slideMotor.setTargetPosition(0);
        intake_claw_servo.setPosition(Claws_closed);

        drive = new SampleMecanumDrive(hardwareMap);

        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(0));
        drive.setPoseEstimate(startPose);


        goFoward = drive.trajectorySequenceBuilder(new Pose2d(0, 0, Math.toRadians(0)))
                .splineToConstantHeading(new Vector2d(24, 0), Math.toRadians(0))
                .build();
        goBack = drive.trajectorySequenceBuilder(new Pose2d(24, 0, Math.toRadians(0)))
                .back(8)
                .build();
        goPickUpFirst = drive.trajectorySequenceBuilder(new Pose2d(16, 0, Math.toRadians(0)))
                .splineToConstantHeading(new Vector2d(40, -48), Math.toRadians(180))
                .build();
        goObsevationZone = drive.trajectorySequenceBuilder(new Pose2d(40,-48, Math.toRadians(180)))
                .forward(5)
                .build();
   /*     goScore = drive.trajectorySequenceBuilder(new Pose2d(13, 40, Math.toRadians(0)))
                .turn(Math.toRadians(-47))
                .build();
        turnAgain = drive.trajectorySequenceBuilder(new Pose2d(13, 40, Math.toRadians(-47)))
                .turn(Math.toRadians(47))
                .build();
        /*goCollectAgain = drive.trajectorySequenceBuilder(new Pose2d(13, 39, Math.toRadians(0)))
                .splineToLinearHeading(new Pose2d(13.00001, 39.00001, Math.toRadians(-47)), Math.toRadians(-47))
                .build();*/


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
        switch (autoState) {
            case PRELOAD:
                intakeElbowServo.setPosition(ElbowRight);
                intakeWristServo.setPosition(WristRight);
                testTimer.reset();
                autoState = ObservationSideAuto.AutoState.DRIVE;
                break;

            case DRIVE:
                slideMotor.setTargetPosition(SlideHighChamber);
                tiltMotor.setTargetPosition(TiltHighChamber + 400);
                if (testTimer.seconds() >= 2) {
                    dunkTimer.reset();
                    autoState = ObservationSideAuto.AutoState.DUNK;
                }
                break;

            case DUNK:
                intakeWristServo.setPosition(WristRight);
                tiltMotor.setTargetPosition(1000);
                if (dunkTimer.seconds() >= 0.4) {
                    intake_claw_servo.setPosition(Claws_open);
                    slideMotor.setTargetPosition(SlideStartPosition);
                    drive.followTrajectorySequenceAsync(goBack);
                    goBackTimer.reset();
                    autoState = ObservationSideAuto.AutoState.GO_BACK;
                }
                break;

            case GO_BACK:
                if (goBackTimer.seconds() > 2) {
                    tiltMotor.setTargetPosition(560);
                    drive.followTrajectorySequenceAsync(goPickUpFirst);
                    intakeElbowServo.setPosition(ElbowLeft);
                    intakeWristServo.setPosition(WristCenter);
                    ;
                    autoState = AutoState.PICK_UP_FIRST_SAMPLE;
                }
                break;

            case PICK_UP_FIRST_SAMPLE:
                tiltMotor.setTargetPosition(540);
                slideMotor.setTargetPosition(SlideMinPosition);
                grabTimer.reset();
                autoState = AutoState.GRAB_FIRST_SAMPLE;

                break;

            case GRAB_FIRST_SAMPLE:
                intake_claw_servo.setPosition(Claws_closed);
                if (grabTimer.seconds() >= .08) {
                    autoState = AutoState.EXTEND;
                }
                break;
            case EXTEND:
                drive.followTrajectorySequence(goObsevationZone);
                if ((Math.abs(drive.getPoseEstimate().getX() - 40) <= 2) && (Math.abs(drive.getPoseEstimate().getY() - -48) <= 2)){
                    slideMotor.setTargetPosition(1000);
                    extendTimer.reset();
                }
                if (extendTimer.seconds() >= 1){
                    intake_claw_servo.setPosition(Claws_open);
                    grabTimer.reset();
                    autoState = AutoState.GRAB_SECOND_SPECIMEN;
                }
                break;
            case GRAB_SECOND_SPECIMEN:
                slideMotor.setTargetPosition(SlideMaxPosition);
                tiltMotor.setTargetPosition(TiltPickupPosition);
                if (grabTimer.seconds() >.8 ){
                    intake_claw_servo.setPosition(Claws_closed);
                    autoState = AutoState.FINISH;
                }

            case FINISH:
                slideMotor.setTargetPosition(0);
                AutoWasRan = true;
                drive.update();
                break;

        }
    }
}

