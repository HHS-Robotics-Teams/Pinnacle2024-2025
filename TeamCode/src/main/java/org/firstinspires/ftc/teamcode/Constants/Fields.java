package org.firstinspires.ftc.teamcode.Constants;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.batteryVoltageSensor;

import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Fields {

    // motor powers

    public static double TiltPower = 1;
    public static double SlidePower = 1;


    // tilt motor positions
    public static int ArmTicks = 100;

    public static int TiltStartPosition = 0;

    public static int TiltMinPosition = 75;
    public static int IntakeRotateThreshold = 250; //100
    public static int TiltPickupPosition = 650;
    public static int TiltLowChamber = 500;
    public static int TiltHomePosition = 650;
    public static int TiltUpThreshold = 1000;

    public static int TiltHighChamber = 1341;
    public static int TiltLowBucket = 1388;
    public static int TiltHighBucket = 1535;
    public static int TiltMaxPosition = 1700;
    public static int TiltSlowSlowPosition = 2000;
    public static int TiltHighBucketBackwards = 2300;


    // slide motor positions
    public static int SlideTicks = 80;
    public static int SlideStartPosition = 0;

    public static int SlideMinPosition = 5;
    public static int SlideLowBucket = 50;
    public static int SlideLowChamber = 250;

    public static int SlideHighChamber = 450;
    public static int SlideHighBucket = 1455;
    public static int SlideMaxPosition = 1455;
    public static int SlideHighBucketBacwards = 1455;





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
    public static Boolean AutoNotRan = true;

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

    // Timers

    // State Machine Runners
    public static int highBasketScore;
    public static int currentRetractionStep = 1;


    public static void applyPowers() {

        tiltMotor.setPower(1);
        slideMotor.setPower(1);
    }


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



