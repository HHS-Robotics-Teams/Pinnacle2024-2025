package org.firstinspires.ftc.teamcode.OpModes.Testing;

import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.batteryVoltageSensor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;


@TeleOp (name = "Pose Finder Simple", group = "testing")
public class PosefinderSimple extends OpMode {

    @Override
    public void init() {
        RobotHardware.init(hardwareMap);


    }

    @Override
    public void loop() {




        telemetry.addData("slide pos", slideMotor.getCurrentPosition());
        telemetry.addData("tilt pos", tiltMotor.getCurrentPosition());
        telemetry.addData("battery voltage", batteryVoltageSensor.getVoltage());
    }
}
