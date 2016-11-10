package com.technawabs.aditya.selfyfever.materialCamera;

import android.app.Fragment;
import android.support.annotation.NonNull;

public class CaptureActivity extends BaseCaptureActivity {

    @Override
    @NonNull
    public Fragment getFragment() {
        return CameraFragment.newInstance();
    }
}