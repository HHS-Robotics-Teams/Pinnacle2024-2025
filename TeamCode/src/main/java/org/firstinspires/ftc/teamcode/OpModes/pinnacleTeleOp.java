package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Constants.intakeCRServo;
import static org.firstinspires.ftc.teamcode.Constants.intakeWristServo;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.excutil.Input;

@TeleOp(name="Pinnacle TeleOp", group="idk")
public class pinnacleTeleOp extends OpMode {

/* ============================== OpMode Attributes and Variables ============================== */

    Input input = new Input();

   

/* ============================== Hardware Configuration Mapping ============================== */

    @Override
    public void init() {

        // ---------- Wheels ----------
        Constants.frontLeftMotor = hardwareMap.get(DcMotor.class, "front_left_motor");
        Constants.frontRightMotor = hardwareMap.get(DcMotor.class, "front_right_motor");
        Constants.backLeftMotor = hardwareMap.get(DcMotor.class, "back_left_motor");
        Constants.backRightMotor = hardwareMap.get(DcMotor.class, "back_right_motor");

        // ---------- Arm and Intake ----------
        Constants.slideMotor = hardwareMap.get(DcMotor.class, "slide_motor");
        Constants.tiltMotor = hardwareMap.get(DcMotor.class, "tilt_motor");
        intakeCRServo = hardwareMap.get(CRServo.class, "wheel_servo");
        intakeWristServo = hardwareMap.get(Servo.class, "wrist_servo");

        // ---------- Claws ----------
        Constants.leftClaw = hardwareMap.get(CRServo.class, "left_claw");
        Constants.rightClaw = hardwareMap.get(CRServo.class, "right_claw");

/* ============================== Hardware Settings Fixes ============================== */

        // ---------- Reverse Left Side For Proper Strafing ----------
        Constants.frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        Constants.backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // ---------- Resist Gravity and Inertia so Arm and Slide Stay In Place ----------
        Constants.slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Constants.tiltMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // ---------- Error Evasion ----------
        Constants.tiltMotor.setTargetPosition(Constants.tiltStartPosition);
        Constants.slideMotor.setTargetPosition(Constants.slideStartPosition);

        // ---------- Enable Encoder Based Movement ----------
        Constants.tiltMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Constants.tiltMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Constants.slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Constants.tiltMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // ---------- Stop Arm From Slamming Backwards ----------
        Constants.tiltMotor.setDirection(DcMotorSimple.Direction.REVERSE);


        // ---------- Confirmation Printing ----------
        telemetry.addData("Status:", "✅ Robot is initialized.");
        telemetry.update();

    }

