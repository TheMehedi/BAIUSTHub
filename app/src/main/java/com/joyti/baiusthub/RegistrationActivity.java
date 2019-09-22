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
    private Button registrationBtn;
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
        registrationBtn = findViewById(R.id.registrationBtn);

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



        registrationBtn.setOnClickListener(new View.OnClickListener() {
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

                //uses email for creation of default user name until '@' founded
                String personUserName = personEmail.substring(0, personEmail.indexOf("@"));

                //Toast.makeText(this, personEmail + personPassword + personUserName, Toast.LENGTH_SHORT).show();


                new AccountSetup(RegistrationActivity.this).execute(personEmail, personPassword, personUserName);

            }
            else{

                Toast.makeText(this, "Please fill all the field!", Toast.LENGTH_SHORT).show();

            }
        }

        else{

            Toast.makeText(this, "Password doesn't match!", Toast.LENGTH_SHORT).show();
        }



    }


    public class AccountSetup extends AsyncTask<String, Void,String> {

        //AlertDialog dialog;
        Context context;
        String datas = "";

        AccountSetup(Context context) {
            this.context = context;
        }



        @Override
        protected void onPreExecute() {



        }



        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();
            String check_user_url = "http://baiusthub.epizy.com/assets/backend/registration.php";

            String personEmail = params[0];
            String personPassword = params[1];
            String personUserName = params[2];


            try {
                URL url = new URL(check_user_url);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, StandardCharsets.UTF_8));
                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(personEmail, "UTF-8")
                        + "&&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(personPassword, "UTF-8")
                        + "&&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(personUserName, "UTF-8");
                writer.write(data);
                writer.flush();
                writer.close();
                ops.close();

                InputStream ips = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips, StandardCharsets.ISO_8859_1));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                datas = result.toString();
                reader.close();
                ips.close();
                http.disconnect();


            }catch (IOException e) {
                e.printStackTrace();
            }


            return datas;

        }


        @Override
        protected void onPostExecute(String s) {
            //dialog.setMessage(s);
            //dialog.show();

            Toast.makeText(context, Html.fromHtml(s), Toast.LENGTH_SHORT).show();



        }


    }


    private void SendUserToMainActivity() {

        Intent loginIntent = new Intent(RegistrationActivity.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }



}