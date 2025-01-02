package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_closed;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.CurrentlyScoring;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ResetArm;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SampleDropped;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SampleMode;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighBucketBackwards;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SpecimenMode;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucket;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucketBackwards;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHomePosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltUpThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideTickThreshold;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristSampleBucketScore;
import static org.firstinspires.ftc.teamcode.Constants.Fields.applyPowers;

import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intake_claw_servo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;

import org.firstinspires.ftc.teamcode.excutil.Input;


@TeleOp(name = " Bad State Machine TeleOp", group = "Competition")
public class BadStateMachineTeleOp extends OpMode {

    public enum ScoringState {

        // ---------- Sample Scoring ----------
        ArmUpToBasket,
        ArmExtendToBasket,
        ClawTurnToBasket,
        ClawDropSample,
        ClawTurnFromBasket,
        ArmRetractFromBasket,
        ArmLowerToHome,

        // ---------- Specimen Scoring ----------
        ArmUpToRung,
        DriveToRung,
        DunkOnRung,
        DriveFromRung,
        OpenClaw,
        ArmGoToHome
    }

    ScoringState CurrentScoringState = ScoringState.ArmUpToBasket;

    ElapsedTime SampleDropTimer = new ElapsedTime();
    ElapsedTime Timer2 = new ElapsedTime(); // Change the names as they're used.
    ElapsedTime Timer3 = new ElapsedTime();

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
        telemetry.speak("I'm done josh ing around.");
        telemetry.update();
    }

    @Override
    public void loop() {

        // ---------- Change Mode ----------
        if (input.back.down()) {
            if (SampleMode) {
                SampleMode = false; // Switches controls to
                SpecimenMode = true; // specimen mode.
            } else {
                SpecimenMode = false; // Switches controls to
                SampleMode = true; // sample mode.
            }
        }

        // ---------- Initiate Scoring ----------
        if (input.y.down()) {
            if (!CurrentlyScoring) {
                CurrentlyScoring = true;
            } else {
                ResetArm();
                if (SampleMode) {
                    CurrentScoringState = ScoringState.ArmUpToBasket;
                } else {
                    CurrentScoringState = ScoringState.ArmUpToRung;
                }
                CurrentlyScoring = false;
            }
        }

        if (CurrentlyScoring) {

            if (SampleMode) {

                switch (CurrentScoringState) {

                    case ArmUpToBasket:
                        tiltMotor.setTargetPosition(TiltHighBucketBackwards);
                        CurrentScoringState = ScoringState.ArmExtendToBasket;
                        break;

                    case ArmExtendToBasket:
                        if (Math.abs(tiltMotor.getCurrentPosition() - TiltHighBucketBackwards) >= TiltUpThreshold) {
                            slideMotor.setTargetPosition(SlideHighBucketBackwards);
                            CurrentScoringState = ScoringState.ClawTurnToBasket;                                   }
                        break;

                    case ClawTurnToBasket:
                        if (Math.abs(slideMotor.getCurrentPosition() - SlideHighBucketBackwards) >= SlideTickThreshold) {
                            intakeElbowServo.setPosition(ElbowRight); // I think these are the right positions,
                            intakeWristServo.setPosition(WristSampleBucketScore); // might need to change them.
                            SampleDropTimer.reset();
                            CurrentScoringState = ScoringState.ClawDropSample;                                          }
                        break;

                    case ClawDropSample:
                        if (SampleDropTimer.seconds() > 0.5) {
                            intake_claw_servo.setPosition(Claws_open);
                            SampleDropped = true;
                                                             }
                        if (SampleDropped) {
                            intake_claw_servo.setPosition(Claws_closed);
                            SampleDropped = false; // Reset for next score.
                            CurrentScoringState = ScoringState.ClawTurnFromBasket;
                                           }
                        break;

                    case ClawTurnFromBasket:
                        intakeElbowServo.setPosition(ElbowCenter);
                        intakeWristServo.setPosition(WristCenter);
                        CurrentScoringState = ScoringState.ArmRetractFromBasket;
                        break;

                    case ArmRetractFromBasket:
                        if (SampleDropTimer.seconds() > 1.0) {
                            slideMotor.setTargetPosition(SlideMinPosition);
                            CurrentScoringState = ScoringState.ArmLowerToHome;
                                                             }
                        break;

                    case ArmLowerToHome:
                        if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) >= SlideTickThreshold){
                            tiltMotor.setTargetPosition(TiltHomePosition);
                            CurrentScoringState = ScoringState.ArmUpToBasket;
                            CurrentlyScoring = false;                                                          }
                        break;
                        
                }
            }
        }





/* ========================= Telemetry ========================= */

        // ---------- Current Mode ----------
        if (SampleMode) {
            telemetry.addData("Mode:", "🧱 Sample Control");
        } else {
            telemetry.addData("Mode:", "📎 Specimen Control");
        }

        // ---------- Current Scoring Step ----------
        telemetry.addData("Current Step:", CurrentScoringState);

        // ---------- Updating ----------
        telemetry.update();

    }
}
