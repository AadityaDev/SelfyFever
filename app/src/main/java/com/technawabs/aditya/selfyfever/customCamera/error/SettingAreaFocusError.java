package com.technawabs.aditya.selfyfever.customCamera.error;

public class SettingAreaFocusError extends Exception {

    public enum Reason {
        NOT_SUPPORT,
        SET_AREA_FOCUS_FAILED
    }

    private Reason reason;

    public SettingAreaFocusError(Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return this.reason;
    }
}