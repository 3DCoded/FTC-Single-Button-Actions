package org.firstinspires.ftc.teamcode.SBAs;

import static org.firstinspires.ftc.teamcode.Utils.GlobalBot.bot;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.Utils.DistanceReader;

@Config
public class DistanceSBA implements SBA {
    public double target;
    public double tolerance;

    public static double farPower = 0.4;
    public static double midPower = 0.2;
    public static double nearPower = 0.15;

    public static double farThreshold = 8;
    public static double nearThreshold = 4;

    public double curPower = 0;

    public DistanceSBA(double target, double tolerance) {
        this.target = target;
        this.tolerance = tolerance;
    }

    public boolean run() {
        double curDist = DistanceReader.read();

        // Drive the robot forward and backwards based on distance to the submersible

        double error = target - curDist;
        double absError = Math.abs(error);
        double power;

        if (absError > farThreshold) {
            power = farPower;
        } else if (absError > nearThreshold) {
            power = midPower;
        } else {
            power = nearPower;
        }

        power = -Math.copySign(power, error);
        curPower = power;

        bot.moveBotMecanum(power, 0, 0, 1);

        if (target < 0.1 || tolerance < 0.01) { return false; }
        return (Math.abs(DistanceReader.read() - target) > tolerance);
    }

    public boolean sanity() { return true; }
}
