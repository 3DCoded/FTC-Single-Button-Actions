package org.firstinspires.ftc.teamcode.SBAs;

public class SBARunner {
    public SBA[] curSBAs = new SBA[] {};
    public int curIdx = 0;
    public int initIdx = 0;

    public void runSBAs(SBA... sbas) {
        curSBAs = sbas;
        curIdx = 0;
        initIdx = -1;
    }

    public void stop() {
        runSBAs(new SBA[] {});
    }

    public void loop() {
        // Ensure we're currently running an SBA list
        if (curSBAs.length == 0 || curSBAs.length < curIdx - 1) { return; }
        SBA sba = curSBAs[curIdx];
        if (!sba.sanity()) {
            stop(); // Quit if sanity check fails
            return;
        }
        if (initIdx < curIdx) {
            sba.init();
            initIdx = curIdx;
        }
        sba.loop();
        if (!sba.isBusy()) { // Move on to next SBA if current SBA finishes
            curIdx ++;
            if (curIdx > curSBAs.length - 1) { stop(); } // End SBA list if curIdx gets past the list limit
        }
    }

    public boolean isBusy() {
        return curSBAs.length > 0;
    }
}
