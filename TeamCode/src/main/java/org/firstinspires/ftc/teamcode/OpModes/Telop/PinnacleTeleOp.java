package org.firstinspires.ftc.teamcode.OpModes.Telop;

// Look at all these import statements :3
import static org.firstinspires.ftc.teamcode.Constants.Fields.AutoNotRan;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_closed;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;

import static org.firstinspires.ftc.teamcode.Constants.Fields.IntakeWristPositionReached;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighBucket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideLowChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMaxPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlidePower;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucketBackwards;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHomePosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltLowBucket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMaxPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltPickupPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltPower;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltSlowSlowPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltUpThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingFloorPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingHighBasket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingHome;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingSubPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingWallPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.buttonPressInitiate;
import static org.firstinspires.ftc.teamcode.Constants.Fields.clawsOpen;
import static org.firstinspires.ftc.teamcode.Constants.Fields.climbPositionReached;
import static org.firstinspires.ftc.teamcode.Constants.Fields.currentRetractionStep;
import static org.firstinspires.ftc.teamcode.Constants.Fields.specimenMode;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.backLeftMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.backRightMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.frontLeftMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.frontRightMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intake_claw_servo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.leftClaw;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.rightClaw;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Constants.Fields;
import org.firstinspires.ftc.teamcode.Constants.HardwareSettings;
import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.excutil.Input;

@TeleOp(name = "Pinnacle TeleOp", group = "competition")
public class PinnacleTeleOp extends OpMode {

    public Input input;

    @Override
    public void init() {


        // ---------- Input Class ----------
        input = new Input();
        RobotHardware.init(hardwareMap);
        //HardwareSettings.init(hardwareMap);
        intake_claw_servo.setPosition(Claws_closed);
//        // ---------- Map Hardware ----------
//        if (AutoNotRan) {
//            RobotHardware.init(hardwareMap);
//            HardwareSettings.init(hardwareMap);
//            slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//            tiltMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        }
//        else {
//            RobotHardware.init(hardwareMap);
//            HardwareSettings.init(hardwareMap);
//        }
        applyPowers();
        // ---------- Confirmation Printing ----------
        telemetry.addData("Status:", "✅ Robot is initialized.");
        telemetry.update();

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
            rotate = rotate / 1.5;
            strafe = strafe / 1.5;
            drive = drive / 2.5;
        }
        if (tiltMotor.getCurrentPosition() >= TiltSlowSlowPosition){
            rotate = rotate / 2.5;
            strafe = strafe / 2.5;
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

        if (tiltMotor.getCurrentPosition() > 1500){
            tiltMotor.setPower(.5);
        }

        // ---------- Set Arm and Intake Power ----------
        tiltMotor.setPower(TiltPower);
        slideMotor.setPower(SlidePower);


        // ---------- Manual Arm Tilt ----------
        if (input.dpad_up.held() && (tiltMotor.getCurrentPosition() <= TiltMaxPosition)) {
            tiltMotor.setTargetPosition(tiltMotor.getTargetPosition() + 20); // Arm up
            buttonPressInitiate = true;
        }

        if (input.dpad_down.held() && (tiltMotor.getCurrentPosition() >= TiltMinPosition)) {
            tiltMotor.setTargetPosition(tiltMotor.getTargetPosition() - 20); // Arm down
        }

        // ---------- Manual Extension ----------
        if (input.left_bumper.held() && (slideMotor.getCurrentPosition() <= SlideMaxPosition)) {
            slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + 80); // Slide out

        }

        if (input.left_trigger.held() && (slideMotor.getCurrentPosition() >= SlideMinPosition)) {
            slideMotor.setTargetPosition(slideMotor.getCurrentPosition() - 80); // Slide in
        }

        // ---------- Intake Wrist Servo ----------
        if (input.dpad_left.down() && IntakeWristPositionReached) {
            intakeWristServo.setPosition(WristCenter);

        }

        if (input.dpad_right.down() && IntakeWristPositionReached) {
            intakeWristServo.setPosition(WristRight);

        }

        // ---------- Intake Claws  ----------
        if (input.right_trigger.held()) { //
            intake_claw_servo.setPosition(Claws_closed);


        }
        if (input.right_bumper.held()) {
            intake_claw_servo.setPosition(Claws_open);

        }

        /* ============================== Scoring ============================== */

