package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "!!! MOTOR HARDWARE TEST !!!", group = "Test")
public class MotorHardwareTest extends LinearOpMode {

    private DcMotorEx testPollen;

    @Override
    public void runOpMode() throws InterruptedException {
        // Map using the exact configuration name you have in the hub
        testPollen = hardwareMap.get(DcMotorEx.class, "sPollen");

        // CRITICAL: Break out of PID mode and run on raw voltage power
        testPollen.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Ready. Press A to run motor at 50% power.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.a) {
                // Send raw, uninterrupted power directly to the motor core
                testPollen.setPower(-1);
                telemetry.addData("Motor State", "RUNNING AT 50% RAW POWER");
            } else {
                testPollen.setPower(0.0);
                telemetry.addData("Motor State", "STOPPED");
            }

            // Read raw encoder ticks just to see if the cable works
            telemetry.addData("Current Encoder Ticks", testPollen.getCurrentPosition());
            telemetry.update();
        }
    }
}
