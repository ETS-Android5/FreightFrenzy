package org.firstinspires.ftc.teamcode.RobotObjects.MAS;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RobotObjects.MAS.Claw;

public class Mecanum_Wheels {
    //Configuration used: 6wheelConfig
    public DcMotorEx frontright;
    public DcMotorEx frontleft;
    public DcMotorEx backright;
    public DcMotorEx backleft;
    //public DcMotorEx middleright;
    //public DcMotorEx middleleft;
    public DcMotorEx arm;
    public Claw claw;
    public boolean IsMASAutonomous = false;

    public double leftErrorAdjustment = 1.0;
    public double rightErrorAdjustment = 1.0;

    public double mecanumWheelCircumference = 12; //inches
    public double omniWheelCircumference = 12; //inches

    public LinearOpMode parent;

    public int velocity = 200;

    private ElapsedTime runtime = new ElapsedTime();


    public Telemetry telemetry;

    public Mecanum_Wheels(HardwareMap hardwareMap) {
        frontright = hardwareMap.get(DcMotorEx.class,"Frontright");
        frontleft = hardwareMap.get(DcMotorEx.class,"Frontleft");
        backright = hardwareMap.get(DcMotorEx.class,"Backright");
        backleft = hardwareMap.get(DcMotorEx.class,"Backleft");
        //middleright = hardwareMap.get(DcMotorEx.class,"Middleright");
        //middleleft = hardwareMap.get(DcMotorEx.class,"Middleleft");
        arm = hardwareMap.get(DcMotorEx.class, "arm");
        //claw = new Claw(hardwareMap);
    }

