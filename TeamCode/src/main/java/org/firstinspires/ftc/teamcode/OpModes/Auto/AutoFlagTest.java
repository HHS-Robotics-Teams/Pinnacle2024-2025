package org.firstinspires.ftc.teamcode.OpModes.Auto;

import static org.firstinspires.ftc.teamcode.Constants.Fields.AutoNotRan;

import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_closed;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intake_claw_servo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.resetEncoders;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;


import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;

import org.firstinspires.ftc.teamcode.Constants.HardwareSettings;
import org.firstinspires.ftc.teamcode.Constants.RobotHardware;

@Config
@Autonomous(name="Auto Flag Test", group="idk")
public class AutoFlagTest extends OpMode {

    @Override
    public void init () {
        RobotHardware.init(hardwareMap);

        resetEncoders();
        applyPowers();
        


        intakeWristServo.setPosition(WristLeft);
        intakeElbowServo.setPosition(ElbowRight);
        tiltMotor.setTargetPosition(TiltMinPosition);
        slideMotor.setTargetPosition(0);
        intake_claw_servo.setPosition(Claws_closed);
    }


    @Override
    public void loop() {
         AutoNotRan = false;
        telemetry.addData("tilt motor", tiltMotor.getMode());
        telemetry.addData("tilt ticks", tiltMotor.getCurrentPosition());
        telemetry.addData("slide motor", slideMotor.getMode());
        telemetry.addData("slide ticks ", slideMotor.getCurrentPosition());
        telemetry.addData("Auto Not ran ", AutoNotRan ? "True" : "False");
        telemetry.update();


        }



    @Override
    public void stop() {

        requestOpModeStop();
    }

}
