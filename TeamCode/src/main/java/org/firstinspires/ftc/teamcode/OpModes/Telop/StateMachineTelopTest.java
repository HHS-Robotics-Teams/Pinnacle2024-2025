package org.firstinspires.ftc.teamcode.OpModes.Telop;


import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_closed;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.IntakeWristPositionReached;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighBucketBackwards;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMaxPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlidePower;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideTicks;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideWallPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltFloorPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucketBackwards;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHomePosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltLowBucket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMaxPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltSlowSlowPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltTickThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltUpThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltWallPickupPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristHorizontalPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSampleBucketScore;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingFloorPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingHighBasket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingHome;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingWallPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.climbPositionReached;
import static org.firstinspires.ftc.teamcode.Constants.Fields.currentClawStep;
import static org.firstinspires.ftc.teamcode.Constants.Fields.currentClimbStep;
import static org.firstinspires.ftc.teamcode.Constants.Fields.currentRetractionStep;
import static org.firstinspires.ftc.teamcode.Constants.Fields.elbowRotate;
import static org.firstinspires.ftc.teamcode.Constants.Fields.isTiltIncrementing;
import static org.firstinspires.ftc.teamcode.Constants.Fields.sampleFloorPickUp;
import static org.firstinspires.ftc.teamcode.Constants.Fields.specimenMode;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ActivelyClimbing;
import static org.firstinspires.ftc.teamcode.Constants.Fields.PreppingClimbers;
import static org.firstinspires.ftc.teamcode.Constants.Fields.clawsOpening;
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
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.colorSensor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants.DetectedColor;
import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.excutil.Input;

@TeleOp (name = "State Machine Telop", group = "Competition")
public class StateMachineTelopTest extends OpMode {
    public static int ExtensionMax;

    public Input input;
    ElapsedTime Extend_timer = new ElapsedTime();
    ElapsedTime Claw_timer = new ElapsedTime();
    ElapsedTime Climber_Timer = new ElapsedTime();
    ElapsedTime Deposit_Timer = new ElapsedTime();

    @Override
    public void init() {

        // ---------- Input Class ----------
        input = new Input();

        // ---------- Initialize Hardware ----------
        RobotHardware.init(hardwareMap);

        // ---------- Add Power ---------
        applyPowers();

        // ---------- Confirmation Printing ----------
        telemetry.addData("Status:", "✅ Robot is initialized.");
        telemetry.update();

    }

