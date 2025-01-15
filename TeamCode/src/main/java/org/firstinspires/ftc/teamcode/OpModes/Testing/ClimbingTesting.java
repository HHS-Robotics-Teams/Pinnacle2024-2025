//package org.firstinspires.ftc.teamcode.OpModes.Testing;
//
//import static org.firstinspires.ftc.teamcode.Constants.Fields.ActivelyClimbing;
//import static org.firstinspires.ftc.teamcode.Constants.Fields.SlideMinPosition;
//import static org.firstinspires.ftc.teamcode.Constants.Fields.TiltHighBucket;
//import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.leftClaw;
//import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.rightClaw;
//import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.slideMotor;
//import static org.firstinspires.ftc.teamcode.Constants.RobotHardware.tiltMotor;
//
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
//
//public class ClimbingTesting {
    // Working climb code
//    {
//    if (input.start.held()) {
//        ActivelyClimbing = true;
//        slideMotor.setTargetPosition(SlideMinPosition);
//        tiltMotor.setTargetPosition(TiltHighBucket);
//        leftClaw.setDirection(DcMotorSimple.Direction.FORWARD);
//        rightClaw.setDirection(DcMotorSimple.Direction.REVERSE);
//        leftClaw.setPower(1);
//        rightClaw.setPower(1);
//        telemetry.speak("ENDGAME ENDGAME ENDGAME");
//    }
//
//
//        else if (input.back.held()) { // Claw controls made by Benny
//        leftClaw.setDirection(DcMotorSimple.Direction.REVERSE);
//        rightClaw.setDirection(DcMotorSimple.Direction.FORWARD);
//        leftClaw.setPower(1); // Debugged by Damien
//        rightClaw.setPower(1);
//    }
//        else {
//        leftClaw.setPower(0);
//        rightClaw.setPower(0);
//    }
//  }

    //        if (input.start.down()) {
//            leftClaw.setDirection(DcMotorSimple.Direction.FORWARD);
//            rightClaw.setDirection(DcMotorSimple.Direction.REVERSE);
//            PreppingClimbers = true;
//            ActivelyClimbing = false;
//        }
//
//        if (PreppingClimbers)
//            switch (currentClimbStep) {
//                case (1):
//                    tiltMotor.setTargetPosition(TiltLowBucket);
//                    slideMotor.setTargetPosition(SlideMinPosition);
//                    if ((tiltMotor.getCurrentPosition() > 1300) && (slideMotor.getCurrentPosition() <= 5 )) {
//                        leftClaw.setPower(1); // while the claws are all the way down / in a
//                        rightClaw.setPower(1); // consistent spot.
//                        Climber_Timer.reset();
//                        currentClimbStep++;
//                    }
//                    break;
//                case (2):
//                    if (Climber_Timer.seconds() > 3){ //needs to be adjusted to match the robot spool time
//                        leftClaw.setPower(0);
//                        rightClaw.setPower(0);
//                        PreppingClimbers = false;
//                        currentClimbStep = 1;
//                    }
//                    break;
//            }
//        if (input.back.down()) {
//            leftClaw.setDirection(DcMotorSimple.Direction.REVERSE);
//            rightClaw.setDirection(DcMotorSimple.Direction.FORWARD);
//            Climber_Timer.reset();
//            ActivelyClimbing = true;
//            PreppingClimbers = false;
//            }
//
//        if (ActivelyClimbing)
//            switch (currentClimbStep) {
//                case (1):
//                    leftClaw.setPower(1); // while the claws are all the way down / in a
//                    rightClaw.setPower(1); // consistent spot.
//                    Climber_Timer.reset();
//                    currentClimbStep ++;
//                    break;
//                case (2):
//                    if (Climber_Timer.seconds() > .5){ /* we can change this to the drivers liking this is the the amount of time
//                    between the claws retracting and stopping encase we miss on the climb.
//                        */
//                        leftClaw.setPower(0);
//                        rightClaw.setPower(0);
//                        ActivelyClimbing = false;
//                        currentClimbStep = 1;
//                    break;
//
//                    }
//            }
//        if (input.start.down()) {
//            tiltMotor.setTargetPosition(TiltLowBucket);
//            leftClaw.setDirection(DcMotorSimple.Direction.FORWARD);
//            rightClaw.setDirection(DcMotorSimple.Direction.REVERSE);
//            PreppingClimbers = true;
//            ActivelyClimbing = false;
//        }
//
//        if (PreppingClimbers) {
//            Climber_Timer.reset(); // During prepping, the claws go up vertically. The timer
//            if (Climber_Timer < 5.0) { // may need to be adjusted. Make sure to init the robot
//                leftClaw.setPower(1); // while the claws are all the way down / in a
//                rightClaw.setPower(1); // consistent spot.
//            }
//            else {
//                leftClaw.setPower(0);
//                rightClaw.setPower(0);
//                PreppingClimbers = false;
//            }
//        }
//
//        if (input.back.down()) {
//            tiltMotor.setTargetPosition(TiltLowBucket);
//            leftClaw.setDirection(DcMotorSimple.Direction.REVERSE);
//            rightClaw.setDirection(DcMotorSimple.Direction.FORWARD);
//            ActivelyClimbing = true;
//            PreppingClimbers = false;
//        }
//
//        if (ActivelyClimbing) {
//            Climber_Timer.reset(); // During active climbing, claws are pulled to the robot,
//            if (Climber_Timer < 10.0) { // making it climb. Additionally, arm goes down onto
//                leftClaw.setPower(1); // the low bar for further support.
//                rightClaw.setPower(1);
//            }
//            else {
//                leftClaw.setPower(0);
//                rightClaw.setPower(0);
//                ActivelyClimbing = false;
//            }
//        }
//}

