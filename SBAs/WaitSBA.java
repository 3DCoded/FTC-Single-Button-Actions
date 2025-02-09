package org.firstinspires.ftc.teamcode.SBAs;

public class WaitSBA implements SBA {
    public double waitMs;
    private double startMs = -1;

    public WaitSBA(double waitMs) {
        this.waitMs = waitMs;
    }

    public boolean sanity() { return true; }

    public void init() { 
        startMs = System.currentTimeMillis();
    }

    public void loop() {}

    public boolean isBusy() {
        // Compare current time to start time
        double curTimeInMillis = System.currentTimeMillis();
        double timeDiff = curTimeInMillis - startMs;
        // Check if waitMs has passed
        return timeDiff < waitMs;
    }
}
