package org.firstinspires.ftc.teamcode.OpModes.Telop;

// Look at all these import statements :3
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_closed;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.CurrentlyScoring;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ResetArm;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SampleDropped;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SampleMode;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighBucketBackwards;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SpecimenMode;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucketBackwards;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHomePosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltSlowSlowPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltUpThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSampleBucketScore;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SetClawPowers;
import static org.firstinspires.ftc.teamcode.Constants.Fields.CurrentScoringMode;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltTickIncrement;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideTickIncrement;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElementsScored;
import static org.firstinspires.ftc.teamcode.Constants.Fields.KhangCheeredOn;
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
import static org.firstinspires.ftc.teamcode.Constants.Fields.CurrentlyQuickGrabbing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;

import org.firstinspires.ftc.teamcode.excutil.Input;

/* =================================== CONTROLS ===================================
 *
 * Start: Unbound, Liam said to avoid using Start.
 * Back: Change the scoring mode between Sample and Specimen. This will change robot's behavior when pressing Y.
 *
 * Y: Score whatever element is held. (Will differ depending on which scoring mode you are in. Current mode is displayed in Telemetry.)
 * B: Unbound
 * A: Unbound
 * X: Quick Grab (Not guaranteed to actually grab the sample.)
 *
 * LB: Pull Climbing Claws Down
 * LT: Lift Climbing Claws Up
 *
 * RB: Close Intake Claw
 * RT: Open Intake Claw
 *
 * ----- D-Pad -----
 * Up: Tilt Arm Up
 * Down: Tilt Arm Down
 * Left: Slide Retract (Slide In)
 * Right: Slide Extend (Slide Out)
 *
 * ----- Left Stick -----
 * Up: Drive Forward
 * Down: Drive Backward
 * Left: Strafe Left
 * Right: Strafe Right
 *
 * ----- Right Stick -----
 * Up: Unbound (Redundant)
 * Down: Unbound (Redundant)
 * Left: Turn Left
 * Right: Turn Right
 *
 */

@TeleOp(name = "Bad State Machine TeleOp", group = "Competition")
public class BadStateMachineTeleOp extends OpMode {

    // Liam said to stick to one enum but it feels better, to me atleast, to separate my concerns
    // into different sections. That way if something goes wrong in specimen scoring, we don't waste
    // time looking in sample scoring. I don't know, it makes sense to me. If issues arise, the
    // code can always be changed!

    public enum SampleScoringState {

        ArmUpToBasket,
        ArmExtendToBasket,
        ClawTurnToBasket,
        ClawDropSample,
        ClawTurnFromBasket,
        ArmRetractFromBasket,
        ArmLowerToHome,

    }

    public enum SpecimenScoringState {

        DunkOnRung,
        DriveFromRung,
        ArmGoToHome

    }

    public enum QuickGrab {

        ExtendArm,
        LowerArm,
        GrabSample,
        RaiseArm,
        RetractArm

    }

    SampleScoringState CurrentSampleScoringState = SampleScoringState.ArmUpToBasket;
    SpecimenScoringState CurrentSpecimenScoringState = SpecimenScoringState.DunkOnRung;
    QuickGrab CurrentQuickGrabStep = QuickGrab.ExtendArm;

    ElapsedTime SampleDropTimer = new ElapsedTime();
    ElapsedTime SpecimenDriveTimer = new ElapsedTime();
    ElapsedTime QuickGrabPickupTimer = new ElapsedTime();

    public Input input;

