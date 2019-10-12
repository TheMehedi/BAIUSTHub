package com.joyti.baiusthub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class ProfileViewActivity extends AppCompatActivity {

    private LinearLayout logoutBtn;
    private TextView myName, myId, myEmail, myDepartment, mySession;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        sharedPreferences = getSharedPreferences("user_details",MODE_PRIVATE);


        //declaration
        myName = findViewById(R.id.myName);
        myId = findViewById(R.id.myId);
        myEmail = findViewById(R.id.myEmail);
        myDepartment = findViewById(R.id.myDepartment);
        mySession = findViewById(R.id.mySession);
        logoutBtn = findViewById(R.id.logoutBtn);


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                SendUserToLoginActivity();

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        new CurrentP(getApplicationContext()).execute();
    }

    public void backBtn(View view) {

        onBackPressed();
    }


    private class CurrentP extends AsyncTask<String, Void,String> {

        Context context;

        CurrentP(Context context) {
            this.context = context;
        }


        @Override
        protected void onPreExecute() {
            //dialog = new AlertDialog.Builder(context).create();
            //dialog.setTitle("Please wait..");
        }



        @Override
        protected String doInBackground(String... parameter) {

            String check_user_url = "http://baiusthub.mygamesonline.org/profileview.php";


            StringBuilder result = new StringBuilder();
            String user = sharedPreferences.getString("id", null);


            try {
                URL url = new URL(check_user_url);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, StandardCharsets.UTF_8));
                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8");
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


            try {

                JSONObject userObject = new JSONObject(s);
                final String id = userObject.getString("id");
                final String name = userObject.getString("full_name");
                final String email = userObject.getString("email");
                final String aid = userObject.getString("aid");
                final String department = userObject.getString("department");
                final String session = userObject.getString("session");


                myName.setText(name);
                myId.setText(aid);
                myEmail.setText(email);
                myDepartment.setText(department);
                mySession.setText(session);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(ProfileViewActivity.this, WelcomeActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

}
