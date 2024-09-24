package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "practiceTest OpMode", group = "idk")

public class driveTesting extends OpMode {

    private DcMotor leftMotor;
    private DcMotor rightMotor;

    @Override
    public void init() {
        leftMotor = hardwareMap.get(DcMotor.class, "left_motor");
        rightMotor = hardwareMap.get(DcMotor.class, "right_motor");

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop(){
        double leftPower = -gamepad1.left_stick_y;
        double rightPower = gamepad1.right_stick_y;

        //TODO the powers are swapped here because the motors were on the wrong slots in config and i dont have time to fix it -James
        leftMotor.setPower(rightPower);
        rightMotor.setPower(leftPower);

        telemetry.addData("Left Motor Power", leftPower);
        telemetry.addData("Right Motor Power", rightPower);
        telemetry.update();
    }

    @Override
    public void stop() {
        leftMotor.setPower(0);
        rightMotor.setPower(0);
        telemetry.addData("Status", "Stopped");
        telemetry.update();
    }


}