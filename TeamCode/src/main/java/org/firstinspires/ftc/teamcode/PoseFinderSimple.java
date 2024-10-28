package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp (name = "PoseFinderSimple")
public class PoseFinderSimple extends OpMode {

    DcMotor slideMotor;
    DcMotor tiltMotor;
    CRServo intakeCRServo;
    Servo intakeWristServo;

    @Override
    public void init() {

        slideMotor = hardwareMap.get(DcMotor.class, "slide_motor");
        tiltMotor = hardwareMap.get(DcMotor.class, "tilt_motor");
        intakeCRServo = hardwareMap.get(CRServo.class, "wheel_servo");
        intakeWristServo = hardwareMap.get(Servo.class, "wrist_servo");


        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setTargetPosition(0);

        tiltMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        tiltMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        tiltMotor.setTargetPosition(0);

    }

    @Override
    public void loop() {


        telemetry.addData("Current Tilt Position: ", tiltMotor.getCurrentPosition());
        telemetry.addData("Current Slide Position ", slideMotor.getCurrentPosition());

        telemetry.update();
    }
}
