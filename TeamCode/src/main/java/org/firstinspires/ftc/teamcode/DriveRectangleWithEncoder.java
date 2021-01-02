// original code from: https://stemrobotics.cs.pdx.edu/node/4746
//  autonomous program that drives bot forward a set distance, stops then
//   backs up to the starting point using encoders to measure the distance.
// Modified by team MARVELS to drive in a small rectangle (or square),
//  first clockwise then counter-clockwise.

package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import org.firstinspires.ftc.teamcode.WorkerClasses.AutonomousWorkerMethods;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;

@Config
@Autonomous(name="Drive Rectangle", group="Templates")
//@Disabled
public class DriveRectangleWithEncoder extends LinearOpMode
{
    // declare our motors
    DcMotorEx leftMotor;
    DcMotorEx rightMotor;
    DcMotorEx middleMotor;
    DcMotorEx shooter;
    DcMotorEx intake;
    DcMotorEx roller;

    // declare our servos
    Servo gate;
    Servo feeder;

    // define an instance of FtcDashboard;
    FtcDashboard dashboard;

    // predefine some variables for dashboard configuration
    public static boolean pauseateachcorner = true;   // set to false if pausing at each corner is not desired
    public static boolean useCustomPIDF = false;      // set to true to use custom PIDF control
    // motor POWER is used for running WITHOUT encoders, motor VELOCITY is used for running WITH encooders
    //  double motorpower = 0.25;       // range 0.0 - 1.0
    public static double motorVelocity = 125;         // units is ticks/second
    public static PIDFCoefficients dashPID_Vleft = new PIDFCoefficients(0,0,0,0);
    public static PIDFCoefficients dashPID_Vright = new PIDFCoefficients(0,0,0,0);
    public static PIDFCoefficients dashPID_Vmiddle = new PIDFCoefficients(0,0,0,0);
    public static PIDFCoefficients dashPID_Pleft = new PIDFCoefficients(0,0,0,0);
    public static PIDFCoefficients dashPID_Pright = new PIDFCoefficients(0,0,0,0);
    public static PIDFCoefficients dashPID_Pmiddle = new PIDFCoefficients(0,0,0,0);

    // IMPORTANT: If you are using a USB WebCam, you must select CAMERA_CHOICE = BACK; and PHONE_IS_PORTRAIT = false;
    private static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;
    private static final boolean PHONE_IS_PORTRAIT = false  ;

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY =
            "AUbzCwP/////AAABmbf8lVGV50gKh7hv59eI3JQCXgBCGxkIDkp1CU2L7prKbjkbN08TcMn1OwKUNp2/2lSPSizaWIxYJlJ8iBcqvsibP5c+irEhGlqHtjYTR3TVLMVyOr4y1arszOyaPDztZUBUr5IMbfOXm4iC8MG2sDFvWQGv8vIIuthnJ19oKlXYRaVmt5dwBo2StzpMe0g9xEIh3SaB+xfb+b2W3yom6Z5jNd9NfVTToeP6vR1ubW/h9+OGu82ybfV0S0pNuRj81qwhYhhAvwaFIFoDR1vG3KVY0QGPKOjP+lyuXToCc1MFKA8SAhk+9BssevFLw5bwO9rspSdw/VtEFKMF70eiS7nYgH2dJyAcp9wQHdytf6E5";

    // Since ImageTarget trackables use mm to specifiy their dimensions, we must use mm for all the physical dimension.
    // We will define some constants and conversions here
    private static final float mmPerInch        = 25.4f;
    private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor

    // Constants for perimeter targets
    private static final float halfField = 72 * mmPerInch;
    private static final float quadField  = 36 * mmPerInch;

    // Class Members
    private OpenGLMatrix lastLocation = null;
    private VuforiaLocalizer vuforia = null;

    /**
     * This is the webcam we are to use. As with other hardware devices such as motors and
     * servos, this device is identified using the robot configuration tool in the FTC application.
     */
    WebcamName webcamName = null;

    private boolean targetVisible = false;
    private float phoneXRotate    = 0;
    private float phoneYRotate    = 0;
    private float phoneZRotate    = 0;

