package org.firstinspires.ftc.teamcode.Constants;

public class Fields {
    // motor powers
    public static double IntakePower = 1;
    public static double TiltPower = 1;
    public static double SlidePower = 1;
    public static double IntakeCurrentPower;

    // tilt motor positions
    public static int ArmTicks = 100;
    public static int IntakeRotateThreshold = 100;
    public static int TiltHomePosition = 60;
    public static int TiltPickupPosition = 90;
    public static int TiltStartPosition = 0;
    public static int TiltMinPosition = 75;
    public static int TiltMaxPosition = 2600;
    public static int TiltUpThreshold = 1000;
    public static int TiltHighChamber = 1245;
    public static int TiltLowChamber = 500;
    public static int TiltHighBucket = 1535;
    public static int TiltLowBucket = 1388;

    // slide motor positions
    public static int SlideTicks = 80;
    public static int SlideStartPosition = 0;
    public static int SlideMinPosition = 5;
    public static int SlideMaxPosition = 1455;
    public static int SlideHighChamber = 450;
    public static int SlideLowChamber = 250;
    public static int SlideHighBucket = 1455;
    public static int SlideLowBucket = 50;

    // Wrist Positions
    public static int WristLeft = 1;
    public static double WristCenter = .5;
    public static int WristRight = 0;

    // Elbow positions
    public static double ElbowStarting = 0.12;
    public static double ElbowSpecimenScoring = 0.4;

    // Flags
    public static Boolean IntakeWristPositionReached = false;
    public static Boolean climbPositionReached = false;
    public static Boolean armRetractingHighBasket = false;
    public static Boolean armRetractingLowBasket  = false;
    public static Boolean armRetractingHighChamber  = false;
    public static Boolean armRetractingLowChamber  = false;
    public static Boolean armRetractingHome = false;
    public static Boolean sampleMode = false;
    public static Boolean specimenMode = false;

    //Finite state machine runners
    public static int currentRetractionStep = 1;

}