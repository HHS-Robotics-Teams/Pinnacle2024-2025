package org.firstinspires.ftc.teamcode.Constants;

import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class RobotHardware {

    public static DcMotor frontLeftMotor;
    public static DcMotor frontRightMotor;
    public static DcMotor backLeftMotor;
    public static DcMotor backRightMotor;

    // ---------- Intake + Arm Motors ----------
    public static DcMotor tiltMotor;
    public static DcMotor slideMotor;
    public static Servo  intake_claw_servo;

    public static CRServo leftClaw;
    public static CRServo rightClaw;
    public static Servo intakeWristServo;
    public static Servo intakeElbowServo;
    public static VoltageSensor batteryVoltageSensor;


    public static BHI260IMU imu;

    public static void init(HardwareMap hardwareMap) {
        // ---------- Wheels ----------
        frontLeftMotor = hardwareMap.get(DcMotor.class, "front_left_motor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "front_right_motor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "back_left_motor");
        backRightMotor = hardwareMap.get(DcMotor.class, "back_right_motor");

        // ---------- Arm and Intake ----------
        slideMotor = hardwareMap.get(DcMotor.class, "slide_motor");
        tiltMotor = hardwareMap.get(DcMotor.class, "tilt_motor");
        intake_claw_servo = hardwareMap.get(Servo.class, "intake_claw_servo");
        intakeWristServo = hardwareMap.get(Servo.class, "wrist_servo");
        intakeElbowServo = hardwareMap.get(Servo.class, "elbow_servo");

        // ---------- Claws ----------
        leftClaw = hardwareMap.get(CRServo.class, "left_claw");
        rightClaw = hardwareMap.get(CRServo.class, "right_claw");

        // ---------- Battery Voltage -----------
        batteryVoltageSensor = hardwareMap.voltageSensor.get("Control Hub");

        // ---------- IMU ----------
        imu = hardwareMap.get(BHI260IMU.class, "imu");

//        /* ============================== Hardware Settings Fixes ============================== */
        // ---------- Reverse Left Side For Proper Strafing ----------
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // ---------- Resist Gravity and Inertia so Arm and Slide Stay In Place ----------
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        tiltMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // ---------- Error Evasion ----------
        tiltMotor.setTargetPosition(0);
        slideMotor.setTargetPosition(0);

        // ---------- Enable Encoder Based Movement ----------
        tiltMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        tiltMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        tiltMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // ---------- Stop Arm From Slamming Backwards ----------
        tiltMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        tiltMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        intake_claw_servo.setDirection(Servo.Direction.REVERSE);

    }






}