    @Override
    public void loop() {
        DetectedColor.updateColor(colorSensor);
        String detectedColor = DetectedColor.getColor();


        input.pollGamepad(gamepad1); // Pass gamepad input through custom class

        /* ============================== Driving and Wheels ============================== */

        // ---------- Maps Wheels to Joysticks ----------
        double rotate = (gamepad1.right_stick_x * 0.8); // Right stick: left and right
        double strafe = -gamepad1.left_stick_x;   // Left stick: left and right
        double drive = -gamepad1.left_stick_y;   //  Left stick: up and down

        // ---------- Slowdown While Arm Up ----------
        if (tiltMotor.getTargetPosition() >= TiltUpThreshold && !ActivelyClimbing) {
            rotate = rotate / 1.5;
            strafe = strafe / 1.5;
            drive = drive / 1.5;
        }
        if (tiltMotor.getCurrentPosition() >= TiltSlowSlowPosition) {
            rotate = rotate / 2.5;
            strafe = strafe / 2.5;
            drive = drive / 2.5;
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

        // ---------- Arm Power Modulation ----------
//        if (tiltMotor.getCurrentPosition() > 1500){
//            tiltMotor.setPower(.5); // So it doesn't fling itself onto the floor
//        }                           // when moving the arm back.


        // ---------- Manual Arm Tilt ----------
        if (input.dpad_up.held() && (tiltMotor.getCurrentPosition() <= TiltMaxPosition)) {
            tiltMotor.setTargetPosition(tiltMotor.getTargetPosition() + 15); // Arm up
        }

        if (input.dpad_down.held() && (tiltMotor.getCurrentPosition() >= TiltMinPosition)) {
            tiltMotor.setTargetPosition(tiltMotor.getTargetPosition() - 15); // Arm down
        }

        // ---------- Manual Extension ----------
        {// Manual Extension limit to stop over extension below arm straight out
            if (tiltMotor.getCurrentPosition() <= TiltHomePosition) {
                ExtensionMax = 515;
            } else {
                ExtensionMax = 550;
            }


            if (input.left_bumper.held() && (slideMotor.getCurrentPosition() <= ExtensionMax)) {
                slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + 80); // Slide out
            }

            if (input.left_trigger.held() && (slideMotor.getCurrentPosition() >= SlideMinPosition)) {
                slideMotor.setTargetPosition(slideMotor.getCurrentPosition() - 80); // Slide in
            }
        }
//        // ---------- Intake Wrist Servo ----------
//        if (input.dpad_left.down() && IntakeWristPositionReached) {
//            intakeWristServo.setPosition(WristCenter);
//        }
//
//        if (input.dpad_right.down() && IntakeWristPositionReached) {
//            intakeWristServo.setPosition(WristRight);
//        }

        // ---------- Intake Claws  ----------
        if (input.right_trigger.held()) { //
            intake_claw_servo.setPosition(Claws_closed);
        }
        if (input.right_bumper.held()) {
            clawsOpening = true;

        }
        if (clawsOpening) {
            switch (currentClawStep) {
                case (1):
                    intake_claw_servo.setPosition(Claws_open);
                    Deposit_Timer.reset();
                    currentClawStep++;
                    break;
                case (2):
                    if (elbowRotate && Deposit_Timer.seconds() > .35) {
                        telemetry.speak("Another score Good job Khang");
                        intakeWristServo.setPosition(WristRight);
                        elbowRotate = false;
                        clawsOpening = false;
                        currentClawStep = 1;
                        break;
                    }
                    if (!elbowRotate) {
                        clawsOpening = false;
                        currentClawStep = 1;
                        break;
                    }
            }
        }


        /* ============================== Scoring ============================== */

        // ------------ High Basket -------------
        if (input.y.down()) {
            armRetractingHighBasket = true;
            IntakeWristPositionReached = false;
            telemetry.speak("High Basket  ");
        }

        if (armRetractingHighBasket) {
            switch (currentRetractionStep) {

                case (1): // Step 1: Move the wrist back to center then retract the arm slide to min position.
                    intakeWristServo.setPosition(WristCenter);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < SlideTicks) {
                        currentRetractionStep++;
                    } // Checks to ensure it is actually at the correct place, then goes to the next step.
                    break;

                case (2): // Step 2: Move the arm up and back in the position it needs to be for backwards high bucket.
                    tiltMotor.setTargetPosition(TiltHighBucketBackwards);
                    intakeElbowServo.setPosition(ElbowRight);
                    Extend_timer.reset();
                    currentRetractionStep++;
                    break;

                case (3): // Step 3: Wait 1 second so tilt can move and inertia can finish, then slide out to high bucket height.
                    if (Math.abs(tiltMotor.getCurrentPosition() - TiltHighBucketBackwards) < TiltTickThreshold) {
                        slideMotor.setTargetPosition(SlideHighBucketBackwards);
                        currentRetractionStep++;
                    }
                    break;

                case (4): // Step 4: Wait a half second then move the elbow and wrist servos to the right positions.
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideHighBucketBackwards) < SlideTicks) {
                        elbowRotate = true;
                        intakeWristServo.setPosition(WristSampleBucketScore);
                        //intakeElbowServo.setPosition(ElbowRight);
                        currentRetractionStep = 1; // These two assignment statements reset the state for
                        armRetractingHighBasket = false; // next time the button is pressed.
                        break;
                    }
            }
        }

        // ------------ High Chamber --------------
        if (input.x.down()) {
            armRetractingHighChamber = true;
            IntakeWristPositionReached = true;
            telemetry.speak("High Chamber Get ready to dunk");
        }

        if (armRetractingHighChamber) {
            switch (currentRetractionStep) {
                case (1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < SlideTicks) {
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
            telemetry.speak("Homing ");
        }

        if (armRetractingHome) {
            switch (currentRetractionStep) {
                case (1):
                    if (sampleFloorPickUp) {
                        tiltMotor.setTargetPosition(TiltHomePosition);
                    }
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < SlideTicks) {
                        currentRetractionStep++;
                    }
                    break;

                case (2):
                    tiltMotor.setTargetPosition(TiltHomePosition);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    intakeWristServo.setPosition(WristCenter);
                    intakeElbowServo.setPosition(ElbowLeft);
                    currentRetractionStep = 1;
                    sampleFloorPickUp = false;
                    armRetractingHome = false;
                    break;
            }
        }

        // ---------- Specimen Wall Pickup ----------
        if (gamepad1.right_stick_button) {
            IntakeWristPositionReached = true;
            specimenMode = true;
            armRetractingWallPickup = true;
        }

        if (armRetractingWallPickup) {
            switch (currentRetractionStep) {

                case (1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < SlideTicks) {
                        intake_claw_servo.setPosition(Claws_open);
                        currentRetractionStep++;
                    }
                    break;

                case (2):
                    tiltMotor.setTargetPosition(TiltWallPickupPosition);
                    slideMotor.setTargetPosition(SlideWallPickup);
                    intakeWristServo.setPosition(WristRight);
                    intakeElbowServo.setPosition(ElbowLeft);
                    currentRetractionStep++;

                    break;
                case (3): {
                    if (detectedColor.equals("Red")) {
                        intake_claw_servo.setPosition(Claws_closed);
                        currentRetractionStep++;
                        break;
                    } else if (isTiltIncrementing) {
                        tiltMotor.setTargetPosition(tiltMotor.getCurrentPosition() + 20);
                        isTiltIncrementing = false;
                    } else if (!tiltMotor.isBusy()) {
                        isTiltIncrementing = true;
                        if (tiltMotor.getCurrentPosition() >= 650) {
                            intake_claw_servo.setPosition(Claws_closed);
                            currentRetractionStep++;
                            break;
                        }
                    }
                }
                case (4):
                    tiltMotor.setTargetPosition(750);
                    isTiltIncrementing = true;
                    currentRetractionStep = 1;
                    break;
            }
        }
            // ------------ Sample Floor Pickup ---------------
            if (input.b.down()) {
                armRetractingFloorPickup = true;
                IntakeWristPositionReached = false;
                telemetry.speak(" Grab another off the Seafloor");
            }

            if (armRetractingFloorPickup) {
                switch (currentRetractionStep) {
                    case (1):
                        slideMotor.setTargetPosition(SlideMinPosition);
                        if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < SlideTicks) {
                            currentRetractionStep++;
                        }
                        break;

                    case (2):
                        tiltMotor.setTargetPosition(TiltFloorPickup);
                        slideMotor.setTargetPosition(SlideWallPickup);
                        intakeWristServo.setPosition(WristCenter);
                        intakeElbowServo.setPosition(ElbowLeft);
                        intake_claw_servo.setPosition(Claws_open);
                        armRetractingFloorPickup = false;
                        sampleFloorPickUp = true;
                        currentRetractionStep = 1;
                        break;
                }
            }


