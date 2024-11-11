package org.firstinspires.ftc.teamcode.OpModes;

import static org.firstinspires.ftc.teamcode.components.RobotComponents.backLeftMotor;
import static org.firstinspires.ftc.teamcode.components.RobotComponents.backRightMotor;
import static org.firstinspires.ftc.teamcode.components.RobotComponents.frontLeftMotor;
import static org.firstinspires.ftc.teamcode.components.RobotComponents.frontRightMotor;
import static org.firstinspires.ftc.teamcode.components.RobotComponents.tiltMotor;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.components.RobotComponents;

@Autonomous(name="Pinnacle Auto", group="idk")
public class PinnacleAuto extends OpMode {

    double startTime;


    @Override
    public void init () {
        RobotComponents.init(hardwareMap);

    }

    public void setStartTime() {
        this.startTime = getRuntime();
    }


    @Override
    public void start() {

        // Make initial movements automatically so the arm doesn't drag against the ground.
        tiltMotor.setPower(1);
        tiltMotor.setTargetPosition(300);
        setStartTime();

    }

    @Override
    public void loop() {

        tiltMotor.setPower(.5);

        if (tiltMotor.getCurrentPosition() >= 200) {

            while (startTime - getRuntime() < 1) { // Move forward for 2.5 seconds.
                frontLeftMotor.setPower(.5);
                frontRightMotor.setPower(.5);
                backLeftMotor.setPower(.5);
                backRightMotor.setPower(.5);
            }

            if (startTime - getRuntime() >= 1) { // Stop moving after 2.5 seconds.
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
