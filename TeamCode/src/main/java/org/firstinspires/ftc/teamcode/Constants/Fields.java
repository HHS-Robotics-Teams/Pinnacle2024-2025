package org.firstinspires.ftc.teamcode.Constants;

import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeCRServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class Fields {
    // motor powers
    public static double IntakePower = 1;
    public static double TiltPower = 1;
    public static double SlidePower = 1;
    public static double IntakeCurrentPower;

    // tilt motor positions
    public static int ArmTicks = 100;
    public static int IntakeRotateThreshold = 100;
    public static int TiltHomePosition = 650;
    public static int TiltPickupPosition = 495;
    public static int TiltStartPosition = 0;
    public static int TiltMinPosition = 75;
    public static int TiltMaxPosition = 1700;
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
    public static double WristLeft = 1.0;
    public static double WristCenter = .5;
    public static double WristRight = 0.0;
    public static double WristSpecimenWallPickup = 0.54;

    // Elbow positions
    public static double ElbowStarting = 0.4;
    public static double ElbowSpecimenScoring = 0.12;

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


    public static void applyPowers() {
        tiltMotor.setPower(1);
        slideMotor.setPower(.8);
    }

}