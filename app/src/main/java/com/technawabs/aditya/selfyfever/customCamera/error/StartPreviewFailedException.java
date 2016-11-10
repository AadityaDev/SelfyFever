package com.technawabs.aditya.selfyfever.customCamera.error;

public class StartPreviewFailedException extends Exception {

    public StartPreviewFailedException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " Cause: " + getCause();
    }
}
