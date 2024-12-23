package org.firstinspires.ftc.teamcode.Constants;

import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.backLeftMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.frontLeftMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.intake_claw_servo;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class HardwareSettings {
        public static void init(HardwareMap hardwareMap){
            /* ============================== Hardware Settings Fixes ============================== */

            // ---------- Reverse Left Side For Proper Strafing ----------
            frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

            // ---------- Resist Gravity and Inertia so Arm and Slide Stay In Place ----------
            slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            tiltMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            // ---------- Error Evasion ----------
            tiltMotor.setTargetPosition(0);
            slideMotor.setTargetPosition(0);

            // ---------- Enable Encoder Based Movement ----------
        //    tiltMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            tiltMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            tiltMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //    slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // ---------- Stop Arm From Slamming Backwards ----------
            tiltMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            tiltMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            intake_claw_servo.setDirection(Servo.Direction.REVERSE);
        }
}
