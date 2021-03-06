package com.joyti.baiusthub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.Objects;

public class ProfileSetupActivity extends AppCompatActivity {

    private String rEmail, rPassword, accountType = "", name, aid, department, session;
    private EditText Name, Department, Id, Session;
    private Button registrationBtn;
    private SharedPreferences sharedPreferences;
    private LinearLayout linearLayoutAID, linearLayoutSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);


        sharedPreferences = getSharedPreferences("user_details",MODE_PRIVATE);
        if(sharedPreferences.contains("email") && sharedPreferences.contains("password")){
            SendUserToMainActivity();
        }

        //retrieving data from registration activity
        rEmail = Objects.requireNonNull(getIntent().getExtras()).getString("email","");
        rPassword = Objects.requireNonNull(getIntent().getExtras()).getString("password","");


        //declaration
        Name = findViewById(R.id.name);
        Department = findViewById(R.id.department);
        Id = findViewById(R.id.id);
        Session = findViewById(R.id.session);
        registrationBtn = findViewById(R.id.registrationBtn);
        linearLayoutAID = findViewById(R.id.linearLayoutAID);
        linearLayoutSession = findViewById(R.id.linearLayoutSession);

        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateUI();

            }
        });


        Spinner mySpinner = findViewById(R.id.spinner);
        final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(ProfileSetupActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.account_type));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //record = myAdapter.getItem(position);

                accountType = myAdapter.getItem(position);

                if(accountType.equals("Student")){

                    linearLayoutAID.setVisibility(View.VISIBLE);
                    linearLayoutSession.setVisibility(View.VISIBLE);
                }

                else if(accountType.equals("Faculty")){

                    aid = "";
                    session = "";
                    linearLayoutAID.setVisibility(View.GONE);
                    linearLayoutSession.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //record = "All";

            }
        });

    }


    private void updateUI() {


        name = Name.getText().toString();
        department = Department.getText().toString();
        aid = Id.getText().toString();
        session = Session.getText().toString();


        if(accountType.equals("Faculty")){

            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(department)){

                aid = "";
                session = "";

                new RegistrationProcess(ProfileSetupActivity.this).execute(name, department, aid, session);

            }
            else{

                Toast.makeText(this, "Please fill all the field!", Toast.LENGTH_SHORT).show();

            }

        }

        else {

            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(department) && !TextUtils.isEmpty(aid) && !TextUtils.isEmpty(session)){


                new RegistrationProcess(ProfileSetupActivity.this).execute(name, department, aid, session);

            }
            else{

                Toast.makeText(this, "Please fill all the field!", Toast.LENGTH_SHORT).show();

            }
        }






    }




    private class RegistrationProcess extends AsyncTask<String, Void,String> {

        Context context;

        RegistrationProcess(Context context) {
            this.context = context;
        }


        @Override
        protected void onPreExecute() {
            //dialog = new AlertDialog.Builder(context).create();
            //dialog.setTitle("Please wait..");
        }



        @Override
        protected String doInBackground(String... parameter) {

            String check_user_url = "http://baiusthub.mygamesonline.org/registration.php";
            String name = parameter[0];
            String department = parameter[1];
            String id = parameter[2];
            String session = parameter[3];


            StringBuilder result = new StringBuilder();


            try {
                URL url = new URL(check_user_url);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, StandardCharsets.UTF_8));
                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(rEmail, "UTF-8")
                        + "&&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(rPassword, "UTF-8")
                        + "&&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
                        + "&&" + URLEncoder.encode("department", "UTF-8") + "=" + URLEncoder.encode(department, "UTF-8")
                        + "&&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(accountType, "UTF-8")
                        + "&&" + URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                        + "&&" + URLEncoder.encode("session", "UTF-8") + "=" + URLEncoder.encode(session, "UTF-8");
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
                final String emailstatus = userObject.getString("emailstatus");
                final String idstatus = userObject.getString("idstatus");
                final String status = userObject.getString("status");
                final String femail = userObject.getString("email");
                final String fpassword = userObject.getString("password");
                final String type = userObject.getString("type");

                if("emailexist".equals(emailstatus)){

                    Toast.makeText(context, "This email is in use by another account!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }

                else if("idexist".equals(idstatus)){

                    Toast.makeText(context, "This academic id is in use by another account!", Toast.LENGTH_SHORT).show();
                }


                else if("yes".equals(status)){


                    if(rEmail.equals(femail) && rPassword.equals(fpassword)){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email",rEmail);
                        editor.putString("password",rPassword);
                        editor.putString("id",id);
                        editor.putString("type",type);
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

        Intent loginIntent = new Intent(ProfileSetupActivity.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }


}
