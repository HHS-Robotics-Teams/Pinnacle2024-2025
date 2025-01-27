package org.firstinspires.ftc.teamcode.Constants;

import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.distanceSensor;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class DetectedColorAndDistance {

    /*
    This code uses a Rev V3 color sensor to see the difference between the floor tiles red, yellow, and blue,
    make sure to implement these 2 lines in the loop to properly call the class
        DetectedColor.updateColor(colorSensor);
        String detectedColor = DetectedColor.getColor();

    to use the color sensor to follow a action in loop change "Yellow"
    to one of the other options for a a action off that sensed color
        detectedColor.equals("Yellow")


    to view telemetry use this line
        telemetry.addData("Detected Color", DetectedColor.getColor());

    to view the Color int values
        telemetry.addData("Red", red);
        telemetry.addData("Green", green);
        telemetry.addData("Blue", blue);

    to view if the light is detected
        telemetry.addData("Light Detected", (OpticalDistanceSensor) colorSensor);


    To have the REV v3 color sensor detected distance use the following lines
    to properly declare and use the class
     double detectedDistance = DetectedColorAndDistance.getDistance();

    to check the distance to an object
        detectedDistance <= 0.0

    to view the distance on the driver station
        telemetry.addData("Distance (cm)", "%.2f", detectedDistance);

    its distance units can me inch, mm, cm, & m. the max distance for the distance range is 6inch

     */
    public static int red;
    public static int green;
    public static int blue;

    // Static variable to store the detected color
    public static String color = "Unknown"; // Default value

    public static double distance = 0.0;

    // Method to update the detected color using the color sensor
    public static void updateColor(ColorSensor colorSensor) {
        // Read RGB values from the sensor

        red = colorSensor.red();
        green = colorSensor.green();
        blue = colorSensor.blue();

        // Determine the color based on thresholds
        if ((red < 60 && red > 35) && (green < 100 && green > 65) && (blue < 90 && blue > 50)) {
            color = "Floor";
        } else if (red > blue) {
            if (green > (red + 21)) {
                color = "Yellow";
            } else {
                color = "Red";
            }
        } else if (blue > (red + 10)) {
            color = "Blue";
        } else {
            color = "Unknown";
        }
        distance = distanceSensor.getDistance(DistanceUnit.CM);
    }

    // Getter for the detected color
    public static String getColor () {
        return color;
    }
    public static double getDistance(){
        return distance;
    }
}
