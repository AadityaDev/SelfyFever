package com.technawabs.aditya.selfyfever.customCamera.request;

import com.technawabs.aditya.selfyfever.customCamera.OnRxCameraPreviewFrameCallback;
import com.technawabs.aditya.selfyfever.customCamera.RxCamera;
import com.technawabs.aditya.selfyfever.customCamera.RxCameraData;
import com.technawabs.aditya.selfyfever.customCamera.error.CameraDataNullException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class TakeOneShotRequest extends BaseRxCameraRequest implements OnRxCameraPreviewFrameCallback {

    private Subscriber<? super RxCameraData> subscriber = null;

    public TakeOneShotRequest(RxCamera rxCamera) {
        super(rxCamera);
    }

    @Override
    public Observable<RxCameraData> get() {
        return Observable.create(new Observable.OnSubscribe<RxCameraData>() {
            @Override
            public void call(Subscriber<? super RxCameraData> subscriber) {
                TakeOneShotRequest.this.subscriber = subscriber;
            }
        }).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                rxCamera.installOneShotPreviewCallback(TakeOneShotRequest.this);
            }
        });
    }

    @Override
    public void onPreviewFrame(byte[] data) {
        if (subscriber != null && !subscriber.isUnsubscribed() && rxCamera.isOpenCamera()) {
            if (data == null || data.length == 0) {
                subscriber.onError(new CameraDataNullException());
            }
            RxCameraData rxCameraData = new RxCameraData();
            rxCameraData.cameraData = data;
            rxCameraData.rotateMatrix = rxCamera.getRotateMatrix();
            subscriber.onNext(rxCameraData);
        }
    }
}
