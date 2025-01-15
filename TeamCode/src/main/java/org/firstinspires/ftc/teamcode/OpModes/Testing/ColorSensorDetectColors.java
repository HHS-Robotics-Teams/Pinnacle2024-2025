package org.firstinspires.ftc.teamcode.OpModes.Testing;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants.Fields;
import org.firstinspires.ftc.teamcode.Constants.RobotHardware;

@TeleOp(name = "Color Sensor for Yellow, Red, Blue", group = "Sensor")
public class ColorSensorDetectColors extends OpMode {

    public static String CurrentColor;
    public static RevColorSensorV3 colorSensor;

    @Override
    public void init() {
        // Initialize the color sensor
        RobotHardware.init(hardwareMap);
        telemetry.speak("Ünknown");
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        // Read RGB values
        int red = colorSensor.red();
        int green = colorSensor.green();
        int blue = colorSensor.blue();

        // Determine the detected color
        String detectedColor = GetColor(red, green, blue);

        String CurrentColor = detectedColor;

        // Display values on telemetry
        telemetry.addData("Light Detected", (OpticalDistanceSensor) colorSensor);
        telemetry.addData("Red", red);
        telemetry.addData("Green", green);
        telemetry.addData("Blue", blue);
        telemetry.addData("Detected Color", detectedColor);
        telemetry.update();
    }

    // Method to determine the color based on RGB thresholds
    public static final String GetColor(int red, int green, int blue) {
        if ((red < 60 && red > 35) && (green < 100 && green > 65) && (blue < 90 && blue > 50)) {
            return "Floor";// The floor, duh
        } else if (red > blue) {
            if (green > (red + 21)) {
                return "Yellow";
            } else {
                return "Red";
            }
        } else if (blue > red) {
            return "Blue"; // High blue, low red and green
        } else {
            return "Ünknown";
        }
    }
}


