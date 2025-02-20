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

    public static final int pivotTicksAtPerpendicular = 635;

    private static int pivotAmount;
    private static int slideAmount;

    private static double weightedDistanceValue;
    private static final double bothFailedDistance = 1e-12;

    //constants
    static double barWallOffset = 1;
    static double lengthOfArm = 21.5;
    static double armBackOnRobotDistance = 14.5;
    static double armHeightOffGround = 13.125;
    static double heightToHighChamber = 33 - armHeightOffGround;
    static double heightToWallPickup =  9 - armHeightOffGround;
    static double wallPickupOffset = -4;
    // angle above/below ground of the arm
    static double theta;

    public static int getPickupPivotAmount() {
        weightedDistanceValue = calculateDistance();

        if(weightedDistanceValue == bothFailedDistance) {
            pivotAmount = TiltWallPickupPosition;
            return pivotAmount;
        }

        if(weightedDistanceValue <= 15) {
            weightedDistanceValue -= 2;
        }
        weightedDistanceValue -= wallPickupOffset;

        theta = Math.toDegrees(Math.atan(heightToWallPickup / weightedDistanceValue));

        pivotAmount = (int) (theta * pivotTicksPerDegree);
        pivotAmount = pivotTicksAtPerpendicular + pivotAmount;

        if(weightedDistanceValue == bothFailedDistance) {
            pivotAmount = TiltWallPickupPosition;
        }
        return pivotAmount;
    }

    public static int getPickupSlideAmount() {
        weightedDistanceValue = calculateDistance();

        if(weightedDistanceValue == bothFailedDistance) {
            slideAmount = SlideWallPickup;
            return slideAmount;
        }

        weightedDistanceValue -= wallPickupOffset;

        // casted to int, pythag theorem to find the length of the slides needed then multiplied by slideTicksPerInch
        slideAmount = (int) (((Math.sqrt(Math.pow(heightToWallPickup,2) + Math.pow(weightedDistanceValue,2))) - lengthOfArm)
                            * slideTicksPerInch);

        return slideAmount;
    }

    public static int getChamberPivotAmount() {
        weightedDistanceValue =  calculateDistance();

        if(weightedDistanceValue == bothFailedDistance) {
            pivotAmount = TiltHighChamber;
            return pivotAmount;
        }

        weightedDistanceValue -=  barWallOffset;

        theta = Math.toDegrees(Math.atan(heightToHighChamber / weightedDistanceValue));

        pivotAmount = (int) (theta * pivotTicksPerDegree);
        pivotAmount = pivotTicksAtPerpendicular + pivotAmount;

        return pivotAmount;
    }

    public static int getChamberSlideAmount() {
        weightedDistanceValue =  calculateDistance();

        if(weightedDistanceValue == bothFailedDistance) {
            slideAmount = SlideHighChamber;
            return slideAmount;
        }

        weightedDistanceValue -= barWallOffset;

        //  pythag theorem to find the length of the slides needed then multiplied by slideTicksPerInch
        double distanceDouble = Math.sqrt(Math.pow(heightToHighChamber, 2) + Math.pow(weightedDistanceValue, 2));
        distanceDouble -= lengthOfArm;

        return (int) (distanceDouble * slideTicksPerInch);
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
        if(dLeft <= threshold2 && dRight <= threshold2) returnDistance = threshold2;
        else if(dLeft <= threshold2) dLeft = dRight;
        else if(dRight <= threshold2) dRight = dLeft;

        //Exception Handling for overly far values
        if((dLeft >= threshold) && (dRight >= threshold)) returnDistance = bothFailedDistance;
        //Average of two
        else if ((dLeft < threshold) && (dRight < threshold)) returnDistance = ((dLeft + dRight) / 2);
        //More Exception Handling
        else if (dLeft < threshold && dRight > threshold) returnDistance = dLeft;
        else if (dLeft > threshold) returnDistance = dRight;
        else returnDistance = bothFailedDistance;


        //Adjusts from distance sensor location to arm location
        if(returnDistance == bothFailedDistance) {
            return returnDistance;
        } else {
            return returnDistance + armBackOnRobotDistance;
        }
    }
}
