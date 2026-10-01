package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="6437 BioBuzz TeleOp", group="Linear OpMode")
//@Disabled
public class BiobuzzTeleop extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx fLMotor = null;
    private DcMotorEx fRMotor = null;
    private DcMotorEx bLMotor = null;
    private DcMotorEx bRMotor = null;
    private DcMotorEx sMotor = null;
    private DcMotorEx iMotor = null;

    private Servo bservo1 = null;

    private Servo bservo2 = null;

    private boolean ShooterOn = false;

    private boolean Brake = false;


    @Override
    public void runOpMode() {
        fLMotor = hardwareMap.get(DcMotorEx.class, "leftFront");
        fRMotor = hardwareMap.get(DcMotorEx.class, "rightFront");
        bLMotor = hardwareMap.get(DcMotorEx.class, "leftBack");
        bRMotor = hardwareMap.get(DcMotorEx.class, "rightBack");
        bservo1 = hardwareMap.get(Servo.class, "BServo1");
        bservo2 = hardwareMap.get(Servo.class, "BServo2");

        fLMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        bLMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        fLMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fRMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bLMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bRMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        //6000 rpm motor 28ppm
        sMotor = hardwareMap.get(DcMotorEx.class, "shooterMotor");

        //1620 rpm motor 103.8pp 9m
        iMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");

        sMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);


        telemetry.addData("Status", "Initialized");
        telemetry.update();


        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            //basic movement code
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;
            double ShooterVelocity = sMotor.getVelocity();

         /* if ((y == 0) && (x == 0) && (rx == 0)); {
                fLMotor.
                fRMotor.setPower(fRPower);
                bLMotor.setPower(bLPower);
                bRMotor.setPower(bRPower);
            }  scrapped brake code ^
          */
            telemetry.addLine();
            telemetry.addData(("Shooter Speed:"), (ShooterVelocity));
            double fLPower = (y + x + rx);
            double bLPower = (y - x + rx);
            double fRPower = (y - x - rx);
            double bRPower = (y + x - rx);

            if(!Brake) {
                fLMotor.setPower(fLPower);
                fRMotor.setPower(fRPower);
                bLMotor.setPower(bLPower);
                bRMotor.setPower(bRPower);
            }

            //Shooter Code

            if (gamepad1.b) {

            } else {

            }


            if (gamepad1.b) {
                ShooterOn = !ShooterOn;
                sleep(250);
            }
            if (gamepad1.right_bumper) {
                Brake = !Brake;
                sleep(250);
            }


            if (ShooterOn) {
                iMotor.setVelocity(28);
                sMotor.setVelocity(5200);
            } else {
                iMotor.setVelocity(0);
                sMotor.setVelocity(0);
            }

            //Brake code
            if (Brake && gamepad1.left_stick_x == 0 && gamepad1.left_stick_y == 0 && gamepad2.left_stick_x == 0 && gamepad2.left_stick_y == 0) {
                sleep(250);
                bservo1.setPosition(0.21);
                bservo2.setPosition(0.21);

            } else {
                bservo1.setPosition(0);
                bservo2.setPosition(0);
                Brake = false;
            }

            telemetry.update();
            telemetry.clear();
        }
    }
}