package org.firstinspires.ftc.teamcode.OpModes.Testing;

import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.IntakeWristPositionReached;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideTicks;
import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideWallPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltFloorPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingFloorPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.armRetractingSubPickup;
import static org.firstinspires.ftc.teamcode.Constants.Fields.currentRetractionStep;
import static org.firstinspires.ftc.teamcode.Constants.Fields.sampleFloorPickUp;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intake_claw_servo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants.Fields;
import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.OpModes.Auto.SensingAuto.SensingAutoBasketSideSpecimen;
import org.firstinspires.ftc.teamcode.excutil.Input;
@Deprecated
@Disabled
@TeleOp(name = "Color Sensor for Yellow, Red, Blue", group = "Testing")
public class ColorSensorDetectColors extends OpMode {
    public Input input;
    public static int red ;
    public static int green;
    public static int blue ;

    public static String CurrentColor;
    public static RevColorSensorV3 colorSensor;
    ElapsedTime LowerTileTimer = new ElapsedTime();
    ElapsedTime grabTimer = new ElapsedTime();

    @Override
    public void init() {
        colorSensor = hardwareMap.get(RevColorSensorV3.class, "color_sensor");

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {

        // Read RGB values
        red = colorSensor.red();
        green = colorSensor.green();
        blue = colorSensor.blue();

        // Determine the detected color
        String detectedColor = GetColor(red, green, blue);

        // Display values on telemetry
        telemetry.addData("Light Detected", (OpticalDistanceSensor) colorSensor);
        telemetry.addData("Red", red);
        telemetry.addData("Green", green);
        telemetry.addData("Blue", blue);
        telemetry.addData("Detected Color", detectedColor);
        telemetry.update();
    }

    // Method to determine the color based on RGB thresholds
    public static String GetColor(int red, int green, int blue) {
        if ((red < 60 && red > 35) && (green < 100 && green > 65) && (blue < 90 && blue > 50)) {
            return "Floor";// The floor, duh
        } else if (red > blue) {
            if (green > (red + 21)) {
                return "Yellow";
            } else {
                return "Red";
            }
        } else if (blue > (red + 10)) {
            return "Blue"; // High blue, low red and green
        } else {
            return "Ünknown";
        }
    }
}