    //initialize for TeleOp
    public void initialize() {
        double reset = 0;
        frontright.setPower(reset);
        frontleft.setPower(reset);
        backleft.setPower(reset);
        backright.setPower(reset);

        if(IsMASAutonomous) {
            {
//            frontright.setDirection(DcMotorSimple.Direction.REVERSE);
//            middleright.setDirection(DcMotorSimple.Direction.REVERSE);
//            backright.setDirection(DcMotorSimple.Direction.REVERSE);

                frontleft.setDirection(DcMotor.Direction.REVERSE);
                frontright.setDirection(DcMotor.Direction.FORWARD);
                //middleright.setDirection(DcMotor.Direction.FORWARD);
                //middleleft.setDirection(DcMotor.Direction.REVERSE);
                backright.setDirection(DcMotor.Direction.FORWARD);
                backleft.setDirection(DcMotor.Direction.REVERSE);

                frontleft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                //middleleft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                backleft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                frontright.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                //middleright.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                backright.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


                frontleft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                //middleleft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                backleft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                frontright.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                //middleright.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                backright.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
        }
    }
   public void liftArm(double power) {
            arm.setPower(power);

   }

    //AUTOMATED arm control
    public void armToLevel(double position) {

    }


    public void moveArm(int level, int currentLevel) {
        if (level == 0) {
            if (currentLevel == 1) {
                arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                arm.setTargetPosition(600);
                claw.moveBucket(0.0);
                arm.setPower(-0.5);
                arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            }

            if (currentLevel == 2) {
                arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                arm.setTargetPosition(1500);
                claw.moveBucket(0.0);
                arm.setPower(-0.5);
                arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
        }

        if (currentLevel == 1) {
            if (currentLevel == 0) {
                arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                arm.setTargetPosition(600);
                claw.moveBucket(0.3);
                arm.setPower(0.5);
                arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            }

            if (currentLevel == 2) {
                arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                arm.setTargetPosition(1300);
                claw.moveBucket(0.3);
                arm.setPower(-0.5);
                arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
        }

        if (level == 2) {
            if (currentLevel == 1) {
                arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                arm.setTargetPosition(1300);
                claw.moveBucket(0.5);
                arm.setPower(0.5);
                arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            }

            if (currentLevel == 0) {
                arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                arm.setTargetPosition(1500);
                claw.moveBucket(0.5);
                arm.setPower(0.5);
                arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
        }

   }

    public void encoderDrive(double speed,
                             double frontLeftInches, double backLeftInches, double frontRightInches,
                             double backRightInches, double timeoutS) {
        int new_frontLeftTarget;
        int new_frontRightTarget;
        //int new_middleLeftTarget=0;
        //int new_middleRightTarget=0;
        int new_backLeftTarget;
        int new_backRightTarget;
        double ticksPerInchMecanum = (537.7 / mecanumWheelCircumference);
        double ticksPerInchOmni = (537.7 / omniWheelCircumference);
        // Ensure that the opmode is still active
        if (parent.opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            new_frontLeftTarget = frontleft.getCurrentPosition() + (int)(frontLeftInches * ticksPerInchMecanum);
            new_frontRightTarget = frontright.getCurrentPosition() + (int)(frontRightInches * ticksPerInchMecanum);
            /*if(middleLeftInches!=0)
                new_middleLeftTarget = frontleft.getCurrentPosition() + (int)(middleLeftInches * ticksPerInchOmni);
            if(middleRightInches!=0)
                new_middleRightTarget = frontright.getCurrentPosition() + (int)(middleRightInches * ticksPerInchOmni);
            */
            new_backLeftTarget = backleft.getCurrentPosition() + (int)(backLeftInches * ticksPerInchMecanum);
            new_backRightTarget = backright.getCurrentPosition() + (int)(backRightInches * ticksPerInchMecanum);
            frontleft.setTargetPosition(new_frontLeftTarget);
            frontright.setTargetPosition(new_frontRightTarget);

            /*if(new_middleLeftTarget!=0)
                middleleft.setTargetPosition(new_middleLeftTarget);
            if(new_middleRightTarget!=0)
                middleright.setTargetPosition(new_middleRightTarget);
            */
            backleft.setTargetPosition(new_backLeftTarget);
            backright.setTargetPosition(new_backRightTarget);

            // Turn On RUN_TO_POSITION
            frontleft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            frontright.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            /*if(new_middleLeftTarget!=0)
                middleleft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            if(new_middleRightTarget!=0)
                middleright.setMode(DcMotor.RunMode.RUN_TO_POSITION);*/
            backleft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backright.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            frontleft.setPower(speed*0.9);
            frontright.setPower(speed);
            /*if(new_middleLeftTarget!=0)
                middleleft.setPower(speed);
            if(new_middleRightTarget!=0)
                middleright.setPower(speed);*/
            backleft.setPower(speed*0.9);
            backright.setPower(speed*0.9);

            // keep looping while we are still active, and there is time left, and both motors are running.
            while (parent.opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    ( frontleft.isBusy() && frontright.isBusy()  && backleft.isBusy() && backright.isBusy()))

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d :%7d :%7d ", new_frontLeftTarget,  new_frontRightTarget, new_backLeftTarget, new_backRightTarget);
            telemetry.addData("Path2",  "Running at %7d :%7d :%7d :%7d ",
                    frontleft.getCurrentPosition(),
                    frontright.getCurrentPosition(),
                    //middleleft.getCurrentPosition(),
                    //middleright.getCurrentPosition(),
                    backleft.getCurrentPosition(),
                    backright.getCurrentPosition());
            telemetry.update();
        }

        // Stop all motion;
        frontleft.setPower(0);
        frontright.setPower(0);
        //middleleft.setPower(0);
        //middleright.setPower(0);
        backleft.setPower(0);
        backright.setPower(0);

        // Turn off RUN_TO_POSITION
        frontleft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontright.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //middleleft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //middleright.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backleft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backright.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //  sleep(250);   // optional pause after each move
    }

    //moveForward for TeleOp
    //The left and right powers are controlled by the left and right y axes
    public void move_forwardback_rotate( double leftPower, double rightPower){
        frontleft.setPower(-leftPower*leftErrorAdjustment);
        backleft.setPower(-leftPower*leftErrorAdjustment);
        frontright.setPower(rightPower*rightErrorAdjustment);
        backright.setPower(rightPower*rightErrorAdjustment);
    //    middleleft.setPower(-leftPower*leftErrorAdjustment);
    //    middleright.setPower(rightPower*rightErrorAdjustment);
    }

    //moveSide for TeleOp
    //The left and right powers are controlled by the left and right x axes
    public void move_side( double leftPower, double rightPower){
        frontleft.setPower(-leftPower*leftErrorAdjustment);
        backleft.setPower(leftPower*leftErrorAdjustment);
        frontright.setPower(rightPower*rightErrorAdjustment);
        backright.setPower(-rightPower*rightErrorAdjustment);
    }


    //frontleft backleft frontright backright
    public void move_right_auto(double speed, double distance, double timeOut) {
        encoderDrive(speed,-distance,distance,-distance,distance, timeOut);
    }

    public void move_left_auto(double speed, double distance, double timeOut) {
        encoderDrive(speed,distance,-distance,-distance,distance, timeOut);
    }

    public void rotate_clock_auto(double speed, double distance, double timeOut) {
        encoderDrive(speed,-distance,-distance,distance,distance, timeOut);
    }

    public void rotate_anti_clock_auto(double speed, double distance, double timeOut) {
        encoderDrive(speed,distance,distance,-distance,-distance, timeOut);
    }
    public void move_backward_auto(double speed, double distance, double timeOut){
        encoderDrive(speed,distance,distance,distance, distance, timeOut);
    }

    public void move_forward_auto(double speed, double distance, double timeOut){
        encoderDrive(speed,-distance,-distance,-distance, -distance, timeOut);
    }

    /*public void middleForwardback(double leftPower, double rightPower){
        middleleft.setPower(leftPower*leftErrorAdjustment);
        middleright.setPower(rightPower*rightErrorAdjustment);
    }*/
}
