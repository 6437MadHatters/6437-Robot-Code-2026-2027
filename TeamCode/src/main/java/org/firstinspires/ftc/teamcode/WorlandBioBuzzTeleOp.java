package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
@TeleOp(name = "WorlandBioBuzzTeleOp")
public class WorlandBioBuzzTeleOp extends LinearOpMode {

    // Declare Drivetrain and Accessory Members
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor fLMotor = null;
    private DcMotor fRMotor = null;
    private DcMotor bLMotor = null;
    private DcMotor bRMotor = null;

    private DcMotor iMotor = null;

    private Servo bservo1 = null;
    private Servo bservo2 = null;

    // --- LINEAR SLIDES MOTOR DECLARATION ---
    private DcMotorEx lSlide = null;

    // Drivetrain state flags
    private boolean Brake = false;
    private boolean isSlowMode = false;
    private boolean wasDpadUpPressed = false;
    private boolean lastRightTrigger = false;

    // --- INTEGRATED HIGH-SPEED DUAL SHOOTER MEMBERS ---
    private DcMotorEx leftShooter;
    private DcMotorEx rightShooter;

    // --- LIMELIGHT SMART CAMERA MEMBER ---
    private Limelight3A limelight;

    // --- AUTO-ALIGN TUNING COEFFICIENTS ---
    // Adjust LIMELIGHT_TURNING_P if the robot spins too fast/oscillates (lower it) or too slow (raise it)
    public static double LIMELIGHT_TURNING_P = 0.03;

    // --- POLLEN WHEEL TUNED COEFFICIENTS ---
    public static double LEFT_P = 0.0075, LEFT_I = 0.00000246, LEFT_D = 0.0001, LEFT_F = 0.00058855;

    // --- NECTAR WHEEL TUNED COEFFICIENTS ---
    public static double RIGHT_P = 0.005, RIGHT_I = 0.0, RIGHT_D = 0.0, RIGHT_F = 0.0004535;

    // Split velocity targets for distinct tuning control profiles
    public static double TARGET_VELOCITY_NECTAR = 1350;
    public static double TARGET_VELOCITY_POLLEN = 1350;

    // Toggle button tracking flags
    private boolean shooterOn = false;
    private boolean lastA = false;

    private boolean rightTriggerState = false;
    private boolean leftTriggerState = false;

    // Creates the Servos for intake existence
    private Servo pservoS;
    private Servo pservoF;
    private Servo nservoS;
    private Servo nservoF;

    private boolean yState = false;
    private boolean xState = false;
    private boolean aState = false;
    private boolean bState = false;
    private boolean servoPSState = false;
    private boolean servoPFState = false;
    private boolean servoNSState = false;
    private boolean servoNFState = false;