    // called when init button is pressed
    @Override
    public void runOpMode() throws InterruptedException
    {
        // get references to hardware components
        leftMotor = hardwareMap.get(DcMotorEx.class,"LeftDrive");
        rightMotor = hardwareMap.get(DcMotorEx.class,"RightDrive");
        middleMotor = hardwareMap.get(DcMotorEx.class,"MiddleDrive");
        shooter = hardwareMap.get(DcMotorEx.class,"Shooter");
        intake = hardwareMap.get(DcMotorEx.class,"Intake");
        roller = hardwareMap.get(DcMotorEx.class,"Roller");
        gate = hardwareMap.get(Servo.class,"Gate");
        feeder = hardwareMap.get(Servo.class,"Feeder");

        // unless disabled, set PIDF coefficients for drive motors
        if (useCustomPIDF) {
            // these values were calculated using a maximum velocity value of XXXX, as measured on mm/dd/yyyy
            leftMotor.setVelocityPIDFCoefficients(dashPID_Vleft.p, dashPID_Vleft.i, dashPID_Vleft.d, dashPID_Vleft.f);
            rightMotor.setVelocityPIDFCoefficients(dashPID_Vright.p, dashPID_Vright.i, dashPID_Vright.d, dashPID_Vright.f);
            middleMotor.setVelocityPIDFCoefficients(dashPID_Vmiddle.p, dashPID_Vmiddle.i, dashPID_Vmiddle.d, dashPID_Vmiddle.f);
            leftMotor.setPositionPIDFCoefficients(dashPID_Pleft.p);
            rightMotor.setPositionPIDFCoefficients(dashPID_Pright.p);
            middleMotor.setPositionPIDFCoefficients(dashPID_Pmiddle.p);
        }

        // You will need to set this based on your robot's
        // gearing to get forward control input to result in
        // forward motion.
        leftMotor.setDirection(DcMotor.Direction.REVERSE);

        // set all encoder counts to zero (target position must be set before RUN_TO_POSITION mode can be set)
        leftMotor.setTargetPosition(0);
        rightMotor.setTargetPosition(0);
        middleMotor.setTargetPosition(0);
        // set motors to run to target encoder position and stop with brakes on
        leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        middleMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // declare worker class(es)
        AutonomousWorkerMethods workers = new AutonomousWorkerMethods();

        // initialize FtcDashboard
        dashboard = FtcDashboard.getInstance();

        // declare dashboard telelmetry
        TelemetryPacket packet = new TelemetryPacket();

        /*
         * Retrieve the camera we are to use.
         */
        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");

        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         * We can pass Vuforia the handle to a camera preview resource (on the RC phone);
         * If no camera monitor is desired, use the parameter-less constructor instead (commented out below).
         */
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;

        /**
         * We also indicate which camera on the RC we wish to use.
         */
        parameters.cameraName = webcamName;

        // Make sure extended tracking is disabled for this example.
        parameters.useExtendedTracking = false;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Load the data sets for the trackable objects. These particular data
        // sets are stored in the 'assets' part of our application.
        VuforiaTrackables targetsUltimateGoal = this.vuforia.loadTrackablesFromAsset("UltimateGoal");
        VuforiaTrackable blueTowerGoalTarget = targetsUltimateGoal.get(0);
        blueTowerGoalTarget.setName("Blue Tower Goal Target");
        VuforiaTrackable redTowerGoalTarget = targetsUltimateGoal.get(1);
        redTowerGoalTarget.setName("Red Tower Goal Target");
        VuforiaTrackable redAllianceTarget = targetsUltimateGoal.get(2);
        redAllianceTarget.setName("Red Alliance Target");
        VuforiaTrackable blueAllianceTarget = targetsUltimateGoal.get(3);
        blueAllianceTarget.setName("Blue Alliance Target");
        VuforiaTrackable frontWallTarget = targetsUltimateGoal.get(4);
        frontWallTarget.setName("Front Wall Target");

        // For convenience, gather together all the trackable objects in one easily-iterable collection */
        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targetsUltimateGoal);

