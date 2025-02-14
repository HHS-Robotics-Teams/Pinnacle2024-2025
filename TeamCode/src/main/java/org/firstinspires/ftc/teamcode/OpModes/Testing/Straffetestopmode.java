package org.firstinspires.ftc.teamcode.OpModes.Testing;


import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.backLeftMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.backRightMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.frontLeftMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.frontRightMotor;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;
import org.firstinspires.ftc.teamcode.excutil.Input;


@TeleOp (name = "Straffe test")
public class Straffetestopmode extends OpMode {

    public Boolean reverse = false;
    public Input input;
    @Override
    public void init() {

        RobotHardware.init(hardwareMap);

        input = new Input();


    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1); // Pass gamepad input through custom class

      if (gamepad1.y) {
          backRightMotor.setPower(.1);
          frontRightMotor.setPower(.1);
      }
      if (gamepad1.b){
            backRightMotor.setPower(.1);
            backLeftMotor.setPower(.1);
      }
      if (gamepad1.x){
            backLeftMotor.setPower(.1);
            frontLeftMotor.setPower(.1);
      }
      if (gamepad1.back){
          backLeftMotor.setPower(0);
          frontLeftMotor.setPower(0);
          backRightMotor.setPower(0);
          frontRightMotor.setPower(0);
      }

    }

}