    @Override
    public void loop() {

/* ============================== Driving and Wheels ============================== */

        input.pollGamepad(gamepad1);

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
        Constants.frontLeftMotor.setPower(frontLeftPower);
        Constants.frontRightMotor.setPower(frontRightPower);
        Constants.backLeftMotor.setPower(backLeftPower);
        Constants.backRightMotor.setPower(backRightPower);

        // ---------- Set Arm and Intake Power ----------
        Constants.tiltMotor.setPower(Constants.tiltPower);
        Constants.slideMotor.setPower(Constants.slidePower);


/* ============================== Robot Controls ============================== */

        // ---------- Intake Wheel Servo ----------
        if (input.a.held() && !input.back.held()) { // 🔘 A button
            Constants.intakeCurrentPower = Constants.intakePower;
            intakeCRServo.setDirection(DcMotorSimple.Direction.FORWARD);
        } else if (input.b.held() && !input.back.held()) { // 🔘 B button
            Constants.intakeCurrentPower = Constants.intakePower;
            intakeCRServo.setDirection(DcMotorSimple.Direction.REVERSE);
        } else {
            Constants.intakeCurrentPower = 0;
        }
        intakeCRServo.setPower(Constants.intakeCurrentPower);

        // ---------- Intake Wrist Servo ----------
        if (input.x.held() && !input.y.held()) { // 🔘 X button
            intakeWristServo.setPosition(0);
        } /* both trigger values are stated to prevent confusion between one trigger and both triggers */
        if (input.y.held() && input.x.held()) { // 🔘 X and Y buttons
            intakeWristServo.setPosition(1);
        }
        if (input.y.held() && !input.x.held()) { // 🔘 Y button
            intakeWristServo.setPosition(0.5);
        }

        // ---------- Slide Movement ----------

        if (input.right_trigger.held()) { // 🔘 D-Pad right
            if (Constants.slideMotor.getCurrentPosition() < 1455) {// Min Slide height is 1455 ticks
                Constants.slideMotor.setTargetPosition(Math.min(Constants.slideMotor.getCurrentPosition() + Constants.slideTicks, 1455));
            }
        }
        if (input.left_trigger.held()) { // 🔘 D-Pad left
            if (Constants.slideMotor.getCurrentPosition() > 5) { // Min Slide height is 5 ticks
                Constants.slideMotor.setTargetPosition(Math.max(Constants.slideMotor.getCurrentPosition() - Constants.slideTicks, 5));
            }
        }

        // ---------- Tilt Movement ----------

        if (input.dpad_up.held()) { // 🔘 D-Pad up
            if (Constants.tiltMotor.getCurrentPosition() < 1475) { // Max Tilt Height is 550 (1453) ticks
                Constants.tiltMotor.setTargetPosition(Math.min(Constants.tiltMotor.getCurrentPosition() + Constants.armTicks, 1475));
            }
        }

        if (input.dpad_down.held()) { // 🔘 D-Pad down
            if (Constants.tiltMotor.getCurrentPosition() > 75) { // Min Tilt Height is 82 ticks
                Constants.tiltMotor.setTargetPosition(Math.max(Constants.tiltMotor.getCurrentPosition() - Constants.armTicks, 75));
            }
        }

        // Random testing position
      //  if (input.y){ // 🔘 Y button
       //     Constants.tiltMotor.setTargetPosition(400);
       // }


        // ----------- Claw Movement -----------

        if (input.left_bumper.held()) {
            Constants.leftClaw.setDirection(DcMotorSimple.Direction.FORWARD);
            Constants.rightClaw.setDirection(DcMotorSimple.Direction.REVERSE);
            Constants.leftClaw.setPower(1);
            Constants.rightClaw.setPower(1);
        }
        if (input.right_bumper.held()) { // Claw controls made by Benny
            Constants.leftClaw.setDirection(DcMotorSimple.Direction.REVERSE);
            Constants.rightClaw.setDirection(DcMotorSimple.Direction.FORWARD);
            Constants.leftClaw.setPower(1); // Debugged by Damien
            Constants.rightClaw.setPower(1);
        }
        if (!input.right_bumper.held() && !input.left_bumper.held()){
            Constants.leftClaw.setPower(0);
            Constants.rightClaw.setPower(0);
        }

        // ---------- Macros ------------
        if(input.a.held() && input.back.held()) {
            if (Constants.slideMotor.getCurrentPosition() < 1455) {
                Constants.slideMotor.setTargetPosition(1455);
            }
        }

        if(input.b.held() && input.back.held()) {
            if (Constants.slideMotor.getCurrentPosition() > 5) {
                Constants.slideMotor.setTargetPosition(5);
            }
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
        telemetry.addData("Slide Motor Power: ", Constants.slideMotor.getPower());
        telemetry.addData("Tilt Motor Power: ", Constants.tiltMotor.getPower());
        telemetry.addData("Current Tilt Position: ", Constants.tiltMotor.getCurrentPosition());
        telemetry.addData("Current Slide Position ", Constants.slideMotor.getCurrentPosition());
        telemetry.addData("Intake Spin Power: ", intakeCRServo.getDirection());

        // ---------- Update ----------
        telemetry.update();


    }

    @Override
    public void stop() {

        // ---------- Stops All Motors ----------
        Constants.frontLeftMotor.setPower(0);
        Constants.frontRightMotor.setPower(0);
        Constants.backLeftMotor.setPower(0);
        Constants.backRightMotor.setPower(0);
        Constants.slideMotor.setPower(0);
        Constants.tiltMotor.setPower(0);

    }

}