        /**
         * In order for localization to work, we need to tell the system where each target is on the field, and
         * where the phone resides on the robot.  These specifications are in the form of <em>transformation matrices.</em>
         * Transformation matrices are a central, important concept in the math here involved in localization.
         * See <a href="https://en.wikipedia.org/wiki/Transformation_matrix">Transformation Matrix</a>
         * for detailed information. Commonly, you'll encounter transformation matrices as instances
         * of the {@link OpenGLMatrix} class.
         *
         * If you are standing in the Red Alliance Station looking towards the center of the field,
         *     - The X axis runs from your left to the right. (positive from the center to the right)
         *     - The Y axis runs from the Red Alliance Station towards the other side of the field
         *       where the Blue Alliance Station is. (Positive is from the center, towards the BlueAlliance station)
         *     - The Z axis runs from the floor, upwards towards the ceiling.  (Positive is above the floor)
         *
         * Before being transformed, each target image is conceptually located at the origin of the field's
         *  coordinate system (the center of the field), facing up.
         */

        //Set the position of the perimeter targets with relation to origin (center of field)
        redAllianceTarget.setLocation(OpenGLMatrix
                .translation(0, -halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

        blueAllianceTarget.setLocation(OpenGLMatrix
                .translation(0, halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));
        frontWallTarget.setLocation(OpenGLMatrix
                .translation(-halfField, 0, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , 90)));

        // The tower goal targets are located a quarter field length from the ends of the back perimeter wall.
        blueTowerGoalTarget.setLocation(OpenGLMatrix
                .translation(halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , -90)));
        redTowerGoalTarget.setLocation(OpenGLMatrix
                .translation(halfField, -quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

        //
        // Create a transformation matrix describing where the phone is on the robot.
        //
        // NOTE !!!!  It's very important that you turn OFF your phone's Auto-Screen-Rotation option.
        // Lock it into Portrait for these numbers to work.
        //
        // Info:  The coordinate frame for the robot looks the same as the field.
        // The robot's "forward" direction is facing out along X axis, with the LEFT side facing out along the Y axis.
        // Z is UP on the robot.  This equates to a bearing angle of Zero degrees.
        //
        // The phone starts out lying flat, with the screen facing Up and with the physical top of the phone
        // pointing to the LEFT side of the Robot.
        // The two examples below assume that the camera is facing forward out the front of the robot.

        // We need to rotate the camera around it's long axis to bring the correct camera forward.
        if (CAMERA_CHOICE == BACK) {
            phoneYRotate = -90;
        } else {
            phoneYRotate = 90;
        }

        // Rotate the phone vertical about the X axis if it's in portrait mode
        if (PHONE_IS_PORTRAIT) {
            phoneXRotate = 90 ;
        }

        // Next, translate the camera lens to where it is on the robot.
        // In this example, it is centered (left to right), but forward of the middle of the robot, and above ground level.
        final float CAMERA_FORWARD_DISPLACEMENT  = 4.0f * mmPerInch;   // eg: Camera is 4 Inches in front of robot-center
        final float CAMERA_VERTICAL_DISPLACEMENT = 8.0f * mmPerInch;   // eg: Camera is 8 Inches above ground
        final float CAMERA_LEFT_DISPLACEMENT     = 0;     // eg: Camera is ON the robot's center line

        OpenGLMatrix robotFromCamera = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));

