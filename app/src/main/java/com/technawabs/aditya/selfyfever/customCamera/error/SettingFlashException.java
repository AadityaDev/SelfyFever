package com.technawabs.aditya.selfyfever.customCamera.error;

public class SettingFlashException extends Exception {

    public enum Reason {
        NOT_SUPPORT,
    }

    private Reason reason;

    public SettingFlashException(Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return this.reason;
    }
}