        // ------------ High Basket -------------
            if (input.y.down()) {
                armRetractingHighBasket = true;
                buttonPressInitiate = true;
                IntakeWristPositionReached = false;
            }
            if (armRetractingHighBasket) {
                switch (currentRetractionStep) {
                    case (1):
                        slideMotor.setTargetPosition(SlideMinPosition);
                        if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50) {


                            currentRetractionStep++;
                        }
                        break;

                    case (2):
                        tiltMotor.setTargetPosition(TiltHighBucket);
                        intakeWristServo.setPosition(WristCenter);
                        slideMotor.setTargetPosition(SlideHighBucket);
                        intakeElbowServo.setPosition(ElbowLeft);
                        currentRetractionStep = 1;
                        armRetractingHighBasket = false;

                        break;
                }
            }

            // ------------ High Chamber --------------
            if (input.x.down()) {
                armRetractingHighChamber = true;
                buttonPressInitiate = true;
                IntakeWristPositionReached = true;

                telemetry.speak("rodo control reached");
            }
            if (armRetractingHighChamber) {
                switch (currentRetractionStep) {
                    case (1):
                        slideMotor.setTargetPosition(SlideMinPosition);
                        if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50) {
                            currentRetractionStep++;
                        }
                        break;
                    case (2):
                        tiltMotor.setTargetPosition(TiltHighChamber);
                        slideMotor.setTargetPosition(SlideHighChamber);
                        intakeWristServo.setPosition(WristRight); // check position
                        intakeElbowServo.setPosition(ElbowLeft);
                        currentRetractionStep = 1;
                        armRetractingHighChamber = false;

                        break;
                }
            }
        /* ============================== Robot Controls ============================== */

        // ---------- Home ----------
        if (gamepad1.left_stick_button) {
            armRetractingHome = true;
            buttonPressInitiate = true;
        }
        if (armRetractingHome) {
            switch (currentRetractionStep) {
                case (1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50) {


                        currentRetractionStep++;
                    }
                    break;

                case (2):
                    tiltMotor.setTargetPosition(TiltHomePosition);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    intakeWristServo.setPosition(WristCenter);
                    intakeElbowServo.setPosition(ElbowLeft);
                    currentRetractionStep = 1;
                    armRetractingHome = false;

                    break;
            }
        }

        // ---------- Specimen Wall Pickup ----------
        if (gamepad1.right_stick_button) {
            IntakeWristPositionReached = true;
            specimenMode = true;
            buttonPressInitiate = true;
            armRetractingWallPickup = true;
        }
        if (armRetractingWallPickup){
            switch (currentRetractionStep){
                case (1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50) {

                        currentRetractionStep ++;
                    }
                    break;
                case (2):
                    tiltMotor.setTargetPosition(TiltPickupPosition);
                    slideMotor.setTargetPosition(1000);
                    intakeWristServo.setPosition(WristRight); // check position
                    intakeElbowServo.setPosition(ElbowLeft);
                    //intake_claw_servo.setPosition(Claws_open);
                    currentRetractionStep = 1;
                    armRetractingWallPickup = false;

                    break;
            }
        }

//        // ---------- Sample Submersible Pickup ----------
//        if (input.a.down()) {
//            armRetractingSubPickup = true;
//            buttonPressInitiate = true;
//            IntakeWristPositionReached = false;
//        }
//        if (armRetractingSubPickup) {
//            switch (currentRetractionStep) {
//                case (1):
//                    slideMotor.setTargetPosition(SlideMinPosition);
//                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50) {
//
//                        currentRetractionStep++;
//                    }
//                    break;
//
//                case (2):
//                    tiltMotor.setTargetPosition(418);
//                    slideMotor.setTargetPosition(SlideLowChamber);
//                    intakeWristServo.setPosition(WristCenter);
//                    intakeElbowServo.setPosition(ElbowLeft);
//                    intake_claw_servo.setPosition(Claws_open);
//                    currentRetractionStep = 1;
//                    armRetractingSubPickup = false;
//
//                    break;
//            }
//        }
        // ------------ Sample Floor Pickup ---------------
        if (input.b.down()) {
            armRetractingFloorPickup = true;
            buttonPressInitiate = true;
            IntakeWristPositionReached = false;
        }
        if (armRetractingFloorPickup) {
            switch (currentRetractionStep) {
                case (1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50) {
                        currentRetractionStep++;
                    }
                    break;

                case (2):
                    tiltMotor.setTargetPosition(650);
                    slideMotor.setTargetPosition(1000);
                    intakeWristServo.setPosition(WristCenter);
                    intakeElbowServo.setPosition(ElbowLeft);
                   // intake_claw_servo.setPosition(Claws_open);
                    currentRetractionStep = 1;
                    armRetractingFloorPickup = false;

                    break;
            }
        }

        /* ============================== Climbing ============================== */

            if (input.start.held()) {
                tiltMotor.setTargetPosition(TiltLowBucket);
                leftClaw.setDirection(DcMotorSimple.Direction.FORWARD);
                rightClaw.setDirection(DcMotorSimple.Direction.REVERSE);
                leftClaw.setPower(1);
                rightClaw.setPower(1);

                telemetry.speak("climb position reached");
            }
            else if (input.back.held()) { // Claw controls made by Benny
                leftClaw.setDirection(DcMotorSimple.Direction.REVERSE);
                rightClaw.setDirection(DcMotorSimple.Direction.FORWARD);
                leftClaw.setPower(1); // Debugged by Damien
                rightClaw.setPower(1);
            }
            else {
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
            //telemetry.addData("Intake Spin Power: ", intakeCRServo.getDirection());

            // ---------- Flags -----------
            telemetry.addData("Climb control status", climbPositionReached ? "True" : "False");
            telemetry.addData("Wrist control status", IntakeWristPositionReached ? "True" : "False");
            telemetry.addData("Current State", currentRetractionStep);

            // ---------- Update ----------
            telemetry.update();
    }
}