        /**  Let all the trackable listeners know where the phone is.  */
        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(robotFromCamera, parameters.cameraDirection);
        }

        FtcDashboard.getInstance().startCameraStream(vuforia, 0);

        // send telemetry to Driver Station using standard SDK interface
        telemetry.addData("Mode", "waiting");
        telemetry.update();
        // send same telemetry to dashboard using packet interface
        packet.put("Mode", "waiting");
        dashboard.sendTelemetryPacket(packet);

        // wait for start button to be pressed
        waitForStart();

        telemetry.addData("Mode", "running");
        telemetry.update();
        // send same telemetry to dashboard using packet interface
        packet.put("Mode", "running");
        dashboard.sendTelemetryPacket(packet);

        // reset encoder counts kept by motors.
        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        middleMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    // Clockwise
    // Rectangle side cw1

        // send robot forward to specified encoder counts
        leftMotor.setTargetPosition(1500);
        rightMotor.setTargetPosition(1500);
        middleMotor.setTargetPosition(0);

        // Set motors to appropriate power levels, movement will start. Sign of power is
        //  ignored since sign of target encoder position controls direction when
        //  running to position.
        leftMotor.setVelocity(motorVelocity);
        rightMotor.setVelocity(motorVelocity);
        middleMotor.setVelocity(0.0);          // TODO will setting this above zero help to immobilize left/right motion?

        // wait while opmode is active and motor is busy running to position
        while (opModeIsActive() && leftMotor.isBusy())   //leftMotor.getCurrentPosition() < leftMotor.getTargetPosition())
        {
            workers.telemeterEncoderPositions();
        }

        // set motor power to zero to turn off motors. The motors stop on their own but
        //  power is still applied so we turn off the power.

        // Commands to set power to zero are now commented out,
        //  because this prevented breaking action and allowed the robot to coast.
        // leftMotor.setPower(0.0);
        // rightMotor.setPower(0.0);

        // unless disabled, wait 5 sec so you can observe the final encoder position
        resetStartTime();
        while (pauseateachcorner && opModeIsActive() && getRuntime() < 5)
        {
            workers.telemeterEncoderPositions();
        }

    // Rectangle side cw2
        // send robot right to specified encoder counts
        leftMotor.setTargetPosition(1500);
        rightMotor.setTargetPosition(1500);
        middleMotor.setTargetPosition(1500);

        // Set motors to appropriate power levels, movement will start. Sign of power is
        //  ignored since sign of target encoder position controls direction when
        //  running to position.
        leftMotor.setVelocity(0.0);            // TODO will setting these above zero help to immobilize forward/back motion?
        rightMotor.setVelocity(0.0);
        middleMotor.setVelocity(motorVelocity);

        // wait while opmode is active and motor is busy running to position.
        while (opModeIsActive() && middleMotor.isBusy())   //middleMotor.getCurrentPosition() < middleMotor.getTargetPosition())
        {
            workers.telemeterEncoderPositions();
        }

        // unless disabled, wait 5 sec so you can observe the final encoder position
        resetStartTime();
        while (pauseateachcorner && opModeIsActive() && getRuntime() < 5)
        {
            workers.telemeterEncoderPositions();
        }

    // Rectangle side cw3

        // send robot back to specified encoder counts
        leftMotor.setTargetPosition(0);
        rightMotor.setTargetPosition(0);
        middleMotor.setTargetPosition(1500);

        // Set motors to appropriate power levels, movement will start. Sign of power is
        //  ignored since sign of target encoder position controls direction when
        //  running to position.
        leftMotor.setVelocity(motorVelocity);
        rightMotor.setVelocity(motorVelocity);
        middleMotor.setVelocity(0.0);

        // wait while opmode is active and motor is busy running to position
        while (opModeIsActive() && leftMotor.isBusy())   //leftMotor.getCurrentPosition() < leftMotor.getTargetPosition())
        {
            workers.telemeterEncoderPositions();
        }

        // unless disabled, wait 5 sec so you can observe the final encoder position
        resetStartTime();
        while (pauseateachcorner && opModeIsActive() && getRuntime() < 5)
        {
            workers.telemeterEncoderPositions();
        }

    // Rectangle side cw4

        // send robot left to specified encoder counts
        leftMotor.setTargetPosition(0);
        rightMotor.setTargetPosition(0);
        middleMotor.setTargetPosition(0);

        // Set motors to appropriate power levels, movement will start. Sign of power is
        //  ignored since sign of target encoder position controls direction when
        //  running to position.
        leftMotor.setVelocity(0.0);
        rightMotor.setVelocity(0.0);
        middleMotor.setVelocity(motorVelocity);

        // wait while opmode is active and motor is busy running to position.
        while (opModeIsActive() && middleMotor.isBusy())   //middleMotor.getCurrentPosition() < middleMotor.getTargetPosition())
        {
            workers.telemeterEncoderPositions();
        }

        // unless disabled, wait 5 sec so you can observe the final encoder position
        resetStartTime();
        while (pauseateachcorner && opModeIsActive() && getRuntime() < 5)
        {
            workers.telemeterEncoderPositions();
        }
    }
}
