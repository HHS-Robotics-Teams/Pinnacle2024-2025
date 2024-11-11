package org.firstinspires.ftc.teamcode.OpModes;

import static org.firstinspires.ftc.teamcode.components.RobotComponents.slideMotor;
import static org.firstinspires.ftc.teamcode.components.RobotComponents.tiltMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.components.RobotComponents;

@TeleOp (name = "PoseFinderSimple")
public class PoseFinderSimple extends OpMode {



    @Override
    public void init() {

        RobotComponents.init(hardwareMap);

    }

    @Override
    public void loop() {


        telemetry.addData("Current Tilt Position: ", tiltMotor.getCurrentPosition());
        telemetry.addData("Current Slide Position ", slideMotor.getCurrentPosition());

        telemetry.update();
    }
}
