package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx; // extended version of DcMotor class... this one is muchh better
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Servo.Direction;

public class Configuration {
    //this class makes sense so you can have one set config. This is super useful if you want to change the direction of a motor or something.
    public DcMotorEx frontRight;
    public DcMotorEx backRight;
    public DcMotorEx frontLeft;
    public DcMotorEx backLeft;
    DcMotorEx leftShooter;
    DcMotorEx rightShooter;
    DcMotorEx intakeMotor;
    DcMotorEx transferMotor;
    Servo tServoNectar;
    Servo tServoPollen;
    Servo bServoLeft;
    Servo bServoRight;
    Servo shootServo;
    ColorSensor colorLeft;
    DistanceSensor rangeLeft;
    ColorSensor colorMid;
    DistanceSensor rangeMid;
    ColorSensor colorRight;
    DistanceSensor rangeRight;
    Limelight3A limelight;

    public Configuration(HardwareMap hardwareMap) {
        // define the motors and stuff
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");
        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        leftShooter = hardwareMap.get(DcMotorEx.class, "leftShooter");
        rightShooter = hardwareMap.get(DcMotorEx.class, "rightShooter");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        transferMotor = hardwareMap.get(DcMotorEx.class, "transferMotor");
        tServoNectar = hardwareMap.get(Servo.class, "tServoNectar");
        tServoPollen = hardwareMap.get(Servo.class, "tServoPollen");
        bServoLeft = hardwareMap.get(Servo.class, "bServoLeft");
        bServoRight = hardwareMap.get(Servo.class, "bServoRight");
        shootServo = hardwareMap.get(Servo.class, "shootServo"); //we good on hardware
        colorLeft = hardwareMap.get(ColorSensor.class, "colorLeft");
        rangeLeft = hardwareMap.get(DistanceSensor.class, "colorLeft");
        colorMid = hardwareMap.get(ColorSensor.class, "colorMiddle");
        rangeMid = hardwareMap.get(DistanceSensor.class, "colorMiddle");
        colorRight = hardwareMap.get(ColorSensor.class, "colorRight");
        rangeRight = hardwareMap.get(DistanceSensor.class, "colorRight");
        limelight = hardwareMap.get(Limelight3A.class, "Limelight 3A");


        //I like to set the direction to forwards, even though it already does that, so that I can visualize what motors are doing what
        frontRight.setDirection(DcMotorEx.Direction.FORWARD);
        backRight.setDirection(DcMotorEx.Direction.FORWARD);
        frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        backLeft.setDirection(DcMotorEx.Direction.REVERSE);
        leftShooter.setDirection(DcMotorEx.Direction.REVERSE);
        rightShooter.setDirection(DcMotorEx.Direction.FORWARD);
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        transferMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        tServoNectar.setDirection(Direction.REVERSE);
        bServoLeft.setDirection(Direction.REVERSE);
        bServoRight.setDirection(Direction.FORWARD);
        tServoPollen.setDirection(Direction.FORWARD);
        shootServo.setDirection(Direction.REVERSE);

        //Brake or float, you should usually use brake. This means that the motor will not go limp, but instead try to resist movement.
        frontRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        //shooterLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE); // shooter doesn't need to brake
        //shooterRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE); // shooter doesn't need to brake
        //intakeMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        //liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE); // it's countersprung
    }
}