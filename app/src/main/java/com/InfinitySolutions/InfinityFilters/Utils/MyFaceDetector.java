package com.InfinitySolutions.InfinityFilters.Utils;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.SparseArray;

import com.InfinitySolutions.InfinityFilters.MainActivity;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;

import java.io.ByteArrayOutputStream;

import static com.InfinitySolutions.InfinityFilters.MainActivity.IC;
import static com.InfinitySolutions.InfinityFilters.MainActivity.OC;
import static com.InfinitySolutions.InfinityFilters.MainActivity.faceCount;
import static com.InfinitySolutions.InfinityFilters.MainActivity.mReady;

public class MyFaceDetector extends Detector<Face> {
    private Detector<Face> mDelegate;
    private Context mContext;

    public MyFaceDetector(Context context, Detector<Face> delegate){
        mContext = context;
        mDelegate = delegate;
    }

    @Override
    public SparseArray<Face> detect(Frame frame) {
        SparseArray<Face> faces = mDelegate.detect(frame);
        if(IC == 1){
            if(faces.size() > 0){
                TakePicture(frame,faces);
            }else{
                IC = 0;
                mReady = true;
            }
        }
        return faces;
    }

    public boolean isOperational() {
        return mDelegate.isOperational();
    }

    public boolean setFocus(int id) {
        return mDelegate.setFocus(id);
    }

    public void release(){
        mDelegate.release();
    }

    private void TakePicture(Frame frame,SparseArray<Face> faces){
        int width = frame.getMetadata().getWidth();
        int height = frame.getMetadata().getHeight();

        YuvImage yuvImage = new YuvImage(frame.getGrayscaleImageData().array(), ImageFormat.NV21,width,height,null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        yuvImage.compressToJpeg(new Rect(0,0,width,height),100,byteArrayOutputStream);
        byte[] imageArray = byteArrayOutputStream.toByteArray();
        IC = 0;
        OC = 1;
        int i = 0;
        for (i = 0; i < faces.size(); i++);
        faceCount = i;
        MainActivity.faceCounter = 1;
        MainActivity.setImageByteArray(imageArray,frame.getMetadata().getRotation());
    }
}
