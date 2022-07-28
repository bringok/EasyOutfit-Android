package com.easyoutfit.easyoutfit;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.media.ExifInterface;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.media.ExifInterface.ORIENTATION_NORMAL;
import static android.support.media.ExifInterface.ORIENTATION_ROTATE_180;
import static android.support.media.ExifInterface.ORIENTATION_ROTATE_270;
import static android.support.media.ExifInterface.ORIENTATION_ROTATE_90;

/**
 * Created by Diogo on 30/01/2018.
 */

public class ImageHandle {

    private Activity thisActivity;
    private View content_layout;

    static final int CACHE = 1;
    static final int PERMANENT = 2;
    private static final double MAX_DIM_COMPRESSED = 1024;
    private static final double THUMBNAIL_TO_SCREEN_RATIO = 0.32;


    ImageHandle(Activity activity, View contentLayout) {
        thisActivity = activity;
        content_layout = contentLayout;
    }


    private Bitmap getSquareBitmap(Bitmap srcBmp)
    {
        int dim = Math.min(srcBmp.getWidth(), srcBmp.getHeight());
        Bitmap dstBmp = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(dstBmp);
        canvas.drawBitmap(srcBmp, (dim - srcBmp.getWidth()) / 2, (dim - srcBmp.getHeight()) / 2, null);

        return dstBmp;

    }

    Bitmap getCompressedBitmapFromFile(String imagePath) {

        int orientation;
        try {
            orientation = checkIfImageIsRotated(imagePath);
        }catch(IOException e){
            orientation = ORIENTATION_NORMAL;
        }

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);

        double photoW, photoH;
        if(orientation == ORIENTATION_ROTATE_90 || orientation == ORIENTATION_ROTATE_270)
        {
            photoW = bmOptions.outHeight;
            photoH = bmOptions.outWidth;
        }

        else
        {
            photoW = bmOptions.outWidth;
            photoH = bmOptions.outHeight;
        }


        // Determine how much to scale down the image
        double scaleFactor = Math.min(photoH/MAX_DIM_COMPRESSED,photoW/MAX_DIM_COMPRESSED);


        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = (int) (scaleFactor);
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);


        Bitmap rotatedBitmap;
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateBitmap(bitmap, 90);
                break;

            case ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateBitmap(bitmap, 180);
                break;

            case ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateBitmap(bitmap, 270);
                break;

            case ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }

        return rotatedBitmap;
    }

    Bitmap getDisplayableBitmap(Bitmap originalBitmap, double screenFactor)
    {

        // Get the dimensions of the Screen
        Point size = new Point();
        thisActivity.getWindowManager().getDefaultDisplay().getSize(size);

        double bitmapW, bitmapH;

        bitmapW = originalBitmap.getWidth();
        bitmapH = originalBitmap.getHeight();

        double target = (int) ((float) size.x * screenFactor);

        double scaleFactor = Math.min(bitmapW/target,bitmapH/target);

        return Bitmap.createScaledBitmap(originalBitmap, (int)(bitmapW/scaleFactor),(int)(bitmapH/scaleFactor), true);

    }

    void createAndSaveThumbnail(String thumbnailPath, String imagePath) {


        Bitmap originalImage = getBitmapFromFile(imagePath);

        Point size = new Point();
        thisActivity.getWindowManager().getDefaultDisplay().getSize(size);

        double bitmapW, bitmapH;

        bitmapW = originalImage.getWidth();
        bitmapH = originalImage.getHeight();

        double target = (int) ((float) Math.min(size.x, size.y) * THUMBNAIL_TO_SCREEN_RATIO);

        double scaleFactor = Math.min(bitmapW/target,bitmapH/target);

        Bitmap newBitmap = Bitmap.createScaledBitmap(originalImage, (int)(bitmapW/scaleFactor),(int)(bitmapH/scaleFactor), true);

        saveBitmap(new File(thumbnailPath), getSquareBitmap(newBitmap));



    }




    void saveBitmap(File dst, Bitmap bitmap)
    {
        try{
            FileOutputStream fOut = new FileOutputStream(dst);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        }catch (IOException e){
            Snackbar quickMessage = Snackbar.make(content_layout, R.string.error_saving_image, LENGTH_LONG);
            quickMessage.show();
        }
    }


    private int checkIfImageIsRotated(String imagePath) throws IOException {
        //see if image is rotated
        ExifInterface ei;

        ei = new ExifInterface(imagePath);

        return ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /*void copyEditedImageToAppDir(Uri imageUri, File imageFile)
    {
        if(imageFile == null)
            return;

        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(thisActivity.getContentResolver(), imageUri);
            FileOutputStream fOut = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        }catch (IOException e){
            Snackbar quickMessage = Snackbar.make(content_layout, R.string.error_saving_image, LENGTH_LONG);
            quickMessage.show();
        }
    }*/

    void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    String getRealPathFromURI(Uri contentURI)
    {
        String result = null;

        Cursor cursor = thisActivity.getContentResolver().query(contentURI,new String[] {MediaStore.Images.ImageColumns.DATA}, null, null, null);

        if (cursor == null)
        { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        }
        else
        {
            if(cursor.moveToFirst())
            {
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                if(idx != -1)
                    result = cursor.getString(idx);
                else
                    result = contentURI.getPath();
            }
            cursor.close();
        }
        return result;
    }

    Bitmap getBitmapFromFile(String imagePath) {


        BitmapFactory.Options bmOptions = new BitmapFactory.Options();


        bmOptions.inJustDecodeBounds = false;
        bmOptions.inPurgeable = true;


        return BitmapFactory.decodeFile(imagePath, bmOptions);
    }

    @Nullable
    String createImageFile(int CacheOrPermanent, String description) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = description + "_JPEG_" + timeStamp + "_";

        File storageDir;

        if(CacheOrPermanent == CACHE)
            storageDir = thisActivity.getExternalCacheDir();
        else if(CacheOrPermanent == PERMANENT)
            storageDir = thisActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        else
            return null;

        File image;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpeg",         /* suffix */
                    storageDir      /* directory */
            );
        }catch(IOException e){
            Snackbar quickMessage = Snackbar.make(content_layout, R.string.error_creating_file, LENGTH_LONG);
            quickMessage.show();
            return null;
        }

        // Save a file: path for use with ACTION_VIEW intents
        return image.getAbsolutePath();
    }


   /* public int pxToDp(int px)
    {
        return (int)(px / (thisActivity.getResources().getDisplayMetrics().density));
    }

    public int dpToPx(int dp)
    {
        return (int) (dp * thisActivity.getResources().getDisplayMetrics().density);
    }*/
}
