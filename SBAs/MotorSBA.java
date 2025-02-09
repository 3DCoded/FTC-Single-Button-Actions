package org.firstinspires.ftc.teamcode.SBAs;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class MotorSBA implements SBA {
    private DcMotorEx motor;
    private double power;
    private int targetPos;
    private int tolerance;

    private int minPos = 0;
    private int maxPos = 0;
    private boolean useBounds;

    public MotorSBA(DcMotorEx motor, double power, int targetPos) {
        this.motor = motor;
        this.power = power;
        tolerance = 10;
        this.targetPos = targetPos;
        useBounds = false;
    }

    public MotorSBA(DcMotorEx motor, double power, int targetPos, int tolerance) {
        this.motor = motor;
        this.power = power;
        this.targetPos = targetPos;
        this.tolerance = tolerance;
        useBounds = false;
    }

    public MotorSBA(DcMotorEx motor, double power, int targetPos, int minPos, int maxPos) {
        this.motor = motor;
        this.power = power;
        this.targetPos = targetPos;
        useBounds = true;
        this.minPos = minPos;
        this.maxPos = maxPos;
    }

    public boolean sanity() {
        // If no bounds are set, no sanity check is required
        if (!useBounds) { return true; }

        // If bounds are set, ensure motor current pos is within bounds
        int curPos = motor.getCurrentPosition();
        return curPos > minPos && curPos < maxPos;
    }

    public void init() {
        // Set motor target position
        motor.setPower(power);
        motor.setTargetPosition(targetPos);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void loop() { }

    public boolean isBusy() {
        int curPos = motor.getCurrentPosition();
        // Calculate difference from current to target positions
        int error = Math.abs(curPos - targetPos);
        // If error is less than tolerance, continue on to next operation
        return error > tolerance;
    }
}
