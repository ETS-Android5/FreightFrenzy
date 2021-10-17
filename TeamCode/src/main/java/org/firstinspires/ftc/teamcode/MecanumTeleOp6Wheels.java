/* This program makes it easier to control the robot by letting the left stick control
forward and backward movement and letting the right stick x control the rotating of
the robot just like an Rc Car.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp(name = "MecanumTeleOp6Wheels")
public class MecanumTeleOp6Wheels extends LinearOpMode {

    //Declaring Motors
    private DcMotor Frontleft;
    private DcMotor Frontright;
    private DcMotor Backleft;
    private DcMotor Backright;
    private DcMotor Middleright;
    private DcMotor Middleleft;

    double lefty = 0.0;
    double leftx = 0.0;
    double righty = 0.0;
    double rightx = 0.0;

    public void move_forwardback_rotate(){
        Frontleft.setPower(-lefty);
        Backleft.setPower(-lefty);
        Frontright.setPower(righty);
        Backright.setPower(righty);
        Middleleft.setPower(-lefty);
        Middleright.setPower(righty);
    }

    public void move_side(){
        Frontleft.setPower(-leftx);
        Backleft.setPower(leftx);
        Frontright.setPower(rightx);
        Backright.setPower(-rightx);
    }
    public void slow_forwardback(){
        Frontleft.setPower(-righty);
        Backleft.setPower(-righty);
        Frontright.setPower(righty);
        Backright.setPower(righty);
    }
    public void slow_side(){
        Frontleft.setPower(-leftx);
        Backleft.setPower(leftx);
        Frontright.setPower(rightx);
        Backright.setPower(-rightx);
    }
    public void middle_forwardback(){
        Middleright.setPower(righty);
        Middleleft.setPower(lefty);
        }


    /*public void diagonal(){
        if (rightx>0 && lefty>0||rightx<0 && lefty<0) {
            Frontleft.setPower(lefty);
            Backright.setPower(-lefty);
        }
        else if (rightx>0 && lefty<0 ||rightx<0 && lefty>0){
            Backleft.setPower(lefty);
            Frontright.setPower(-lefty);
        }*/


    @Override
    public void runOpMode() throws InterruptedException {
        //Hardware Mapping
        Frontright = hardwareMap.get(DcMotor.class, "Frontright");
        Frontleft = hardwareMap.get(DcMotor.class, "Frontleft");
        Backright = hardwareMap.get(DcMotor.class, "Backright");
        Backleft = hardwareMap.get(DcMotor.class, "Backleft");
        Middleright = hardwareMap.get(DcMotor.class, "Middleright");
        Middleleft = hardwareMap.get(DcMotor.class, "Middleleft");

        waitForStart();
        while (opModeIsActive()) {
            double reset = 0;
            Frontright.setPower(reset);
            Frontleft.setPower(reset);
            Backleft.setPower(reset);
            Backright.setPower(reset);
            Middleleft.setPower(reset);
            Middleright.setPower(reset);

            lefty = gamepad1.left_stick_y;
            leftx = gamepad1.left_stick_x;
            righty = gamepad1.right_stick_y;
            rightx = gamepad1.right_stick_x;
            Boolean modeTwo = false;
            if(gamepad1.right_bumper) {
                modeTwo = true;
            } else{
                modeTwo = false;
            }
            Boolean modeThree = false;
            if(gamepad1.left_bumper){
                modeThree = true;
            }
            else{
                modeThree=false;
            }
            if(modeTwo==true){
                move_side();
                middle_forwardback();
            }

            if(modeThree==true) {
                slow_forwardback();
                slow_side();
            }
            move_forwardback_rotate();


        }




            //diagonal();

            //Displays power of each motor on the robot controller
            telemetry.addData("front left motor", "%.2f", Frontleft.getPower());
            telemetry.addData("front right motor", "%.2f", Frontright.getPower());
            telemetry.addData("back left motor", "%.2f", Backleft.getPower());
            telemetry.addData("back right motor", "%.2f", Backright.getPower());
            telemetry.addData("middle left motor", "%.2f", Middleleft.getPower());
            telemetry.addData("middle right motor", "%.2f", Middleright.getPower());

            telemetry.addData("lefty", "%.2f", lefty);
            telemetry.addData("leftx", "%.2f", leftx);

            telemetry.addData("rightx", "%.2f", rightx);
            telemetry.addData("righty", "%.2f", righty);

            telemetry.update();
        }
    }


