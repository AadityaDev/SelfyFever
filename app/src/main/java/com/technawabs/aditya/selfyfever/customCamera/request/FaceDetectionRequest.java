package com.technawabs.aditya.selfyfever.customCamera.request;

import android.hardware.Camera;

import com.technawabs.aditya.selfyfever.customCamera.RxCamera;
import com.technawabs.aditya.selfyfever.customCamera.RxCameraData;
import com.technawabs.aditya.selfyfever.customCamera.error.FaceDetectionNotSupportError;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class FaceDetectionRequest extends BaseRxCameraRequest implements Camera.FaceDetectionListener {

    private Subscriber<? super RxCameraData> subscriber;

    public FaceDetectionRequest(RxCamera rxCamera) {
        super(rxCamera);
    }

    @Override
    public Observable<RxCameraData> get() {
        return Observable.create(new Observable.OnSubscribe<RxCameraData>() {
            @Override
            public void call(Subscriber<? super RxCameraData> subscriber) {
                if (rxCamera.getNativeCamera().getParameters().getMaxNumDetectedFaces() > 0) {
                    FaceDetectionRequest.this.subscriber = subscriber;
                } else {
                    subscriber.onError(new FaceDetectionNotSupportError("Camera not support face detection"));
                }
            }
        }).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                rxCamera.getNativeCamera().setFaceDetectionListener(FaceDetectionRequest.this);
                rxCamera.getNativeCamera().startFaceDetection();
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                rxCamera.getNativeCamera().setFaceDetectionListener(null);
                rxCamera.getNativeCamera().stopFaceDetection();
            }
        });
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        if (subscriber != null && !subscriber.isUnsubscribed() && rxCamera.isOpenCamera()) {
            RxCameraData cameraData = new RxCameraData();
            cameraData.faceList = faces;
            subscriber.onNext(cameraData);
        }
    }
}
