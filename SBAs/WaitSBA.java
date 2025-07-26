package org.firstinspires.ftc.teamcode.NextYearTesting.SBAs;

public class WaitSBA implements SBA {
    public double waitMs;
    private double startMs;

    public WaitSBA(double waitMs) {
        this.waitMs = waitMs;
    }

    public void init() {
        startMs = System.currentTimeMillis();
    }

    public void loop() { }

    public boolean isBusy() {
        // Compare current time to start time
        double curMs = System.currentTimeMillis();
        double timeDiff = curMs - startMs;
        // Check if waitMs has passed
        return timeDiff < waitMs;
    }

    public boolean sanity() { return true; }
}