    @Override
    public void init() {

        // ---------- Initialize Input ----------
        input = new Input();

        // ---------- Initialize Hardware ----------
        RobotHardware.init(hardwareMap);

        // ---------- Add Power ---------
        applyPowers();

        // ---------- Confirmation Printing ----------
        telemetry.addData("Status:", "✅ Robot is initialized.");
        telemetry.speak("I'm done josh ing around, let's do this.");
        telemetry.update();
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

/* =================================== Driving =================================== */

        // ---------- Map Driving Controls ----------
        double rotate = (gamepad1.right_stick_x * 0.8);
        double strafe = -gamepad1.left_stick_x;
        double drive = -gamepad1.left_stick_y;

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

        // ---------- Slowdown While Arm Up ----------
        if (tiltMotor.getTargetPosition() >= TiltUpThreshold) {
            telemetry.speak("Activating slow mode.");
            rotate = rotate / 1.5;
            strafe = strafe / 1.5;
            drive = drive / 2.5;                              }
        // Cuts speed when arm is too high to
        // prevent inertia from overpowering the arm.
        if (tiltMotor.getCurrentPosition() >= TiltSlowSlowPosition) {
            telemetry.speak("Activating snail mode.");
            rotate = rotate / 2.5;
            strafe = strafe / 2.5;
            drive = drive / 3;                                      }

        // ---------- Arm Power Modulation ----------
        if (tiltMotor.getCurrentPosition() > 1500)  {
            telemetry.speak("Dropping arm power, please exercise caution.");
            tiltMotor.setPower(.5);                 }
            // So it doesn't fling itself onto the floor when moving the arm back.


/* =================================== Scoring =================================== */

        // ---------- Quick Grab ----------
        if (input.x.down()) {
            CurrentlyQuickGrabbing = true; // Initiate the Quick Grab.
            telemetry.speak("Attempting Quick Grab, wish me luck.");
        }

        if (CurrentlyQuickGrabbing) {  // Case names should explain enough.

            switch (CurrentQuickGrabStep) {

                case ExtendArm:
                    intakeElbowServo.setPosition(ElbowLeft);
                    intakeWristServo.setPosition(WristCenter);
                    slideMotor.setTargetPosition(600);
                    intake_claw_servo.setPosition(Claws_open);
                    CurrentQuickGrabStep = QuickGrab.LowerArm;
                    break;

                case LowerArm:
                    tiltMotor.setTargetPosition(25);
                    CurrentQuickGrabStep = QuickGrab.GrabSample;
                    QuickGrabPickupTimer.reset();
                    break;

                case GrabSample:
                    if (QuickGrabPickupTimer.seconds() > 5)           {
                        intake_claw_servo.setPosition(Claws_closed);
                        CurrentQuickGrabStep = QuickGrab.RaiseArm;      }
                    break;

                case RaiseArm:
                    if (QuickGrabPickupTimer.seconds() > 5)         {
                        tiltMotor.setTargetPosition(TiltHomePosition);
                        CurrentQuickGrabStep = QuickGrab.RetractArm;    }
                    break;

                case RetractArm:
                    if (Math.abs(tiltMotor.getCurrentPosition() - TiltHomePosition) >= 50) {
                        slideMotor.setTargetPosition(SlideMinPosition);
                        CurrentQuickGrabStep = QuickGrab.LowerArm;
                        CurrentlyQuickGrabbing = false; /* Resets the Quick Grab. */     }
                    break;
            }
        }

        // ---------- Change Mode ----------
        if (input.back.down()) {
            if (SampleMode) {
                SampleMode = false; // Switches controls to
                SpecimenMode = true; // specimen mode.
                CurrentScoringMode = "🧱 Sample Scoring";
                telemetry.speak("Sample Mode activated.");
            } else {
                SpecimenMode = false; // Switches controls to
                SampleMode = true; // sample mode.
                CurrentScoringMode = "📎 Specimen Scoring";
                telemetry.speak("Specimen Mode activated.");
            }
        }

        // ---------- Initiate Scoring ----------
        if (input.y.down()) {
            if (!CurrentlyScoring) {
                CurrentlyScoring = true; // Initiate the state machine.
                telemetry.speak("Attempting to score the element.");
            } else {
                ResetArm(); // If pressed again it will reset itself.
                telemetry.speak("Nevermind, I am now resetting my arm.");
                if (SampleMode) {
                    CurrentSampleScoringState = SampleScoringState.ArmUpToBasket;
                } else { // Ex: You realized you weren't lined up properly, before it was too late.
                    CurrentSpecimenScoringState = SpecimenScoringState.DunkOnRung;
                }
                CurrentlyScoring = false;
            }
        }

        if (CurrentlyScoring) { // Case names should explain enough.

            if (SampleMode) { // Only does this switch if the robot is in Sample Mode.

                switch (CurrentSampleScoringState) {

                    case ArmUpToBasket:
                        tiltMotor.setTargetPosition(TiltHighBucketBackwards);
                        CurrentSampleScoringState = SampleScoringState.ArmExtendToBasket;
                        break;

                    case ArmExtendToBasket:
                        if (Math.abs(tiltMotor.getCurrentPosition() - TiltHighBucketBackwards) >= 25) {
                            slideMotor.setTargetPosition(SlideHighBucketBackwards);
                            CurrentSampleScoringState = SampleScoringState.ClawTurnToBasket;          }
                        break;

                    case ClawTurnToBasket:
                        if (Math.abs(slideMotor.getCurrentPosition() - SlideHighBucketBackwards) >= 40) {
                            intakeElbowServo.setPosition(ElbowRight); // I think these are the right positions,
                            intakeWristServo.setPosition(WristSampleBucketScore); // might need to change them.
                            SampleDropTimer.reset();
                            CurrentSampleScoringState = SampleScoringState.ClawDropSample;              }
                        break;

                    case ClawDropSample:
                        if (SampleDropTimer.seconds() > 0.5)            {
                            intake_claw_servo.setPosition(Claws_open);
                            telemetry.speak("Sample scored!");
                            ElementsScored++;
                            SampleDropped = true;                       }
                        if (SampleDropped)                                                      {
                            intake_claw_servo.setPosition(Claws_closed);
                            SampleDropped = false; // Reset for next score.
                            CurrentSampleScoringState = SampleScoringState.ClawTurnFromBasket;  }
                        break;

                    case ClawTurnFromBasket:
                        intakeElbowServo.setPosition(ElbowCenter);
                        intakeWristServo.setPosition(WristCenter);
                        CurrentSampleScoringState = SampleScoringState.ArmRetractFromBasket;
                        break;

                    case ArmRetractFromBasket:
                        if (SampleDropTimer.seconds() > 1.0)                                {
                            slideMotor.setTargetPosition(SlideMinPosition);
                            CurrentSampleScoringState = SampleScoringState.ArmLowerToHome;  }
                        break;

                    case ArmLowerToHome:
                        if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) >= 300) {
                            ResetArm();
                            CurrentSampleScoringState = SampleScoringState.ArmUpToBasket;
                            CurrentlyScoring = false;                                            }
                        break;

                }

            }

