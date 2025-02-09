package org.firstinspires.ftc.teamcode.SBAs;

public interface SBA {
    boolean sanity();
    void init();
    void loop();
    boolean isBusy();
}
