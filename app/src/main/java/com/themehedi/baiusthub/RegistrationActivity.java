package com.themehedi.baiusthub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

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

                //Toast.makeText(this, personName + personEmail + personPassword + personUserName + rPhoneNumber, Toast.LENGTH_SHORT).show();


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
                        +"&&"+URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(personPassword, "UTF-8")
                        +"&&"+URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(personUserName, "UTF-8");
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
                reader.close();
                ips.close();
                http.disconnect();
                return result.toString();


            } catch (IOException e) {
                result = new StringBuilder(e.getMessage());
            }



            return result.toString();

        }


        @Override
        protected void onPostExecute(String s) {
            //dialog.setMessage(s);
            //dialog.show();

            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

            try {

                JSONObject userObject = new JSONObject(s);
                final String id = userObject.getString("id");
                final String emailstatus = userObject.getString("emailstatus");
                final String status = userObject.getString("status");
                final String femail = userObject.getString("email");
                final String fpassword = userObject.getString("password");
                final String username = userObject.getString("username");

                if("emailexist".equals(emailstatus)){

                    Toast.makeText(context, "This email is in use by another account!", Toast.LENGTH_SHORT).show();
                }



                else if("yes".equals(status)){

                    String textusername = email.getText().toString();
                    String textpassword = password.getText().toString();

                    if(textusername.equals(femail) && textpassword.equals(fpassword)){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username",username);
                        editor.putString("password",textpassword);
                        editor.putString("email",textusername);
                        editor.putString("id",id);
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Registration Successful",Toast.LENGTH_SHORT).show();
                        SendUserToMainActivity();
                    }

                }

                else{

                    Toast.makeText(context, "Credentials are not valid!", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }


    private void SendUserToMainActivity() {

        Intent loginIntent = new Intent(RegistrationActivity.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }



}