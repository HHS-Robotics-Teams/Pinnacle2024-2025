package org.firstinspires.ftc.teamcode.excutil.keyframer.wrappers;

import com.qualcomm.robotcore.hardware.DcMotor;

public class DcMotorPositionable implements IPositionable {

    private DcMotor motor;

    public DcMotorPositionable(DcMotor motor) {
        this.motor = motor;
    }

    @Override
    public double getPosition() {
        return (double)motor.getCurrentPosition();
    }
}
