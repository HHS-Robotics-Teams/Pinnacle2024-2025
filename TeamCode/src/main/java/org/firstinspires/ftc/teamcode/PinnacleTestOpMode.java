package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.excutil.Input;

@TeleOp (name = "PinnacleTestOpMode" )
public class PinnacleTestOpMode extends OpMode {

    Input input= new Input();

    /* ============================== OpMode Attributes and Variables ============================== */

    // ---------- Wheel Motors ----------
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;

    // ---------- Intake + Arm Motors ----------
    private DcMotor tiltMotor;
    private DcMotor slideMotor;
    private CRServo intakeCRServo;
    private Servo intakeWristServo;

    double intakeCurrentPower;
    double intakePower = 1;
    double tiltPower = 1;
    double slidePower = 1;

    int intakeServoRight = 0;
    double intakeServoCenter =.5;
    int intakeServoLeft = 1;



    int tiltStartPosition = 0;
    int tiltpickuppos = 80;
    int tiltdrivepos = 50;
    int tiltlowbucketpos = 200;
    int tilthighbucketpos = 550;
    int tiltlowchamberpos = 150;
    int tilthighchamberpos = 400;

    int slideStartPosition = 0;
    int slidepickuppos = 80;
    int slidedrivepos = 50;
    int slidelowbucketpos = 200;
    int slidehighbucketpos = 550;
    int slidelowchamberpos = 150;
    int slidehighchamberpos = 400;

    int slideextensionlimit = 1455;
    boolean slideextensionlimitreached = false;


    /* ============================== Hardware Configuration Mapping ============================== */

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
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        tiltMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // ---------- Stop Arm From Slamming Backwards ----------
        tiltMotor.setDirection(DcMotorSimple.Direction.REVERSE);


        // ---------- Confirmation Printing ----------
        telemetry.addData("Status:", "✅ Robot is initialized.");
        telemetry.update();

    }

    @Override
    public void loop() {

        input.pollGamepad(gamepad1);
        if (tiltMotor.getCurrentPosition()>slideextensionlimit){
            slideextensionlimitreached=true;
            tiltMotor.setTargetPosition(tiltdrivepos);
        }

        /* ============================== Driving and Wheels ============================== */

        // ---------- Maps Wheels to Joysticks ----------
        double rotate = -gamepad1.right_stick_x; // right stick: left and right
        double strafe = gamepad1.left_stick_x;   // left stick: left and right
        double drive = -gamepad1.left_stick_y;   //  left stick: up and down

        // ---------- Wheel Calculations ----------
        double frontLeftPower = drive + strafe - rotate;
        double frontRightPower = drive - strafe + rotate;
        double backLeftPower = drive - strafe - rotate;
        double backRightPower = drive + strafe + rotate;

        // ---------- Set Wheel Power ----------
        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);

        // ---------- Set Arm and Intake Power ----------
        tiltMotor.setPower(tiltPower);
        slideMotor.setPower(slidePower);


        /* ============================== Robot Controls ============================== */

        // ---------- Intake Wheel Servo ----------
        if (input.right_bumper.held()) { // 🔘 A button
            intakeCRServo.setPower(intakePower);
            intakeCRServo.setDirection(DcMotorSimple.Direction.FORWARD);
        } else if (input.left_bumper.held()) { // 🔘 B button
            intakeCRServo.setPower(intakePower);
            intakeCRServo.setDirection(DcMotorSimple.Direction.REVERSE);
        } else {
            intakeCRServo.setPower(0);
        }


        // ---------- Intake Wrist Servo ----------
        if (gamepad1.right_trigger > 0.5 && gamepad1.left_trigger < 0.5) { // 🔘 Right trigger
            intakeWristServo.setPosition(intakeServoRight);
        } /* both trigger values are stated to prevent confusion between one trigger and both triggers */
        if (gamepad1.left_trigger > 0.5 && gamepad1.right_trigger < 0.5) { // 🔘 Left trigger
            intakeWristServo.setPosition(intakeServoLeft);
        }
        if (gamepad1.left_trigger > 0.5 && gamepad1.right_trigger > 0.5) { // 🔘 Both triggers
            intakeWristServo.setPosition(intakeServoCenter);
        }
        // pickup
        if (input.dpad_down.down()){
            tiltMotor.setTargetPosition(tiltpickuppos);
            slideMotor.setTargetPosition(slidepickuppos);
        }
        // high bucket scoring
        if (input.y.down()) {
            tiltMotor.setTargetPosition(tilthighbucketpos);
            slideMotor.setTargetPosition(slidehighbucketpos);
            intakeWristServo.setPosition(intakeServoCenter);

        }
        // low bucket scoring
        if (input.x.down()){
            tiltMotor.setTargetPosition(tiltlowbucketpos);
            slideMotor.setTargetPosition(slidelowbucketpos);
            intakeWristServo.setPosition(intakeServoCenter);
        }
        // high chamber scoring
        if (input.b.down()){
            tiltMotor.setTargetPosition(tilthighchamberpos);
            slideMotor.setTargetPosition(slidehighchamberpos);
            intakeWristServo.setPosition(intakeServoLeft);
        }
        // low chamber scoring
        if (input.a.down()){
            tiltMotor.setTargetPosition(tiltlowchamberpos);
            slideMotor.setTargetPosition(slidelowchamberpos);
            intakeWristServo.setPosition(intakeServoLeft);
        }
        // climbing



        /* ============================== Telemetry For Debugging ============================== */

        // ---------- Wheels and Driving ----------
        telemetry.addData("Front Left Power: ", frontLeftPower);
        telemetry.addData("Front Right Power: ", frontRightPower);
        telemetry.addData("Back Left Power: ", backLeftPower);
        telemetry.addData("Back Right Power: ", backRightPower);
        telemetry.addData("Drive Power: ", drive);
        telemetry.addData("Strafe Power: ", strafe);
        telemetry.addData("Rotate Power: ", rotate);

        // ---------- Arm and Intake ----------
        telemetry.addData("Slide Motor Power: ", slideMotor.getPower());
        telemetry.addData("Tilt Motor Power: ", tiltMotor.getPower());
        telemetry.addData("Current Tilt Position: ", tiltMotor.getCurrentPosition());
        telemetry.addData("Current Slide Position ", slideMotor.getCurrentPosition());
        telemetry.addData("Intake Spin Power: ", intakeCRServo.getDirection());

        // ---------- Update ----------
        telemetry.update();
    }
}