//        /* ============================== Climbing ============================== */

            if (input.start.held()) {
                ActivelyClimbing = true;
                slideMotor.setTargetPosition(SlideMinPosition);
                tiltMotor.setTargetPosition(TiltHighBucket);
                leftClaw.setDirection(DcMotorSimple.Direction.FORWARD);
                rightClaw.setDirection(DcMotorSimple.Direction.REVERSE);
                leftClaw.setPower(1);
                rightClaw.setPower(1);
                telemetry.speak("ENDGAME ENDGAME ENDGAME");
            } else if (input.back.held()) { // Claw controls made by Benny
                leftClaw.setDirection(DcMotorSimple.Direction.REVERSE);
                rightClaw.setDirection(DcMotorSimple.Direction.FORWARD);
                leftClaw.setPower(1); // Debugged by Damien
                rightClaw.setPower(1);

            } else {
                leftClaw.setPower(0);
                rightClaw.setPower(0);
                telemetry.speak("Hook line and sinker");
            }

            /* ============================== Telemetry For Debugging ============================== */

            // ---------- Wheels and Driving ----------
//            telemetry.addData("Front Left Power: ", frontLeftPower);
//            telemetry.addData("Front Right Power: ", frontRightPower);
//            telemetry.addData("Back Left Power: ", backLeftPower);
//            telemetry.addData("Back Right Power: ", backRightPower);
//            telemetry.addData("Drive Power: ", drive);
//            telemetry.addData("Strafe Power: ", strafe);
//            telemetry.addData("Rotate Power: ", rotate);

            // ---------- Arm and Intake ----------
//            telemetry.addData("Slide Motor Power: ", slideMotor.getPower());
//            telemetry.addData("Tilt Motor Power: ", tiltMotor.getPower());
            telemetry.addData("Current Tilt Position: ", tiltMotor.getCurrentPosition());
            telemetry.addData("Current Slide Position ", slideMotor.getCurrentPosition());

            // ---------- Flags -----------
            telemetry.addData("Climb control status", climbPositionReached ? "True" : "False");
            telemetry.addData("Wrist control status", IntakeWristPositionReached ? "True" : "False");

            telemetry.addData("Current State", currentRetractionStep);

            // ---------- Update ----------
            telemetry.update();
        }
    }




