package com.technawabs.aditya.selfyfever.materialCamera;

public class TimeLimitReachedException extends Exception {

    public TimeLimitReachedException() {
        super("You've reached the time limit without starting a recording.");
    }
}
