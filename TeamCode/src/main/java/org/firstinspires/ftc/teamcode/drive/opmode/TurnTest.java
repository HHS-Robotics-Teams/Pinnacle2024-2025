package org.firstinspires.ftc.teamcode.drive.opmode;

import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowSpecimenScoring;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

/*
 * This is a simple routine to test turning capabilities.
 */

@Config
@Autonomous(group = "drive")
public class TurnTest extends LinearOpMode {
    public static double ANGLE = 90; // deg

    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        RobotHardware.init(hardwareMap);
        applyPowers();

        while (opModeInInit()) {
            intakeWristServo.setPosition(WristLeft);
            intakeElbowServo.setPosition(ElbowSpecimenScoring);
            tiltMotor.setTargetPosition(TiltMinPosition);
            slideMotor.setTargetPosition(0);
        }

        waitForStart();

        if (isStopRequested()) return;

        drive.turn(Math.toRadians(ANGLE));
    }
}
