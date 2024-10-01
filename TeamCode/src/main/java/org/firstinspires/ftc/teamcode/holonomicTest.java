package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@TeleOp(name = "Holonomic Drive Test OpMode", group = "idk")
public class holonomicTest extends OpMode {

    // our four motors
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;

    @Override
    public void init() {
        // maps hardware config and notifies us that our robot is initialized
        frontLeftMotor = hardwareMap.get(DcMotor.class, "front_left_motor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "front_right_motor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "back_left_motor");
        backRightMotor = hardwareMap.get(DcMotor.class, "back_right_motor");

        telemetry.addData("Status:", "✅ Robot is initialized.");
    }

    @Override
    public void loop() {

        // maps power values to stick positions
        double drive = -gamepad1.right_stick_x; // left stick: up and down
        double strafe = gamepad1.right_stick_y; // left stick: left and right
        double rotate = gamepad1.left_stick_x; // right stick: left and right
        /* As of 4:09PM 10/1/24: Forward/Backward drive is on left stick, move stick left to right (needs to be forward backward)
           Strafe and Rotate are on right stick (strafe must be on right), rotate is left to right as needed, strafe is forward
           backward (should be left to right on left stick).
         */



        /* i don't know how any of this part works, i consulted chat gpt 💀
         * i will figure out the specifics once we can get the robot moving */
        // wheel calculations
        double frontLeftPower = drive + strafe + rotate;
        double frontRightPower = drive - strafe - rotate;
        double backLeftPower = drive - strafe + rotate;
        double backRightPower = drive + strafe - rotate;

        // normalizes power for the wheel motors
        double maxPower = Math.max(1.0, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));

        frontLeftPower /= maxPower;
        frontRightPower /= maxPower;
        backLeftPower /= maxPower;
        backRightPower /= maxPower;

        // set motor power
        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);

        // debugging telemetry printing
        telemetry.addData("Front Left Power: ", frontLeftPower);
        telemetry.addData("Front Right Power: ", frontRightPower);
        telemetry.addData("Back Left Power: ", backLeftPower);
        telemetry.addData("Back Right Power", backRightPower);
        telemetry.update();
    }

    @Override
    public void stop() {
        // stops the motors
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
    }
}