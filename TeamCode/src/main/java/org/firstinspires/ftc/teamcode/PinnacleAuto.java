package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.excutil.Input;

@Autonomous
public class PinnacleAuto extends OpMode {

    Input input = new Input();

    // ---------- Wheel Motors ----------
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;

    // ---------- Intake + Arm Motors ----------
    private DcMotor tiltMotor;
    private DcMotor slideMotor;
    private CRServo intakeCRServo;

    private CRServo leftClaw;
    private CRServo rightClaw;
    private Servo intakeWristServo;

    double intakeCurrentPower;
    double intakePower = 1;
    double tiltPower = 1;
    double slidePower = 1;

    int slideStartPosition = 0;
    int tiltStartPosition = 0;

    int armTicks = 100;
    int slideTicks = 80;

    @Override
    public void init() {

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
        tiltMotor.setTargetPosition(tiltStartPosition);
        slideMotor.setTargetPosition(slideStartPosition);

        // ---------- Enable Encoder Based Movement ----------
        tiltMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        tiltMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        tiltMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // ---------- Stop Arm From Slamming Backwards ----------
        tiltMotor.setDirection(DcMotorSimple.Direction.REVERSE);


        // ---------- Confirmation Printing ----------
        telemetry.addData("Status:", "✅ Robot is initialized.");
        telemetry.update();
    }

    @Override
    public void loop() {

    }
}
