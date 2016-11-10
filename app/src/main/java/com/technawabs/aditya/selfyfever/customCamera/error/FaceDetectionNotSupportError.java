package com.technawabs.aditya.selfyfever.customCamera.error;

public class FaceDetectionNotSupportError extends Exception {

    public FaceDetectionNotSupportError(String detailMessage) {
        super(detailMessage);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}