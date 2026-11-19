package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "autoTestFun")
public class autoTestFun extends LinearOpMode {

    private DcMotor fLMotor = null;
    private DcMotor fRMotor = null;
    private DcMotor bLMotor = null;
    private DcMotor bRMotor = null;
    private Servo bservo1 = null;
    private Servo bservo2 = null;

    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        fLMotor = hardwareMap.get(DcMotor.class, "leftFront");
        fRMotor = hardwareMap.get(DcMotor.class, "rightFront");
        bLMotor = hardwareMap.get(DcMotor.class, "leftBack");
        bRMotor = hardwareMap.get(DcMotor.class, "rightBack");
        bservo1 = hardwareMap.get(Servo.class, "bservo1");
        bservo2 = hardwareMap.get(Servo.class, "bservo2");

        // Drivetrain direction setup
        fLMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        bLMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        fLMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fRMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bLMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bRMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // --- DRIVETRAIN PARAMETER CALCULATIONS ---
        double y = 1.0;
        double x = 0.0;
        double rx = 0.0;

        double fLPower = (y + x + rx);
        double bLPower = (y - x + rx);
        double fRPower = (y - x - rx);
        double bRPower = (y + x - rx);

        // Normalize powers so they don't exceed 1.0
        double max = Math.max(Math.abs(fLPower), Math.max(Math.abs(bLPower),
                Math.max(Math.abs(fRPower), Math.abs(bRPower))));
        if (max > 1.0) {
            fLPower /= max;
            bLPower /= max;
            fRPower /= max;
            bRPower /= max;
        }

        if (opModeIsActive()) {
            // TURN MOTORS ON
            fLMotor.setPower(fLPower);
            bLMotor.setPower(bLPower);
            fRMotor.setPower(fRPower);
            bRMotor.setPower(bRPower);

            // RUN FOR 1 SECOND
            sleep(600);

            // STOP THE ROBOT
            fLMotor.setPower(0);
            bLMotor.setPower(0);
            fRMotor.setPower(0);
            bRMotor.setPower(0);
        }
    }
}

