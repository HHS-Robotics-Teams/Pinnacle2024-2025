package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Pinnacle TeleOp", group="idk")
public class pinnacleTeleOp extends OpMode {

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

    int slideStartPosition = 0;
    int tiltStartPosition = 0;

    int armTicks = 20;
    int slideTicks = 50;

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
        if (gamepad1.a) {
            intakeCurrentPower = intakePower;
            intakeCRServo.setDirection(DcMotorSimple.Direction.FORWARD);
        } else if (gamepad1.b) {
            intakeCurrentPower = intakePower;
            intakeCRServo.setDirection(DcMotorSimple.Direction.REVERSE);
        } else {
            intakeCurrentPower = 0;
        }
        intakeCRServo.setPower(intakeCurrentPower);

        // ---------- Intake Wrist Servo ----------
        if (gamepad1.right_trigger > 0.3) {
            intakeWristServo.setPosition(0);
        }  else if (gamepad1.left_trigger > 0.3) {
            intakeWristServo.setPosition(1);
        } else {
            intakeWristServo.setPosition(intakeWristServo.getCurrentPosition());
        }

        // ---------- Slide Movement ----------
        
        if (gamepad1.dpad_right) {
            slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + slideTicks);
        }
        if (gamepad1.dpad_left) {
            slideMotor.setTargetPosition(slideMotor.getCurrentPosition() - slideTicks);
        }

        // ---------- Tilt Movement ----------

        if (gamepad1.dpad_up) {
            tiltMotor.setTargetPosition(tiltMotor.getCurrentPosition() + armTicks);
        }
        if (gamepad1.dpad_down) {
            tiltMotor.setTargetPosition(tiltMotor.getCurrentPosition() - armTicks);
        }

        // ---------- Adjust Arm Tilting Speed ----------

        if (gamepad1.back && gamepad1.b) {
            int iteration;
            iteration = 0;
            if (iteration < 1) {
                if (armTicks > 0) {
                    armTicks++;
                    iteration++;
                }
            }
        }

        if (gamepad1.back && gamepad1.a) {
            int iteration;
            iteration = 0;
            if (iteration < 1) {
                if (armTicks > 0) {
                    armTicks--;
                    iteration++;
                }
            }
        }

        if(gamepad1.y){
            tiltPower=tiltPower+.05;
        }
        if(gamepad1.x){
            tiltPower=tiltPower-.05;
        }

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
        telemetry.addData("Move Speed: ", armTicks);
        telemetry.addData("Intake Spin Power: ", intakeCRServo.getDirection());

        // ---------- Update ----------
        telemetry.update();


    }

    @Override
    public void stop() {

        // ---------- Stops All Motors ----------
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
        slideMotor.setPower(0);
        tiltMotor.setPower(0);

    }

}