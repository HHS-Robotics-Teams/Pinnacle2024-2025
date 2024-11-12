package org.firstinspires.ftc.teamcode.components;

import static org.firstinspires.ftc.teamcode.OpModes.Constants.SlideStartPosition;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.TiltStartPosition;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class RobotComponents {

    public static DcMotor frontLeftMotor = null;
    public static DcMotor frontRightMotor = null;
    public static DcMotor backLeftMotor = null;
    public static DcMotor backRightMotor = null;

    // ---------- Intake + Arm Motors ----------
    public static DcMotor tiltMotor = null;
    public static DcMotor slideMotor = null;
    public static CRServo intakeCRServo = null;

    public static CRServo leftClaw = null;
    public static CRServo rightClaw = null;
    public static Servo intakeWristServo = null;

    public static void init(HardwareMap hardwareMap) {
        // ---------- Wheels ----------
        frontLeftMotor = hardwareMap.get(DcMotor.class, "front_left_motor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "front_right_motor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "back_left_motor");
        backRightMotor = hardwareMap.get(DcMotor.class, "back_right_motor");

        // ---------- Arm and Intake ----------
        slideMotor = hardwareMap.get(DcMotor.class, "slide_motor");
        tiltMotor = hardwareMap.get(DcMotor.class, "tilt_motor");
        intakeCRServo = hardwareMap.get(CRServo.class, "wheel_servo");
        intakeWristServo = hardwareMap.get(Servo.class, "wrist_servo");

        // ---------- Claws ----------
        leftClaw = hardwareMap.get(CRServo.class, "left_claw");
        rightClaw = hardwareMap.get(CRServo.class, "right_claw");

        /* ============================== Hardware Settings Fixes ============================== */

        // ---------- Reverse Left Side For Proper Strafing ----------
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // ---------- Resist Gravity and Inertia so Arm and Slide Stay In Place ----------
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        tiltMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // ---------- Error Evasion ----------
        tiltMotor.setTargetPosition(TiltStartPosition);
        slideMotor.setTargetPosition(SlideStartPosition);

        // ---------- Enable Encoder Based Movement ----------
        tiltMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        tiltMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        tiltMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // ---------- Stop Arm From Slamming Backwards ----------
        tiltMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        tiltMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


    }






}
