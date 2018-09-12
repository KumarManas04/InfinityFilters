package com.InfinitySolutions.InfinityFilters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import static com.InfinitySolutions.InfinityFilters.Contract.ValuesContract.DO_NOT_ASK;

public class Settings extends AppCompatActivity {

    private int count = 0;
    private long startMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void suggestFeature(View view){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // Set type to "email"
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"infinitysolutionsv1.1@gmail.com"};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        // the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Feature suggestion");
        startActivity(Intent.createChooser(emailIntent , "Suggest feature"));
    }

    public void reportBug(View view){
        // save logcat in file
        File outputFile = new File(Environment.getExternalStorageDirectory(),
                "logcat.txt");
        try {
            Runtime.getRuntime().exec(
                    "logcat -f " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //send file using email
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // Set type to "email"
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"infinitysolutionsv1.1@gmail.com"};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
        Uri bugFileUri = FileProvider.getUriForFile(this, getString(R.string.file_provider_authority), outputFile);
        emailIntent .putExtra(Intent.EXTRA_STREAM, bugFileUri);
        // the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Issue report");
        startActivity(Intent.createChooser(emailIntent , "Bug report"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        if(eventAction == MotionEvent.ACTION_UP){
            long time = System.currentTimeMillis();

            if(startMillis == 0 || (time - startMillis > 2000)){
                startMillis = time;
                count = 1;
            }else{
                count++;
            }
            Log.d("HeyBuddy","count = " + count);

            if(count == 5){
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(Settings.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(Settings.this);
                }

                builder.setTitle("Developer Details")
                        .setMessage("Solo Developer : Kumar Manas")
                        .setCancelable(false)
                        .setPositiveButton("Okay", null)
                        .show();
            }

            return true;
        }
        return false;
    }
}
