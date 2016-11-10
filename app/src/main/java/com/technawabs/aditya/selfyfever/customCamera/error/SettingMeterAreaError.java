package com.technawabs.aditya.selfyfever.customCamera.error;

public class SettingMeterAreaError extends Exception {

    public enum Reason {
        NOT_SUPPORT,
    }

    private Reason reason;

    public SettingMeterAreaError(Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return this.reason;
    }
}
