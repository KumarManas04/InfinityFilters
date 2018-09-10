package com.InfinitySolutions.InfinityFilters;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.IOException;

public class Settings extends AppCompatActivity {

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
}
