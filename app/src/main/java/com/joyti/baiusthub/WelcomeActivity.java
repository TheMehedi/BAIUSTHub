package com.joyti.baiusthub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SharedMemory;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        sharedPreferences = getSharedPreferences("user_details",MODE_PRIVATE);
        if(sharedPreferences.contains("email") && sharedPreferences.contains("password")){
            SendUserToMainActivity();
        }

    }


    public void goToLogin(View view) {

        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToRegistration(View view) {

        Intent intent = new Intent(WelcomeActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void SendUserToMainActivity() {

        Intent loginIntent = new Intent(WelcomeActivity.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}
