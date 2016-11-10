package com.technawabs.aditya.selfyfever;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class BaseApplication extends Application {

    private final String TAG = this.getClass().getSimpleName();
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        MultiDex.install(this);
    }

    public Context getBaseContext() {
        return context;
    }
}
