package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Holonomic Drive Test OpMode", group = "idk")
public class holonomicTest extends OpMode {

    // our four motors
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;

    private DcMotor tiltMotor;
    private DcMotor slideMotor;
    private CRServo intakeCRServo;

    @Override
    public void init() {

        // maps hardware config for the wheels
        frontLeftMotor = hardwareMap.get(DcMotor.class, "front_left_motor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "front_right_motor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "back_left_motor");
        backRightMotor = hardwareMap.get(DcMotor.class, "back_right_motor");

        // maps hardware config for the arm and intake
        tiltMotor = hardwareMap.get(DcMotor.class, "tilt_motor");
        slideMotor = hardwareMap.get(DcMotor.class, "slide_motor");
        intakeCRServo = hardwareMap.get(CRServo.class, "wheel_servo");

        // Reverse the left wheels so it can strafe properly
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // initial behavior for the arm and intake
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        tiltMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status:", "✅ Robot is initialized.");
    }

    @Override
    public void loop() {

// ---------------------------- Driving and Wheels ----------------------------

        // maps power values to stick positions
        double rotate = -gamepad1.right_stick_x; // right stick: left and right
        double strafe = gamepad1.left_stick_x;   // left stick: left and right
        double drive = -gamepad1.left_stick_y;   //  left stick: up and down


        /* i don't know how any of this part works, i consulted chat gpt 💀
         * i will figure out the specifics once we can get the robot moving - Damien */
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


// ---------------------------- Intake Servo Control ----------------------------

        if (gamepad1.a) {
            intakeCRServo.setDirection(DcMotorSimple.Direction.FORWARD);
        } else if (gamepad1.b) {
            intakeCRServo.setDirection(DcMotorSimple.Direction.REVERSE);
        }

// ---------------------------- Telemetry For Debugging ----------------------------

        // ---------- Wheels and Driving ----------
        telemetry.addData("Front Left Power: ", frontLeftPower);
        telemetry.addData("Front Right Power: ", frontRightPower);
        telemetry.addData("Back Left Power: ", backLeftPower);
        telemetry.addData("Back Right Power: ", backRightPower);
        telemetry.addData("Drive Power: ", drive);
        telemetry.addData("Strafe Power: ", strafe);
        telemetry.addData("Rotate Power: ", rotate);

        // ---------- Arm and Intake ----------
        telemetry.addData("Intake Spin Power: ", intakeCRServo.getDirection());

        // ---------- Update ----------
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