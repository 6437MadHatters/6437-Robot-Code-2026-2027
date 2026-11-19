package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

public class ShooterPIDF {
    private double kP, kI, kD, kF;
    private double integralSum = 0;
    private double lastError = 0;

    private ElapsedTime timer = new ElapsedTime();
    private boolean hasRun = false;

    public ShooterPIDF(double kP, double kI, double kD, double kF) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
    }

    public void setCoefficients(double kP, double kI, double kD, double kF) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
    }

    public double calculate(double targetVelocity, double currentVelocity) {
        double dt = timer.seconds();

        if (!hasRun || dt <= 0.0001) {
            hasRun = true;
            timer.reset();
            double error = targetVelocity - currentVelocity;
            lastError = error;
            return targetVelocity * kF;
        }

        double error = targetVelocity - currentVelocity;
        double derivative = (error - lastError) / dt;

        if (Math.abs(error) < (targetVelocity * 0.25)) {
            integralSum += error * dt;
        } else {
            integralSum = 0;
        }

        lastError = error;
        timer.reset();

        double output = (kP * error) + (kI * integralSum) + (kD * derivative) + (targetVelocity * kF);
        return Math.max(-1.0, Math.min(1.0, output));
    }

    public void reset() {
        integralSum = 0;
        lastError = 0;
        hasRun = false;
    }
}