            if (SpecimenMode) { // Only does this switch if robot is in Specimen Mode.

                switch (CurrentSpecimenScoringState) { // Case names should explain enough.

                    case DunkOnRung:
                        tiltMotor.setTargetPosition(TiltHighChamber - 50); // Using -50 for now, we will make a variable when we test.
                        CurrentSpecimenScoringState = SpecimenScoringState.DriveFromRung;
                        SpecimenDriveTimer.reset();
                        break;

                    case DriveFromRung:
                        if (Math.abs(tiltMotor.getCurrentPosition() - (TiltHighChamber - 50)) >= 20) {
                            if (SpecimenDriveTimer.seconds() < 0.5) { // Drive backwards for half a second.
                                frontLeftMotor.setPower(-1);
                                frontRightMotor.setPower(-1);
                                backLeftMotor.setPower(-1);
                                backRightMotor.setPower(-1);
                            } else {
                                intake_claw_servo.setPosition(Claws_open);
                                telemetry.speak("Specimen scored!");
                                ElementsScored++;
                                CurrentSpecimenScoringState = SpecimenScoringState.ArmGoToHome;
                            }                                                                      }
                        break;

                    case ArmGoToHome:
                        ResetArm();
                        CurrentSpecimenScoringState = SpecimenScoringState.DunkOnRung;
                        break;

                }

            }

        }

/* =================================== Manual Controls =================================== */


        // ---------- Tilt Motor ----------
        if (input.dpad_up.held()) /* Up */                                                                     {
            tiltMotor.setTargetPosition(Math.abs(tiltMotor.getCurrentPosition() + TiltTickIncrement));    }

        if (input.dpad_down.held()) /* Down */                                                                 {
            tiltMotor.setTargetPosition(Math.abs(tiltMotor.getCurrentPosition() - TiltTickIncrement));    }


        // ---------- Slide Motor ----------
        if (input.dpad_right.held()) /* Extend */                                                              {
            slideMotor.setTargetPosition(Math.abs(slideMotor.getCurrentPosition() + SlideTickIncrement)); }

        if (input.dpad_left.held()) /* Retract */                                                              {
            slideMotor.setTargetPosition(Math.abs(slideMotor.getCurrentPosition() - SlideTickIncrement)); }

        // ---------- Intake Claw ----------
        if (input.right_bumper.down()) /* Closed */         {
            intake_claw_servo.setPosition(Claws_closed);    }

        if (input.right_trigger.down()) /* Open */          {
            intake_claw_servo.setPosition(Claws_open);      }


/* =================================== Climbing =================================== */

        // ---------- Claw Controls ----------
        if (input.left_bumper.held()) {
            leftClaw.setDirection(DcMotorSimple.Direction.REVERSE);
            rightClaw.setDirection(DcMotorSimple.Direction.FORWARD);
            SetClawPowers(1.0);
        } else { SetClawPowers(0.0); }

        if (input.left_trigger.held()) {
            leftClaw.setDirection(DcMotorSimple.Direction.FORWARD);
            rightClaw.setDirection(DcMotorSimple.Direction.REVERSE);
            SetClawPowers(1.0);
        } else { SetClawPowers(0.0); }


/* =================================== Telemetry =================================== */

        // ---------- Current Mode ----------
        telemetry.addData("Current Mode:", CurrentScoringMode);
        telemetry.addData("Curently QuickGrabbing? (T/F)", CurrentlyQuickGrabbing);

        // ---------- Current Scoring Step ----------
        telemetry.addData("Current Step:", CurrentSampleScoringState);
        telemetry.addData("Current Step:", CurrentSpecimenScoringState);

        // ---------- Updating ----------
        telemetry.update();

        // ---------- Extras ----------
        if (ElementsScored == 3 && !KhangCheeredOn) {
            telemetry.speak("You can do it Khang!");
            KhangCheeredOn = true;
        }
    }
}
