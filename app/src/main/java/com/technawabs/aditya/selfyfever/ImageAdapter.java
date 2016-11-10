//package com.technawabs.aditya.selfyfever;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.provider.MediaStore;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//
//class ImageAdapter extends BaseAdapter {
//
//    private Context context;
//    private Cursor cursor;
//
//    public ImageAdapter(Context localContext,Cursor cursor) {
//        context = localContext;
//        this.cursor=cursor;
//    }
//
//    public int getCount() {
//        return cursor.getCount();
//    }
//
//    public Object getItem(int position) {
//        return position;
//    }
//
//    public long getItemId(int position) {
//
//        return position;
//
//    }
//
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder = new ViewHolder();
//
//
//        if (convertView == null) {
//            holder.picturesView = new ImageView(context);
//            //Converting the Row Layout to be used in Grid View
//            convertView = getLayoutInflater().inflate(R.layout.row, parent, false);
//
//            //You can convert Layout in this Way with the Help of View Stub. View Stub is newer. Read about ViewStub.Inflate
//            // and its parameter.
//            //convertView= ViewStub.inflate(context,R.layout.row,null);
//
//            convertView.setTag(holder);
//
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        cursor.moveToPosition(position);
//        int imageID = cursor.getInt(columnIndex);
//
//        //In Uri "" + imageID is to convert int into String as it only take String Parameter and imageID is in Integer format.
//        //You can use String.valueOf(imageID) instead.
//        Uri uri = Uri.withAppendedPath(
//                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID);
//
//        //Setting Image to View Holder Image View.
//        holder.picturesView = (ImageView) convertView.findViewById(R.id.imageview);
//        holder.picturesView.setImageURI(uri);
//        holder.picturesView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//
//
//        return convertView;
//
//    }
//    // View Holder pattern used for Smooth Scrolling. As View Holder pattern recycle the findViewById() object.
//    class ViewHolder {
//        private ImageView picturesView;
//    }
//}
//}