package org.firstinspires.ftc.teamcode.OpModes.Testing;

import static org.firstinspires.ftc.teamcode.Constants.DetectedColorAndDistance.blue;
import static org.firstinspires.ftc.teamcode.Constants.DetectedColorAndDistance.green;
import static org.firstinspires.ftc.teamcode.Constants.DetectedColorAndDistance.red;
import static org.firstinspires.ftc.teamcode.Constants.DetectedHueAndDistance.brightness;
import static org.firstinspires.ftc.teamcode.Constants.DetectedHueAndDistance.hue;
import static org.firstinspires.ftc.teamcode.Constants.DetectedHueAndDistance.saturation;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.IntakeWristPositionReached;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighBucketBackwards;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideTicks;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideWallPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltFloorPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucketBackwards;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucketBackwardsAuto;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHomePosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltTickThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSampleBucketScore;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingFloorPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingHighBasket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingHome;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingSubPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.buttonPressInitiate;
import static org.firstinspires.ftc.teamcode.Constants.Fields.climbPositionReached;
import static org.firstinspires.ftc.teamcode.Constants.Fields.currentRetractionStep;
import static org.firstinspires.ftc.teamcode.Constants.Fields.elbowRotate;
import static org.firstinspires.ftc.teamcode.Constants.Fields.sampleFloorPickUp;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.colorSensor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intake_claw_servo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants.DetectedColorAndDistance;
import org.firstinspires.ftc.teamcode.Constants.DetectedHueAndDistance;
import org.firstinspires.ftc.teamcode.Constants.Fields;
import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.excutil.Input;
@Disabled
@TeleOp (name = "Inspection Mode", group = "Inspection")
public class InspectionOpMode extends OpMode {


    public Input input;
    ElapsedTime Extend_timer = new ElapsedTime();
    ElapsedTime Claw_timer = new ElapsedTime();
    ElapsedTime Climber_Timer = new ElapsedTime();
    ElapsedTime Game_Timer = new ElapsedTime();
    @Override
    public void init() {
        // ---------- Input Class ----------
        input = new Input();

        // ---------- Initialize Hardware ----------
        RobotHardware.init(hardwareMap);
        RobotHardware.resetEncoders();

        // ---------- Add Power ---------
        Fields.applyPowers();

        // ---------- Confirmation Printing ----------
        telemetry.addData("Status:", "✅ Robot is initialized.");
        telemetry.update();


    }

    @Override
    public void loop() {
        DetectedColorAndDistance.updateColor(colorSensor);

        String detectedColor = DetectedColorAndDistance.getColor();

        double detectedDistance = DetectedColorAndDistance.getDistance();

        // HSV Color Sensing
        DetectedHueAndDistance.updateColor(colorSensor);

        String detectedHue = DetectedHueAndDistance.getColor();

        input.pollGamepad(gamepad1); // Pass gamepad input through custom class

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
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < SlideTicks) {
                        currentRetractionStep++;
                        break;
                    } // Checks to ensure it is actually at the correct place, then goes to the next step.
                    break;

                case (2): // Step 2: Move the arm up and back in the position it needs to be for backwards high bucket.
                    tiltMotor.setTargetPosition(TiltHighBucketBackwardsAuto);
                    intakeElbowServo.setPosition(ElbowRight);
                    Extend_timer.reset();
                    currentRetractionStep++;
                    break;

                case (3): // Step 3: Wait 1 second so tilt can move and inertia can finish, then slide out to high bucket height.
                    if (Math.abs(tiltMotor.getCurrentPosition() - TiltHighBucketBackwardsAuto) < TiltTickThreshold) {
                        slideMotor.setTargetPosition(SlideHighBucketBackwards);
                        currentRetractionStep++;
                        break;
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
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < SlideTicks) {
                        currentRetractionStep++;
                        break;
                    }
                    break;

                case (2):
                    tiltMotor.setTargetPosition(535);
                    slideMotor.setTargetPosition(455);
                    intakeWristServo.setPosition(WristCenter);
                    intakeElbowServo.setPosition(ElbowLeft);
                    intake_claw_servo.setPosition(Claws_open);
                    armRetractingFloorPickup = false;
                    sampleFloorPickUp = true;
                    currentRetractionStep = 1;
                    break;
            }
        }
        if (input.a.down() || input.cross.down()) {
            armRetractingSubPickup = true;
            IntakeWristPositionReached = false;
            telemetry.speak(" Grab another off the Seafloor");
        }

        if (armRetractingSubPickup) {
            switch (currentRetractionStep) {
                case (1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < SlideTicks) {
                        currentRetractionStep++;
                        break;
                    }
                    break;

                case (2):
                    tiltMotor.setTargetPosition(420);
                    slideMotor.setTargetPosition(190);
                    intakeWristServo.setPosition(WristCenter);
                    intakeElbowServo.setPosition(ElbowLeft);
                    intake_claw_servo.setPosition(Claws_open);
                    armRetractingFloorPickup = false;
                    sampleFloorPickUp = true;
                    currentRetractionStep = 1;
                    break;
            }
        }

        telemetry.addData("Hue Color", detectedHue);
        telemetry.addData("Hue", hue);
        telemetry.addData("Saturation", saturation);
        telemetry.addData("Brightness", brightness);
        telemetry.addData("Red", red);
        telemetry.addData("Green", green);
        telemetry.addData("Blue", blue);
        telemetry.addData("Detected Color", detectedColor);
        telemetry.addData("Distance (cm)", "%.2f", detectedDistance);


        telemetry.addData("Current Tilt Position: ", tiltMotor.getCurrentPosition());
        telemetry.addData("Current Slide Position ", slideMotor.getCurrentPosition());

        // ---------- Flags -----------
        telemetry.addData("Climb control status", climbPositionReached ? "True" : "False");
        telemetry.addData("Wrist control status", IntakeWristPositionReached ? "True" : "False");
        telemetry.addData("floor pickup", sampleFloorPickUp ? "True" : "False");
        telemetry.addData("elbow rotate", elbowRotate ? "True": "False");
        telemetry.addData("Current State", currentRetractionStep);

        // ---------- Update ----------
        telemetry.update();
    }
}
