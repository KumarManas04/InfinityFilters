package com.InfinitySolutions.InfinityFilters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

import com.InfinitySolutions.InfinityFilters.Utils.FaceData;
import com.InfinitySolutions.InfinityFilters.ui.GraphicOverlay;
import com.google.android.gms.vision.CameraSource;

import static com.InfinitySolutions.InfinityFilters.MainActivity.OC;
import static com.InfinitySolutions.InfinityFilters.MainActivity.faceCount;
import static com.InfinitySolutions.InfinityFilters.MainActivity.faceCounter;


public class FaceGraphic extends GraphicOverlay.Graphic {

    private Paint mAugment;
    private Bitmap mChineseBeardBitmap,mThugGlassesBitmap, mThugCigaretteBitmap, mMoustacheBitmap, mPirateHatBitmap, mCoolBitmap, mCrownBitmap, mBatmanBitmap, mRedHornsBitmap;
    private Bitmap mSantaCapBitmap,mSantaBeardBitmap,mJokerCapBitmap,mJokerNoseBitmap,mHaloBitmap,mAngelWingsBitmap,mCuteEyeBitmap,mDogTongueBitmap,mDogEarsBitmap,mDogNoseBitmap;
    private volatile FaceData mFaceData;
    private Context mContext;
    private Matrix mMatrix;
    private int mCameraFacing;

