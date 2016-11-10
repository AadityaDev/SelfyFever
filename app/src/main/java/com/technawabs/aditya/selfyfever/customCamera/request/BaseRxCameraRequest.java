package com.technawabs.aditya.selfyfever.customCamera.request;

import com.technawabs.aditya.selfyfever.customCamera.RxCamera;
import com.technawabs.aditya.selfyfever.customCamera.RxCameraData;

import rx.Observable;

public abstract class BaseRxCameraRequest {

    protected RxCamera rxCamera;

    public BaseRxCameraRequest(RxCamera rxCamera) {
        this.rxCamera = rxCamera;
    }

    public abstract Observable<RxCameraData> get();
}
