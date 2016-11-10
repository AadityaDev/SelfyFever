package com.technawabs.aditya.selfyfever.customCamera.error;

public class BindSurfaceFailedException extends Exception {

    public BindSurfaceFailedException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " Cause: " + getCause();
    }
}
