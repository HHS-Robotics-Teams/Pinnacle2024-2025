package org.firstinspires.ftc.teamcode.Constants;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.batteryVoltageSensor;

import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Fields {

    // motor powers

    public static double TiltPower = 1;
    public static double SlidePower = 1;


    // tilt motor positions
    public  int ArmTicks = 100;

    public static int TiltStartPosition = 0;

    public static int TiltMinPosition = 75;
    public static int IntakeRotateThreshold = 250; //100
    public static int TiltPickupPosition = 485;
    public static int TiltLowChamber = 500;
    public static int TiltHomePosition = 650;
    public static int TiltWallPickupPosition = 660;
    public static int TiltUpThreshold = 1360;

    public static int TiltHighChamber = 1100;
    public static int TiltLowBucket = 1388;
    public static int TiltHighBucket = 1535;
    public static int TiltMaxPosition = 1700;
    public static int TiltSlowSlowPosition = 2000;
    public static int TiltHighBucketBackwards = 2225;


    // slide motor positions
    public static int SlideTicks = 80;
    public static int SlideTickThreshold = 15;
    public static int SlideStartPosition = 0;

    public static int SlideMinPosition = 5;
    public static int SlideLowBucket = 50;
    public static int SlideLowChamber = 250;

    public static int SlideHighChamber = 450;
    public static int SlideHighBucket = 1455;
    public static int SlideMaxPosition = 1455;
    public static int SlideHighBucketBackwards = 1455;





    // Wrist Positions
    public static double WristRight = 0.0;
    public static double WristHorizontalPickup = .25;

    public static double WristCenter = .5;
    public static double WristSpecimenWallPickup = 0.54;
    public static double WristSampleBucketScore = .65;

    public static double WristLeft = 1.0;




    // Elbow positions
    public static double ElbowRight = 0.12; //ElbowSpecimenScoring

    public static double ElbowSpecimenScoring = .12;
    public static double ElbowCenter = 0.4; //ElbowStarting
    public static double ElbowLeft = .7;

    // Claw Positions
    public static double Claws_open = .25;
    public static double Claws_closed = 0;

    // Flags
    public static Boolean IntakeWristPositionReached = false;
    public static Boolean climbPositionReached = false;
    public static Boolean armRetractingHighBasket = false;
    public static Boolean armRetractingHighChamber = false;
    public static Boolean armRetractingFloorPickup = false;
    public static Boolean armRetractingSubPickup = false;
    public static Boolean armRetractingHome = false;
    public static Boolean armRetractingWallPickup = false;

    public static Boolean sampleMode = false;
    public static Boolean specimenMode = false;
    public static Boolean buttonPressInitiate = false;
    public static Boolean clawsOpen = false;
    public static Boolean clawsClosed = false;
    public static boolean ActivelyClimbing = false;
    public static boolean PreppingClimbers = false;

    // Timers

    // State Machine Runners
    public static int currentRetractionStep = 1;
    public static int currentClimbStep = 1;


    public static void applyPowers() {
        tiltMotor.setPower(1);
        slideMotor.setPower(1);
    }


    // ----- For use in BadStateMachineTeleOp.java ------
    public static String CurrentScoringMode = "🧱 Sample Scoring";
    public static boolean SampleMode = true; // Code will default to Sample Scoring.
    public static boolean SpecimenMode = false;
    public static boolean CurrentlyScoring = false; // I know some of these already exist with
    public static boolean SampleDropped = false;    // different names, I just don't care :3
    public static boolean CurrentlyQuickGrabbing = false;
    public static boolean KhangCheeredOn = false;
    public static int TiltTickIncrement = 20;
    public static int SlideTickIncrement = 100;
    public static int ElementsScored = 0;

    // I got tired of writing setPower() multiple times 
    // so I made a function to cut down on line count.
    public static void SetClawPowers(double PowerDouble) {
        leftClaw.setPower(PowerDouble);
        rightClaw.setPower(PowerDouble);                 }

    // Instead of writing these lines multiple times in the
    // last steps of state machines, just call this function
    // as it doesn't have to be accurate, just has to be done.
    public static void ResetArm()                           {
        tiltMotor.setTargetPosition(TiltHomePosition);
        slideMotor.setTargetPosition(SlideMinPosition);
        intakeWristServo.setPosition(WristCenter);
        intakeElbowServo.setPosition(ElbowSpecimenScoring); }
    // --------------------------------------------------



    // Battery Voltage Multiplier
    /* BVM stands for battery voltage multiplier. It will be used to extend all distances and
     * rotations for the robot. BVM = ideal battery voltage divided by current battery voltage.
     * BVM = idealBatteryV/actualVoltage
     */
    public static final double IDEALBATTERYV = 14.0;
    public static double BVM;

    public static void applyBVM() {
        double actualVoltage = batteryVoltageSensor.getVoltage();
        BVM = IDEALBATTERYV / actualVoltage;
    }
}



