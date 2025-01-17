package org.firstinspires.ftc.teamcode.Constants;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;

public class DetectedColor {

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
     */
    public static int red;
    public static int green;
    public static int blue;

    // Static variable to store the detected color
    public static String color = "Unknown"; // Default value

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
    }

    // Getter for the detected color
    public static String getColor () {
        return color;
    }
}
