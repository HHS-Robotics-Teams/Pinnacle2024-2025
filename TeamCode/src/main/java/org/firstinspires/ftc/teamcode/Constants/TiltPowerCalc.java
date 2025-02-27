package org.firstinspires.ftc.teamcode.Constants;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

public class TiltPowerCalc {
    // Reference to the motor that provides the tick count.
    // DcMotor tiltMotor;

    // Constructor that accepts the motor reference.
    //public TiltPowerCalc(DcMotor tiltMotor) {
    //    this.tiltMotor = tiltMotor;
    //}

    /**
     * Calculates and returns a scaled power value based on the motor's tick count.
     * - Returns 1.0 for tick counts <= 1000.
     * - Returns 0.5 for tick counts >= 1800.
     * - For tick counts between 1000 and 1800, the power decreases linearly from 1.0 to 0.5.
     *
     * @return the scaled power value.
     */
    public double setTiltPower() {
        int ticks = tiltMotor.getCurrentPosition();
        if (tiltMotor.getPower() == 0) {
            return 0;
        }

        // Return full power if ticks are at or below 1000.
        if (ticks <= 1000) {
            tiltMotor.setPower(1.0);
            return 1.0;
        }
        // Return minimum power if ticks are at or above 1800.
        else if (ticks >= 1800) {
            tiltMotor.setPower(0.7);
            return 0.7;
        }
        // Calculate linear interpolation between 1000 and 1800 ticks.
        else {
            // Determine how far between 1000 and 1800 the current tick count is.
            double scale = (ticks - 1000) / 800.0; // scale ranges from 0 to 1
            // Calculate power: starting at 1.0 and decreasing by up to 0.5.
            double power = 1.0 - (scale * 0.7);
            if (ticks > 1700){
                power = 1; // set power back to 1 to stabilize and hold
            }
            tiltMotor.setPower(power);
            return power;
        }
    }


}
