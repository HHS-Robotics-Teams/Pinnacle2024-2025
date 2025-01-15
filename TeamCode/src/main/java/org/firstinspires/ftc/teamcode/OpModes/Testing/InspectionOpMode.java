package org.firstinspires.ftc.teamcode.OpModes.Testing;

import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.IntakeWristPositionReached;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighBucketBackwards;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideWallPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltFloorPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucketBackwards;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucketBackwardsAuto;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSampleBucketScore;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingFloorPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingHighBasket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.buttonPressInitiate;
import static org.firstinspires.ftc.teamcode.Constants.Fields.climbPositionReached;
import static org.firstinspires.ftc.teamcode.Constants.Fields.currentRetractionStep;
import static org.firstinspires.ftc.teamcode.Constants.Fields.elbowRotate;
import static org.firstinspires.ftc.teamcode.Constants.Fields.sampleFloorPickUp;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intake_claw_servo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants.Fields;
import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.excutil.Input;

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

        input.pollGamepad(gamepad1); // Pass gamepad input through custom class

        // ------------ High Basket -------------
        if (input.y.down()) {
            armRetractingHighBasket = true;
            buttonPressInitiate = true;
            IntakeWristPositionReached = false;
            telemetry.speak("High Basket");
        }

        if (armRetractingHighBasket) {
            switch (currentRetractionStep) {

                case (1): // Step 1: Move the wrist back to center then retract the arm slide to min position.
                    intakeWristServo.setPosition(WristCenter);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50) {
                        currentRetractionStep++;
                    } // Checks to ensure it is actually at the correct place, then goes to the next step.
                    break;

                case (2): // Step 2: Move the arm up and back in the position it needs to be for backwards high bucket.
                    tiltMotor.setTargetPosition(TiltHighBucketBackwardsAuto);
                    intakeElbowServo.setPosition(ElbowRight);
                    Extend_timer.reset();
                    currentRetractionStep++;
                    break;

                case (3): // Step 3: Wait 1 second so tilt can move and inertia can finish, then slide out to high bucket height.
                    if (Extend_timer.seconds() > 1) {
                        slideMotor.setTargetPosition(SlideHighBucketBackwards);
                        Claw_timer.reset();
                        currentRetractionStep++;
                    }
                    break;

                case (4): // Step 4: Wait a half second then move the elbow and wrist servos to the right positions.
                    if (Claw_timer.seconds() > .5) {
                        elbowRotate = true;
                        intakeWristServo.setPosition(WristSampleBucketScore);
                        //intakeElbowServo.setPosition(ElbowRight);
                        currentRetractionStep = 1; // These two assignment statements reset the state for
                        armRetractingHighBasket = false; // next time the button is pressed.
                        break;
                    }
            }
        }
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
                    tiltMotor.setTargetPosition(TiltFloorPickup);
                    slideMotor.setTargetPosition(1420);
                    intakeWristServo.setPosition(WristCenter);
                    intakeElbowServo.setPosition(ElbowLeft);
                    currentRetractionStep = 1;
                    armRetractingFloorPickup = false;
                    break;
            }
        }
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
