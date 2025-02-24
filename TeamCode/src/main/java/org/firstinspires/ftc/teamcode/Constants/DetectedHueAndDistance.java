package org.firstinspires.ftc.teamcode.Constants;


import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.distanceSensor;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


public class DetectedHueAndDistance {
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
     double detectedDistance = DetectedHueAndDistance.getDistance();

    to check the distance to an object
        detectedDistance <= 0.0

    to view the distance on the driver station
        telemetry.addData("Distance (cm)", "%.2f", detectedDistance);

    its distance units can me inch, mm, cm, & m. the max distance for the distance range is 6inch

     */
    public static int red;
    public static int green;
    public static int blue;

    public static float hue = 0;
    public static float saturation = 0;
    public static float brightness = 0;


    // Static variable to store the detected color
    public static String color = "Unknown"; // Default value

    public static double distance = 0.0;

    // Method to update the detected color using the color sensor
    public static void updateColor( ColorSensor colorSensor) {
        // Read RGB values from the sensor

        red = colorSensor.red();
        green = colorSensor.green();
        blue = colorSensor.blue();
        // Convert RGB to HSV
        float[] hsvValues = new float[3]; // array to store HSV values
        Color.RGBToHSV(red * 255 / 1024, green * 255 / 1024, blue * 255 / 1024, hsvValues);

        hue = hsvValues[0]; // Extract Hue value
        saturation = hsvValues[1]; // Extract Saturation
        brightness = hsvValues[2]; // Extract Brightness

        // Determine the color based on thresholds
        if (hue >= 0 && hue <= 10 || hue >= 350 && hue <= 360) {
            color = "Red";
        } else if (hue >= 45 && hue <= 65) {
            color = "Yellow";
        } else if (hue >= 180 && hue <= 250) {
            color = "Blue";
        } else if (saturation < 0.2 && brightness > 0.3) {
            color = "Floor"; // Likely the gray FTC tile
        } else if (saturation < 0.1 && brightness > 0.8) {
            color = "White Tape"; // Likely a boundary marker
        }else {
            color = "Unknown";
        }
        distance = distanceSensor.getDistance(DistanceUnit.CM);
    }

    // Getter for the detected color
    public static String getColor () {
        return color;
    }
    public float getHue() {
        return hue;
    }
    public float getSaturation(){
        return saturation;
    }
    public static float getBrightness() {
        return brightness;
    }
    public static double getDistance(){
        return distance;
    }
}

