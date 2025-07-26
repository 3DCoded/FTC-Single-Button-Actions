package org.firstinspires.ftc.teamcode.NextYearTesting.SBAs;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

public class SBAAction implements Action {
    private boolean initialized = false;
    private SBA sba;

    public SBAAction(SBA sba) {
        this.sba = sba;
    }

    @Override
    public boolean run(@NonNull TelemetryPacket packet) {
        if (!initialized) {
            if (!sba.sanity()) {
                return true;
            }
            sba.init();
        }

        sba.loop();
        return !sba.isBusy();
    }
}
