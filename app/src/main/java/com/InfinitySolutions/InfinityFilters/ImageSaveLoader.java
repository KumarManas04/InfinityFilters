package com.InfinitySolutions.InfinityFilters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.InfinitySolutions.InfinityFilters.Utils.AnimatedGIFWriter;
import com.google.android.gms.vision.CameraSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import static android.os.Environment.getExternalStorageDirectory;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.FINAL_GIF_PROCESSING;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.GIF_FRAME_DELAY;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.GIF_FRAME_PROCESSING;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.PHOTO_PROCESSING;

public class ImageSaveLoader extends AsyncTaskLoader<Bitmap> {

    private byte[] mImageBytes;
    private ArrayList<Bitmap> mFaceOverlays;
    private int mRotation;
    private int mProcessingMode;
    private ArrayList<Bitmap> mGifFrames;
    private int mCameraFacing;
    private Context mContext;

    public ImageSaveLoader(Context context, byte[] imageBytes, ArrayList<Bitmap> faceOverlays, int rotation, int processingMode, int cameraFacing) {
        super(context);
        onContentChanged();
        mImageBytes = imageBytes;
        mFaceOverlays = faceOverlays;
        mRotation = rotation;
        mProcessingMode = processingMode;
        mCameraFacing = cameraFacing;
        mContext = context;
    }

    public ImageSaveLoader(Context context, ArrayList<Bitmap> gifFrames, int processingMode) {
        super(context);
        onContentChanged();
        mGifFrames = gifFrames;
        mProcessingMode = processingMode;
        mContext = context;
    }

    @Override
    public Bitmap loadInBackground() {
        if (mProcessingMode == GIF_FRAME_PROCESSING) {
            if(mFaceOverlays.size() <= 0){
                return null;
            }
            return generateResultBitmap(mImageBytes, mFaceOverlays, mRotation);
        } else if (mProcessingMode == FINAL_GIF_PROCESSING) {
            if (mGifFrames.size() <= 0) {
                return null;
            }
            Bitmap gifFrame = null;
            AnimatedGIFWriter writer = new AnimatedGIFWriter(true);
            try {
                String mainPath = getExternalStorageDirectory() + "/" + "InfinityFilters" + "/";
                File basePath = new File(mainPath);
                if (!basePath.exists()) {
                    Log.i("CAPTURE_BASE_PATH", basePath.mkdir() ? "Success" : "Failed");
                }
                String path = mainPath + "photo_" + getPhotoTime() + ".gif";
                File captureFile = new File(path);
                if (!captureFile.exists()) {
                    Log.i("CAPTURE_FILE_PATH", captureFile.createNewFile() ? "Success" : "Failed");
                }
                OutputStream os = new FileOutputStream(captureFile);
                writer.prepareForWrite(os, -1, -1);

                for (int i = 0; i < mGifFrames.size(); i++) {
                    gifFrame = mGifFrames.get(i);
                    writer.writeFrame(os, gifFrame, (int) GIF_FRAME_DELAY);
                }
                writer.finishWrite(os);
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(captureFile)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mGifFrames.get(0);
        } else if (mProcessingMode == PHOTO_PROCESSING) {
            if(mFaceOverlays.size() <= 0){
                return null;
            }
            Bitmap result = generateResultBitmap(mImageBytes, mFaceOverlays, mRotation);
            try {
                String mainPath = getExternalStorageDirectory() + "/" + "InfinityFilters" + "/";
                File basePath = new File(mainPath);
                if (!basePath.exists()) {
                    Log.i("CAPTURE_BASE_PATH", basePath.mkdir() ? "Success" : "Failed");
                }
                String path = mainPath + "photo_" + getPhotoTime() + ".jpg";
                File captureFile = new File(path);
                if (!captureFile.exists()) {
                    Log.i("CAPTURE_FILE_PATH", captureFile.createNewFile() ? "Success" : "Failed");
                }
                FileOutputStream stream = new FileOutputStream(captureFile);
                result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(captureFile)));
                stream.flush();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
        return null;
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged()) {
            forceLoad();
        }
    }


    private Bitmap generateResultBitmap(byte[] imageByteArray, ArrayList<Bitmap> faceOverlays, int rotation) {
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        switch (rotation) {
            case 1:
                imageBitmap = rotateImage(imageBitmap, 90);
                break;
            case 2:
                imageBitmap = rotateImage(imageBitmap, 180);
                break;
            case 3:
                imageBitmap = rotateImage(imageBitmap, 270);
                break;
            default:
                break;
        }

        Bitmap overlayBitmap = faceOverlays.get(0);
        for (int i = 1; i < faceOverlays.size(); i++) {
            overlayBitmap = mergeBitmaps(overlayBitmap, faceOverlays.get(i));
        }
        if (mCameraFacing == CameraSource.CAMERA_FACING_FRONT) {
            imageBitmap = flipHorizontally(imageBitmap);
        }
        Bitmap result = mergeBitmaps(imageBitmap, overlayBitmap);
        mFaceOverlays.clear();
        mFaceOverlays = null;
        imageBitmap.recycle();
        return result;
    }

    private Bitmap mergeBitmaps(Bitmap face, Bitmap overlay1) {
        int width = face.getWidth();
        int height = face.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, face.getConfig());

        Matrix m = new Matrix();
        float scaleWidth = ((float) width) / overlay1.getWidth();
        float scaleHeight = ((float) height) / overlay1.getHeight();
        m.postScale(scaleWidth, scaleHeight);
        Bitmap overlay = Bitmap.createBitmap(overlay1, 0, 0, overlay1.getWidth(), overlay1.getHeight(), m, false);

        Rect faceRect = new Rect(0, 0, width, height);
        Rect overlayRect = new Rect(0, 0, overlay.getWidth(), overlay.getHeight());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(face, faceRect, faceRect, null);
        canvas.drawBitmap(overlay, overlayRect, faceRect, null);

        return result;
    }

    private Bitmap flipHorizontally(Bitmap face) {
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1, face.getWidth() / 2f, face.getHeight() / 2f);
        return Bitmap.createBitmap(face, 0, 0, face.getWidth(), face.getHeight(), matrix, true);
    }

    private String getPhotoTime() {
        String photoTime = "";
        String separator = "_";
        Calendar cal = Calendar.getInstance();
        String time = "" + cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) + cal.get(Calendar.MILLISECOND);
        String date = "" + cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH) + cal.get(Calendar.DAY_OF_MONTH);
        photoTime = date + separator + time;
        return photoTime;
    }
}
