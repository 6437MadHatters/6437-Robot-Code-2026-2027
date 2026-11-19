package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Config
@TeleOp(name = "DualShooterPID")
public class DualShooterPIDOpMode extends LinearOpMode {

    private DcMotorEx leftShooter;
    private DcMotorEx rightShooter;

    // Adjustable coefficients via FTC Dashboard
    public static double LEFT_P = 0.005, LEFT_I = 0.0, LEFT_D = 0.0, LEFT_F = 0.00057;
    public static double RIGHT_P = 0.0035, RIGHT_I = 0.0, RIGHT_D = 0.00004, RIGHT_F = 0.00039;
    public static double TARGET_VELOCITY_NECTAR = 1200;

    public static double TARGET_VELOCITY_POLLEN = 1180;


    // Toggle button tracking flags
    private boolean shooterOn = false;
    private boolean lastA = false;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        leftShooter = hardwareMap.get(DcMotorEx.class, "leftShooter");
        rightShooter = hardwareMap.get(DcMotorEx.class, "rightShooter");

        // Direction configuration updated to spin inverted direction
        leftShooter.setDirection(DcMotorEx.Direction.FORWARD);
        rightShooter.setDirection(DcMotorEx.Direction.REVERSE);

        leftShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        ShooterPIDF leftController = new ShooterPIDF(LEFT_P, LEFT_I, LEFT_D, LEFT_F);
        ShooterPIDF rightController = new ShooterPIDF(RIGHT_P, RIGHT_I, RIGHT_D, RIGHT_F);

        waitForStart();

        while (opModeIsActive()) {
            leftController.setCoefficients(LEFT_P, LEFT_I, LEFT_D, LEFT_F);
            rightController.setCoefficients(RIGHT_P, RIGHT_I, RIGHT_D, RIGHT_F);

            // Toggle state tracking logic
            boolean currentA = gamepad1.a;
            if (currentA && !lastA) {
                shooterOn = !shooterOn;
            }
            lastA = currentA;

            double currentLeftVel = leftShooter.getVelocity();
            double currentRightVel = rightShooter.getVelocity();

            double leftPower = leftController.calculate(TARGET_VELOCITY_POLLEN, currentLeftVel);
            double rightPower = rightController.calculate(TARGET_VELOCITY_NECTAR, currentRightVel);

            // Controller loop based on the active button state
            if (shooterOn) {
                leftShooter.setPower(leftPower);
                rightShooter.setPower(rightPower);
            } else {
                leftShooter.setPower(0);
                rightShooter.setPower(0);
                leftController.reset();
                rightController.reset();
            }

            telemetry.addData("P_Target Vel", TARGET_VELOCITY_POLLEN);
            telemetry.addData("N_Target Vel", TARGET_VELOCITY_NECTAR);
            telemetry.addData("1_Left Current Vel", currentLeftVel);
            telemetry.addData("2_Right Current Vel", currentRightVel);
            telemetry.addData("Shooter Status", shooterOn ? "RUNNING" : "STOPPED");
            telemetry.update();
        }
    }
}
