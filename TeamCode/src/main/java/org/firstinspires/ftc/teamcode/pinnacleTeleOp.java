package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Pinnacle TeleOp", group="idk")
public class pinnacleTeleOp extends OpMode {

    // our four motors
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;

    private DcMotor tiltMotor;
    private DcMotor slideMotor;
    private CRServo intakeCRServo;
    private Servo intakeWristServo;

    double INTAKECURRENTPOWER;
    double INTAKEPOWER = 0.92;
    int slideStartPosition = 0;
    int tiltStartPosition = 0;
    int moveTicks = 20;
    double tiltPower = 1;
    double slidePower = 1;

    @Override
    public void init() {

        /*
         * --------------- Hardware Configuration Mapping ---------------
         */

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

        /*
         * --------------- Hardware Settings Fixes ---------------
         */

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

        /*
         * ---------------------------- Driving and Wheels ----------------------------
         */

        // maps power values to stick positions
        double rotate = -gamepad1.right_stick_x; // right stick: left and right
        double strafe = gamepad1.left_stick_x;   // left stick: left and right
        double drive = -gamepad1.left_stick_y;   //  left stick: up and down


        /* i don't know how any of this part works, i consulted chat gpt 💀
         * i will figure out the specifics once we can get the robot moving - Damien */
        // I also dont understand this, its way complex math - James //

        // wheel calculations
        double frontLeftPower = drive + strafe - rotate;
        double frontRightPower = drive - strafe + rotate;
        double backLeftPower = drive - strafe - rotate;
        double backRightPower = drive + strafe + rotate;


        // set motor power
        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);


        /*
         * ---------------------------- Intake Servo Control ----------------------------
         */

        // Intake CR Servo
        if (gamepad1.a) {
            INTAKECURRENTPOWER = INTAKEPOWER;
            intakeCRServo.setDirection(DcMotorSimple.Direction.FORWARD);
        } else if (gamepad1.b) {
            INTAKECURRENTPOWER = INTAKEPOWER;
            intakeCRServo.setDirection(DcMotorSimple.Direction.REVERSE);
        } else {
            INTAKECURRENTPOWER = 0;
        }
        intakeCRServo.setPower(INTAKECURRENTPOWER);

        if (gamepad1.right_trigger > 0.3) {
            intakeWristServo.setPosition(0);
        } else if (gamepad1.left_trigger > 0.3) {
            intakeWristServo.setPosition(1);
        } else {
            intakeWristServo.setPosition(0.5);
        }

        // --------------------------------------------------------------------------------


        tiltMotor.setPower(tiltPower);

        slideMotor.setPower(slidePower);


        // ---------- Slide controls ----------
        if (gamepad1.dpad_right) {
            slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + moveTicks);
        }

        if (gamepad1.dpad_left) {
            slideMotor.setTargetPosition(slideMotor.getCurrentPosition() - moveTicks);
        }

        // ---------- Tilt controls ----------
        if (gamepad1.dpad_up) {
            tiltMotor.setTargetPosition(tiltMotor.getCurrentPosition() + moveTicks);
        }

        if (gamepad1.dpad_down) {
            tiltMotor.setTargetPosition(tiltMotor.getCurrentPosition() - moveTicks);
        }

        // ---------- Adjust arm tilting speed ----------

        if (gamepad1.back && gamepad1.b) {
            int iteration;
            iteration = 0;
            if (iteration < 1) {
                if (moveTicks > 0) {
                    moveTicks++;
                    iteration++;
                }
            }
        }

        if (gamepad1.back && gamepad1.a) {
            int iteration;
            iteration = 0;
            if (iteration < 1) {
                if (moveTicks > 0) {
                    moveTicks--;
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











        // -------------------------------------------------------------------------------------

        /*
         * ---------------------------- Telemetry For Debugging ----------------------------
         */

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
        telemetry.addData("Move Speed: ", moveTicks);
        telemetry.addData("Intake Spin Power: ", intakeCRServo.getDirection());

        // ---------- Update ----------
        telemetry.update();


    }

    @Override
    public void stop() {
        // stops the motors
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
        slideMotor.setPower(0);
        tiltMotor.setPower(0);
    }

}