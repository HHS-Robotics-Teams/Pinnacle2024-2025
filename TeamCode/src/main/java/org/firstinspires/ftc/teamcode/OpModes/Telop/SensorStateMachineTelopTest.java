package org.firstinspires.ftc.teamcode.OpModes.Telop;


import static org.firstinspires.ftc.teamcode.Constants.DetectedColorAndDistance.distance;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ActivelyClimbing;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_closed;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.CurrentlyQuickGrabbing;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.IntakeWristPositionReached;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighBucketBackwards;
import static org.firstinspires.ftc.teamcode.Constants.Fields.currentWallStep;
import static org.firstinspires.ftc.teamcode.Constants.Fields.manualSlideAdjustmentBasketAmount;
import static org.firstinspires.ftc.teamcode.Constants.Fields.manualSlideAdjustmentChamberAmount;
import static org.firstinspires.ftc.teamcode.Constants.Fields.manualSlideAdjustmentWallAmount;
import static org.firstinspires.ftc.teamcode.Constants.Fields.manualSlideChange;
import static org.firstinspires.ftc.teamcode.Constants.Fields.manualTiltChange;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideTicks;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideWallPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltFloorPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucketBackwards;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHomePosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMaxPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltSlowSlowPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltTickThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltUpThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltWallPickupPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSampleBucketScore;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingFloorPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingHighBasket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingHome;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingWallPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.clawsOpening;
import static org.firstinspires.ftc.teamcode.Constants.Fields.climbPositionReached;
import static org.firstinspires.ftc.teamcode.Constants.Fields.currentClawStep;
import static org.firstinspires.ftc.teamcode.Constants.Fields.currentRetractionStep;
import static org.firstinspires.ftc.teamcode.Constants.Fields.elbowRotate;
import static org.firstinspires.ftc.teamcode.Constants.Fields.manualTiltAdjustmentBasketAmount;
import static org.firstinspires.ftc.teamcode.Constants.Fields.manualTiltAdjustmentChamberAmount;
import static org.firstinspires.ftc.teamcode.Constants.Fields.manualTiltAdjustmentWallAmount;
import static org.firstinspires.ftc.teamcode.Constants.Fields.resetFlags;
import static org.firstinspires.ftc.teamcode.Constants.Fields.sampleFloorPickUp;
import static org.firstinspires.ftc.teamcode.Constants.Fields.specimenMode;
import static org.firstinspires.ftc.teamcode.Constants.FrontDistanceSensorCalculations.getChamberPivotAmount;
import static org.firstinspires.ftc.teamcode.Constants.FrontDistanceSensorCalculations.getChamberSlideAmount;
import static org.firstinspires.ftc.teamcode.Constants.FrontDistanceSensorCalculations.getPickupPivotAmount;
import static org.firstinspires.ftc.teamcode.Constants.FrontDistanceSensorCalculations.getPickupSlideAmount;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.backLeftMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.backRightMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.colorSensor;
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
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants.DetectedColorAndDistance;
import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.excutil.Input;

@TeleOp (name = "State Machine Telop", group = "Competition")
public class SensorStateMachineTelopTest extends OpMode {
    public static int ExtensionMax;

    public Input input;
    ElapsedTime StateMachine_Timer = new ElapsedTime();
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

        // --------- Reset Flags --------
        resetFlags();
        armRetractingHome = true;

