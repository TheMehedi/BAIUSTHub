package com.joyti.baiusthub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton profileBtn;
    private LinearLayout exploreFile, uploadFile, aboutApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        profileBtn = findViewById(R.id.profileBtn);
        exploreFile = findViewById(R.id.exploreFile);
        uploadFile = findViewById(R.id.uploadFile);
        aboutApp = findViewById(R.id.aboutApp);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainActivity.this, ProfileViewActivity.class);
                startActivity(intent);
            }
        });


        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, UploadFileActivity.class);
                startActivity(intent);

            }
        });

    }
}