    FaceGraphic(GraphicOverlay overlay, Context context,int cameraFacing) {
        super(overlay);
        mContext = context;
        mCameraFacing = cameraFacing;

        mThugGlassesBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.thug_life_sunglass);
        mThugCigaretteBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.thug_life_cigarette);
        mMoustacheBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.moustache);
        mPirateHatBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pirate_hat);
        mCoolBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cool);
        mCrownBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.crown);
        mBatmanBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.batman);
        mRedHornsBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.red_horns);
        mChineseBeardBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.chinese_beard);
        mSantaCapBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.santa_cap);
        mSantaBeardBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.santa_beard);
        mJokerCapBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.joker_cap);
        mJokerNoseBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.joker_nose);
        mHaloBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.halo_ring);
        mAngelWingsBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.angel_wings);
        mCuteEyeBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.cute_eyes);
        mDogEarsBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.dog_ears);
        mDogNoseBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.dog_nose);

        mAugment = new Paint();
        mMatrix = new Matrix();
    }

    public void updateFace(FaceData faceData) {
        mFaceData = faceData;
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap overlayBitmap;
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        switch (MainActivity.filterType) {
            case 0:
                overlayBitmap = filterNone(width, height);
                break;
            case 1:
                overlayBitmap = filterMoustache(width, height);
                break;
            case 2:
                overlayBitmap = filterThugLife(width, height);
                break;
            case 3:
                overlayBitmap = filterPirateHat(width, height);
                break;
            case 4:
                overlayBitmap = filterCool(width, height);
                break;
            case 5:
                overlayBitmap = filterCrown(width, height);
                break;
            case 6:
                overlayBitmap = filterBatman(width, height);
                break;
            case 7:
                overlayBitmap = filterRedHorns(width, height);
                break;
            case 8:
                overlayBitmap = filterSanta(width,height);
                break;
            case 9:
                overlayBitmap = filterJoker(width,height);
                break;
            case 10:
                overlayBitmap = filterAngel(width,height);
                break;
            case 11:
                overlayBitmap = filterCute(width,height);
                break;
            case 12:
                overlayBitmap = filterDog(width,height);
                break;
            default:
                overlayBitmap = filterThugLife(width, height);
                break;

        }

        if (overlayBitmap == null) {
            return;
        }
        canvas.drawBitmap(overlayBitmap, 0, 0, mAugment);
        if (OC == 1) {
            if (faceCounter >= faceCount) {
                OC = 0;
            }
            MainActivity.setOverlayBitmap(overlayBitmap);
        }
    }

    private Bitmap filterNone(int canvasWidth, int canvasHeight) {
        Bitmap overlayBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        return overlayBitmap;
    }

    private Bitmap filterThugLife(int canvasWidth, int canvasHeight) {
        //Retrieving face details
        PointF detectPosition = mFaceData.getPosition();
        PointF detectLeftEyePosition = mFaceData.getLeftEyePosition();
        PointF detectRightEyePosition = mFaceData.getRightEyePosition();
        PointF detectRightMouthPosition = mFaceData.getRightMouthPosition();
        PointF detectBottomMouthPosition = mFaceData.getBottomMouthPosition();

        if ((detectPosition == null)
                || (detectLeftEyePosition == null)
                || (detectRightEyePosition == null)
                || (detectRightMouthPosition == null)
                || (detectBottomMouthPosition == null)) {
            return null;
        }

        Bitmap overlayBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);

        //Scaling positions and dimens of face
        float faceWidth = scaleX(mFaceData.getWidth());

        // Eye coordinates
        PointF leftEyePosition = new PointF(translateX(detectLeftEyePosition.x),
                translateY(detectLeftEyePosition.y));
        PointF rightEyePosition = new PointF(translateX(detectRightEyePosition.x),
                translateY(detectRightEyePosition.y));

        //Mouth coordinates
        PointF rightMouthPosition = new PointF(translateX(detectRightMouthPosition.x),
                translateY(detectRightMouthPosition.y));
        PointF bottomMouthPosition = new PointF(translateX(detectBottomMouthPosition.x),
                translateY(detectBottomMouthPosition.y));

        if(mCameraFacing == CameraSource.CAMERA_FACING_BACK){
            PointF temp = leftEyePosition;
            leftEyePosition = rightEyePosition;
            rightEyePosition = temp;
        }
        float GLASSES_PROPORTION = 0.8f;

        //Distance between the two eyes
        float distance = (float) Math.sqrt(
                (rightEyePosition.x - leftEyePosition.x) * (rightEyePosition.x - leftEyePosition.x) +
                        (rightEyePosition.y - leftEyePosition.y) * (rightEyePosition.y - leftEyePosition.y));


        float bitmapWidth = faceWidth * GLASSES_PROPORTION;
        float scaleFactor = bitmapWidth / mThugGlassesBitmap.getWidth();
        float bitmapHeight = mThugGlassesBitmap.getHeight() * scaleFactor;

        Bitmap thugGlassesBitmap = Bitmap.createScaledBitmap(mThugGlassesBitmap, (int) bitmapWidth, (int) bitmapHeight, false);

        float faceRotation = mFaceData.getFaceRotation();
        if (mCameraFacing == CameraSource.CAMERA_FACING_BACK){
            faceRotation = 360 - faceRotation;
        }
        mMatrix.setRotate(faceRotation, bitmapWidth / 2, bitmapHeight / 2);
        mMatrix.postTranslate(leftEyePosition.x + (distance / 2) - (bitmapWidth / 2), leftEyePosition.y - (bitmapHeight / 2));
        canvas.drawBitmap(thugGlassesBitmap, mMatrix, mAugment);

        bitmapWidth = faceWidth * 0.4f;
        scaleFactor = bitmapWidth / mThugCigaretteBitmap.getWidth();
        bitmapHeight = mThugCigaretteBitmap.getHeight() * scaleFactor;
        Bitmap thugCigaretteBitmap = Bitmap.createScaledBitmap(mThugCigaretteBitmap, (int) bitmapWidth, (int) bitmapHeight, false);
        float distY = rightMouthPosition.y - bottomMouthPosition.y;
        if (distY < 0) {
            distY = distY * (-1);
        }
        mMatrix.setRotate(0, bitmapWidth, bitmapHeight);
        mMatrix.postTranslate(bottomMouthPosition.x, bottomMouthPosition.y - distY);
        canvas.drawBitmap(thugCigaretteBitmap, mMatrix, mAugment);

        return overlayBitmap;
    }

    private Bitmap filterMoustache(int canvasWidth, int canvasHeight) {
        PointF detectNoseBasePosition = mFaceData.getNoseBasePosition();
        PointF detectBottomMouthPosition = mFaceData.getBottomMouthPosition();

        if (detectNoseBasePosition == null
                || detectBottomMouthPosition == null) {
            return null;
        }

        PointF noseBasePosition = new PointF(translateX(detectNoseBasePosition.x), translateY(detectNoseBasePosition.y));
        PointF bottomMouthPosition = new PointF(translateX(detectBottomMouthPosition.x),translateY(detectBottomMouthPosition.y));

        float faceWidth = scaleX(mFaceData.getWidth());
        float faceHeight = scaleY(mFaceData.getHeight());

        float MOUSTACHE_PROPORTION = 0.95f;
        float bitmapWidth = faceWidth * MOUSTACHE_PROPORTION;
        float scaleFactor = bitmapWidth / mMoustacheBitmap.getWidth();
        float bitmapHeight = mMoustacheBitmap.getHeight() * scaleFactor;

        Bitmap batmanBitmap = Bitmap.createScaledBitmap(mMoustacheBitmap, (int) bitmapWidth, (int) bitmapHeight, false);

        Bitmap overlayBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);

        float faceRotation = mFaceData.getFaceRotation();
        if (mCameraFacing == CameraSource.CAMERA_FACING_BACK){
            faceRotation = 360 - faceRotation;
        }
        mMatrix.setRotate(faceRotation, bitmapWidth / 2, 0);
        mMatrix.postTranslate(
                noseBasePosition.x - bitmapWidth / 2,
                noseBasePosition.y - (0.03f * faceHeight));
        canvas.drawBitmap(batmanBitmap, mMatrix, mAugment);

        bitmapWidth = faceWidth * 0.2f;
        scaleFactor = bitmapWidth / mChineseBeardBitmap.getWidth();
        bitmapHeight = mChineseBeardBitmap.getHeight() * scaleFactor;

        Bitmap chineseBeard = Bitmap.createScaledBitmap(mChineseBeardBitmap,(int)bitmapWidth,(int)bitmapHeight,false);
        mMatrix.setRotate(faceRotation, bitmapWidth / 2, 0);
        mMatrix.postTranslate(
                bottomMouthPosition.x - bitmapWidth/2,
                bottomMouthPosition.y
        );
        canvas.drawBitmap(chineseBeard,mMatrix,mAugment);
        return overlayBitmap;
    }

    private Bitmap filterPirateHat(int canvasWidth, int canvasHeight) {
        //Retrieving face details
        PointF detectPosition = mFaceData.getPosition();
        PointF detectLeftEyePosition = mFaceData.getLeftEyePosition();
        PointF detectRightEyePosition = mFaceData.getRightEyePosition();

        if ((detectPosition == null)
                || (detectLeftEyePosition == null)
                || (detectRightEyePosition == null)) {
            return null;
        }

        //Scaling positions and dimens of face
        float faceWidth = scaleX(mFaceData.getWidth());

        // Eye coordinates
        PointF leftEyePosition = new PointF(translateX(detectLeftEyePosition.x),
                translateY(detectLeftEyePosition.y));
        PointF rightEyePosition = new PointF(translateX(detectRightEyePosition.x),
                translateY(detectRightEyePosition.y));

        Bitmap overlayBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);

        //Scaling the Bitmap to appropriate size
        float HAT_PROPORTION = 1.2f;
        float bitmapWidth = faceWidth * HAT_PROPORTION;
        float scaleFactor = bitmapWidth / mPirateHatBitmap.getWidth();
        float bitmapHeight = mPirateHatBitmap.getHeight() * scaleFactor;
        Bitmap pirateHatBitmap = Bitmap.createScaledBitmap(mPirateHatBitmap, (int) bitmapWidth, (int) bitmapHeight, false);

        PointF centerHeadPosition = new PointF(
                (rightEyePosition.x / 2) + (leftEyePosition.x / 2),
                (rightEyePosition.y / 2) + (leftEyePosition.y / 2));

        //Drawing the bitmap on the canvas
        float faceRotation = mFaceData.getFaceRotation();
        if (mCameraFacing == CameraSource.CAMERA_FACING_BACK){
            faceRotation = 360 - faceRotation;
        }
        mMatrix.setRotate(faceRotation, bitmapWidth / 2, bitmapHeight);
        mMatrix.postTranslate(centerHeadPosition.x - (bitmapWidth / 2), centerHeadPosition.y - bitmapHeight);
        canvas.drawBitmap(pirateHatBitmap, mMatrix, mAugment);

        return overlayBitmap;
    }

    private Bitmap filterCool(int canvasWidth, int canvasHeight) {
        PointF detectLeftEyePosition = mFaceData.getLeftEyePosition();
        PointF detectRightEyePosition = mFaceData.getRightEyePosition();

        if ((detectLeftEyePosition == null) || (detectRightEyePosition == null)) {
            return null;
        }

        //Scaling positions and dimens of face
        float faceWidth = scaleX(mFaceData.getWidth());

        // Eye coordinates
        PointF leftEyePosition = new PointF(translateX(detectLeftEyePosition.x),
                translateY(detectLeftEyePosition.y));
        PointF rightEyePosition = new PointF(translateX(detectRightEyePosition.x),
                translateY(detectRightEyePosition.y));

        if(mCameraFacing == CameraSource.CAMERA_FACING_BACK){
            PointF temp = leftEyePosition;
            leftEyePosition = rightEyePosition;
            rightEyePosition = temp;
        }

        float GLASSES_PROPORTION = 0.85f;

        //Distance between the two eyes
        float distance = (float) Math.sqrt(
                (rightEyePosition.x - leftEyePosition.x) * (rightEyePosition.x - leftEyePosition.x) +
                        (rightEyePosition.y - leftEyePosition.y) * (rightEyePosition.y - leftEyePosition.y));


        float bitmapWidth = faceWidth * GLASSES_PROPORTION;
        float scaleFactor = bitmapWidth / mCoolBitmap.getWidth();
        float bitmapHeight = mCoolBitmap.getHeight() * scaleFactor;

        Bitmap coolBitmap = Bitmap.createScaledBitmap(mCoolBitmap, (int) bitmapWidth, (int) bitmapHeight, false);

        Bitmap overlayBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);

        float faceRotation = mFaceData.getFaceRotation();
        if (mCameraFacing == CameraSource.CAMERA_FACING_BACK){
            faceRotation = 360 - faceRotation;
        }
        mMatrix.setRotate(faceRotation, bitmapWidth / 2, bitmapHeight / 2);
        mMatrix.postTranslate(leftEyePosition.x + (distance / 2) - (bitmapWidth / 2), leftEyePosition.y - (bitmapHeight / 2));
        canvas.drawBitmap(coolBitmap, mMatrix, mAugment);

        return overlayBitmap;
    }

    private Bitmap filterCrown(int canvasWidth, int canvasHeight) {
        PointF detectPosition = mFaceData.getPosition();
        PointF detectLeftEyePosition = null;
        if(mCameraFacing == CameraSource.CAMERA_FACING_BACK){
            detectLeftEyePosition = mFaceData.getRightEyePosition();
        }else{
            detectLeftEyePosition = mFaceData.getLeftEyePosition();
        }

        if ((detectPosition == null)
                || (detectLeftEyePosition == null)) {
            return null;
        }

        //Scaling positions and dimens of face
        float faceWidth = scaleX(mFaceData.getWidth());

        // Modified coordinates
        PointF leftEyePosition = new PointF(translateX(detectLeftEyePosition.x),
                translateY(detectLeftEyePosition.y));

        float CROWN_PROPORTION = 0.5f;
        float bitmapWidth = faceWidth * CROWN_PROPORTION;
        float scaleFactor = bitmapWidth / mCrownBitmap.getWidth();
        float bitmapHeight = mCrownBitmap.getHeight() * scaleFactor;

        Bitmap crownBitmap = Bitmap.createScaledBitmap(mCrownBitmap, (int) bitmapWidth, (int) bitmapHeight, false);


        Bitmap overlayBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);

        float faceRotation = mFaceData.getFaceRotation();
        if (mCameraFacing == CameraSource.CAMERA_FACING_BACK){
            faceRotation = 360 - faceRotation;
        }
        mMatrix.setRotate(faceRotation, bitmapWidth / 1.3f, bitmapHeight / 2);
        mMatrix.postTranslate(leftEyePosition.x - (bitmapWidth / 1.3f),
                (detectPosition.y / 2) + (leftEyePosition.y / 2) - ((bitmapHeight) / 2));
        canvas.drawBitmap(crownBitmap, mMatrix, mAugment);

        return overlayBitmap;
    }

    private Bitmap filterBatman(int canvasWidth, int canvasHeight) {

        PointF detectNoseBasePosition = mFaceData.getNoseBasePosition();

        if (detectNoseBasePosition == null) {
            return null;
        }

        PointF noseBasePosition = new PointF(translateX(detectNoseBasePosition.x), translateY(detectNoseBasePosition.y));

        float faceWidth = scaleX(mFaceData.getWidth());

        float MASK_PROPORTION = 0.95f;
        float bitmapWidth = faceWidth * MASK_PROPORTION;
        float scaleFactor = bitmapWidth / mBatmanBitmap.getWidth();
        float bitmapHeight = mBatmanBitmap.getHeight() * scaleFactor;

        Bitmap batmanBitmap = Bitmap.createScaledBitmap(mBatmanBitmap, (int) bitmapWidth, (int) bitmapHeight, false);

        Bitmap overlayBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);

        float faceRotation = mFaceData.getFaceRotation();
        if (mCameraFacing == CameraSource.CAMERA_FACING_BACK){
            faceRotation = 360 - faceRotation;
        }
        mMatrix.setRotate(faceRotation, bitmapWidth / 2, bitmapHeight / (1.04f));
        mMatrix.postTranslate(
                noseBasePosition.x - bitmapWidth / 2,
                noseBasePosition.y - (bitmapHeight / (1.04f)));
        canvas.drawBitmap(batmanBitmap, mMatrix, mAugment);

        return overlayBitmap;
    }

    private Bitmap filterRedHorns(int canvasWidth, int canvasHeight) {
        PointF detectLeftEyePosition = mFaceData.getLeftEyePosition();
        PointF detectRightEyePosition = mFaceData.getRightEyePosition();

        if ((detectLeftEyePosition == null) || (detectRightEyePosition == null)) {
            return null;
        }


        //Scaling positions and dimens of face
        float faceWidth = scaleX(mFaceData.getWidth());

        // Eye coordinates
        PointF leftEyePosition = new PointF(translateX(detectLeftEyePosition.x),
                translateY(detectLeftEyePosition.y));
        PointF rightEyePosition = new PointF(translateX(detectRightEyePosition.x),
                translateY(detectRightEyePosition.y));

        float HORNS_PROPORTION = 0.9f;
        float bitmapWidth = faceWidth * HORNS_PROPORTION;
        float scaleFactor = bitmapWidth / mRedHornsBitmap.getWidth();
        float bitmapHeight = mRedHornsBitmap.getHeight() * scaleFactor;

        Bitmap redHornsBitmap = Bitmap.createScaledBitmap(mRedHornsBitmap, (int) bitmapWidth, (int) bitmapHeight, false);

        Bitmap overlayBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);

        float faceRotation = mFaceData.getFaceRotation();
        if (mCameraFacing == CameraSource.CAMERA_FACING_BACK){
            faceRotation = 360 - faceRotation;
        }
        mMatrix.setRotate(faceRotation, bitmapWidth / 2, bitmapHeight * 1.4f);
        mMatrix.postTranslate(
                (leftEyePosition.x / 2) + (rightEyePosition.x / 2) - (bitmapWidth / 2),
                (leftEyePosition.y / 2) + (rightEyePosition.y / 2) - (bitmapHeight * 1.4f));
        canvas.drawBitmap(redHornsBitmap, mMatrix, mAugment);

        return overlayBitmap;
    }

    private Bitmap filterSanta(int canvasWidth,int canvasHeight){
        //Retrieving face details
        PointF detectPosition = mFaceData.getPosition();
        PointF detectLeftEyePosition = mFaceData.getLeftEyePosition();
        PointF detectRightEyePosition = mFaceData.getRightEyePosition();
        PointF detectNoseBasePosition = mFaceData.getNoseBasePosition();

        if ((detectPosition == null)
                || (detectLeftEyePosition == null)
                || (detectRightEyePosition == null)
                || (detectNoseBasePosition == null)) {
            return null;
        }

        //Scaling positions and dimens of face
        float faceWidth = scaleX(mFaceData.getWidth());

        // Eye coordinates
        PointF leftEyePosition = new PointF(translateX(detectLeftEyePosition.x),
                translateY(detectLeftEyePosition.y));
        PointF rightEyePosition = new PointF(translateX(detectRightEyePosition.x),
                translateY(detectRightEyePosition.y));
        PointF noseBasePosition = new PointF(translateX(detectNoseBasePosition.x),
                translateY(detectNoseBasePosition.y));

        float faceRotation = mFaceData.getFaceRotation();
        if (mCameraFacing == CameraSource.CAMERA_FACING_BACK){
            PointF temp = leftEyePosition;
            leftEyePosition = rightEyePosition;
            rightEyePosition = temp;
            faceRotation = 360 - faceRotation;
        }

        Bitmap overlayBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);

        //Scaling the Bitmap to appropriate size
        float HAT_PROPORTION = 1.2f;
        float bitmapWidth = faceWidth * HAT_PROPORTION;
        float scaleFactor = bitmapWidth / mSantaCapBitmap.getWidth();
        float bitmapHeight = mSantaCapBitmap.getHeight() * scaleFactor;
        Bitmap santaCapBitmap = Bitmap.createScaledBitmap(mSantaCapBitmap, (int) bitmapWidth, (int) bitmapHeight, false);

        PointF centerHeadPosition = new PointF(
                (rightEyePosition.x / 2) + (leftEyePosition.x / 2),
                (rightEyePosition.y / 2) + (leftEyePosition.y / 2));

        //Drawing the bitmap on the canvas
        mMatrix.setRotate(faceRotation, bitmapWidth * 0.4f, bitmapHeight * 1.2f);
        mMatrix.postTranslate(centerHeadPosition.x - (bitmapWidth * 0.4f), centerHeadPosition.y - (bitmapHeight * 1.2f));
        canvas.drawBitmap(santaCapBitmap, mMatrix, mAugment);

        float BEARD_PROPORTION = 0.7f;
        bitmapWidth = faceWidth * BEARD_PROPORTION;
        scaleFactor = bitmapWidth / mSantaBeardBitmap.getWidth();
        bitmapHeight = mSantaBeardBitmap.getHeight() * scaleFactor;
        Bitmap santaBeardBitmap = Bitmap.createScaledBitmap(mSantaBeardBitmap, (int) bitmapWidth, (int) bitmapHeight, false);

        mMatrix.setRotate(faceRotation, bitmapWidth / 2, 0);
        mMatrix.postTranslate(noseBasePosition.x - bitmapWidth/2,noseBasePosition.y);
        canvas.drawBitmap(santaBeardBitmap,mMatrix,mAugment);
        return overlayBitmap;
    }

    private Bitmap filterJoker(int canvasWidth,int canvasHeight){
        //Retrieving face details
        PointF detectPosition = mFaceData.getPosition();
        PointF detectLeftEyePosition = mFaceData.getLeftEyePosition();
        PointF detectRightEyePosition = mFaceData.getRightEyePosition();
        PointF detectNoseBasePosition = mFaceData.getNoseBasePosition();

        if ((detectPosition == null)
                || (detectLeftEyePosition == null)
                || (detectRightEyePosition == null)
                || (detectNoseBasePosition == null)) {
            return null;
        }

        //Scaling positions and dimens of face
        float faceWidth = scaleX(mFaceData.getWidth());

        // Eye coordinates
        PointF leftEyePosition = new PointF(translateX(detectLeftEyePosition.x),
                translateY(detectLeftEyePosition.y));
        PointF rightEyePosition = new PointF(translateX(detectRightEyePosition.x),
                translateY(detectRightEyePosition.y));
        PointF noseBasePosition = new PointF(translateX(detectNoseBasePosition.x),
                translateY(detectNoseBasePosition.y));

        float faceRotation = mFaceData.getFaceRotation();
        if (mCameraFacing == CameraSource.CAMERA_FACING_BACK){
            PointF temp = leftEyePosition;
            leftEyePosition = rightEyePosition;
            rightEyePosition = temp;
            faceRotation = 360 - faceRotation;
        }

        Bitmap overlayBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);

        //Scaling the Bitmap to appropriate size
        float HAT_PROPORTION = 0.8f;
        float bitmapWidth = faceWidth * HAT_PROPORTION;
        float scaleFactor = bitmapWidth / mJokerCapBitmap.getWidth();
        float bitmapHeight = mJokerCapBitmap.getHeight() * scaleFactor;
        Bitmap jokerCapBitmap = Bitmap.createScaledBitmap(mJokerCapBitmap, (int) bitmapWidth, (int) bitmapHeight, false);

        PointF centerHeadPosition = new PointF(
                (rightEyePosition.x / 2) + (leftEyePosition.x / 2),
                (rightEyePosition.y / 2) + (leftEyePosition.y / 2));

        //Drawing the bitmap on the canvas
        mMatrix.setRotate(faceRotation, bitmapWidth / 2, bitmapHeight * 1.2f);
        mMatrix.postTranslate(centerHeadPosition.x - (bitmapWidth/2), centerHeadPosition.y - (bitmapHeight * 1.2f));
        canvas.drawBitmap(jokerCapBitmap, mMatrix, mAugment);

        float NOSE_PROPORTION = 0.24f;
        bitmapWidth = faceWidth * NOSE_PROPORTION;
        scaleFactor = bitmapWidth / mJokerNoseBitmap.getWidth();
        bitmapHeight = mJokerNoseBitmap.getHeight() * scaleFactor;
        Bitmap jokerNoseBitmap = Bitmap.createScaledBitmap(mJokerNoseBitmap, (int) bitmapWidth, (int) bitmapHeight, false);

        mMatrix.setRotate(faceRotation,bitmapWidth/2,bitmapHeight/2);
        mMatrix.postTranslate(noseBasePosition.x - bitmapWidth/2,noseBasePosition.y - bitmapHeight/2);
        canvas.drawBitmap(jokerNoseBitmap,mMatrix,mAugment);
        return overlayBitmap;
    }

    private Bitmap filterAngel(int canvasWidth,int canvasHeight){
        //Retrieving face details
        PointF detectPosition = mFaceData.getPosition();
        PointF detectLeftEyePosition = mFaceData.getLeftEyePosition();
        PointF detectRightEyePosition = mFaceData.getRightEyePosition();
        PointF detectNoseBasePosition = mFaceData.getNoseBasePosition();

        if ((detectPosition == null)
                || (detectLeftEyePosition == null)
                || (detectRightEyePosition == null)
                || (detectNoseBasePosition == null)) {
            return null;
        }

        //Scaling positions and dimens of face
        float faceWidth = scaleX(mFaceData.getWidth());

        // Eye coordinates
        PointF leftEyePosition = new PointF(translateX(detectLeftEyePosition.x),
                translateY(detectLeftEyePosition.y));
        PointF rightEyePosition = new PointF(translateX(detectRightEyePosition.x),
                translateY(detectRightEyePosition.y));
        PointF noseBasePosition = new PointF(translateX(detectNoseBasePosition.x),
                translateY(detectNoseBasePosition.y));

        float faceRotation = mFaceData.getFaceRotation();
        if (mCameraFacing == CameraSource.CAMERA_FACING_BACK){
            PointF temp = leftEyePosition;
            leftEyePosition = rightEyePosition;
            rightEyePosition = temp;
            faceRotation = 360 - faceRotation;
        }

        Bitmap overlayBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);

        //Scaling the Bitmap to appropriate size
        float RING_PROPORTION = 0.8f;
        float bitmapWidth = faceWidth * RING_PROPORTION;
        float scaleFactor = bitmapWidth / mHaloBitmap.getWidth();
        float bitmapHeight = mHaloBitmap.getHeight() * scaleFactor;
        Bitmap haloBitmap = Bitmap.createScaledBitmap(mHaloBitmap, (int) bitmapWidth, (int) bitmapHeight, false);

        PointF centerHeadPosition = new PointF(
                (rightEyePosition.x / 2) + (leftEyePosition.x / 2),
                (rightEyePosition.y / 2) + (leftEyePosition.y / 2));

        //Drawing the bitmap on the canvas
        mMatrix.setRotate(faceRotation, bitmapWidth / 2, bitmapHeight * 2.5f);
        mMatrix.postTranslate(centerHeadPosition.x - (bitmapWidth/2), centerHeadPosition.y - (bitmapHeight * 2.5f));
        canvas.drawBitmap(haloBitmap, mMatrix, mAugment);

        float WINGS_PROPORTION = 2.5f;
        bitmapWidth = faceWidth * WINGS_PROPORTION;
        scaleFactor = bitmapWidth / mAngelWingsBitmap.getWidth();
        bitmapHeight = mAngelWingsBitmap.getHeight() * scaleFactor;
        Bitmap angelWingsBitmap = Bitmap.createScaledBitmap(mAngelWingsBitmap, (int) bitmapWidth, (int) bitmapHeight, false);

        mMatrix.setRotate(faceRotation,bitmapWidth/2,bitmapHeight/2);
        mMatrix.postTranslate(noseBasePosition.x - bitmapWidth/2,noseBasePosition.y - bitmapHeight/2);
        canvas.drawBitmap(angelWingsBitmap,mMatrix,mAugment);

        return overlayBitmap;
    }

    private Bitmap filterCute(int canvasWidth,int canvasHeight){
        PointF detectLeftEyePosition = mFaceData.getLeftEyePosition();
        PointF detectRightEyePosition = mFaceData.getRightEyePosition();

        if ((detectLeftEyePosition == null) || (detectRightEyePosition == null)) {
            return null;
        }

        //Scaling positions and dimens of face
        float faceWidth = scaleX(mFaceData.getWidth());

        // Eye coordinates
        PointF leftEyePosition = new PointF(translateX(detectLeftEyePosition.x),
                translateY(detectLeftEyePosition.y));
        PointF rightEyePosition = new PointF(translateX(detectRightEyePosition.x),
                translateY(detectRightEyePosition.y));

        float faceRotation = mFaceData.getFaceRotation();
        if(mCameraFacing == CameraSource.CAMERA_FACING_BACK){
            PointF temp = leftEyePosition;
            leftEyePosition = rightEyePosition;
            rightEyePosition = temp;
            faceRotation = 360 - faceRotation;
        }

        float GLASSES_PROPORTION = 0.7f;

        //Distance between the two eyes
        float distance = (float) Math.sqrt(
                (rightEyePosition.x - leftEyePosition.x) * (rightEyePosition.x - leftEyePosition.x) +
                        (rightEyePosition.y - leftEyePosition.y) * (rightEyePosition.y - leftEyePosition.y));


        float bitmapWidth = faceWidth * GLASSES_PROPORTION;
        float scaleFactor = bitmapWidth / mCuteEyeBitmap.getWidth();
        float bitmapHeight = mCuteEyeBitmap.getHeight() * scaleFactor;

        Bitmap cuteEyesBitmap = Bitmap.createScaledBitmap(mCuteEyeBitmap, (int) bitmapWidth, (int) bitmapHeight, false);

        Bitmap overlayBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);

        mMatrix.setRotate(faceRotation, bitmapWidth / 2, bitmapHeight / 2);
        mMatrix.postTranslate(leftEyePosition.x + (distance / 2) - (bitmapWidth / 2), leftEyePosition.y - (bitmapHeight / 2));
        canvas.drawBitmap(cuteEyesBitmap, mMatrix, mAugment);

        return overlayBitmap;
    }

    private Bitmap filterDog(int canvasWidth,int canvasHeight){
        //Retrieving face details
        PointF detectPosition = mFaceData.getPosition();
        PointF detectLeftEyePosition = mFaceData.getLeftEyePosition();
        PointF detectRightEyePosition = mFaceData.getRightEyePosition();
        PointF detectNoseBasePosition = mFaceData.getNoseBasePosition();

        if ((detectPosition == null)
                || (detectLeftEyePosition == null)
                || (detectRightEyePosition == null)
                || (detectNoseBasePosition == null)) {
            return null;
        }

        //Scaling positions and dimens of face
        float faceWidth = scaleX(mFaceData.getWidth());

        // Eye coordinates
        PointF leftEyePosition = new PointF(translateX(detectLeftEyePosition.x),
                translateY(detectLeftEyePosition.y));
        PointF rightEyePosition = new PointF(translateX(detectRightEyePosition.x),
                translateY(detectRightEyePosition.y));
        PointF noseBasePosition = new PointF(translateX(detectNoseBasePosition.x),
                translateY(detectNoseBasePosition.y));

        float faceRotation = mFaceData.getFaceRotation();
        if (mCameraFacing == CameraSource.CAMERA_FACING_BACK){
            PointF temp = leftEyePosition;
            leftEyePosition = rightEyePosition;
            rightEyePosition = temp;
            faceRotation = 360 - faceRotation;
        }

        Bitmap overlayBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);

        //Scaling the Bitmap to appropriate size
        float EARS_PROPORTION = 1.1f;
        float bitmapWidth = faceWidth * EARS_PROPORTION;
        float scaleFactor = bitmapWidth / mDogEarsBitmap.getWidth();
        float bitmapHeight = mDogEarsBitmap.getHeight() * scaleFactor;
        Bitmap dogEarsBitmap = Bitmap.createScaledBitmap(mDogEarsBitmap, (int) bitmapWidth, (int) bitmapHeight, false);

        PointF centerHeadPosition = new PointF(
                (rightEyePosition.x / 2) + (leftEyePosition.x / 2),
                (rightEyePosition.y / 2) + (leftEyePosition.y / 2));

        //Drawing the bitmap on the canvas
        mMatrix.setRotate(faceRotation, bitmapWidth / 2, bitmapHeight * 1.1f);
        mMatrix.postTranslate(centerHeadPosition.x - (bitmapWidth/2), centerHeadPosition.y - (bitmapHeight * 1.1f));
        canvas.drawBitmap(dogEarsBitmap, mMatrix, mAugment);

        float NOSE_PROPORTION = 0.3f;
        bitmapWidth = faceWidth * NOSE_PROPORTION;
        scaleFactor = bitmapWidth / mDogNoseBitmap.getWidth();
        bitmapHeight = mDogNoseBitmap.getHeight() * scaleFactor;
        Bitmap dogNoseBitmap = Bitmap.createScaledBitmap(mDogNoseBitmap, (int) bitmapWidth, (int) bitmapHeight, false);

        mMatrix.setRotate(faceRotation,bitmapWidth/2,bitmapHeight/2);
        mMatrix.postTranslate(noseBasePosition.x - bitmapWidth/2,noseBasePosition.y - (bitmapHeight*0.6f));
        canvas.drawBitmap(dogNoseBitmap,mMatrix,mAugment);

        return overlayBitmap;
    }
}