        // ---------- Confirmation Printing ----------
        telemetry.addData("Status:", "✅ Robot is initialized.");
        telemetry.update();

    }

    @Override
    public void loop() {
        DetectedColorAndDistance.updateColor(colorSensor);

        double detectedDistance = DetectedColorAndDistance.getDistance();

        String detectedColor = DetectedColorAndDistance.getColor();

        input.pollGamepad(gamepad1); // Pass gamepad input through custom class

        /* ============================== Driving and Wheels ============================== */

        // ---------- Maps Wheels to Joysticks ----------
        double rotate = gamepad1.right_stick_x; // Right stick: left and right
        double strafe = gamepad1.left_stick_x;   // Left stick: left and right
        double drive = -gamepad1.left_stick_y;   //  Left stick: up and down

        // ---------- Slowdown While Arm Up or Out ----------
        if (armRetractingFloorPickup || armRetractingWallPickup) {
            rotate = rotate / 10;
        }
        if (tiltMotor.getTargetPosition() >= TiltUpThreshold && !ActivelyClimbing) {
            rotate = rotate / 2;
            strafe = strafe / 1.5;
            drive = drive / 1.5;
        }
        if (tiltMotor.getCurrentPosition() >= TiltSlowSlowPosition) {
            rotate = rotate / 3.5;
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

        // ---------- Manual Arm Tilt ----------
        if (input.dpad_up.held() && (tiltMotor.getCurrentPosition() <= TiltMaxPosition)) {
            tiltMotor.setTargetPosition(tiltMotor.getTargetPosition() + manualTiltChange); // Arm up
            if(armRetractingHighBasket){
                manualTiltAdjustmentBasketAmount++;
            }
            if(armRetractingHighChamber){
                manualTiltAdjustmentChamberAmount++;
            }
            if(armRetractingWallPickup){
                manualTiltAdjustmentWallAmount++;
            }
        }

        if (input.dpad_down.held() && (tiltMotor.getCurrentPosition() >= TiltMinPosition)) {
            tiltMotor.setTargetPosition(tiltMotor.getTargetPosition() - manualTiltChange); // Arm down
            if(armRetractingHighBasket){
                manualTiltAdjustmentBasketAmount--;
            }
            if(armRetractingHighChamber){
                manualTiltAdjustmentChamberAmount--;
            }
            if(armRetractingWallPickup){
                manualTiltAdjustmentWallAmount--;
            }
        }

        // ---------- Manual Extension ----------
        {// Manual Extension limit to stop over extension below arm straight out
            if (tiltMotor.getCurrentPosition() <= TiltHomePosition) {
                ExtensionMax = 455;
            } else {
                ExtensionMax = 510;
            }


            if (input.left_bumper.held() && (slideMotor.getCurrentPosition() <= ExtensionMax)) {
                slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + manualSlideChange); // Slide out
                if(armRetractingHighBasket){
                    manualSlideAdjustmentBasketAmount++;
                }
                if(armRetractingHighChamber){
                    manualSlideAdjustmentChamberAmount++;
                }
                if(armRetractingWallPickup){
                    manualSlideAdjustmentWallAmount++;
                }
            }

            if (input.left_trigger.held() && (slideMotor.getCurrentPosition() >= SlideMinPosition)) {
                slideMotor.setTargetPosition(slideMotor.getCurrentPosition() - manualSlideChange); // Slide in
                if(armRetractingHighBasket){
                    manualSlideAdjustmentBasketAmount--;
                }
                if(armRetractingHighChamber){
                    manualSlideAdjustmentChamberAmount--;
                }
                if(armRetractingWallPickup){
                    manualSlideAdjustmentWallAmount--;
                }
            }
        }
        /* ============================== Reset State ============================*/
        if (input.a.down() || input.cross.down()){
            telemetry.speak("No Way Home");
            currentRetractionStep = 1;
            resetFlags();

        }
        if (StateMachine_Timer.seconds() > 3){
            telemetry.speak("You have been timed out");
            currentRetractionStep = 1;
            StateMachine_Timer.reset();
            resetFlags();
        }
        // manual Wrist Control
        if (input.dpad_left.down() && elbowRotate) {
            intakeWristServo.setPosition(WristRight);
        }
        if (input.dpad_right.down() && elbowRotate){
            intakeWristServo.setPosition(WristSampleBucketScore);
        }

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
                    if (elbowRotate && Deposit_Timer.seconds() > .25) {
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
        if (input.y.down() || input.delta.down()) {
            armRetractingHighBasket = true;
            IntakeWristPositionReached = false;
            telemetry.speak("High Basket");
        }

        if (armRetractingHighBasket) {
            switch (currentRetractionStep) {

                case (1): // Step 1: Move the wrist back to center then retract the arm slide to min position.
                    intakeWristServo.setPosition(WristCenter);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) <= SlideTicks) {
                        currentRetractionStep++;
                        break;
                    } // Checks to ensure it is actually at the correct place, then goes to the next step.
                    break;

                case (2): // Step 2: Move the arm up and back in the position it needs to be for backwards high bucket.
                    tiltMotor.setTargetPosition(TiltHighBucketBackwards + (manualTiltAdjustmentBasketAmount * manualTiltChange));
                    intakeElbowServo.setPosition(ElbowRight);
                    Extend_timer.reset();
                    currentRetractionStep++;
                    break;

                case (3): // Step 3: Wait 1 second so tilt can move and inertia can finish, then slide out to high bucket height.
                    if (Math.abs(tiltMotor.getCurrentPosition() - TiltHighBucketBackwards) <= TiltTickThreshold) {
                        slideMotor.setTargetPosition(SlideHighBucketBackwards + (manualSlideAdjustmentBasketAmount * manualSlideChange));
                        currentRetractionStep++;
                        break;
                    }
                    break;

                case (4): // Step 4: Wait a half second then move the elbow and wrist servos to the right positions.
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideHighBucketBackwards) <= SlideTicks) {
                        elbowRotate = true;
                        intakeWristServo.setPosition(WristSampleBucketScore);
                        //intakeElbowServo.setPosition(ElbowRight);
                        StateMachine_Timer.reset();
                        currentRetractionStep = 1; // These two assignment statements reset the state for
                        armRetractingHighBasket = false; // next time the button is pressed.
                        break;
                    }
            }
        }

        // ------------ High Chamber --------------
        if (input.x.down() || input.square.down()) {
            armRetractingHighChamber = true;
            IntakeWristPositionReached = true;
            telemetry.speak("High Chamber Get ready to dunk");
        }

        if (armRetractingHighChamber) {
            switch (currentRetractionStep) {
                case (1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) <= SlideTicks) {
                        currentRetractionStep++;
                        break;
                    }
                    break;

                case (2):
                    tiltMotor.setTargetPosition(getChamberPivotAmount());
                    slideMotor.setTargetPosition(getChamberSlideAmount());
                    intakeWristServo.setPosition(WristRight); // check position
                    intakeElbowServo.setPosition(ElbowLeft);
                    StateMachine_Timer.reset();
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
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) <= SlideTicks) {
                        currentRetractionStep++;
                        break;
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
            telemetry.speak("picking up off the wall");
        }

        if (armRetractingWallPickup) {
            switch (currentRetractionStep) {

                case (1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) <= SlideTicks) {
                            intake_claw_servo.setPosition(Claws_open);
                            currentRetractionStep++;
                            break;

                    }
                        break;

                    case (2):
                        tiltMotor.setTargetPosition(getPickupPivotAmount());
                        slideMotor.setTargetPosition(getPickupSlideAmount());
                        intakeWristServo.setPosition(WristRight);
                        intakeWristServo.setPosition(WristRight);
                        intakeElbowServo.setPosition(ElbowLeft);
                        intake_claw_servo.setPosition(Claws_open);
                        StateMachine_Timer.reset();
                        currentRetractionStep = 1;
                        armRetractingWallPickup = false;
                        CurrentlyQuickGrabbing = true;
                        currentWallStep = 1;
                        break;
                    }
            }
        if (CurrentlyQuickGrabbing) {
            switch (currentWallStep) {
                case (1):
                    if (distance < 2) {
                        intake_claw_servo.setPosition(Claws_closed);
                        Claw_timer.reset();
                        currentWallStep ++;
                        break;
                    }
                    break;
                case (2):
                    if (Claw_timer.seconds() > .35) {
                        tiltMotor.setTargetPosition(tiltMotor.getCurrentPosition() + 75);
                        currentWallStep = 1;
                        CurrentlyQuickGrabbing = false;
                        break;
                    }
            }

        }

            // ------------ Sample Floor Pickup ---------------
            if (input.b.down() || input.circle.down()) {
                armRetractingFloorPickup = true;
                IntakeWristPositionReached = false;
                telemetry.speak(" Grab another off the Seafloor");
            }

            if (armRetractingFloorPickup) {
                switch (currentRetractionStep) {
                    case (1):
                        slideMotor.setTargetPosition(SlideMinPosition);
                        if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) <= SlideTicks) {
                            currentRetractionStep++;
                            break;
                        }
                        break;

                    case (2):
                        tiltMotor.setTargetPosition(TiltFloorPickup);
                        slideMotor.setTargetPosition(SlideWallPickup);
                        intakeWristServo.setPosition(WristCenter);
                        intakeElbowServo.setPosition(ElbowLeft);
                        intake_claw_servo.setPosition(Claws_open);
                        StateMachine_Timer.reset();
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
            telemetry.addData("Distance (cm)", "%.2f", detectedDistance);
            telemetry.addData("Current Tilt Position: ", tiltMotor.getCurrentPosition());
            telemetry.addData("Current Slide Position ", slideMotor.getCurrentPosition());

            // ---------- Flags -----------
            telemetry.addData("High Basket status", armRetractingHighBasket ? "True" : "False");
            telemetry.addData("High Chamber status", armRetractingHighChamber ? "True" : "False");
            telemetry.addData("Floor Pickup status", armRetractingFloorPickup ? "True" : "False");
            telemetry.addData("Home status", armRetractingHome ? "True" : "False");
            telemetry.addData("Wall Pickup status", armRetractingWallPickup ? "True" : "False");
            telemetry.addData("Climb control status", climbPositionReached ? "True" : "False");
            telemetry.addData("Wrist control status", IntakeWristPositionReached ? "True" : "False");

            telemetry.addData("Current State", currentRetractionStep);

            // ---------- Update ----------
            telemetry.update();
        }
    }




