package com.technawabs.aditya.selfyfever.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyPingCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.kinvey.java.core.MediaHttpUploader;
import com.kinvey.java.core.UploaderProgressListener;
import com.kinvey.java.model.FileMetaData;
import com.technawabs.aditya.selfyfever.BaseActivity;
import com.technawabs.aditya.selfyfever.R;
import com.technawabs.aditya.selfyfever.RxFile.RxFile;
import com.technawabs.aditya.selfyfever.constants.Constants;
import com.technawabs.aditya.selfyfever.customCamera.RxCamera;
import com.technawabs.aditya.selfyfever.customCamera.action.RxCameraConfig;
import com.technawabs.aditya.selfyfever.materialCamera.MaterialCamera;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.kinvey.java.model.KinveyMetaData.AccessControlList;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private static Client mKinveyClient;
    private final static int CAMERA_RQ = 6969;
    private final static int PERMISSION_RQ = 84;
    private Context context;
    private final String TAG = this.getClass().getSimpleName();
    private RxCamera camera;
    private TextureView textureView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = HomeActivity.this;

        findViewById(R.id.launchCamera).setOnClickListener(this);
        findViewById(R.id.launchCameraStillshot).setOnClickListener(this);
        findViewById(R.id.launchFile).setOnClickListener(this);

        RxFile.setLoggingEnabled(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission to save videos in external storage
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_RQ);
        }
        mKinveyClient = new Client.Builder("kid_Skwvnkmn", "eb90d7af36894ff18ab5bcbf3b536cf1"
                , this.getApplicationContext()).build();
        mKinveyClient.ping(new KinveyPingCallback() {
            public void onFailure(Throwable t) {
                Log.d(TAG, "Kinvey Ping Failed", t);
            }

            public void onSuccess(Boolean b) {
                Log.d(TAG, "Kinvey Ping Success");
            }
        });

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == Constants.REQUEST_PERMISSION_CODE) {
//            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                openCamera();
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // Sample was denied WRITE_EXTERNAL_STORAGE permission
            Toast.makeText(this, "Videos will be saved in a cache directory instead of an external storage directory since permission was denied.", Toast.LENGTH_LONG).show();
        }
    }

    private String readableFileSize(long size) {
        if (size <= 0) return size + " B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : Constants.REQUEST_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private String fileSize(File file) {
        return readableFileSize(file.length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_RQ) {
            if (resultCode == RESULT_OK) {
                try {
                    final File file = new File(data.getData().getPath());
                    Toast.makeText(this, String.format("Saved to: %s, size: %s",
                            file.getAbsolutePath(), fileSize(file)), Toast.LENGTH_LONG).show();
                    file.createNewFile();
                    mKinveyClient.user().login(new KinveyUserCallback() {
                        @Override
                        public void onSuccess(User user) {
                            Log.d(TAG, "Success");
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.d(TAG, "Error");
                        }
                    });
//                    FileMetaData fileMetaData = new FileMetaData(file.getName() + "124");
//                    fileMetaData.setPublic(true);
//                    fileMetaData.setAcl(new AccessControlList());
//                    fileMetaData.setFileName(file.getName());
                    mKinveyClient.file().upload(file, new UploaderProgressListener() {
                        @Override
                        public void progressChanged(MediaHttpUploader mediaHttpUploader) throws IOException {

                        }

                        @Override
                        public void onSuccess(FileMetaData fileMetaData) {
                            Toast.makeText(HomeActivity.this, "File Saved", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.d(TAG, throwable.getMessage());
                            Toast.makeText(HomeActivity.this, "File Not Saved", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                if (e != null) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, Constants.REQUEST_PERMISSIONS, Constants.REQUEST_PERMISSION_CODE);
    }

    private void openCamera() {
        RxCameraConfig config = new RxCameraConfig.Builder()
                .useBackCamera()
                .setAutoFocus(true)
                .setPreferPreviewFrameRate(15, 30)
                .setPreferPreviewSize(new Point(640, 480), false)
                .setHandleSurfaceEvent(true)
                .build();
        Log.d(TAG, "config: " + config);
        RxCamera.open(this, config).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                Log.d(TAG, "isopen: " + rxCamera.isOpenCamera() + ", thread: " + Thread.currentThread());
                camera = rxCamera;
                return rxCamera.bindTexture(textureView);
            }
        }).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                Log.d(TAG, "isbindsurface: " + rxCamera.isBindSurface() + ", thread: " + Thread.currentThread());
                return rxCamera.startPreview();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<RxCamera>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "open camera error: " + e.getMessage());
            }

            @Override
            public void onNext(final RxCamera rxCamera) {
                camera = rxCamera;
                Log.d(TAG, "open camera success: " + camera);
                Toast.makeText(context, "Now you can tap to focus", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.closeCamera();
        }
    }

//    private void faceDetection() {
//        camera.request().faceDetectionRequest().subscribe(new Action1<RxCameraData>() {
//            @Override
//            public void call(RxCameraData rxCameraData) {
//                Log.d(TAG,"on face detection: ");
//            }
//        });
//    }

    private void switchCamera() {
        if (!checkCamera()) {
            return;
        }
        camera.switchCamera().subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                Log.d(TAG, "switch camera result: " + aBoolean);
            }
        });
    }

    private boolean checkCamera() {
        if (camera == null || !camera.isOpenCamera()) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onClick(View view) {
        File saveDir = null;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Only use external storage directory if permission is granted, otherwise cache directory is used by default
            saveDir = new File(Environment.getExternalStorageDirectory(), "MaterialCamera");
            saveDir.mkdirs();
        }

        MaterialCamera materialCamera = new MaterialCamera(this)
                .saveDir(saveDir)
                .showPortraitWarning(true)
                .allowRetry(true)
                .defaultToFrontFacing(true)
                .labelConfirm(R.string.mcam_use_video);

        if (view.getId() == R.id.launchCameraStillshot)
            materialCamera
                    .stillShot() // launches the Camera in stillshot mode
                    .labelConfirm(R.string.mcam_use_stillshot);
        materialCamera.start(CAMERA_RQ);

        if (view.getId() == R.id.launchFile) {
            Intent intent = new Intent(this, GalleryActivity.class);
            startActivity(intent);
        }
    }
}
