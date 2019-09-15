package com.themehedi.baiusthub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {


    private EditText loginUserName, loginPassword;
    private Button loginBtn;
    private TextView forgetPassword, registrationLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        loginUserName = findViewById(R.id.loginUserName);
        loginPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        forgetPassword = findViewById(R.id.forgetPassword);
        registrationLink = findViewById(R.id.registrationLink);

    }
}
