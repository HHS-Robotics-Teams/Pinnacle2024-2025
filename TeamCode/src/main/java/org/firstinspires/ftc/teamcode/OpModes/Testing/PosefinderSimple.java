package org.firstinspires.ftc.teamcode.OpModes.Testing;

import static org.firstinspires.ftc.teamcode.Constants.DetectedColorAndDistance.blue;
import static org.firstinspires.ftc.teamcode.Constants.DetectedColorAndDistance.green;
import static org.firstinspires.ftc.teamcode.Constants.DetectedColorAndDistance.red;
import static org.firstinspires.ftc.teamcode.Constants.DetectedHueAndDistance.brightness;
import static org.firstinspires.ftc.teamcode.Constants.DetectedHueAndDistance.hue;
import static org.firstinspires.ftc.teamcode.Constants.DetectedHueAndDistance.saturation;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.colorSensor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.teamcode.Constants.DetectedColorAndDistance;
import org.firstinspires.ftc.teamcode.Constants.DetectedHueAndDistance;
import org.firstinspires.ftc.teamcode.Constants.RobotHardware;


@TeleOp (name = "Pose Finder Simple", group = "testing")
public class PosefinderSimple extends OpMode {

    @Override
    public void init() {
        RobotHardware.init(hardwareMap);

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        // RGB Color sensing
        DetectedColorAndDistance.updateColor(colorSensor);

        String detectedColor = DetectedColorAndDistance.getColor();

        double detectedDistance = DetectedColorAndDistance.getDistance();

        // HSV Color Sensing
        DetectedHueAndDistance.updateColor(colorSensor);

        String detectedHue = DetectedHueAndDistance.getColor();



        // Display values on telemetry
        telemetry.addData("Light Detected", (OpticalDistanceSensor) colorSensor);
        telemetry.addData("Hue Color", detectedHue);
        telemetry.addData("Hue", hue);
        telemetry.addData("Saturation", saturation);
        telemetry.addData("Brightness", brightness);
        telemetry.addData("Red", red);
        telemetry.addData("Green", green);
        telemetry.addData("Blue", blue);
        telemetry.addData("Detected Color", detectedColor);
        telemetry.addData("Distance (cm)", "%.2f", detectedDistance);
        telemetry.addData("slide pos", slideMotor.getCurrentPosition());
        telemetry.addData("tilt pos", tiltMotor.getCurrentPosition());
        telemetry.update();
    }
}
