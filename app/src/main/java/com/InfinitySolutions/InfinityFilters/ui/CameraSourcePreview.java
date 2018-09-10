package com.InfinitySolutions.InfinityFilters.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

public class CameraSourcePreview extends ViewGroup {
    private String TAG = "CameraSourcePreview";


    private Context mContext;
    private CameraSource mCameraSource;
    public SurfaceView mSurfaceView;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;

    private GraphicOverlay mOverlay;

    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mSurfaceAvailable = false;
        mStartRequested = false;

        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(mSurfaceView);

    }


    public void start(CameraSource cameraSource,GraphicOverlay overlay) throws IOException{
        mOverlay = overlay;
        start(cameraSource);
    }

    public void start(CameraSource cameraSource) throws IOException{
        if(cameraSource == null){
            stop();
        }

        mCameraSource = cameraSource;

        if(mCameraSource != null){
            mStartRequested = true;
            startIfReady();
        }
    }

    public void stop(){
        if(mCameraSource != null){
            mCameraSource.stop();
        }
    }

    @SuppressLint("MissingPermission")
    public void startIfReady() throws IOException{
        if(mStartRequested && mSurfaceAvailable){
            mCameraSource.start(mSurfaceView.getHolder());
            if(mOverlay != null){
                Size size = mCameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(),size.getHeight());
                int max = Math.max(size.getWidth(),size.getHeight());
                if(isPortraitMode()){
                    mOverlay.setCameraInfo(min , max , mCameraSource.getCameraFacing());
                }else{
                    mOverlay.setCameraInfo(max , min , mCameraSource.getCameraFacing());
                }
                mOverlay.clear();
            }
            mStartRequested = false;
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback{
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceAvailable = true;
            try{
                startIfReady();
            }catch(IOException e){
                Log.e(TAG,"Could not start Camera Source",e);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceAvailable = false;
        }
    }

    public void release(){
        if(mCameraSource != null){
            mCameraSource.release();
            mCameraSource = null;
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = 640;
        int height = 480;

        if(mCameraSource != null){
            Size size = mCameraSource.getPreviewSize();
            if(size != null){
                width = size.getWidth();
                height = size.getHeight();
            }
        }

        if(isPortraitMode()){
            int temp = width;
            width = height;
            height = temp;
        }

        final int layoutWidth = right - left;
        final int layoutHeight = bottom - top;

        int childWidth = layoutWidth;
        int childHeight = (int)(((float) layoutWidth / (float) width) * height);

        if(childHeight > layoutHeight){
            childHeight = layoutHeight;
            childWidth = (int)(((float) layoutHeight / (float) height) * width);
        }

        for(int i = 0 ; i < getChildCount() ; ++i){
            getChildAt(i).layout(0,0,childWidth , childHeight);
        }

        try{
            startIfReady();
        }catch(IOException e){
            Log.e(TAG , "Could not start camera source.",e);
        }
    }

    public boolean isPortraitMode(){
        int orientation = mContext.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            return false;
        }

        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            return true;
        }

        Log.i(TAG, "isPortraitMode returning false by default");
        return false;
    }
}
