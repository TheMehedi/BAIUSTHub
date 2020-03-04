package com.joyti.baiusthub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.race604.drawable.wave.WaveDrawable;

public class IntroActivity extends AppCompatActivity {

    private ImageView imageView;
    private Drawable mWaveDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        imageView = findViewById(R.id.imageView);

        mWaveDrawable = new WaveDrawable(this,R.drawable.logo);


        // Use as common drawable
        imageView.setImageDrawable(mWaveDrawable);

        mWaveDrawable.setLevel(5000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(IntroActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}
