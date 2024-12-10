package org.firstinspires.ftc.teamcode.excutil.keyframer.wrappers;

import com.qualcomm.robotcore.hardware.Servo;

public class ServoPositionable implements IPositionable {
    Servo servo;

    public ServoPositionable(Servo servo) {
        this.servo = servo;
    }

    @Override
    public double getPosition() {
        return servo.getPosition();
    }
}
