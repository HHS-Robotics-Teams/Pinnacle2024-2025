package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class Constants {

    // ---------- Wheel Motors ----------
    public static DcMotor frontLeftMotor;
    public static DcMotor frontRightMotor;
    public static DcMotor backLeftMotor;
    public static DcMotor backRightMotor;

    // ---------- Intake + Arm Motors ----------
    public static DcMotor tiltMotor;
    public static DcMotor slideMotor;
    public static CRServo intakeCRServo;

    public static CRServo leftClaw;
    public static CRServo rightClaw;
    public static Servo intakeWristServo;

    public static double intakeCurrentPower;
    public static double intakePower = 1;
    public static double tiltPower = 1;
    public static double slidePower = 1;

    public static int slideStartPosition = 0;
    public static int tiltStartPosition = 0;
    public static int tiltUpThreshold = 800;

    public static int armTicks = 100;
    public static int slideTicks = 80;





}
