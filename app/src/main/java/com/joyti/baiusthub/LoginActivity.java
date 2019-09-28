package com.joyti.baiusthub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity {


    private EditText loginUserName, loginPassword;
    private Button loginBtn;
    private TextView forgetPassword, registrationLink;
    private String email, password;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        sharedPreferences = getSharedPreferences("user_details",MODE_PRIVATE);

        loginUserName = findViewById(R.id.loginUserName);
        loginPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        forgetPassword = findViewById(R.id.forgetPassword);
        registrationLink = findViewById(R.id.registrationLink);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = loginUserName.getText().toString();
                password = loginPassword.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                    new LoginCheck(LoginActivity.this).execute(email, password);

                }

            }
        });


        registrationLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }


    public class LoginCheck extends AsyncTask<String, Void,String> {

        //AlertDialog dialog;
        Context context;

        LoginCheck(Context context) {
            this.context = context;
        }



        @Override
        protected void onPreExecute() {
            //dialog = new AlertDialog.Builder(context).create();
            //dialog.setTitle("Please wait..");
        }



        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();
            String check_user_url = "http://baiusthub.mygamesonline.org/login.php";

            String personEmail = params[0];
            String personPassword = params[1];


            try {
                URL url = new URL(check_user_url);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, StandardCharsets.UTF_8));
                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(personEmail, "UTF-8")
                        +"&&"+URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(personPassword, "UTF-8");
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

            try {

                JSONObject userObject = new JSONObject(s);
                final String id = userObject.getString("id");
                final String email = userObject.getString("username");
                final String password = userObject.getString("password");
                final String status = userObject.getString("status");

                if("yes".equals(status)){

                    String textusername = loginUserName.getText().toString();
                    String textpassword = loginPassword.getText().toString();

                    if(textusername.equals(email) && textpassword.equals(password)){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("password",password);
                        editor.putString("email",email);
                        editor.putString("id",id);
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Login Successful",Toast.LENGTH_SHORT).show();
                        SendUserToMainActivity();
                    }

                    else{

                        Toast.makeText(context, "Credentials are not valid!", Toast.LENGTH_SHORT).show();

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

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

}
