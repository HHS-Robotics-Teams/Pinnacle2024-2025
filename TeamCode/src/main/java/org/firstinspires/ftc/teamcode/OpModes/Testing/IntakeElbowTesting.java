package org.firstinspires.ftc.teamcode.OpModes.Testing;



import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_closed;
import static org.firstinspires.ftc.teamcode.Constants.Fields.Claws_open;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.ElbowRight;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristCenter;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristLeft;
import static org.firstinspires.ftc.teamcode.Constants.Fields.WristRight;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeElbowServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intakeWristServo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intake_claw_servo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.excutil.Input;

@TeleOp(name = "Intake Elbow Testing", group = "testing")
public class IntakeElbowTesting extends OpMode {

    public Input input;

    @Override
    public void init() {
        // ---------- Input Class ----------
        input = new Input();


        RobotHardware.init(hardwareMap);
        tiltMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //applyPowers();

        // ---------- Confirmation Printing ----------
        telemetry.addData("Status:", "✅ Robot is initialized.");
        telemetry.update();
    }

    @Override
    public void loop() {

        input.pollGamepad(gamepad1);

        if (input.left_trigger.down() && !input.right_trigger.down()) {
            intakeElbowServo.setPosition(ElbowRight); // Go to specimen pickup
        }
        if (input.right_trigger.down() && !input.left_trigger.down()) {
            intakeElbowServo.setPosition(ElbowLeft);
        }
        if (input.left_trigger.down() && input.right_trigger.down()) {
            intakeElbowServo.setPosition(ElbowCenter); // Go to center
        }


        if (input.left_bumper.down() && !input.right_bumper.down()) {
            intakeWristServo.setPosition(WristLeft);
        }
        if (input.right_bumper.down() && !input.left_bumper.down()) {
            intakeWristServo.setPosition(WristRight);
        }
        if (input.left_bumper.down() && input.right_bumper.down()) {
            intakeWristServo.setPosition(WristCenter);
        }
        if (input.y.down()) {
            intake_claw_servo.setPosition(Claws_open);
        }
        if (input.a.down()) {
            intake_claw_servo.setPosition(Claws_closed);
        }
        telemetry.addData("Claw position  ", intake_claw_servo.getPosition());
        telemetry.addData("Wrist position", intakeWristServo.getPosition());
        telemetry.addData("Elbow", intakeElbowServo.getPosition());
        telemetry.addData("tilt motor", tiltMotor.getMode());
        telemetry.addData("tilt ticks", tiltMotor.getCurrentPosition());
        telemetry.addData("slide motor", slideMotor.getMode());
        telemetry.addData("slide ticks ", slideMotor.getCurrentPosition());
        telemetry.update();
    }
}
