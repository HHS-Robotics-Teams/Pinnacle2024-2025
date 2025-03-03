package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.IntakeWristPositionReached;
import static org.firstinspires.ftc.teamcode.Constants.Fields.climbPositionReached;
import static org.firstinspires.ftc.teamcode.Constants.Fields.currentRetractionStep;
import static org.firstinspires.ftc.teamcode.Constants.Fields.elbowRotate;
import static org.firstinspires.ftc.teamcode.Constants.Fields.sampleFloorPickUp;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants.Fields;
import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.excutil.Input;
@TeleOp(name = "ReSetRobot", group = "Inspection")
public class TelopReset extends OpMode {

        public Input input;

        @Override
        public void init() {
            // ---------- Input Class ----------
            input = new Input();

            // ---------- Initialize Hardware ----------
            RobotHardware.init(hardwareMap);
            RobotHardware.resetEncoders();

            // ---------- Add Power ---------
            Fields.applyPowers();

            // ---------- Confirmation Printing ----------
            telemetry.addData("Status:", "✅ Robot is initialized.");
            telemetry.update();


        }

        @Override
        public void loop() {

            input.pollGamepad(gamepad1); // Pass gamepad input through custom class

            // ---------- Manual Arm Tilt ----------
            if (input.dpad_up.held()) {
                tiltMotor.setTargetPosition(tiltMotor.getTargetPosition() + 30); // Arm up
            }

            if (input.dpad_down.held()) {
                tiltMotor.setTargetPosition(tiltMotor.getTargetPosition() - 30); // Arm down
            }
            if (input.left_bumper.held()) {
                slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + 80); // Slide out
            }

            if (input.left_trigger.held()) {
                slideMotor.setTargetPosition(slideMotor.getCurrentPosition() - 80); // Slide in
            }

            if (input.dpad_left.down() ) {
                intakeWristServo.setPosition(.75);
                intakeElbowServo.setPosition(ElbowRight);
            }


            telemetry.addData("Current Tilt Position: ", tiltMotor.getCurrentPosition());
            telemetry.addData("Current Slide Position ", slideMotor.getCurrentPosition());

            // ---------- Flags -----------
            telemetry.addData("Climb control status", climbPositionReached ? "True" : "False");
            telemetry.addData("Wrist control status", IntakeWristPositionReached ? "True" : "False");
            telemetry.addData("floor pickup", sampleFloorPickUp ? "True" : "False");
            telemetry.addData("elbow rotate", elbowRotate ? "True": "False");
            telemetry.addData("Current State", currentRetractionStep);

            // ---------- Update ----------
            telemetry.update();
        }
    }

