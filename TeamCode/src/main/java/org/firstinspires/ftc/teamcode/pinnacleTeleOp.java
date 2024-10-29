package org.firstinspires.ftc.teamcode;

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

    String slideSpeedLabelSlow;
    String slideSpeedLabelNormal;
    String slideSpeedLabelFast;
    String slideSpeedLabel;
    int armTicks = 20;
    int slideTicks = 80;

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
        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);

        // ---------- Set Arm and Intake Power ----------
        tiltMotor.setPower(tiltPower);
        slideMotor.setPower(slidePower);


/* ============================== Robot Controls ============================== */

        // ---------- Intake Wheel Servo ----------
        if (input.a.held() && !input.back.held()) { // 🔘 A button
            intakeCurrentPower = intakePower;
            intakeCRServo.setDirection(DcMotorSimple.Direction.FORWARD);
        } else if (input.b.held() && !input.back.held()) { // 🔘 B button
            intakeCurrentPower = intakePower;
            intakeCRServo.setDirection(DcMotorSimple.Direction.REVERSE);
        } else {
            intakeCurrentPower = 0;
        }
        intakeCRServo.setPower(intakeCurrentPower);

        // ---------- Intake Wrist Servo ----------
        if (input.right_trigger.held() && !input.left_trigger.held()) { // 🔘 Right trigger
            intakeWristServo.setPosition(0);
        } /* both trigger values are stated to prevent confusion between one trigger and both triggers */
        if (input.left_trigger.held() && !input.right_trigger.held()) { // 🔘 Left trigger
            intakeWristServo.setPosition(1);
        }
        if (input.left_trigger.held() && input.right_trigger.held()) { // 🔘 Both triggers
            intakeWristServo.setPosition(0.5);
        }

        // ---------- Slide Movement ----------
        
        if (input.dpad_right.held()) { // 🔘 D-Pad right
            if (slideMotor.getCurrentPosition() < 1455) {// Min Slide height is 1455 ticks
                slideMotor.setTargetPosition(Math.min(slideMotor.getCurrentPosition() + slideTicks, 1455));
            }
        }
        if (input.dpad_left.held()) { // 🔘 D-Pad left
            if (slideMotor.getCurrentPosition() > 5) { // Min Slide height is 5 ticks
                slideMotor.setTargetPosition(Math.max(slideMotor.getCurrentPosition() - slideTicks, 5));
            }
        }

        // ---------- Tilt Movement ----------

        if (input.dpad_up.held()) { // 🔘 D-Pad up
            if (tiltMotor.getCurrentPosition() < 550) { // Max Tilt Height is 550 ticks
                tiltMotor.setTargetPosition(Math.min(tiltMotor.getCurrentPosition() + armTicks, 550));
            }
        }

        if (input.dpad_down.held()) { // 🔘 D-Pad down
            if (tiltMotor.getCurrentPosition() > 82) { // Min Tilt Height is 82 ticks
                tiltMotor.setTargetPosition(Math.max(tiltMotor.getCurrentPosition() - armTicks, 82));
            }
        }

        // Random testing position
      //  if (input.y){ // 🔘 Y button
       //     tiltMotor.setTargetPosition(400);
       // }


        // ----------- Claw Movement -----------

        if (input.left_bumper.held()) {
            leftClaw.setDirection(DcMotorSimple.Direction.FORWARD);
            rightClaw.setDirection(DcMotorSimple.Direction.REVERSE);
            leftClaw.setPower(1);
            rightClaw.setPower(1);
        }
        if (input.right_bumper.held()) { // Claw controls made by Benny
            leftClaw.setDirection(DcMotorSimple.Direction.REVERSE);
            rightClaw.setDirection(DcMotorSimple.Direction.FORWARD);
            leftClaw.setPower(1); // Debugged by Damien
            rightClaw.setPower(1);
        }
        if (!input.right_bumper.held() && !input.left_bumper.held()){
            leftClaw.setPower(0);
            rightClaw.setPower(0);
        }

        // ---------- Macros ------------
        if(input.a.held() && input.back.held()) {
            if (slideMotor.getCurrentPosition() < 1455) {
                slideMotor.setTargetPosition(1455);
            }
        }

        if(input.b.held() && input.back.held()) {
            if (slideMotor.getCurrentPosition() > 5) {
                slideMotor.setTargetPosition(5);
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
        telemetry.addData("Slide Motor Power: ", slideMotor.getPower());
        telemetry.addData("Tilt Motor Power: ", tiltMotor.getPower());
        telemetry.addData("Current Tilt Position: ", tiltMotor.getCurrentPosition());
        telemetry.addData("Current Slide Position ", slideMotor.getCurrentPosition());
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