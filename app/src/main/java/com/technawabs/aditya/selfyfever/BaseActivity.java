package com.technawabs.aditya.selfyfever;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyPingCallback;

public class BaseActivity<T> extends AppCompatActivity{
    private Context context;
    private final String TAG=this.getClass().getSimpleName();
    private FirebaseStorage firebaseStorage;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        context=this;
        MultiDex.install(this);
        Firebase.setAndroidContext(context);
        FirebaseApp.getInstance();
        firebaseStorage=FirebaseStorage.getInstance(FirebaseApp.initializeApp(context, FirebaseOptions.fromResource(context)));
        final Client mKinveyClient = new Client.Builder("kid_Skwvnkmn", "eb90d7af36894ff18ab5bcbf3b536cf1"
                , this.getApplicationContext()).build();
        mKinveyClient.ping(new KinveyPingCallback() {
            public void onFailure(Throwable t) {
                Log.e(TAG, "Kinvey Ping Failed", t);
            }
            public void onSuccess(Boolean b) {
                Log.d(TAG, "Kinvey Ping Success");
            }
        });

        Firebase.setAndroidContext(context);
        FirebaseStorage storage=null;
        if(storage==null){
            FirebaseOptions firebaseOptions=FirebaseOptions.fromResource(context);
            FirebaseApp firebaseApp=FirebaseApp.initializeApp(context,firebaseOptions);
            storage=FirebaseStorage.getInstance(firebaseApp);
            Log.d(TAG,"Firebase"+storage.toString());
        }
    }

    public FirebaseStorage getFirebaseStorage(){
        return firebaseStorage;
    }

    public Context getBaseActivityContext(){
        return context;
    }
}
