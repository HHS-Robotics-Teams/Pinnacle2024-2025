package org.firstinspires.ftc.teamcode.OpModes.Testing;

import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.excutil.Input;

@TeleOp(name = "Intake Elbow Testing", group = "idk")
public class IntakeElbowTesting extends OpMode {

    public Input input;

    @Override
    public void init() {
        // ---------- Input Class ----------
        input = new Input();

        // ---------- Map Hardware ----------
        RobotHardware.init(hardwareMap);

        // ---------- Confirmation Printing ----------
        telemetry.addData("Status:", "✅ Robot is initialized.");
        telemetry.update();
    }

    @Override
    public void loop() {

        input.pollGamepad(gamepad1);

        if (input.left_trigger.down() && !input.right_trigger.down()) {
            intakeElbowServo.setPosition(0.12); // Go to specimen pickup
        }
        if (input.right_trigger.down() && !input.left_trigger.down()) {
            intakeElbowServo.setPosition(1.0);
        }
        if (input.left_trigger.down() && input.right_trigger.down()) {
            intakeElbowServo.setPosition(0.4); // Go to center
        }


        if (input.left_bumper.down() && !input.right_bumper.down()) {
            intakeWristServo.setPosition(0.0);
        }
        if (input.right_bumper.down() && !input.left_bumper.down()) {
            intakeWristServo.setPosition(1.0);
        }
        if (input.left_bumper.down() && input.right_bumper.down()) {
            intakeWristServo.setPosition(0.5);
        }

    }
}
