package org.firstinspires.ftc.teamcode;

import androidx.annotation.CheckResult;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name="Damien Arm Testing OpMode", group="idk")
public class damienArmTesting extends OpMode {

    DcMotor slideMotor;
    DcMotor tiltMotor;
    int slideStartPosition = 0;
    int tiltStartPosition = 0;
    int moveTicks = 20;
    double tiltPower = 1;
    double slidePower = 1;

    @Override
    public void init() {

        slideMotor = hardwareMap.get(DcMotor.class, "slide_motor");
        tiltMotor = hardwareMap.get(DcMotor.class, "tilt_motor");

        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        tiltMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        tiltMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);  // Reset encoder values
        tiltMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        tiltMotor.setTargetPosition(tiltStartPosition);
        tiltMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        slideMotor.setTargetPosition(slideStartPosition);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        tiltMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("✅", "Motors initialized.");
        telemetry.update();

    }

    @Override
    public void loop() {


        tiltMotor.setPower(tiltPower);

        slideMotor.setPower(slidePower);

       // if (gamepad1.a) {
      //      tiltMotor.setTargetPosition(300);
      //  }

        // ---------- Slide controls ----------
        if (gamepad1.dpad_right) {
            slideMotor.setTargetPosition(slideMotor.getCurrentPosition() + moveTicks);
        }

        if (gamepad1.dpad_left) {
            slideMotor.setTargetPosition(slideMotor.getCurrentPosition() - moveTicks);
        }

        // ---------- Tilt controls ----------
        if (gamepad1.dpad_up) {
            tiltMotor.setTargetPosition(tiltMotor.getCurrentPosition() + moveTicks);
        }

        if (gamepad1.dpad_down) {
            tiltMotor.setTargetPosition(tiltMotor.getCurrentPosition() - moveTicks);
        }

        // ---------- Adjust arm tilting speed ----------
        if (gamepad1.start) {
            moveTicks++;
        }

        if (gamepad1.back) {
            moveTicks--;
        }

        if(gamepad1.y){
            tiltPower=tiltPower+.05;
        }
        if(gamepad1.x){
            tiltPower=tiltPower-.05;
        }

        telemetry.addData("Slide Motor Power: ", slideMotor.getPower());
        telemetry.addData("Tilt Motor Power: ", tiltMotor.getPower());
        telemetry.addData("Current Tilt Position: ", tiltMotor.getCurrentPosition());
        telemetry.addData("Move Speed: ", moveTicks);
        telemetry.update();

    }

    @Override
    public void stop() {
        // Stop the motors when the OpMode is stopped
        slideMotor.setPower(0);
        tiltMotor.setPower(0);
    }

}
