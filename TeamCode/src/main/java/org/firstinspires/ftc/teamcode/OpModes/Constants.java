package org.firstinspires.ftc.teamcode.OpModes;



public class Constants {
    // motor powers
    public static double IntakePower = 1;
    public static double TiltPower = 1;
    public static double SlidePower = 1;
    public static double IntakeCurrentPower;

    // tilt motor positions
    public static int ArmTicks = 100;
    public static int IntakeRotateThreshold = 200;
    public static int TiltHomePosition = 75;
    public static int TiltStartPosition = 0;
    public static int TiltMinPosition = 75;
    public static int TiltMaxPosition = 2600;
    public static int TiltUpThreshold = 800;
    public static int TiltHighChamber = 1000;
    public static int TiltLowChamber = 500;
    public static int TiltHighBucket = 1500;
    public static int TiltLowBucket = 750;

    // slide motor positions
    public static int SlideTicks = 80;
    public static int SlideStartPosition = 0;
    public static int SlideMinPosition = 5;
    public static int SlideMaxPosition = 1455;
    public static int SlideHighChamber = 450;
    public static int SlideLowChamber = 250;
    public static int SlideHighBucket = 1455;
    public static int SlideLowBucket = 750;

    // Wrist Positions
    public static int WristLeft = 0;
    public static double WristCenter = .5;
    public static int WristRight = 1;




    // Flags
    public static Boolean IntakeWristPositionReached = false;

    public static Boolean climbPositionReached = false;
    public static Boolean InitPositionReached = false;
}
