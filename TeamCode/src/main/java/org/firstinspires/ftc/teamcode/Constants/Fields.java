package org.firstinspires.ftc.teamcode.Constants;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.batteryVoltageSensor;

import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.leftClaw;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.rightClaw;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;


public class Fields {

    // motor powers

    public static double TiltPower = 1;
    public static double SlidePower = 1;


    // tilt motor positions
    public static int TiltTickThreshold = 20;

    public static int TiltMinPosition = 34;
    public static int IntakeRotateThreshold = 223; //100
    public static int TiltPickupPosition = 433;
    public static int TiltLowChamber = 446;
    public static int TiltFloorPickup = 519;
    public static int TiltHomePosition = 535;
    public static int TiltWallPickupPosition = 545;
    public static int TiltUpThreshold = 1210;

    public static int TiltHighChamber = 970;
    public static int TiltLowBucket = 1235;
    public static int TiltHighBucket = 1366;
    public static int TiltMaxPosition = 1513;
    public static int TiltSlowSlowPosition = 1780;
    public static int TiltHighBucketBackwards = 1875;
    // Tilt motor positions auto
    public static int TiltHighBucketBackwardsAuto = 1850;
    public static int TiltHighBucketLastSpeciemnAuto = 1885;


    // slide motor positions

    public static int SlideTicks = 18;
    public static int SlideTickThreshold = 20 ;
    public static int SlideStartPosition = 0;

    public static int SlideMinPosition = (int) (5 * .37760416666);
    public static int SlideLowBucket = (int) (50 * .37760416666);
    public static int SlideLowChamber = (int) (250 * .37760416666);

    public static int SlideHighChamber = (int) (450 * .37760416666);
    public static int SlideWallPickup = (int) (1000 * .37760416666);
    public static int SlideHighBucketBackwardsAuto = 510;
    public static int SlideHighBucket = (int) (1455 * .37760416666);
    public static int SlidePickupPosition = 300; //Robot actual max is 550, this is the 42in box backwards scoring
    public static int SlideMaxPosition = 550;
    public static int SlideHighBucketBackwards = 510;
    public static int SlideGrabStart = 190;
    public static int SlideGrabTarget = SlideGrabStart;
    public static int SlideGrabSecond  = 250;
    public static int SlideGrabSecondTarget = SlideGrabSecond;
    public static  int SlidePickupAdjust;

    // Wrist Positions

    public static double WristRight = 0.0;
    public static double WristHorizontalPickup = .25;

    public static double WristCenter = .5;
    public static double WristSpecimenWallPickup = 0.54;
    public static double WristSampleBucketScore = .65;

    public static double WristLeft = 1.0;


    // Elbow positions
    public static double ElbowRight = 0.7; //ElbowSpecimenScoring

    public static double ElbowSpecimenScoring = .12;
    public static double ElbowCenter = 0.4; //ElbowStarting
    public static double ElbowLeft = .12;

    // Claw Positions
    public static double Claws_open = .25;
    public static double Claws_closed = 0;

    // Flags
    public static Boolean isSlideIncrementing = true;
    public static Boolean isTiltIncrementing = true;
    public static Boolean slideIsOut = false;
    public static Boolean IntakeWristPositionReached = false;
    public static Boolean climbPositionReached = false;
    public static Boolean armRetractingHighBasket = false;
    public static Boolean armRetractingHighChamber = false;
    public static Boolean armRetractingFloorPickup = false;
    public static Boolean armRetractingSubPickup = false;
    public static Boolean armRetractingHome = false;
    public static Boolean armRetractingWallPickup = false;
    public static Boolean sampleFloorPickUp = false;
    public static Boolean specimenMode = false;
    public static Boolean buttonPressInitiate = false;
    public static Boolean clawsOpening = false;
    public static Boolean clawsClosing = false;
    public static Boolean ActivelyClimbing = false;
    public static Boolean PreppingClimbers = false;
    public static Boolean elbowRotate = false;
    public static void resetFlags(){
        IntakeWristPositionReached = false;
        climbPositionReached = false;
        armRetractingHighBasket = false;
        armRetractingHighChamber = false;
        armRetractingFloorPickup = false;
        armRetractingSubPickup = false;
        armRetractingHome = false;
        armRetractingWallPickup = false;
        sampleFloorPickUp = false;
        specimenMode = false;
        buttonPressInitiate = false;
        ActivelyClimbing = false;
        PreppingClimbers = false;

    }

    // Timers

    // State Machine Runners
    public static int currentClawStep = 1;
    public static int currentRetractionStep = 1;
    public static int currentClimbStep = 1;
    public static int currentWallStep = 1;


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
    public static Boolean CurrentlyQuickGrabbing = false;
    public static boolean KhangCheeredOn = false;
    public static int TiltTickIncrement = 20;
    public static int SlideTickIncrement = 100;
    public static int ElementsScored = 0;
    public static int manualTiltChange = 15;
    public static int manualSlideChange = 80;

    // --------- Robot Memory of Movement ---------
    public static int manualTiltAdjustmentWallAmount = 0;
    public static int manualTiltAdjustmentChamberAmount = 0;
    public static int manualTiltAdjustmentBasketAmount = 0;

    public static int manualSlideAdjustmentWallAmount = 0;
    public static int manualSlideAdjustmentChamberAmount = 0;
    public static int manualSlideAdjustmentBasketAmount = 0;

    // I got tired of writing setPower() multiple times
    // so I made a function to cut down on line count.
    public static void SetClawPowers(double PowerDouble) {
        leftClaw.setPower(PowerDouble);
        rightClaw.setPower(PowerDouble);
    }

    // Instead of writing these lines multiple times in the
    // last steps of state machines, just call this function
    // as it doesn't have to be accurate, just has to be done.
    public static void ResetArm() {
        tiltMotor.setTargetPosition(TiltHomePosition);
        slideMotor.setTargetPosition(SlideMinPosition);
        intakeWristServo.setPosition(WristCenter);
        intakeElbowServo.setPosition(ElbowSpecimenScoring);
    }

    public static double OldDetectedDistance = 0.0;
    public static double NewDetectedDistance = 0.0;

}



