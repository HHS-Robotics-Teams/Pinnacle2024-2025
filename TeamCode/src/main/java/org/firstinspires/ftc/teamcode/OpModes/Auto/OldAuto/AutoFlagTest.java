package org.firstinspires.ftc.teamcode.OpModes.Auto.OldAuto;

import static org.firstinspires.ftc.teamcode.Constants.Fields.AutoWasRan;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Constants.RobotHardware;

@Config
@Autonomous(name="Auto Flag Test", group="idk")
public class AutoFlagTest extends OpMode {

    @Override
    public void init () {

        RobotHardware.init(hardwareMap);
    }


    @Override
    public void loop() {
         AutoWasRan = true;

        telemetry.addData("Auto ran ", AutoWasRan ? "True" : "False");
        telemetry.update();


        }



    @Override
    public void stop() {

        requestOpModeStop();
    }

}
