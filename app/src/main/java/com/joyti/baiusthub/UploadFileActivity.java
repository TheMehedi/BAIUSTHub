package com.joyti.baiusthub;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UploadFileActivity extends AppCompatActivity {

    private Button chooseFilebtn, uploadBtn;
    private TextView chooseFileText;
    private Bitmap bitmap, converetdImage;
    private String img = "null", stringImage, user_id, department, course, teacher, category;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);


        //declaration
        chooseFilebtn = findViewById(R.id.chooseFileBtn);
        chooseFileText = findViewById(R.id.chooseFileText);
        uploadBtn = findViewById(R.id.uploadBtn);



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

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //record = "All";

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


                if(!department.equals("") && !course.equals("") && !teacher.equals("") && !category.equals("")){

                    new UploadFile(UploadFileActivity.this).execute();

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

        pickImage();
    }


    private void pickImage() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data != null){

                Uri path = data.getData();
                imageUri = path;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                    converetdImage = getResizedBitmap(bitmap, 100);
                    stringImage = imageToString();


                } catch (IOException e) {
                    e.printStackTrace();
                }



        }

        showImages();

        chooseFileText.setText(getFileName(imageUri));
    }

    private void showImages() {


            img = stringImage;


    }

    private String imageToString(){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte,Base64.DEFAULT);
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }



    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
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


            StringBuilder result = new StringBuilder();


            try {
                URL url = new URL(check_user_url);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, StandardCharsets.UTF_8));
                String data = URLEncoder.encode("file", "UTF-8") + "=" + URLEncoder.encode(img, "UTF-8")
                        + "&&" + URLEncoder.encode("department", "UTF-8") + "=" + URLEncoder.encode(department, "UTF-8")
                        + "&&" + URLEncoder.encode("course", "UTF-8") + "=" + URLEncoder.encode(course, "UTF-8")
                        + "&&" + URLEncoder.encode("teacher", "UTF-8") + "=" + URLEncoder.encode(teacher, "UTF-8")
                        + "&&" + URLEncoder.encode("category", "UTF-8") + "=" + URLEncoder.encode(category, "UTF-8");
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



    public void backBtn(View view) {
        onBackPressed();
    }
}
