package com.joyti.baiusthub;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class UploadFileActivity extends AppCompatActivity {

    private static final int PICK_FILE = 1;
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private Button chooseFilebtn, uploadBtn;
    private TextView chooseFileText;
    private String img = "null", user_id, department, course, teacher, category,semester, type = "";
    private ArrayList<Uri> FileList = new ArrayList<>();
    private ArrayList<String> NameList = new ArrayList<>();
    private ArrayList<String> ExtensionList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private LinearLayout linearLayoutFileType;
    private int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        user_id = sharedPreferences.getString("id",null);

        //declaration
        chooseFilebtn = findViewById(R.id.chooseFileBtn);
        chooseFileText = findViewById(R.id.chooseFileText);
        uploadBtn = findViewById(R.id.uploadBtn);
        linearLayoutFileType = findViewById(R.id.linearLayoutFileType);



        Spinner mySpinner1 = findViewById(R.id.spinner1);
        final ArrayAdapter<String> myAdapter1 = new ArrayAdapter<>(UploadFileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.department));

        myAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner1.setAdapter(myAdapter1);

        mySpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //record = myAdapter.getItem(position);
                department = myAdapter1.getItem(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //record = "All";

            }
        });



        Spinner mySpinner2 = findViewById(R.id.spinner2);
        final ArrayAdapter<String> myAdapter2 = new ArrayAdapter<>(UploadFileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.course));

        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner2.setAdapter(myAdapter2);

        mySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //record = myAdapter.getItem(position);

                course = myAdapter2.getItem(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //record = "All";

            }
        });



        Spinner mySpinner3 = findViewById(R.id.spinner3);
        final ArrayAdapter<String> myAdapter3 = new ArrayAdapter<>(UploadFileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.teacher));

        myAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner3.setAdapter(myAdapter3);

        mySpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //record = myAdapter.getItem(position);

                teacher = myAdapter3.getItem(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //record = "All";

            }
        });

        Spinner mySpinner6 = findViewById(R.id.spinner6);
        final ArrayAdapter<String> myAdapter6 = new ArrayAdapter<>(UploadFileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.semester));

        myAdapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner6.setAdapter(myAdapter6);

        mySpinner6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //record = myAdapter.getItem(position);

                semester = myAdapter6.getItem(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //record = "All";

            }
        });



        Spinner mySpinner4 = findViewById(R.id.spinner4);
        final ArrayAdapter<String> myAdapter4 = new ArrayAdapter<>(UploadFileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.file_category));

        myAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner4.setAdapter(myAdapter4);

        mySpinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //record = myAdapter.getItem(position);

                category = myAdapter4.getItem(position);


                if (category.equals("Presentation Slide")){

                    linearLayoutFileType.setVisibility(View.GONE);
                }


                else if(category.equals("Lecture Notes")){

                    linearLayoutFileType.setVisibility(View.VISIBLE);

                    type = "";

                    Spinner mySpinner5 = findViewById(R.id.spinner5);
                    final ArrayAdapter<String> myAdapter5 = new ArrayAdapter<>(UploadFileActivity.this,
                            android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.file_type1));

                    myAdapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mySpinner5.setAdapter(myAdapter5);

                    mySpinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            //record = myAdapter.getItem(position);

                            type = myAdapter5.getItem(position);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                            //record = "All";

                        }
                    });

                }

                else if(category.equals("Previous Questions")){

                    linearLayoutFileType.setVisibility(View.VISIBLE);

                    type = "";

                    Spinner mySpinner5 = findViewById(R.id.spinner5);
                    final ArrayAdapter<String> myAdapter5 = new ArrayAdapter<>(UploadFileActivity.this,
                            android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.file_type2));

                    myAdapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mySpinner5.setAdapter(myAdapter5);

                    mySpinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            //record = myAdapter.getItem(position);

                            type = myAdapter5.getItem(position);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                            //record = "All";

                        }
                    });
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //record = "All";
                linearLayoutFileType.setVisibility(View.GONE);


            }
        });


        chooseFilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                checkPermission();
            }
        });


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(!department.equals("") && !course.equals("") && !teacher.equals("") && !category.equals("") && !semester.equals("") && counter>0){


                    if(category.equals("Lecture Notes") || category.equals("Previous Questions")){

                        if (!type.equals("")){

                            for(int j = 0; j<FileList.size(); j++){

                                Uri PerFile = FileList.get(j);

                                final String uniqname = generateRandomString(10) + ExtensionList.get(j);
                                final String name = NameList.get(j);

                                StorageReference folder = FirebaseStorage.getInstance().getReference().child(department);
                                final StorageReference filename = folder.child(uniqname);

                                //Toast.makeText(UploadFileActivity.this, ExtensionList.get(j), Toast.LENGTH_SHORT).show();
                                filename.putFile(PerFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        filename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                new UploadFile(getApplicationContext()).execute(uri.toString(), uniqname, name);

                                                //Toast.makeText(UploadFileActivity.this, uri.toString() + uniqname, Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    }
                                });
                            }

                            Toast.makeText(UploadFileActivity.this, "All File Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }

                        else{

                            Toast.makeText(UploadFileActivity.this, "Select all field!", Toast.LENGTH_SHORT).show();
                        }
                    }


                    else {

                        for(int j = 0; j<FileList.size(); j++){

                            Uri PerFile = FileList.get(j);

                            final String uniqname = generateRandomString(10) + ExtensionList.get(j);
                            final String name = NameList.get(j);

                            StorageReference folder = FirebaseStorage.getInstance().getReference().child(department);
                            final StorageReference filename = folder.child(uniqname);

                            //Toast.makeText(UploadFileActivity.this, ExtensionList.get(j), Toast.LENGTH_SHORT).show();
                            filename.putFile(PerFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    filename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            new UploadFile(getApplicationContext()).execute(uri.toString(), uniqname, name);

                                            //Toast.makeText(UploadFileActivity.this, uri.toString() + uniqname, Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            });
                        }

                        Toast.makeText(UploadFileActivity.this, "All File Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }



                }

                else {

                    Toast.makeText(UploadFileActivity.this, "Select all field!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }




    public void checkPermission(){

        if(ActivityCompat.checkSelfPermission(UploadFileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(UploadFileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            return;
        }

        ExtensionList.clear();
        NameList.clear();
        FileList.clear();
        pickFile();
    }


    private void pickFile() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_FILE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == PICK_FILE){

            if(resultCode == RESULT_OK){

                if(data.getClipData() != null){

                    int count = data.getClipData().getItemCount();
                    counter = count;

                    chooseFileText.setText(count + " files");

                    int i = 0;

                    while ((i < count)){

                        Uri file = data.getClipData().getItemAt(i).getUri();
                        String filename = "";
                        File name = new File(file.toString());

                        if (file.toString().startsWith("content://")) {
                            try (Cursor cursor = getApplicationContext().getContentResolver().query(file, null, null, null, null)) {
                                if (cursor != null && cursor.moveToFirst()) {
                                    filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                }
                            }
                        } else if (file.toString().startsWith("file://")) {
                            filename = name.getName();
                        }


                        String extension = filename.substring(filename.lastIndexOf("."));

                        //Toast.makeText(this, extension, Toast.LENGTH_SHORT).show();
                        FileList.add(file);
                        NameList.add(filename);
                        ExtensionList.add(extension);
                        i++;
                    }

                }

                else {

                    counter = 1;
                    chooseFileText.setText("1 files");

                    Uri file = data.getData();

                    String filename = "";
                    File name = new File(file.toString());

                    if (file.toString().startsWith("content://")) {
                        try (Cursor cursor = getApplicationContext().getContentResolver().query(file, null, null, null, null)) {
                            if (cursor != null && cursor.moveToFirst()) {
                                filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        }
                    } else if (file.toString().startsWith("file://")) {
                        filename = name.getName();
                    }


                    String extension = filename.substring(filename.lastIndexOf("."));
                    //Toast.makeText(this, extension, Toast.LENGTH_SHORT).show();
                    FileList.add(file);
                    NameList.add(filename);
                    ExtensionList.add(extension);
                    /*File filename= new File(data.getData().getPath());
                    String extension = filename.getName().substring(filename.getName().lastIndexOf(".") + 1);
                    Toast.makeText(this, extension, Toast.LENGTH_SHORT).show();
                    NameList.add("." + extension);*/
                }
            }
        }
    }



    private class UploadFile extends AsyncTask<String, Void,String> {

        Context context;

        UploadFile(Context context) {
            this.context = context;
        }


        @Override
        protected void onPreExecute() {


        }



        @Override
        protected String doInBackground(String... parameter) {

            String check_user_url = "http://baiusthub.mygamesonline.org/upload_file.php";
            String uri = parameter[0];
            String uniqname = parameter[1];
            String name = parameter[2];

            StringBuilder result = new StringBuilder();


            try {
                URL url = new URL(check_user_url);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, StandardCharsets.UTF_8));
                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8")
                        + "&&" + URLEncoder.encode("department", "UTF-8") + "=" + URLEncoder.encode(department, "UTF-8")
                        + "&&" + URLEncoder.encode("course", "UTF-8") + "=" + URLEncoder.encode(course, "UTF-8")
                        + "&&" + URLEncoder.encode("teacher", "UTF-8") + "=" + URLEncoder.encode(teacher, "UTF-8")
                        + "&&" + URLEncoder.encode("category", "UTF-8") + "=" + URLEncoder.encode(category, "UTF-8")
                        + "&&" + URLEncoder.encode("semester", "UTF-8") + "=" + URLEncoder.encode(semester, "UTF-8")
                        + "&&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8")
                        + "&&" + URLEncoder.encode("uri", "UTF-8") + "=" + URLEncoder.encode(uri, "UTF-8")
                        + "&&" + URLEncoder.encode("uniqname", "UTF-8") + "=" + URLEncoder.encode(uniqname, "UTF-8")
                        + "&&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
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


            if(s.equals("yes")){

                Toast.makeText(context, "File Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UploadFileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }



        }

    }


    public String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }

        return builder.toString();
    }

    public void backBtn(View view) {
        onBackPressed();
    }
}