    @Override
    public void runOpMode() throws InterruptedException {
        // Multi-telemetry maps output streams to both dashboard and standard driver station log fields
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        fLMotor = hardwareMap.get(DcMotor.class, "frontLeft");
        fRMotor = hardwareMap.get(DcMotor.class, "frontRight");
        bLMotor = hardwareMap.get(DcMotor.class, "backLeft");
        bRMotor = hardwareMap.get(DcMotor.class, "backRight");
        //bservo1 = hardwareMap.get(Servo.class, "bservo1");
        //bservo2 = hardwareMap.get(Servo.class, "bservo2");
        iMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        /*pservoS = hardwareMap.tryGet(Servo.class, "pservoS");
        pservoF = hardwareMap.tryGet(Servo.class, "pservoF");
        nservoS = hardwareMap.tryGet(Servo.class, "nservoS");
        nservoF = hardwareMap.tryGet(Servo.class, "nservoF");*/

      /*  // --- SHOOTER HARDWARE INITIALIZATION ---
        leftShooter = hardwareMap.get(DcMotorEx.class, "leftShooter");
        rightShooter = hardwareMap.get(DcMotorEx.class, "rightShooter");

        // Direction configuration updated to spin inverted direction
        leftShooter.setDirection(DcMotorEx.Direction.FORWARD);
        rightShooter.setDirection(DcMotorEx.Direction.REVERSE);

        // Kept at RUN_WITHOUT_ENCODER so your custom math does the raw calculations directly
        leftShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        ShooterPIDF leftController = new ShooterPIDF(LEFT_P, LEFT_I, LEFT_D, LEFT_F);
        ShooterPIDF rightController = new ShooterPIDF(RIGHT_P, RIGHT_I, RIGHT_D, RIGHT_F);
*/
        /*// --- LIMELIGHT HARDWARE INITIALIZATION ---
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0); // Switches to pipeline index 0 on start
*/
        // Drivetrain direction setup
        fLMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        bLMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        fLMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fRMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bLMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bRMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        /*lSlide = hardwareMap.get(DcMotorEx.class, "lSlide");

        // Reset encoder to 0 when the robot turns on (make sure slides are fully down!)
        lSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // --- SETUP SLIDES ---
        lSlide.setTargetPosition(0);
        lSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lSlide.setPower(0.85);

        //INITIALIZE SERVOS
        if (gamepad1.y){
            pservoS.setPosition(0.3);

        } else{
            pservoS.setPosition(0.0);
        }
        //when x pressed it moves servo .3
        if (gamepad1.x){
            pservoF.setPosition(0.3);
        } else {
            pservoF.setPosition(0.0);
        }

        //when b pressed it moves servo .3
        if (gamepad1.b){
            nservoS.setPosition(0.3);
        } else {
            nservoS.setPosition(0.0);
        }

        //when a pressed it moves servo .3
        if (gamepad1.a){
            nservoF.setPosition(0.3);
        } else {
            nservoF.setPosition(0.0);
        }

        // Start processing camera frames right before waiting for start
        limelight.start();*/

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {

            /*// --- LIMELIGHT TRACKING DATA & AUTO-ALIGN CALCULATIONS ---
            double tx = 0;
            boolean hasTarget = false;

            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                tx = result.getTx(); // Horizontal offset from target (-31 to 31 degrees)
                double ty = result.getTy(); // Vertical offset from target
                double ta = result.getTa(); // Target Area (% of frame)
                hasTarget = true;

                telemetry.addData("Limelight Target", "Visible");
                telemetry.addData("Limelight TX", tx);
                telemetry.addData("Limelight TY", ty);
                telemetry.addData("Limelight TA", ta);
            } else {
                telemetry.addData("Limelight Target", "No Valid Target");
            }
            */

           /* // --- SHOOTER CONTROL LOGIC WITH BUTTON A DEBOUNCING ---
            boolean currentA = gamepad1.a;
            if (currentA && !lastA) {
                shooterOn = !shooterOn;
            }
            lastA = currentA;

            double currentLeftVel = leftShooter.getVelocity();
            double currentRightVel = rightShooter.getVelocity();

            if (shooterOn) {
                // Live-update coefficients down inside active frame loops only when running to save CPU time
                leftController.setCoefficients(LEFT_P, LEFT_I, LEFT_D, LEFT_F);
                rightController.setCoefficients(RIGHT_P, RIGHT_I, RIGHT_D, RIGHT_F);

                // Left uses Pollen, Right uses Nectar
                double leftPower = leftController.calculate(TARGET_VELOCITY_POLLEN, currentLeftVel);
                double rightPower = rightController.calculate(TARGET_VELOCITY_NECTAR, currentRightVel);

                leftShooter.setPower(leftPower);
                rightShooter.setPower(rightPower);
            } else {
                leftShooter.setPower(0);
                rightShooter.setPower(0);
                leftController.reset();
                rightController.reset();
            }
*/
            // --- DRIVETRAIN PARAMETER CALCULATIONS ---
            double y = -gamepad1.right_stick_y;
            double x = gamepad1.right_stick_x;
            double rx = gamepad1.left_stick_x; // Manual turning input

          /*  // --- AUTOMATIC LIMELIGHT ALIGNMENT OVERRIDE ---
            // If button B is held down, let the camera take over the rotation speed (rx)
            if (gamepad1.b) {
                if (hasTarget) {
                    // Multiply target error (tx) by a tuning constant to scale how hard it spins
                    rx = tx * LIMELIGHT_TURNING_P;
                    telemetry.addData("Alignment Status", "Aligning Automatically...");
                } else {
                    // If B is held but no target is visible, stop turning entirely to protect the robot
                    rx = 0;
                    telemetry.addData("Alignment Status", "No Target! Stop Turning.");
                }
            } else {
                telemetry.addData("Alignment Status", "Manual Control");
            }*/

            if (gamepad1.dpad_up && !wasDpadUpPressed) {
                isSlowMode = !isSlowMode;
            }
            wasDpadUpPressed = gamepad1.dpad_up;

            double speedMultiplier = isSlowMode ? 0.5 : 1.0;

            double fLPower = (y + x + rx) * speedMultiplier;
            double bLPower = (y - x + rx) * speedMultiplier;
            double fRPower = (y - x - rx) * speedMultiplier;
            double bRPower = (y + x - rx) * speedMultiplier;

            // --- DRIVE MOTOR POWER DEPLOYMENT ---
            if (!Brake) {
                fLMotor.setPower(fLPower);
                fRMotor.setPower(fRPower);
                bLMotor.setPower(bLPower);
                bRMotor.setPower(bRPower);
            } else {
                fLMotor.setPower(0);
                fRMotor.setPower(0);
                bLMotor.setPower(0);
                bRMotor.setPower(0);
            }

            //INTAKE CODE

            //INtake code
            if (gamepad1.left_bumper){
                iMotor.setPower(-1.0);
            } else {
                iMotor.setPower(0.0);
            }

            //OUT TAKE CODE
            //OUTtake
            /*if (gamepad1.left_trigger > 0.3){
                boolean leftTriggerState = true;
            } else {
                boolean leftTriggerState = false;
            }

            if (gamepad1.left_bumper == false && leftTriggerState){
                iMotor.setPower(1);
            } else {
                iMotor.setPower(0.0);
            }*/



            /*// HELD BRAKE
            if (gamepad1.right_trigger > 0.3){
                boolean rightTriggerState = true;
            } else {
                boolean rightTriggerState = false;
            }

            if (rightTriggerState) {
                Brake = !Brake;
                //sleep(250);    Maybe need to apply but im not totally sure
            }


            //Brake code
            if (Brake && gamepad1.left_stick_x == 0 && gamepad1.left_stick_y == 0 && gamepad2.left_stick_x == 0 && gamepad2.left_stick_y == 0) {
                sleep(250);
                bservo1.setPosition(0.23);
                bservo2.setPosition(0.23);

            } else {
                bservo1.setPosition(0);
                bservo2.setPosition(0.0);
                Brake = false;
            }




            //LINEAR SLIDE CODE
            if (gamepad1.right_bumper){
                lSlide.setTargetPosition(1500);
            } else {
                lSlide.setTargetPosition(0);
            }*/


        }
        telemetry.addData("dummy mode status", isSlowMode);
        telemetry.update();
    }
}