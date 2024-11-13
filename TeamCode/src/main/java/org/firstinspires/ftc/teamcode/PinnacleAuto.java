package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name="Pinnacle Auto", group="idk")
public class PinnacleAuto extends OpMode {

    double startTime;

    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;

    private DcMotor tiltMotor;

    int tiltStartPosition = 0;

    @Override
    public void init () {
        frontLeftMotor = hardwareMap.get(DcMotor.class, "front_left_motor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "front_right_motor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "back_left_motor");
        backRightMotor = hardwareMap.get(DcMotor.class, "back_right_motor");

        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);


        tiltMotor = hardwareMap.get(DcMotor.class, "tilt_motor");

        tiltMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        tiltMotor.setTargetPosition(tiltStartPosition);

        tiltMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        tiltMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        tiltMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        tiltMotor.setDirection(DcMotorSimple.Direction.REVERSE);

    }

    public void setStartTime() {
        this.startTime = getRuntime();
    }


    @Override
    public void start() {

        // Make initial movements automatically so the arm doesn't drag against the ground.
        tiltMotor.setPower(1);
        tiltMotor.setTargetPosition(250);
        setStartTime();

    }

    @Override
    public void loop() {

        tiltMotor.setPower(.5);

        if (tiltMotor.getCurrentPosition() >= 250) {

            while (startTime - getRuntime() < 1.5) { // Move forward for 2.5 seconds.
                frontLeftMotor.setPower(.5);
                frontRightMotor.setPower(.5);
                backLeftMotor.setPower(.5);
                backRightMotor.setPower(.5);
            }

            if (startTime - getRuntime() >= 1.5) { // Stop moving after 2.5 seconds.
                frontLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backLeftMotor.setPower(0);
                backRightMotor.setPower(0);
            }

        }

    }

    @Override
    public void stop() {

        // ---------- Stops All Motors ----------
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
        tiltMotor.setPower(0);

        requestOpModeStop();
    }

}
