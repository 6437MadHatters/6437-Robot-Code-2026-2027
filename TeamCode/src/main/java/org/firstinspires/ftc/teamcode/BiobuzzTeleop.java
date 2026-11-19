package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.annotations.ServoType;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.teamcode.BotState.botState;

import java.util.concurrent.TimeUnit;


@TeleOp
public class BiobuzzTeleop extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor fLMotor = null;
    private DcMotor fRMotor = null;
    private DcMotor bLMotor = null;
    private DcMotor bRMotor = null;
    private DcMotorEx lSlide = null;
    private DcMotorEx iMotor = null;

    private Servo bservo1 = null;

    private Servo bservo2 = null;

    private boolean Brake = false;
    
    private boolean isSlowMode = false;
    
    private boolean wasDpadUpPressed = false;

    // these variables are for headless mode, I don't really understand it, I stole them from bubbles which he stole from 3189
    double driveTurn;
    double driveVertical;
    double driveHorizontal;
    double gamepadX;
    double gamepadY;
    double gamepadHypot;
    double gamepadDegree;
    double robotDegree;
    double movementDegree;
    double gamepadXControl;
    double gamepadYControl;

    // whether or not headless mode is on
    boolean headlessMode = false;

    //Transfer servos
    private Servo pollenTServo = null;
    private Servo nectarTServo = null;
    // --- INTEGRATED HIGH-SPEED DUAL SHOOTER MEMBERS ---
    private DcMotorEx leftShooter;
    private DcMotorEx rightShooter;

    // --- POLLEN WHEEL TUNED COEFFICIENTS ---
    public static double LEFT_P = 0.0075, LEFT_I = 0.00000246, LEFT_D = 0.0001, LEFT_F = 0.00058855;

    // --- NECTAR WHEEL TUNED COEFFICIENTS ---
    public static double RIGHT_P = 0.005, RIGHT_I = 0.0, RIGHT_D = 0.0, RIGHT_F = 0.0004535;

    // Split velocity targets for distinct tuning control profiles
    public static double TARGET_VELOCITY_NECTAR = 1350;
    public static double TARGET_VELOCITY_POLLEN = 1350;

    // Toggle button tracking flags
    private boolean shooterOn = false;

    double leftStick = 0; // variable to change heading and keep the april tag centered when firing

    // class variables.
    Configuration config;
    Methods method;
    BotState state;


    @Override
    public void runOpMode() {

        // initialize ALL other classes with the proper class argument. !!!!! IMPORTANT
        config = new Configuration(hardwareMap);
        method = new Methods(config);
        state = new BotState(config, method, telemetry);

        //Get the hardware from ftc import
        fLMotor = hardwareMap.get(DcMotor.class, "fLMotor");
        fRMotor = hardwareMap.get(DcMotor.class, "fRMotor");
        bLMotor = hardwareMap.get(DcMotor.class, "bLMotor");
        bRMotor = hardwareMap.get(DcMotor.class, "bRMotor");
        /*bservo1 = hardwareMap.get(Servo.class, "bservo1");
        bservo2 = hardwareMap.get(Servo.class, "bservo2");
        iMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        pollenTServo = hardwareMap.get(Servo.class, "PollenTServo");
        nectarTServo = hardwareMap.get(Servo.class, "NectarTServo");*/

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        //lastState tracks what state the bot is in
        BotState.botState lastState = BotState.botState.idle;

        // define all the button objects that will use the button class
        // gamepad 1
        Button gamepad1A = new Button(gamepad1.a);
        Button gamepad1B = new Button(gamepad1.b);
        Button gamepad1X = new Button(gamepad1.x);
        Button gamepad1Y = new Button(gamepad1.y);
        Button gamepad1Dpad_Up = new Button(gamepad1.dpad_up);
        Button gamepad1Dpad_Down = new Button(gamepad1.dpad_down);
        Button gamepad1Dpad_Left = new Button(gamepad1.dpad_left);
        Button gamepad1Dpad_Right = new Button(gamepad1.dpad_right);
        Button gamepad1_right_stick_button = new Button(gamepad1.right_stick_button);
        Button gamepad1_left_stick_button = new Button(gamepad1.left_stick_button);
        Button gamepad1_Right_bumper = new Button(gamepad1.right_bumper);
        Button gamepad1_Left_bumper = new Button(gamepad1.left_bumper);
        // gamepad 2
        Button gamepad2A = new Button(gamepad2.a);
        Button gamepad2B = new Button(gamepad2.b);
        Button gamepad2X = new Button(gamepad2.x);
        Button gamepad2Y = new Button(gamepad2.y);
        Button gamepad2Dpad_Up = new Button(gamepad2.dpad_up);
        Button gamepad2Dpad_Down = new Button(gamepad2.dpad_down);
        Button gamepad2Dpad_Left = new Button(gamepad2.dpad_left);
        Button gamepad2Dpad_Right = new Button(gamepad2.dpad_right);
        Button gamepad2_right_stick_button = new Button(gamepad2.right_stick_button);
        Button gamepad2_left_stick_button = new Button(gamepad2.left_stick_button);
        Button gamepad2_Right_bumper = new Button(gamepad2.right_bumper);
        Button gamepad2_Left_bumper = new Button(gamepad2.left_bumper);

        // initialize the gyro (we only use it for driving)
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        // tell when init is done
        telemetry.addLine("init done");
        telemetry.update();

        waitForStart();
        // START THE TIMER WHEN ROBOT STARTS
        runtime.reset();
        long savedTime = 0;

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            //update all of the buttons
            // gamepad 1
            gamepad1A.update(gamepad1.a);
            gamepad1B.update(gamepad1.b);
            gamepad1X.update(gamepad1.x);
            gamepad1Y.update(gamepad1.y);
            gamepad1Dpad_Up.update(gamepad1.dpad_up);
            gamepad1Dpad_Down.update(gamepad1.dpad_down);
            gamepad1Dpad_Left.update(gamepad1.dpad_left);
            gamepad1Dpad_Right.update(gamepad1.dpad_right);
            gamepad1_right_stick_button.update(gamepad1.right_stick_button);
            gamepad1_left_stick_button.update(gamepad1.left_stick_button);
            gamepad1_Right_bumper.update(gamepad1.right_bumper);
            gamepad1_Left_bumper.update(gamepad1.left_bumper);
            // gamepad 2
            gamepad2A.update(gamepad2.a);
            gamepad2B.update(gamepad2.b);
            gamepad2X.update(gamepad2.x);
            gamepad2Y.update(gamepad2.y);
            gamepad2Dpad_Up.update(gamepad2.dpad_up);
            gamepad2Dpad_Down.update(gamepad2.dpad_down);
            gamepad2Dpad_Left.update(gamepad2.dpad_left);
            gamepad2Dpad_Right.update(gamepad2.dpad_right);
            gamepad2_right_stick_button.update(gamepad2.right_stick_button);
            gamepad2_left_stick_button.update(gamepad2.left_stick_button);
            gamepad2_Right_bumper.update(gamepad2.right_bumper);
            gamepad2_Left_bumper.update(gamepad2.left_bumper);

            //dummy button to set the movement speed slower
            if (gamepad1.dpad_up && !wasDpadUpPressed) {//Scale inputs by half
                isSlowMode = !isSlowMode; //toggles between true and 1
            }
            wasDpadUpPressed = gamepad1.dpad_up; //Saves the button state

            //assign the speed multiplier based on the toggle state
            double speedMultiplier = isSlowMode ? 0.5 : 1.0;

            telemetry.addData("dummy Mode Is Active", isSlowMode);
            telemetry.update();

            // INTAKE CODE
            //INtake code
            if (gamepad1.left_bumper){
                iMotor.setPower(1);
            } else {
                iMotor.setPower(0);
            }

            /*if (gamepad1.right_trigger > 0.5) {
                iMotor.setPower(-1);
            }
                else {
                    iMotor.setPower(0);
            }*/


            /*//Brake code
            if (Brake && gamepad1.left_stick_x == 0 && gamepad1.left_stick_y == 0 && gamepad2.left_stick_x == 0 && gamepad2.left_stick_y == 0) {
                sleep(250);
                bservo1.setPosition(0.23);
                bservo2.setPosition(0);

            } else {
                bservo1.setPosition(0);
                bservo2.setPosition(0.23);
                Brake = false;
            }

*/

            }

        // DRIVE !!! \/

        // headless mode toggle (right stick toggle headless, left stick reset gyro)
        telemetry.addData("Headless Mode", headlessMode);

        // SPEED
        double turnMultiplier = .9;

        //drive code, I don't really understand much.
        if (headlessMode) {
            driveTurn = -leftStick; // leftStick is gamepad1.left_stick_x
            driveHorizontal = gamepad1.right_stick_x;
            driveVertical = -gamepad1.right_stick_y;

            gamepadX = gamepad1.right_stick_x; //this simply gives our x value relative to the driver
            gamepadY = -gamepad1.right_stick_y; //this simply gives our y value relative to the driver
            gamepadHypot = Range.clip(Math.hypot(gamepadX, gamepadY), 0, 1);
            // finds just how much power to give the robot based on how much x and y given by gamepad
            // range.clip helps us keep our power within positive 1
            // also helps set maximum possible value of 1/sqrt(2) for x and y controls if at a 45 degree angle (which yields greatest possible value for y+x)

            // prevent /0 error
            if (gamepadX != 0) {
                //calculates gamepad degree with inverse tangent
                gamepadDegree = Math.toDegrees(Math.atan(gamepadY / gamepadX));
                //if stick is left, make angle left
                if (gamepadX < 0) {
                    gamepadDegree += 180;
                }
            }
            //straight up
            else if (gamepadY > 0) {
                gamepadDegree = 90;
            }
            // straight down
            else if (gamepadY < 0) {
                gamepadDegree = 270;
            }
            // gamepad has not been moved
            else {
                gamepadDegree = 0;
            }

            // RESET GYRO IF NEEDED !!!!!!!!!!!!
            if (gamepad1.left_stick_button) {
                imu.initialize(parameters);
            }

            //ANGLE MATH
            //gives us the angle our robot is at
            robotDegree = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle + 180;
            //find angle relative to robot position
            movementDegree = gamepadDegree - robotDegree;
            //by finding the adjacent side, we can find sideways movement relative to robot
            gamepadXControl = Math.cos(Math.toRadians(movementDegree)) * gamepadHypot;
            driveHorizontal = Math.cos(Math.toRadians(movementDegree)) * gamepadHypot;
            //by finding the opposite side, we can find vertical movement relative to robot
            gamepadYControl = Math.sin(Math.toRadians(movementDegree)) * gamepadHypot;
            driveVertical = Math.sin(Math.toRadians(movementDegree)) * gamepadHypot;


            //drive math
            double strafeSpeed = 1.5;
            driveTurn *= .7; // yaw speed
            config.frontRight.setPower((-driveVertical + (driveHorizontal * strafeSpeed) + driveTurn) * turnMultiplier);
            config.backRight.setPower((-driveVertical - (driveHorizontal * strafeSpeed) + driveTurn) * turnMultiplier);
            config.frontLeft.setPower((-driveVertical - (driveHorizontal * strafeSpeed) - driveTurn) * turnMultiplier);
            config.backLeft.setPower((-driveVertical + (driveHorizontal * strafeSpeed) - driveTurn) * turnMultiplier);
        } else {
            //basic mecanum code (MAKE STICK INPUTS NEGATIVE TO CHANGE ROBOT DIRECTION (RIGHT STICK ONLY!!!!)) !!!!!!!!!!!!!!!! // leftStick is gamepad1.left_stick_x
            config.frontLeft.setPower((-gamepad1.right_stick_y + (gamepad1.right_stick_x) + leftStick) * turnMultiplier); // leftStick is gamepad1.left_stick_x
            config.backLeft.setPower((-gamepad1.right_stick_y - (gamepad1.right_stick_x) + leftStick) * turnMultiplier); // leftStick is gamepad1.left_stick_x
            config.frontRight.setPower((-gamepad1.right_stick_y - (gamepad1.right_stick_x) - leftStick) * turnMultiplier); // leftStick is gamepad1.left_stick_x
            config.backRight.setPower((-gamepad1.right_stick_y + (gamepad1.right_stick_x) - leftStick) * turnMultiplier); // leftStick is gamepad1.left_stick_x
        }

        //drive code, I don't really understand much.
        if (headlessMode) {
            driveTurn = -gamepad1.left_stick_x;
            driveHorizontal = gamepad1.right_stick_x;
            driveVertical = -gamepad1.right_stick_y;

            gamepadX = gamepad1.right_stick_x; //this simply gives our x value relative to the driver
            gamepadY = -gamepad1.right_stick_y; //this simply gives our y value relative to the driver
            gamepadHypot = Range.clip(Math.hypot(gamepadX, gamepadY), 0, 1);
            //finds just how much power to give the robot based on how much x and y given by gamepad
            //range.clip helps us keep our power within positive 1
            // also helps set maximum possible value of 1/sqrt(2) for x and y controls if at a 45 degree angle (which yields greatest possible value for y+x)

            // prevent /0 error
            if (gamepadX != 0) {
                //calculates gamepad degree with inverse tangent
                gamepadDegree = Math.toDegrees(Math.atan(gamepadY / gamepadX));
                //if stick is left, make angle left
                if (gamepadX < 0) {
                    gamepadDegree += 180;
                }
            }
            //straight up
            else if (gamepadY > 0) {
                gamepadDegree = 90;
            }
            //straight down
            else if (gamepadY < 0) {
                gamepadDegree = 270;
            }
            //gamepad has not been moved
            else {
                gamepadDegree = 0;
            }

            //reset gyro if needed
            if (gamepad1.x) {
                imu.initialize(parameters);
            }
            telemetry.addData("runtime - saved time", runtime.now(TimeUnit.MILLISECONDS) - savedTime);


            //ANGLE MATH
            //gives us the angle our robot is at
            robotDegree = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
            telemetry.addData("Robot degree", robotDegree);
            //find angle relative to robot position
            movementDegree = gamepadDegree - robotDegree;
            //by finding the adjacent side, we can find sideways movement relative to robot
            gamepadXControl = Math.cos(Math.toRadians(movementDegree)) * gamepadHypot;
            driveHorizontal = Math.cos(Math.toRadians(movementDegree)) * gamepadHypot;
            //by finding the opposite side, we can find vertical movement relative to robot
            gamepadYControl = Math.sin(Math.toRadians(movementDegree)) * gamepadHypot;
            driveVertical = Math.sin(Math.toRadians(movementDegree)) * gamepadHypot;


            //drive math
            double straifSpeed = 1.5;
            driveTurn *= .5;
            config.frontRight.setPower((driveVertical - (driveHorizontal * straifSpeed) + driveTurn) * turnMultiplier);
            config.backRight.setPower((driveVertical + (driveHorizontal * straifSpeed) + driveTurn) * turnMultiplier);
            config.frontLeft.setPower((driveVertical + (driveHorizontal * straifSpeed) - driveTurn) * turnMultiplier);
            config.backLeft.setPower((driveVertical - (driveHorizontal * straifSpeed) - driveTurn) * turnMultiplier);
        } else {
            //basic mecanum code
            config.frontLeft.setPower((-gamepad1.right_stick_y + (gamepad1.right_stick_x) + gamepad1.left_stick_x));
            config.backLeft.setPower((-gamepad1.right_stick_y - (gamepad1.right_stick_x) + gamepad1.left_stick_x));
            config.frontRight.setPower((-gamepad1.right_stick_y - (gamepad1.right_stick_x) - gamepad1.left_stick_x));
            config.backRight.setPower((-gamepad1.right_stick_y + (gamepad1.right_stick_x) - gamepad1.left_stick_x));
        }

            telemetry.addData("dummy mode status", isSlowMode);
            telemetry.update();

        }
    }
