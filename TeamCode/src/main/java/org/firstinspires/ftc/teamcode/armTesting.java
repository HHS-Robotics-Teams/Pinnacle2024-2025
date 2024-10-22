package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="Arm Testing OpMode", group="idk")
public class armTesting extends OpMode {

    // Declare motor variables
    private DcMotor slideMotor;
    private DcMotor tiltMotor;
    int startpos = 0;
    int tiltpos = 100;

    @Override
    public void init() {
        // Initialize the motors
        slideMotor = hardwareMap.get(DcMotor.class, "slide_motor");
        tiltMotor = hardwareMap.get(DcMotor.class, "tilt_motor");

        // Set motor directions if needed
        tiltMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.getCurrentPosition();
        slideMotor.setDirection(DcMotor.Direction.FORWARD); // Adjust as needed
        slideMotor.setTargetPosition(startpos);
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        tiltMotor.getCurrentPosition();
        tiltMotor.setDirection(DcMotor.Direction.FORWARD);  // Adjust as needed
        tiltMotor.setTargetPosition(startpos);
        tiltMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    @Override
    public void loop() {
        // Control the slide motor with left stick Y-axis
        double slidePower = -gamepad1.left_stick_y; // Invert for correct direction
        slideMotor.setPower(slidePower);

        // Control the tilt motor with right stick Y-axis
        if (gamepad1.a) {
            tiltMotor.setTargetPosition(tiltpos);
        }

        // Add telemetry for debugging
        telemetry.addData("Slide Motor Power", slidePower);
        telemetry.addData("Tilt Motor position", tiltpos);
        telemetry.update();
    }

    @Override
    public void stop() {
        // Stop the motors when the OpMode is stopped
        slideMotor.setPower(0);
        tiltMotor.setPower(0);
    }
}
