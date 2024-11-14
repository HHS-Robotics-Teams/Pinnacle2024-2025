package org.firstinspires.ftc.teamcode.OpModes;

import static org.firstinspires.ftc.teamcode.OpModes.Constants.IntakeRotateThreshold;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.IntakeWristPositionReached;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.IntakeCurrentPower;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.IntakePower;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.SlideHighBucket;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.SlideHighChamber;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.SlideLowBucket;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.SlideLowChamber;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.SlideMaxPosition;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.SlideMinPosition;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.SlidePower;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.TiltHighBucket;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.TiltHighChamber;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.TiltHomePosition;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.TiltLowBucket;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.TiltLowChamber;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.TiltMaxPosition;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.TiltMinPosition;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.TiltPickupPosition;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.TiltPower;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.TiltUpThreshold;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.WristCenter;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.WristLeft;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.WristRight;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.armRetractingHighBasket;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.armRetractingHighChamber;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.armRetractingHome;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.armRetractingLowBasket;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.armRetractingLowChamber;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.climbPositionReached;
import static org.firstinspires.ftc.teamcode.OpModes.Constants.currentRetractionStep;
import static org.firstinspires.ftc.teamcode.components.RobotComponents.backLeftMotor;
import static org.firstinspires.ftc.teamcode.components.RobotComponents.backRightMotor;
import static org.firstinspires.ftc.teamcode.components.RobotComponents.frontLeftMotor;
import static org.firstinspires.ftc.teamcode.components.RobotComponents.frontRightMotor;
import static org.firstinspires.ftc.teamcode.components.RobotComponents.intakeCRServo;
import static org.firstinspires.ftc.teamcode.components.RobotComponents.intakeWristServo;
import static org.firstinspires.ftc.teamcode.components.RobotComponents.leftClaw;
import static org.firstinspires.ftc.teamcode.components.RobotComponents.rightClaw;
import static org.firstinspires.ftc.teamcode.components.RobotComponents.slideMotor;
import static org.firstinspires.ftc.teamcode.components.RobotComponents.tiltMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.components.RobotComponents;
import org.firstinspires.ftc.teamcode.excutil.Input;

@TeleOp(name = "CompDrive25", group = "Test jr")
public class CompDrive25 extends OpMode {

    public Input input;

    @Override
    public void init() {
        input = new Input();

        RobotComponents.init(hardwareMap);

        // ---------- Confirmation Printing ----------
        telemetry.addData("Status:", "✅ Robot is initialized.");
        telemetry.update();

    }
    @Override
    public void start() {
        tiltMotor.setTargetPosition(TiltMinPosition);
        slideMotor.setTargetPosition(SlideMinPosition);
        intakeWristServo.setPosition(WristLeft);
    }

    @Override
    public void loop() {


        input.pollGamepad(gamepad1);

        /* ============================== Driving and Wheels ============================== */

        // ---------- Maps Wheels to Joysticks ----------
        double rotate = gamepad1.right_stick_x; // right stick: left and right
        double strafe = -gamepad1.left_stick_x;   // left stick: left and right
        double drive = -gamepad1.left_stick_y;   //  left stick: up and down

        // ---------- Slowdown While Arm Up ----------
        if (tiltMotor.getTargetPosition() >= TiltUpThreshold) {
            rotate = rotate / 3;
            strafe = strafe / 3;
            drive = drive / 4;
        }

        // ---------- Wheel Calculations ----------
        double frontLeftPower = drive + strafe + rotate;
        double frontRightPower = drive - strafe - rotate;
        double backLeftPower = drive - strafe + rotate;
        double backRightPower = drive + strafe - rotate;

        // ---------- Set Wheel Power ----------
        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);


        // ---------- Set Arm and Intake Power ----------
        tiltMotor.setPower(TiltPower);
        slideMotor.setPower(SlidePower);



        // ----------- Arm Flags ------------
        if (tiltMotor.getCurrentPosition() <= IntakeRotateThreshold) {
            IntakeWristPositionReached = false;
            intakeWristServo.setPosition(WristCenter);
        }



        /* ============================== Robot Controls ============================== */

        // ----------- Home -----------------
        if (gamepad1.left_stick_button) {
            armRetractingHome = true;
        }

