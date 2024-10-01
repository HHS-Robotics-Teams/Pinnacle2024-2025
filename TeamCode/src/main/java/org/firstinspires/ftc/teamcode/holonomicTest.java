package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

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

        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Status:", "✅ Robot is initialized.");
    }

    @Override
    public void loop() {

        // maps power values to stick positions
        double rotate = -gamepad1.right_stick_x; // right stick: left and right
        double strafe = gamepad1.left_stick_x;   // left stick: left and right
        double drive = -gamepad1.left_stick_y;   //  left stick: up and down


        /* i don't know how any of this part works, i consulted chat gpt 💀
         * i will figure out the specifics once we can get the robot moving - Damien 1*/
        // I also dont understand this, its way complex math - James //

        // wheel calculations
        double frontLeftPower = drive + strafe - rotate;
        double frontRightPower = drive - strafe + rotate;
        double backLeftPower = drive - strafe - rotate;
        double backRightPower = drive + strafe + rotate;


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
        telemetry.addData("Drive Power", drive);
        telemetry.addData("Strafe Power", strafe);
        telemetry.addData("Rotate Power", rotate);
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