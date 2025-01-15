package org.firstinspires.ftc.teamcode.OpModes.Testing;

import static org.firstinspires.ftc.teamcode.Constants.ColorSensing.Blue;
import static org.firstinspires.ftc.teamcode.Constants.ColorSensing.Floor;
import static org.firstinspires.ftc.teamcode.Constants.ColorSensing.Red;
import static org.firstinspires.ftc.teamcode.Constants.ColorSensing.Yellow;
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

@TeleOp(name = "Color Sensor for Yellow, Red, Blue", group = "Sensor")
public class ColorSensorDetectColors extends OpMode {
    public Input input;

    public static String CurrentColor;
    public static RevColorSensorV3 colorSensor;
    ElapsedTime LowerTileTimer = new ElapsedTime();
    ElapsedTime grabTimer = new ElapsedTime();

    @Override
    public void init() {
        input = new Input();
        // Initialize the color sensor
        RobotHardware.init(hardwareMap);
        telemetry.speak("Ünknown");
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1); // Pass gamepad input through custom class

        if (input.b.down()) {
            armRetractingFloorPickup = true;
            IntakeWristPositionReached = false;
            telemetry.speak("Auto Pickup First Sample");
        }

        if (armRetractingFloorPickup) {
            switch (currentRetractionStep) {
                case (1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < SlideTicks) {
                        currentRetractionStep++;
                    }
                    break;

                case (2):
                    tiltMotor.setTargetPosition(TiltFloorPickup);
                    slideMotor.setTargetPosition(SlideWallPickup);
                    intakeWristServo.setPosition(WristCenter);
                    intakeElbowServo.setPosition(ElbowLeft);
                    intake_claw_servo.setPosition(Claws_open);
                    armRetractingFloorPickup = false;
                    sampleFloorPickUp = true;
                    currentRetractionStep = 1;
                    break;
            }
        }
        if (input.a.down()) {
            armRetractingSubPickup = true;
            IntakeWristPositionReached = false;
            telemetry.speak(" Grab Second Sample");
        }

        if (armRetractingSubPickup) {
            switch (currentRetractionStep) {
                case (1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if (Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < SlideTicks) {
                        currentRetractionStep++;
                    }
                    break;

                case (2):
                    intakeElbowServo.setPosition(ElbowLeft);
                    intakeWristServo.setPosition(WristCenter);
                    intake_claw_servo.setPosition(Claws_open);
                    tiltMotor.setTargetPosition(538);
                    LowerTileTimer.reset();
                    armRetractingFloorPickup = false;
                    sampleFloorPickUp = true;
                    currentRetractionStep ++;
                    break;
                case (3):
                    if (Math.abs(tiltMotor.getCurrentPosition() - 538) <= 5 && LowerTileTimer.seconds() >= 2.5) {
                        slideMotor.setTargetPosition(385);
                        grabTimer.reset();
                        if (Yellow) {
                            telemetry.addData("Yellow", Yellow);
                            grabTimer.reset();
                            tiltMotor.setPower(0);
                            currentRetractionStep = 1;
                            break;

                        }
                    }
            }
        }

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


