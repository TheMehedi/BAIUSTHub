package com.joyti.baiusthub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class RegistrationActivity extends AppCompatActivity {

    private TextView loginLink;
    private EditText email, password, confirmPassword;
    private Button nextBtn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        //declaration
        loginLink = findViewById(R.id.loginLink);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        nextBtn = findViewById(R.id.nextBtn);

        sharedPreferences = getSharedPreferences("user_details",MODE_PRIVATE);
        if(sharedPreferences.contains("username") && sharedPreferences.contains("password")){
            SendUserToMainActivity();
        }

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });



        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateUI();
            }
        });


    }



    private void updateUI() {


        String personEmail = email.getText().toString();
        String personPassword = password.getText().toString();
        String confirmPass = confirmPassword.getText().toString();


        if(personPassword.equals(confirmPass)){

            if(!TextUtils.isEmpty(personEmail) || !TextUtils.isEmpty(personPassword)){

                //Toast.makeText(this, personEmail + personPassword + personUserName, Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(RegistrationActivity.this, ProfileSetupActivity.class);
                intent.putExtra("email",personEmail);
                intent.putExtra("password",personPassword);
                startActivity(intent);

            }
            else{

                Toast.makeText(this, "Please fill all the field!", Toast.LENGTH_SHORT).show();

            }
        }

        else{

            Toast.makeText(this, "Password doesn't match!", Toast.LENGTH_SHORT).show();
        }



    }



    private void SendUserToMainActivity() {

        Intent loginIntent = new Intent(RegistrationActivity.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }



}