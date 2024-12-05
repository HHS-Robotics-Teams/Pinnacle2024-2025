package org.firstinspires.ftc.teamcode.OpModes;

// Look at all these import statements :3
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowSpecimenScoring;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowStarting;
import static org.firstinspires.ftc.teamcode.Constants.Fields.IntakeCurrentPower;
import static org.firstinspires.ftc.teamcode.Constants.Fields.IntakePower;
import static org.firstinspires.ftc.teamcode.Constants.Fields.IntakeRotateThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.IntakeWristPositionReached;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighBucket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideLowBucket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideLowChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMaxPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlidePower;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHomePosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltLowBucket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltLowChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMaxPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltPickupPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltPower;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltUpThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSpecimenWallPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingHighBasket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingHome;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingLowBasket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingLowChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.climbPositionReached;
import static org.firstinspires.ftc.teamcode.Constants.Fields.currentRetractionStep;
import static org.firstinspires.ftc.teamcode.Constants.Fields.specimenMode;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.backLeftMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.backRightMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.frontLeftMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.frontRightMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeCRServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.leftClaw;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.rightClaw;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.excutil.Input;

@TeleOp(name = "Pinnacle TeleOp", group = "idk")
public class pinnacleTeleOp extends OpMode {

    public Input input;

    @Override
    public void init() {

        // ---------- Input Class ----------
        input = new Input();

        // ---------- Map Hardware ----------
        RobotHardware.init(hardwareMap);

        // ---------- Confirmation Printing ----------
        telemetry.addData("Status:", "✅ Robot is initialized.");
        telemetry.update();

    }

    @Override
    public void start() {

        // ---------- Sets Starting Positions ----------
        tiltMotor.setTargetPosition(TiltMinPosition);
        slideMotor.setTargetPosition(SlideMinPosition);
        intakeWristServo.setPosition(WristLeft);
        intakeElbowServo.setPosition(ElbowStarting);

    }

    @Override
    public void loop() {

        input.pollGamepad(gamepad1); // Pass gamepad input through custom class

        /* ============================== Driving and Wheels ============================== */

        // ---------- Maps Wheels to Joysticks ----------
        double rotate = gamepad1.right_stick_x; // Right stick: left and right
        double strafe = -gamepad1.left_stick_x;   // Left stick: left and right
        double drive = -gamepad1.left_stick_y;   //  Left stick: up and down

        // ---------- Slowdown While Arm Up ----------
        if (tiltMotor.getTargetPosition() >= TiltUpThreshold) {
            rotate = rotate / 2;
            strafe = strafe / 2;
            drive = drive / 3;
        }

        // ---------- Wheel Calculations ----------
        double frontLeftPower = drive + strafe + rotate;
        double frontRightPower = drive - strafe - rotate;
        double backLeftPower = drive - strafe + rotate;
        double backRightPower = drive + strafe - rotate;

        // ---------- Set Wheel Power ----------
        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);

        // ---------- Set Arm and Intake Power ----------
        tiltMotor.setPower(TiltPower);
        slideMotor.setPower(SlidePower);

        // ---------- Arm Flags ----------
        if (tiltMotor.getCurrentPosition() <= IntakeRotateThreshold) {
            IntakeWristPositionReached = false;
            intakeWristServo.setPosition(WristCenter);
        }

        /* ============================== Robot Controls ============================== */

        // ---------- Home ----------
        if (gamepad1.left_stick_button) {
            armRetractingHome = true;
        }

        if (armRetractingHome) {
            switch (currentRetractionStep) {
                case (1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if(Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50) {
                        currentRetractionStep++;
                    }
                    break;

                case (2):
                    tiltMotor.setTargetPosition(TiltHomePosition);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    intakeWristServo.setPosition(WristCenter);
                    intakeElbowServo.setPosition(ElbowStarting);
                    currentRetractionStep = 1; armRetractingHome = false;
                    break;
            }
        }

        // ---------- Specimen Wall Pickup ----------
        if (gamepad1.right_stick_button) {
            tiltMotor.setTargetPosition(TiltPickupPosition);
            slideMotor.setTargetPosition(SlideMinPosition);
            intakeWristServo.setPosition(WristSpecimenWallPickup);
            intakeElbowServo.setPosition(ElbowSpecimenScoring); specimenMode = true;
        }

        // ---------- Manual Arm Tilt ----------
        if (input.dpad_up.held() && (tiltMotor.getCurrentPosition() <= TiltMaxPosition)) {
            tiltMotor.setTargetPosition(tiltMotor.getTargetPosition() + 20); // Arm up
        }

        if (input.dpad_down.held() && (tiltMotor.getCurrentPosition() >= TiltMinPosition)) {
            tiltMotor.setTargetPosition(tiltMotor.getTargetPosition() - 20); // Arm down
        }

        // ---------- Manual Extension ----------
        if (input.left_bumper.held() && (slideMotor.getCurrentPosition() <= SlideMaxPosition)) {
            slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + 60); // Slide out
        }

