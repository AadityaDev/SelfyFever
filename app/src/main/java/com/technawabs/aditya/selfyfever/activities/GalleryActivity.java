package com.technawabs.aditya.selfyfever.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.technawabs.aditya.selfyfever.BaseActivity;
import com.technawabs.aditya.selfyfever.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();
    private Gallery g;
    private final String[] projection = {MediaStore.Images.Media._ID};
    private List<File> createFile = new ArrayList<File>();
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        File extS = Environment.getExternalStorageDirectory();
        File f = new File(extS.getAbsolutePath() + "/MaterialCamera");
        GridView sdcardimage = (GridView) findViewById(R.id.gridview);
        List<File> files = getListFiles(f);
        Log.d(TAG, files.size() + "");
        Log.d(TAG, createFile.size() + "");
        Log.d(TAG, bitmaps.size() + "");

        ImageAdapter adapter = new ImageAdapter(this, createFile);
        sdcardimage.setAdapter(adapter);

        sdcardimage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, createFile.get(position).getName());
                if (createFile.get(position).getName().endsWith(".mp4")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(new File(createFile.get(position).getAbsolutePath())));
                    intent.setDataAndType(Uri.fromFile(new File(createFile.get(position).getAbsolutePath())), "video/mp4");
                    startActivity(intent);
                }
                try {
                    progressDialog = ProgressDialog.show(GalleryActivity.this, "", "Uploading...", true);
                } catch (Exception e) {

                }
                uploadFileOnFirebase(createFile.get(position));
            }
        });
    }

    private List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                Log.d(TAG, file.getName());
                createFile.add(file);
                if (file.getName().endsWith(".mp4")) {
                    inFiles.add(file);
                    Uri uri = Uri.fromFile(file);
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                    bitmaps.add(bitmap);
                }
                if (file.getName().endsWith(".jpg")) {
                    inFiles.add(file);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    bitmaps.add(bitmap);
                }
            }
        }
        return inFiles;
    }

    private class ImageAdapter extends BaseAdapter {
        private Context context;
        private List<File> fileList;

        public ImageAdapter(Context localContext, List<File> fileList) {
            context = localContext;
            this.fileList = fileList;
        }

        public int getCount() {
            return fileList.size();
        }

        public Object getItem(int position) {
            return fileList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                holder.picturesView = new ImageView(context);
                convertView = getLayoutInflater().inflate(R.layout.row, parent, false);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //Setting Image to View Holder Image View.
            holder.picturesView = (ImageView) convertView.findViewById(R.id.imageview);
            final ViewHolder finalHolder = holder;
            holder.picturesView.setImageBitmap(bitmaps.get(position));
            holder.picturesView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.typeImage = (ImageView) convertView.findViewById(R.id.type_icon);
            holder.uploadFile = (ImageView) convertView.findViewById(R.id.upload);
            if (fileList.get(position).getName().endsWith(".mp4")) {
                holder.typeImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_videocam));
            } else {
                holder.typeImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_wallpaper));
            }
            holder.uploadFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                }
            });
            return convertView;
        }

        // View Holder pattern used for Smooth Scrolling. As View Holder pattern recycle the findViewById() object.
        class ViewHolder {
            private ImageView picturesView;
            private ImageView typeImage;
            private ImageView uploadFile;
        }
    }

    public void uploadFileOnFirebase(@NonNull File file) {
        InputStream stream = null;
        UploadTask uploadTask;
        try {
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://firebase-selfyfever.appspot.com");
// Create a reference to "mountains.jpg"
            StorageReference mountainsRef = storageRef.child(file.getName());
// Create a reference to 'images/mountains.jpg'
            StorageReference mountainImagesRef = storageRef.child("images/" + file.getName());
// While the file names are the same, the references point to different files
            mountainsRef.getName().equals(mountainImagesRef.getName());    // true
            mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false


            stream = new FileInputStream(new File(file.getAbsolutePath()));
            uploadTask = mountainsRef.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {

                    }
                    Toast.makeText(GalleryActivity.this, "File not uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    try {
                        progressDialog.dismiss();
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.d(TAG, downloadUrl + "");
                        Toast.makeText(GalleryActivity.this, "File uploaed", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {

                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}