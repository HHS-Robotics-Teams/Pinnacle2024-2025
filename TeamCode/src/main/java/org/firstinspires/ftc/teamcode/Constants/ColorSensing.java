package org.firstinspires.ftc.teamcode.Constants;

import com.qualcomm.hardware.rev.RevColorSensorV3;

public class ColorSensing {
    public static Boolean Floor;
    public static Boolean Yellow;
    public static Boolean Red;
    public static Boolean Blue;
    public static Boolean Unknown;
    public static RevColorSensorV3 colorSensor;

        // Read RGB values
        int red = colorSensor.red();
        int green = colorSensor.green();
        int blue = colorSensor.blue();


        // Determine the detected color
        Boolean detectedColor = GetColor(red, green, blue);


        // Method to determine the color based on RGB thresholds
    public static final Boolean GetColor(int red, int green, int blue) {
        if ((red < 60 && red > 35) && (green < 100 && green > 65) && (blue < 90 && blue > 50)) {
            return Floor;// The floor, duh
        } else if (red > blue) {
            if (green > (red + 21)) {
                return Yellow; // High green, high red, low blue
            } else {
                return Red; // High red, low blue and green
            }
        } else if (blue > red) {
            return Blue; // High blue, low red and green
        } else {
            return Unknown;
        }
    }
}