        if (input.left_trigger.held() && (slideMotor.getCurrentPosition() >= SlideMinPosition)) {
            slideMotor.setTargetPosition(slideMotor.getCurrentPosition() - 60); // Slide in
        }

        // ---------- Intake Wrist Servo ----------
        if (input.dpad_left.down() && IntakeWristPositionReached) {
            intakeWristServo.setPosition(WristCenter);
        }

        if (input.dpad_right.down() && IntakeWristPositionReached) {
            intakeWristServo.setPosition(WristRight);
        }

        // ---------- Intake Wheel Servo ----------
        if (input.right_trigger.held()) { //
            IntakeCurrentPower = IntakePower;
            intakeCRServo.setDirection(DcMotorSimple.Direction.REVERSE);
        } else if (input.right_bumper.held()) {
            IntakeCurrentPower = IntakePower;
            intakeCRServo.setDirection(DcMotorSimple.Direction.FORWARD);
        } else {
            IntakeCurrentPower = 0;
        }
        intakeCRServo.setPower(IntakeCurrentPower);

        /* ============================== Scoring ============================== */

        // ------------ High Basket -------------
        if (input.y.down()) {
            armRetractingHighBasket = true;
            IntakeWristPositionReached = false;
        }

        if (armRetractingHighBasket) {
            switch (currentRetractionStep) {
                case (1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if(Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50) {
                        currentRetractionStep++;
                    }
                    break;

                case (2):
                    tiltMotor.setTargetPosition(TiltHighBucket);
                    intakeWristServo.setPosition(WristCenter);
                    slideMotor.setTargetPosition(SlideHighBucket);
                    intakeElbowServo.setPosition(ElbowStarting);
                    currentRetractionStep = 1;
                    armRetractingHighBasket = false;
                    break;
            }
        }

        // ------------ Low Basket ---------------
        if (input.b.down()) {
            armRetractingLowBasket = true;
            IntakeWristPositionReached = false;
        }

        if (armRetractingLowBasket) {
            switch (currentRetractionStep) {
                case (1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if(Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50){
                        currentRetractionStep++;
                    }
                    break;

                case (2):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    tiltMotor.setTargetPosition(TiltLowBucket);
                    intakeWristServo.setPosition(WristCenter);
                    slideMotor.setTargetPosition(SlideLowBucket);
                    intakeElbowServo.setPosition(ElbowStarting);
                    armRetractingLowBasket = false;
                    break;
            }
        }

        // ------------ High Chamber --------------
        if (input.x.down()) {
            armRetractingHighChamber = true;
            IntakeWristPositionReached = true;

            telemetry.speak("rodo control reached");
        }

        if (armRetractingHighChamber) {
            switch (currentRetractionStep) {
                case(1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if(Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50){
                        currentRetractionStep++;
                    }
                    break;
                case(2):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    tiltMotor.setTargetPosition(TiltHighChamber);
                    intakeWristServo.setPosition(WristCenter);
                    slideMotor.setTargetPosition(SlideHighChamber);
                    intakeElbowServo.setPosition(ElbowSpecimenScoring);
                    armRetractingHighChamber = false;
                    break;
            }
        }

        // ------------- Low Chamber ----------------
        if (input.a.down()) {
            armRetractingLowChamber = true;
            IntakeWristPositionReached = true;

            telemetry.speak("rodo control reached");
        }

        if (armRetractingLowChamber) {
            switch (currentRetractionStep) {
                case (1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50) {
                        currentRetractionStep++;
                    }
                    break;

                case (2):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    tiltMotor.setTargetPosition(TiltLowBucket);
                    intakeWristServo.setPosition(WristCenter);
                    slideMotor.setTargetPosition(SlideLowChamber);
                    intakeElbowServo.setPosition(ElbowSpecimenScoring);
                    armRetractingLowChamber = false;
                    break;
            }
        }

        /* ============================== Climbing ============================== */

        if (input.back.held()) {
            leftClaw.setDirection(DcMotorSimple.Direction.FORWARD);
            rightClaw.setDirection(DcMotorSimple.Direction.REVERSE);
            leftClaw.setPower(1);
            rightClaw.setPower(1);

            telemetry.speak("climb position reached");
        } else if (input.start.held()) { // Claw controls made by Benny
            leftClaw.setDirection(DcMotorSimple.Direction.REVERSE);
            rightClaw.setDirection(DcMotorSimple.Direction.FORWARD);
            leftClaw.setPower(1); // Debugged by Damien
            rightClaw.setPower(1);
        } else {
            leftClaw.setPower(0);
            rightClaw.setPower(0);
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

        // ---------- Flags -----------
        telemetry.addData("Climb control status", climbPositionReached ? "True" : "False");
        telemetry.addData("Wrist control status", IntakeWristPositionReached ? "True" : "False");

        // ---------- Update ----------
        telemetry.update();

    }

}