        if(armRetractingHome){
            switch(currentRetractionStep){
                case(1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if(Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50){
                        currentRetractionStep++;
                    }
                    break;
                case(2):
                    tiltMotor.setTargetPosition(TiltLowChamber);
                    slideMotor.setTargetPosition(SlideMinPosition);
                    intakeWristServo.setPosition(WristCenter);
                    currentRetractionStep = 1;
                    armRetractingHome = false;
                    break;
            }
        }

        // ---------- Pickup ------------------

        if (gamepad1.right_stick_button ){
            tiltMotor.setTargetPosition(TiltPickupPosition);
            slideMotor.setTargetPosition(SlideMinPosition);
            intakeWristServo.setPosition(WristCenter);
        }
        // --------------- Manual Arm Tilt -------------------
            // Arm up
        if (input.dpad_up.held() && (tiltMotor.getCurrentPosition() <= TiltMaxPosition)) {
            tiltMotor.setTargetPosition(tiltMotor.getTargetPosition() +20 );
        }

            // Arm Down
        if (input.dpad_down.held() && (tiltMotor.getCurrentPosition() >= TiltMinPosition)) {
           tiltMotor.setTargetPosition(tiltMotor.getTargetPosition() -20 );
        }

        // -------------- Manual Extension --------------------
            // Slide out
        if (input.left_bumper.held() && (slideMotor.getCurrentPosition() <= SlideMaxPosition) ) {
            slideMotor.setTargetPosition(slideMotor.getCurrentPosition()+60);
        }

            // Slide in
        if (input.left_trigger.held() && (slideMotor.getCurrentPosition() >= SlideMinPosition))  {
            slideMotor.setTargetPosition(slideMotor.getCurrentPosition()-60);

        }

        // ---------- Intake Wrist Servo ----------
        if (input.dpad_left.down() && IntakeWristPositionReached) {
            intakeWristServo.setPosition(WristLeft);
        }
        if (input.dpad_right.down() && IntakeWristPositionReached) {
            intakeWristServo.setPosition(WristRight);
        }

        // ---------- Intake Wheel Servo ----------
        if (input.right_bumper.held()) { //
            IntakeCurrentPower = IntakePower;
            intakeCRServo.setDirection(DcMotorSimple.Direction.REVERSE);
        } else if (input.right_trigger.held()) {
            IntakeCurrentPower = IntakePower;
            intakeCRServo.setDirection(DcMotorSimple.Direction.FORWARD);
        } else {
            IntakeCurrentPower = 0;
        }
        intakeCRServo.setPower(IntakeCurrentPower);


        /* ============================== Scoring ============================== */

        // ------------ High Basket-------------
        if (input.y.down()) {
            armRetractingHighBasket = true;
            IntakeWristPositionReached = false;
        }

        if(armRetractingHighBasket){
            switch(currentRetractionStep){
                case(1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if(Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50){
                        currentRetractionStep++;
                    }
                    break;
                case(2):
                    tiltMotor.setTargetPosition(TiltHighBucket);
                    intakeWristServo.setPosition(WristCenter);
                    slideMotor.setTargetPosition(SlideHighBucket);
                    currentRetractionStep = 1;
                    armRetractingHighBasket = false;
                    break;
            }
        }

        // ------------ Low Basket ---------------
        if (input.b.down()) {
            armRetractingLowBasket = true;
            IntakeWristPositionReached = false;
        }

        if(armRetractingLowBasket){
            switch(currentRetractionStep){
                case(1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if(Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50){
                        currentRetractionStep++;
                    }
                    break;
                case(2):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    tiltMotor.setTargetPosition(TiltLowBucket);
                    intakeWristServo.setPosition(WristCenter);
                    slideMotor.setTargetPosition(SlideLowBucket);
                    armRetractingLowBasket = false;
                    break;
            }
        }

        // ------------ High Chamber --------------
        if (input.x.down()) {
            armRetractingHighChamber = true;
            IntakeWristPositionReached = true;

            telemetry.speak("rodo control reached");
        }

        if(armRetractingHighChamber){
            switch(currentRetractionStep){
                case(1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if(Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50){
                        currentRetractionStep++;
                    }
                    break;
                case(2):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    tiltMotor.setTargetPosition(TiltHighChamber);
                    intakeWristServo.setPosition(WristLeft);
                    slideMotor.setTargetPosition(SlideHighChamber);
                    armRetractingHighChamber = false;
                    break;
            }
        }


        // ------------- Low Chamber ----------------
        if (input.a.down()) {
            armRetractingLowChamber = true;
            IntakeWristPositionReached = true;

            telemetry.speak("rodo control reached");
        }
        if(armRetractingLowChamber){
            switch(currentRetractionStep){
                case(1):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    if(Math.abs(slideMotor.getCurrentPosition() - SlideMinPosition) < 50){
                        currentRetractionStep++;
                    }
                    break;
                case(2):
                    slideMotor.setTargetPosition(SlideMinPosition);
                    tiltMotor.setTargetPosition(TiltLowBucket);
                    intakeWristServo.setPosition(WristLeft);
                    slideMotor.setTargetPosition(SlideLowChamber);
                    armRetractingLowChamber = false;
                    break;
            }
        }

        /* ============================== Climbing ============================== */

            if (input.back.held()) {
                leftClaw.setDirection(DcMotorSimple.Direction.FORWARD);
                rightClaw.setDirection(DcMotorSimple.Direction.REVERSE);
                leftClaw.setPower(1);
                rightClaw.setPower(1);



                telemetry.speak("climb position reached");
            }
            else if (input.start.held()) { // Claw controls made by Benny
                leftClaw.setDirection(DcMotorSimple.Direction.REVERSE);
                rightClaw.setDirection(DcMotorSimple.Direction.FORWARD);
                leftClaw.setPower(1); // Debugged by Damien
                rightClaw.setPower(1);


            }
            else {
                leftClaw.setPower(0);
                rightClaw.setPower(0);
        }


            /* ============================== Telemetry For Debugging ============================== */

            // ---------- Wheels and Driving ----------
            telemetry.addData("Front Left Power: ", frontLeftPower);
            telemetry.addData("Front Right Power: ", frontRightPower);
            telemetry.addData("Back Left Power: ", backLeftPower);
            telemetry.addData("Back Right Power: ", backRightPower);
            telemetry.addData("Drive Power: ", drive);
            telemetry.addData("Strafe Power: ", strafe);
            telemetry.addData("Rotate Power: ", rotate);

            // ---------- Arm and Intake ----------
            telemetry.addData("Slide Motor Power: ", slideMotor.getPower());
            telemetry.addData("Tilt Motor Power: ", tiltMotor.getPower());
            telemetry.addData("Current Tilt Position: ", tiltMotor.getCurrentPosition());
            telemetry.addData("Current Slide Position ", slideMotor.getCurrentPosition());
            telemetry.addData("Intake Spin Power: ", intakeCRServo.getDirection());

            // ---------- Flags -----------
            telemetry.addData("Climb control status", climbPositionReached ? "True" : "False");
            telemetry.addData("Wrist control status", IntakeWristPositionReached ? "True" : "False");

            // ---------- Update ----------
            telemetry.update();


        }


    }

