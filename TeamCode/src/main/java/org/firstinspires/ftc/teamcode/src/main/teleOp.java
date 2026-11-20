package org.firstinspires.ftc.teamcode.src.main;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.teamcode.BotState;
import org.firstinspires.ftc.teamcode.Button;
import org.firstinspires.ftc.teamcode.Configuration;
import org.firstinspires.ftc.teamcode.Methods;

import java.util.concurrent.TimeUnit;

@TeleOp
public class teleOp extends LinearOpMode {


    // DRIVE TRAIN YOU WILL ALWAYS NEED
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor fLMotor = null;
    private DcMotor fRMotor = null;
    private DcMotor bLMotor = null;
    private DcMotor bRMotor = null;
    double leftStick = 0; // variable to change heading and keep the april tag centered when firing

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

    //Now Declare the members that are based on each challenge
    private DcMotorEx intakeMotor = null;
    private Servo bservo1 = null;
    private Servo bservo2 = null;
    private boolean Brake = false;
    private boolean isSlowMode = false;
    private boolean wasDpadUpPressed = false;
    //Transfer servos
    private Servo pollenTServo = null;
    private Servo nectarTServo = null;

    //SHOOTER STUFF
    private DcMotorEx leftShooter;
    private DcMotorEx rightShooter;
    public static double LEFT_P = 0.0075, LEFT_I = 0.00000246, LEFT_D = 0.0001, LEFT_F = 0.00058855; //Pollen PID tuning variables
    public static double RIGHT_P = 0.005, RIGHT_I = 0.0, RIGHT_D = 0.0, RIGHT_F = 0.0004535; //Nectar tuning variables
    // Split velocity targets for distinct tuning control profiles
    public static double TARGET_VELOCITY_NECTAR = 1350;
    public static double TARGET_VELOCITY_POLLEN = 1350;
    // Toggle button tracking flags
    private boolean shooterOn = false;
    // class variables.
    Configuration config; //Change the Configuration java class to align with your challenge
    Methods method; //Change the Method java class to align with your challenge
    BotState state; //Change the BotState java class to align with your challenge


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
        bRMotor = hardwareMap.get(DcMotor.class, "bRMotor"); //After all this add your challenge specific motors and servos

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        //lastState tracks what state the bot is in
        BotState.botState lastState = BotState.botState.idle;


        //This is not completely neccecary but recomended
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
//random change
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
            //This is not completely neccecary but recomended
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

        }

    }
}
