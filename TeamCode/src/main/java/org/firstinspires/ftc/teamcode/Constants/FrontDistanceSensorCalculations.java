package org.firstinspires.ftc.teamcode.Constants;

import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideWallPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltWallPickupPosition;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.leftDistanceSensor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.rightDistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class FrontDistanceSensorCalculations {

    public static final double slideTicksPerInch = (145.1) / (Math.PI * 1.404);
    // (ticks per revolution / degrees per revolution) * gear ratio
    public static final double pivotTicksPerDegree = (3895.9 / 360) * (1/1.25);

    public static final int pivotTicksAtPerpendicular = 597;

    private static int pivotAmount;
    private static int slideAmount;

    private static double weightedDistanceValue;
    private static final double bothFailedDistance = 1e-12;

    //constants
    static double barWallOffset = 1;
    static double lengthOfArm = 22;
    static double armBackOnRobotDistance = 14.5;
    static double armHeightOffGround = 13.125;
    static double heightToHighChamber = 21 - armHeightOffGround;
    static double heightToWallPickup = 12 - armHeightOffGround;
    static double wallPickupOffset = 2;
    // angle above/below ground of the arm
    static double theta;

    public static int getPickupPivotAmount() {
        weightedDistanceValue = calculateDistance() + wallPickupOffset;
        theta = Math.toDegrees(Math.atan(heightToWallPickup / weightedDistanceValue));

        pivotAmount = (int) (theta * pivotTicksPerDegree);
        pivotAmount = pivotTicksAtPerpendicular + pivotAmount;

        if(weightedDistanceValue == bothFailedDistance) {
            pivotAmount = TiltWallPickupPosition;
        }
        return pivotAmount;
    }

    public static int getPickupSlideAmount() {
        weightedDistanceValue = calculateDistance() + wallPickupOffset;

        // casted to int, pythag theorem to find the length of the slides needed then multiplied by slideTicksPerInch
        slideAmount = (int) (Math.sqrt(Math.pow(heightToWallPickup,2) + Math.pow(weightedDistanceValue,2))
                            * slideTicksPerInch);

        if(weightedDistanceValue == bothFailedDistance) {
            slideAmount = SlideWallPickup;
        }
        return slideAmount;
    }

    public static int getChamberPivotAmount() {
        weightedDistanceValue = calculateDistance() + barWallOffset;
        theta = Math.toDegrees(Math.atan(heightToHighChamber / weightedDistanceValue));

        pivotAmount = (int) (theta * pivotTicksPerDegree);
        pivotAmount = pivotTicksAtPerpendicular + pivotAmount;

        if(weightedDistanceValue == bothFailedDistance) {
            pivotAmount = TiltHighChamber;
        }
        return pivotAmount;
    }

    public static int getChamberSlideAmount() {
        weightedDistanceValue = calculateDistance() + barWallOffset;
        theta = Math.toDegrees(Math.atan(heightToHighChamber / weightedDistanceValue));

        // casted to int, pythag theorem to find the length of the slides needed then multiplied by slideTicksPerInch
        slideAmount = (int) (Math.sqrt(Math.pow(heightToHighChamber,2) + Math.pow(weightedDistanceValue,2))
                            * slideTicksPerInch);

        if(weightedDistanceValue == bothFailedDistance) {
            slideAmount = SlideHighChamber;
        }
        return slideAmount;
    }

    private static double calculateDistance() {
        double returnDistance;
        //max
        double threshold = 22;
        //min
        double threshold2 = 13;

        //Gets distances
        double dLeft = leftDistanceSensor.getDistance(DistanceUnit.INCH);
        double dRight = rightDistanceSensor.getDistance(DistanceUnit.INCH);

        //Exception Handling for overly close values
        if(dLeft <= threshold2 && dRight <= threshold2) {returnDistance = threshold2;}
        else if(dLeft <= threshold2) {dLeft = dRight;}
        else if(dRight <= threshold2) {dRight = dLeft;}

        //Exception Handling for overly far values
        if((dLeft >= threshold) && (dRight >= threshold)) returnDistance = bothFailedDistance;
        //Average of two
        else if ((dLeft < threshold) && (dRight < threshold)) {returnDistance = ((dLeft + dRight) / 2);}
        //More Exception Handling
        else if (dLeft < threshold && dRight > threshold) {returnDistance = dLeft;}
        else if (dLeft > threshold) {returnDistance = dRight;}
        else {returnDistance = bothFailedDistance;}


        //Adjusts from distance sensor location to arm location
        return  returnDistance + armBackOnRobotDistance - lengthOfArm;
    }
}
