package com.InfinitySolutions.InfinityFilters;

import android.content.Context;
import android.graphics.PointF;

import com.InfinitySolutions.InfinityFilters.Utils.FaceData;
import com.InfinitySolutions.InfinityFilters.ui.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GraphicFaceTracker extends Tracker<Face> {

    private String TAG = "Face Tracker";
    private GraphicOverlay mOverlay;
    private FaceGraphic mFaceGraphic;
    private FaceData mFaceData;
    private Context mContext;
    private int mCameraFacing;
    private Map<Integer, PointF> mPreviousLandmarkPositions = new ConcurrentHashMap<>();

    public GraphicFaceTracker(GraphicOverlay overlay, Context context, int cameraFacing) {
        mOverlay = overlay;
        mContext = context;
        mCameraFacing = cameraFacing;
        mFaceData = new FaceData();
    }

    @Override
    public void onNewItem(int faceId, Face item) {
        mFaceGraphic = new FaceGraphic(mOverlay, mContext, mCameraFacing);
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        mOverlay.add(mFaceGraphic);
        if (face != null) {
            updatePreviousLandmarkPositions(face);
            setLandmarks(face);
            mFaceGraphic.updateFace(mFaceData);
        }
    }

    @Override
    public void onMissing(Detector.Detections<Face> detections) {
        mOverlay.remove(mFaceGraphic);
    }

    @Override
    public void onDone() {
        mOverlay.remove(mFaceGraphic);
    }

    private void setLandmarks(Face face) {

        //Face positions, dimens and rotations
        mFaceData.setPosition(face.getPosition());
        mFaceData.setWidth(face.getWidth());
        mFaceData.setHeight(face.getHeight());
        mFaceData.setFaceRotation(face.getEulerZ());

        //Eye positions
        mFaceData.setLeftEyePosition(getLandMarkPosition(face, Landmark.LEFT_EYE));
        mFaceData.setRightEyePosition(getLandMarkPosition(face, Landmark.RIGHT_EYE));

        //Mouth positions
        mFaceData.setRightMouthPosition(getLandMarkPosition(face, Landmark.RIGHT_MOUTH));
        mFaceData.setLeftMouthPosition(getLandMarkPosition(face, Landmark.LEFT_MOUTH));
        mFaceData.setBottomMouthPosition(getLandMarkPosition(face, Landmark.BOTTOM_MOUTH));

        //Ear positions
        mFaceData.setNoseBasePosition(getLandMarkPosition(face, Landmark.NOSE_BASE));
    }

    private PointF getLandMarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return landmark.getPosition();
            }
        }

        PointF landmarkPosition = mPreviousLandmarkPositions.get(landmarkId);
        if (landmarkPosition == null) {
            return null;
        }
        float x = face.getPosition().x + (landmarkPosition.x * face.getWidth());
        float y = face.getPosition().y + (landmarkPosition.y * face.getHeight());
        return new PointF(x, y);
    }


    private void updatePreviousLandmarkPositions(Face face) {
            for (Landmark landmark : face.getLandmarks()) {
                PointF position = landmark.getPosition();
                float xProp = (position.x - face.getPosition().x) / face.getWidth();
                float yProp = (position.y - face.getPosition().y) / face.getHeight();
                mPreviousLandmarkPositions.put(landmark.getType(), new PointF(xProp, yProp));
            }
    }
}
