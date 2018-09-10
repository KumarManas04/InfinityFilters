package com.InfinitySolutions.InfinityFilters;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.InfinitySolutions.InfinityFilters.Utils.FiltersListItem;
import com.InfinitySolutions.InfinityFilters.Utils.MyAdapter;
import com.InfinitySolutions.InfinityFilters.Utils.MyFaceDetector;
import com.InfinitySolutions.InfinityFilters.Utils.intVar;
import com.InfinitySolutions.InfinityFilters.ui.CameraSourcePreview;
import com.InfinitySolutions.InfinityFilters.ui.GraphicOverlay;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.ASK_FOR_RATING;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.All_permission_message;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.CAPTURE_MODE;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.Camera_permission_message;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.DO_NOT_ASK;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.FINAL_GIF_PROCESSING;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.GIF_FRAME_DELAY;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.GIF_FRAME_PROCESSING;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.GIF_MODE;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.LOADER_AVAILABLE;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.MAX_LOADER_COUNT;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.PHOTO_PROCESSING;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.RC_HANDLE_ALL_PERM;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.RC_HANDLE_CAMERA_PERM;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.RC_HANDLE_STORAGE_PERM;
import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.Storage_permission_message;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Bitmap> {

    private int RC_HANDLE_GMS = 9001;
    private String TAG = "Hey guys what's up";
    private CameraSource mCameraSource;
    private GraphicOverlay mGraphicOverlay;
    private CameraSourcePreview mPreview;
    private Button mCaptureButton;
    private Button mGifButton;
    private int mProcessingMode;
    private int mLoaderIds[];
    public static int IC;
    public static int OC;
    private static byte[] mImageByteArray;
    private static int mRotation;
    private static intVar checkIfProcessed;
    public static int filterType;
    private Handler handler;
    public static boolean mReady;
    private ProgressDialog mDialog;
    private Context mContext;
    private ImageView mPreviewImage;
    private RecyclerView mFiltersListView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int cameraMode;
    private int mCameraFacing;
    public static int faceCount;
    public static int faceCounter;
    public int counter;
    private AnimatorSet gifIn;
    private AnimatorSet captureOut;
    private AnimatorSet gifOut;
    private AnimatorSet captureIn;
    private static ArrayList<Bitmap> mFaceOverlays;
    private ArrayList<Bitmap> mGifFrames;
    private ImageView mModeIndicator;
    private int mRatingState;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);

        mLoaderIds = new int[MAX_LOADER_COUNT];

        //Declaring every value as available
        for (int i = 0; i < MAX_LOADER_COUNT; i++) {
            mLoaderIds[i] = LOADER_AVAILABLE;
        }

        mCaptureButton = (Button) findViewById(R.id.capture_button);
        mGifButton = (Button) findViewById(R.id.gif_button);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.overlay);
        mPreview = (CameraSourcePreview) findViewById(R.id.camera_preview);
        mModeIndicator = (ImageView)findViewById(R.id.mode_indicator);
        mGifFrames = new ArrayList<Bitmap>();
        mContext = this;
        mReady = true;
        handler = new Handler();
        mDialog = new ProgressDialog(this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("Saving GIF...");
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
        mPreviewImage = (ImageView) findViewById(R.id.preview_image);
        IC = OC = 0;
        counter = 1;
        faceCounter = 0;
        mFaceOverlays = new ArrayList<Bitmap>();

        if(savedInstanceState != null){
            mCameraFacing = savedInstanceState.getInt("camera_direction");
            filterType = savedInstanceState.getInt("filter_type");
            cameraMode = savedInstanceState.getInt("camera_mode");
        }else {
            filterType = 1;
            mCameraFacing = CameraSource.CAMERA_FACING_FRONT;
            cameraMode = CAPTURE_MODE;
        }

        //Setting up the overlay picker
        mFiltersListView = (RecyclerView) findViewById(R.id.filters_list);
        mFiltersListView.setHasFixedSize(true);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        }else{
            mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
        mFiltersListView.setLayoutManager(mLayoutManager);
        ArrayList<FiltersListItem> dataSet = new ArrayList<FiltersListItem>();
        dataSet.add(new FiltersListItem("None", R.drawable.none_preview));
        dataSet.add(new FiltersListItem("Moustache", R.drawable.moustach_preview));
        dataSet.add(new FiltersListItem("Thug life", R.drawable.thug_life_preview));
        dataSet.add(new FiltersListItem("Pirate hat", R.drawable.pirate_hat_preview));
        dataSet.add(new FiltersListItem("Cool", R.drawable.cool_glasses_preview));
        dataSet.add(new FiltersListItem("Crown", R.drawable.crown_preview));
        dataSet.add(new FiltersListItem("Batman", R.drawable.batman_preview));
        dataSet.add(new FiltersListItem("Red Horns", R.drawable.red_horns_preview));
        dataSet.add(new FiltersListItem("Santa",R.drawable.santa_preview));
        dataSet.add(new FiltersListItem("Joker",R.drawable.joker_preview));
        dataSet.add(new FiltersListItem("Angel",R.drawable.angel_preview));
        dataSet.add(new FiltersListItem("Cute",R.drawable.cute_eyes_preview));
        dataSet.add(new FiltersListItem("Dog",R.drawable.dog_preview));
        mAdapter = new MyAdapter(this, dataSet);
        mFiltersListView.setAdapter(mAdapter);

        //Setting animations
        gifIn = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.gif_button_switch_in);
        gifIn.setTarget(mGifButton);
        captureOut = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.capture_button_switch_out);
        captureOut.setTarget(mCaptureButton);
        gifOut = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.gif_button_switch_out);
        gifOut.setTarget(mGifButton);
        captureIn = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.capture_button_switch_in);
        captureIn.setTarget(mCaptureButton);

        if(cameraMode == GIF_MODE){
            AnimatorSet gifInFast = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.gif_button_switch_in_fast);
            gifInFast.setTarget(mGifButton);
            gifInFast.start();
            AnimatorSet captureOutFast = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.capture_button_switch_out_fast);
            captureOutFast.setTarget(mCaptureButton);
            captureOutFast.start();
        }

        final Runnable takeGif = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mReady) {
                        if (counter == 32) {
                            throw new RuntimeException();
                        }
                        IC = 1;
                        counter++;
                        mReady = false;
                    }
                    handler.postDelayed(this, GIF_FRAME_DELAY);
                }catch (Exception e){
                    mDialog.setMessage("8 seconds over.\nAutosaving...");
                    mDialog.show();
                    mProcessingMode = FINAL_GIF_PROCESSING;
                    getSupportLoaderManager().restartLoader(getId(), null, MainActivity.this);
                }
            }
        };

        mGifButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (cameraMode == GIF_MODE) {
                            mProcessingMode = GIF_FRAME_PROCESSING;
                            mGifFrames.clear();
                            mModeIndicator.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.recording_indicator));
                            mModeIndicator.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.indicator_fade));
                            handler.postDelayed(takeGif, GIF_FRAME_DELAY);
                        } else {
                            gifIn.start();
                            captureOut.start();
                            mModeIndicator.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.gif_mode));
                            mModeIndicator.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.indicator_fade));
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (cameraMode == GIF_MODE) {
                            handler.removeCallbacks(takeGif);
                            new Handler().postDelayed(new Runnable(){
                                @Override
                                public void run() {
                                    mDialog.show();
                                }
                            },1);
                            mProcessingMode = FINAL_GIF_PROCESSING;
                            getSupportLoaderManager().restartLoader(getId(), null, MainActivity.this);
                        }else{
                            cameraMode = GIF_MODE;
                        }
                        break;
                }
                return true;
            }
        });

        //Checker to check if data for taking pic is available and then save the image
        checkIfProcessed = new intVar();
        checkIfProcessed.setListener(new intVar.changeListener() {
            @Override
            public void onChange() {
                if (mImageByteArray != null) {
                    if (mProcessingMode == GIF_FRAME_PROCESSING) {
                        getSupportLoaderManager().restartLoader(getId(),null,MainActivity.this);
                        mReady = true;
                    } else if(mProcessingMode == PHOTO_PROCESSING){
                        mPreview.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.capture_feedback));
                        getSupportLoaderManager().restartLoader(getId(), null, MainActivity.this);
                    }
                    checkIfProcessed.setVal(0);
                }
            }
        });


        //Checking if already have permission for camera and storage access
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int rs = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (rc != PackageManager.PERMISSION_GRANTED && rs != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA};
            requestPermission(permissions, RC_HANDLE_ALL_PERM, All_permission_message);
        } else if (rs != PackageManager.PERMISSION_GRANTED) {
            //If Storage permission not granted then request it
            String[] permissionsS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermission(permissionsS, RC_HANDLE_STORAGE_PERM, Storage_permission_message);
        } else if (rc == PackageManager.PERMISSION_GRANTED) {
            //If Camera permission granted already then start CameraSource
            createCameraSource();
        } else {
            //If Camera permission not granted already then Request the permission
            String[] permissionsC = new String[]{Manifest.permission.CAMERA};
            requestPermission(permissionsC, RC_HANDLE_CAMERA_PERM, Camera_permission_message);
        }
    }

    private void requestPermission(final String[] permissions, final int requestCode, String message) {

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
            return;
        }

        final Activity thisActivity = this;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(thisActivity, permissions, requestCode);
            }
        };

        View parentLayout = findViewById(R.id.parent);

        Snackbar.make(parentLayout,
                message,
                Snackbar.LENGTH_INDEFINITE)
                .setAction("Ok", listener)
                .setActionTextColor(ContextCompat.getColor(mContext,R.color.snackbar_action_text_color))
                .show();
    }

    private void createCameraSource() {
        Context context = getApplicationContext();
        FaceDetector faceDetector = new FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setProminentFaceOnly(false)
                .setTrackingEnabled(true)
                .setMinFaceSize((mCameraFacing == CameraSource.CAMERA_FACING_FRONT) ? 0.35f : 0.15f)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        MyFaceDetector myFaceDetector = new MyFaceDetector(this, faceDetector);

        myFaceDetector.setProcessor(new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory()).build());

        if (!myFaceDetector.isOperational()) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("API not found")
                    .setMessage("Make sure you have an internet connection and enough storage space.This is required only once.")
                    .setPositiveButton("Okay",listener)
                    .show();
        }

        mCameraSource = new CameraSource.Builder(this, myFaceDetector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(mCameraFacing)
                .setRequestedFps(10f)
                .setAutoFocusEnabled(true)
                .build();
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        loadPreview();
        switchCaptureButton(1);
        startCameraSource();

        //Reading shared preferences
        final SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        mRatingState = sharedPref.getInt("rating_state", ASK_FOR_RATING);
        int firstLaunch = sharedPref.getInt("first_time_launch",1);

        final int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        final int rs = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(rc == PackageManager.PERMISSION_GRANTED && rs == PackageManager.PERMISSION_GRANTED && firstLaunch == 1){

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("first_time_launch",0);
            editor.apply();

            TapTargetSequence tapTargetSequence = new TapTargetSequence(this)
                    .targets(
                            TapTarget.forView(findViewById(R.id.filters_list),"Select overlays from here")
                                    .outerCircleColor(R.color.showcase_outer_circle)
                                    .tintTarget(false),
                            TapTarget.forView(findViewById(R.id.capture_button),"Use capture button to take pictures")
                                    .outerCircleColor(R.color.showcase_outer_circle),
                            TapTarget.forView(findViewById(R.id.gif_button),"Tap to switch to GIF mode. Then tap and hold to record. Leave to save.")
                                    .outerCircleColor(R.color.showcase_outer_circle),
                            TapTarget.forView(findViewById(R.id.switch_camera),"Tap to switch between back and front camera")
                                    .outerCircleColor(R.color.showcase_outer_circle)
                    );

            tapTargetSequence.start();
        }
    }

    public void startCameraSource() {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());

        if (code != ConnectionResult.SUCCESS) {

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dialog.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCameraSource != null) {
            mCameraSource.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length == 0 || (requestCode != RC_HANDLE_CAMERA_PERM && requestCode != RC_HANDLE_STORAGE_PERM && requestCode != RC_HANDLE_ALL_PERM)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        String message = "Something went wrong while obtaining permissions.Infinity Filters will now exit.\nPlease restart the app";
        if (requestCode == RC_HANDLE_ALL_PERM) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                createCameraSource();
                return;
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }else{
                message = "Couldn't obtain storage permissions.Infinity Filters will now exit.";
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                createCameraSource();
            }else{
                message = "Couldn't obtain camera permissions.Infinity Filters will now exit.";
            }
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED){
                message = "Couldn't obtain any necessary permissions.Infinity Filters will now exit";
            }
        }

        if (requestCode == RC_HANDLE_CAMERA_PERM) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                createCameraSource();
                return;
            }else{
                message = "Couldn't obtain camera permissions.Infinity Filters will now exit.";
            }
        }

        if (requestCode == RC_HANDLE_STORAGE_PERM) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                createCameraSource();
                return;
            }else{
                message = "Couldn't obtain storage permissions.Infinity Filters will now exit.";
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Ok", listener)
                .setTitle("InfinityFilters")
                .setMessage(message)
                .show();
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay, mContext,mCameraFacing);
        }
    }

    public void takeAPicture(View view) {

        if (cameraMode == CAPTURE_MODE) {
            if (mReady) {
                mProcessingMode = PHOTO_PROCESSING;
                IC = 1;
                mReady = false;
            }
        } else {
            gifOut.start();
            captureIn.start();
            mModeIndicator.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.photo_mode));
            mModeIndicator.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.indicator_fade));
            cameraMode = CAPTURE_MODE;
        }
    }

    @Override
    public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
        mLoaderIds[id] = id;
        int flag = 0;
        for (int i = 0; i < MAX_LOADER_COUNT; i++) {
            if (mLoaderIds[i] == LOADER_AVAILABLE) {
                flag = 1;
                break;
            }
        }
        if (flag == 0) {
            switchCaptureButton(0);
        }

        if (mProcessingMode == PHOTO_PROCESSING ) {
            byte[] imageByteArray = mImageByteArray;
            mImageByteArray = null;
            mReady = true;
            return new ImageSaveLoader(this, imageByteArray, mFaceOverlays, mRotation, mProcessingMode,mCameraFacing);
        } else if(mProcessingMode == GIF_FRAME_PROCESSING) {
            mReady = true;
            return new ImageSaveLoader(this, mImageByteArray, mFaceOverlays, mRotation, mProcessingMode,mCameraFacing);
        }else {
            return new ImageSaveLoader(this,mGifFrames,mProcessingMode);
        }
    }

    @Override
    public void onLoadFinished(Loader<Bitmap> loader, Bitmap preview) {
        switchCaptureButton(1);
        int id = loader.getId();
        mLoaderIds[id] = LOADER_AVAILABLE;
        if(preview != null){
            if(mProcessingMode == GIF_FRAME_PROCESSING){
                mGifFrames.add(preview);
                mFaceOverlays.clear();
            }else {
                if(mProcessingMode == FINAL_GIF_PROCESSING){
                    counter = 1;
                    mReady = true;
                    mDialog.dismiss();
                }
                mPreviewImage.setImageBitmap(preview);
                faceCounter = 1;
                mFaceOverlays.clear();
            }
        }else{
            mDialog.dismiss();
        }
    }

    @Override
    public void onLoaderReset(Loader<Bitmap> loader) {
    }

    public void openPreview(View view) {
        try {
            final String[] projection = new String[]{"DISTINCT "
                    + MediaStore.Images.Media.BUCKET_DISPLAY_NAME + ", "
                    + MediaStore.Images.Media.DATA};

            String[] selectionArgs = new String[]{"InfinityFilters"};
            final Cursor cur = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ?",
                    selectionArgs,
                    "_ID DESC");

            String path = "";
            if (cur != null) {
                cur.moveToFirst();
                path = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));
                cur.close();
            }

            File file = new File(path);
            Uri photoUri = FileProvider.getUriForFile(this, getString(R.string.file_provider_authority), file);
            Intent i = new Intent(Intent.ACTION_VIEW, photoUri);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(i);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "No photos found", Toast.LENGTH_SHORT).show();
        }
    }

    private void switchCaptureButton(int value) {
        if (value == 1) {
            mCaptureButton.setEnabled(true);
        } else if (value == 0) {
            mCaptureButton.setEnabled(false);
        }
    }

    public static void setImageByteArray(byte[] imageByteArray, int rotation) {
        mImageByteArray = imageByteArray;
        mRotation = rotation;
    }

    public static void setOverlayBitmap(Bitmap overlayBitmap) {
        if(faceCounter < faceCount){
            mFaceOverlays.add(overlayBitmap);
            faceCounter++;
        }else {
            mFaceOverlays.add(overlayBitmap);
            checkIfProcessed.setVal(1);
        }
    }

    public int getId() {
        int id;
        for (id = 0; id < MAX_LOADER_COUNT; id++) {
            if (mLoaderIds[id] == LOADER_AVAILABLE) {
                break;
            }
        }
        return id;
    }

    private void loadPreview(){
        //Set the preview image from the storage
        try {
            long id = 1;
            final String[] projection = new String[]{"DISTINCT "
                    + MediaStore.Images.Media.BUCKET_DISPLAY_NAME + ", "
                    + MediaStore.Images.Media._ID};

            String[] selectionArgs = new String[]{"InfinityFilters"};
            final Cursor cur = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ?",
                    selectionArgs,
                    "_ID DESC");

            if (cur != null) {
                mPreviewImage.setVisibility(View.VISIBLE);
                cur.moveToFirst();
                id = cur.getLong(cur.getColumnIndex(MediaStore.Images.Media._ID));
                cur.close();
            }

            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                    getContentResolver(), id,
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    (BitmapFactory.Options) null);
            if (bitmap != null) {
                mPreviewImage.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            mPreviewImage.setImageDrawable(null);
        }
    }

    public void switchCamera(View view){
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
        if(mCameraFacing == CameraSource.CAMERA_FACING_FRONT){
            mCameraFacing = CameraSource.CAMERA_FACING_BACK;
        }else{
            mCameraFacing = CameraSource.CAMERA_FACING_FRONT;
        }
        createCameraSource();
        startCameraSource();
    }

    public void openSettings(View view){
        Intent intent = new Intent(this,Settings.class);
        startActivity(intent);
    }

    public void askForRatings(){
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }

        builder.setTitle("Rate it")
                .setMessage("If you like using Infinity Filters please take a moment to rate it. Thanks for your support!")
                .setPositiveButton("Rate Infinity Filters", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putInt("rating_state",DO_NOT_ASK);
                        mRatingState = DO_NOT_ASK;
                        editor.apply();
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.InfinitySolutions.InfinityFilters")));
                    }
                })
                .setNeutralButton("Remind me later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRatingState = DO_NOT_ASK;
                    }
                })
                .setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putInt("rating_state",DO_NOT_ASK);
                        editor.apply();
                        mRatingState = DO_NOT_ASK;
                    }
                })
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("camera_direction",mCameraFacing);
        savedInstanceState.putInt("filter_type",filterType);
        savedInstanceState.putInt("camera_mode",cameraMode);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if(mRatingState == ASK_FOR_RATING){
            askForRatings();
        }else{
            super.onBackPressed();
        }
    }

}