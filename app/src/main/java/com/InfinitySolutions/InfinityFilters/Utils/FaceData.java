package com.InfinitySolutions.InfinityFilters.Utils;

import android.graphics.PointF;

public class FaceData {

    //Face dimens
    private PointF mPosition;
    private float mWidth;
    private float mHeight;

    //Facial Landmarks
    private PointF mRightEyePosition;
    private PointF mLeftEyePosition;
    private PointF mRightMouthPosition;
    private PointF mLeftMouthPosition;
    private PointF mBottomMouthPosition;
    private PointF mNoseBasePosition;
    private float mRotation;

    public void setPosition(PointF position){
        mPosition = position;
    }

    public PointF getPosition(){
        return mPosition;
    }

    public void setWidth(float width){
        mWidth = width;
    }

    public float getWidth(){
        return mWidth;
    }

    public void setHeight(float height){
        mHeight = height;
    }

    public float getHeight(){
        return mHeight;
    }

    public void setLeftEyePosition(PointF leftEyePosition){
        mLeftEyePosition = leftEyePosition;
    }

    public PointF getLeftEyePosition(){
        return mLeftEyePosition;
    }

    public void setRightEyePosition(PointF rightEyePosition){
        mRightEyePosition = rightEyePosition;
    }

    public PointF getRightEyePosition(){
        return mRightEyePosition;
    }

    public void setRightMouthPosition(PointF rightMouthPosition){
        mRightMouthPosition = rightMouthPosition;
    }

    public PointF getRightMouthPosition(){
        return mRightMouthPosition;
    }

    public void setLeftMouthPosition(PointF leftMouthPosition){
        mLeftMouthPosition = leftMouthPosition;
    }

    public PointF getLeftMouthPosition(){
        return mLeftMouthPosition;
    }

    public void setBottomMouthPosition(PointF bottomMouthPosition){
        mBottomMouthPosition = bottomMouthPosition;
    }

    public PointF getBottomMouthPosition(){
        return mBottomMouthPosition;
    }

    public void setFaceRotation(float rotation){
        mRotation = rotation;
    }

    public float getFaceRotation(){
        return mRotation;
    }

    public void setNoseBasePosition(PointF noseBasePosition){
        mNoseBasePosition = noseBasePosition;
    }

    public PointF getNoseBasePosition(){
        return mNoseBasePosition;
    }
}
