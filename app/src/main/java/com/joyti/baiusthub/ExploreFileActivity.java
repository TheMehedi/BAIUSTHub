package com.joyti.baiusthub;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ExploreFileActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String[] mId = null;
    private String[] mFileName = null;
    private String[] mDeptName = null;
    private String[] mCourseName = null;
    private String[] mDownloadLink = null;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_file);

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        listView = findViewById(R.id.listView);

        new FetchData(getApplicationContext(), listView).execute();
    }


    private class FetchData extends AsyncTask<String, Void,String> {

        String data = "";

        Context context;
        ListView lv;

        FetchData(Context context, ListView lv) {
            this.context = context;
            this.lv = lv;
        }


        @Override
        protected void onPreExecute() {
            //dialog = new AlertDialog.Builder(context).create();
            //dialog.setTitle("Please wait..");
        }

        @Override
        protected String doInBackground(String... parameter) {

            String check_user_url = "http://baiusthub.mygamesonline.org/show_file.php";


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

            ArrayList<String> idList = new ArrayList<>();
            ArrayList<String> nameList = new ArrayList<>();
            ArrayList<String> deptList = new ArrayList<>();
            ArrayList<String> courseList = new ArrayList<>();
            ArrayList<String> linkList = new ArrayList<>();

            try {


                //JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = new JSONArray(s);

                int count = 0;

                while (count < jsonArray.length()) {

                    JSONObject JO = jsonArray.getJSONObject(count);
                    String id = JO.getString("id");
                    String filename = JO.getString("filename");
                    String department = JO.getString("department");
                    String course = JO.getString("course");
                    String link = JO.getString("link");


                    idList.add(id);
                    nameList.add(filename);
                    deptList.add(department);
                    courseList.add(course);
                    linkList.add(link);

                    count++;
                }


                mId =idList.toArray(new String[0]);
                mFileName =nameList.toArray(new String[0]);
                mDeptName =deptList.toArray(new String[0]);
                mCourseName =courseList.toArray(new String[0]);
                mDownloadLink =linkList.toArray(new String[0]);




                // Save the ListView state (= includes scroll position) as a Parceble

                Parcelable state = listView.onSaveInstanceState();
                MyAdapter adapter = new MyAdapter(context, mId, mFileName, mDeptName, mCourseName, mDownloadLink);
                lv.setAdapter(adapter);
                listView.onRestoreInstanceState(state);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }


    //set data to activity retrieved from database
    class MyAdapter extends ArrayAdapter<String> {


        Context context;
        String[] rId;
        String[] rName;
        String[] rDept;
        String[] rCourse;
        String[] rLink;

        MyAdapter(Context context, String[] id, String[] name, String[] dept, String[] course, String[] link){
            super(context,R.layout.filelist,R.id.fileName,name);

            this.context = context;
            this.rId = id;
            this.rName = name;
            this.rDept = dept;
            this.rCourse = course;
            this.rLink = link;
        }


        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("ViewHolder") View row = Objects.requireNonNull(layoutInflater).inflate(R.layout.filelist, parent, false);

            TextView fileName = findViewById(R.id.fileName);
            TextView deptName = findViewById(R.id.deptName);
            TextView courseName = findViewById(R.id.courseName);

            /*Picasso.with(context).invalidate("http://banglapuzzle.website/wh/assets/backend/images/post/" + rImage[position]);
            Picasso.with(context)
                    .load("http://banglapuzzle.website/wh/assets/backend/images/post/" + rImage[position])
                    .fit()
                    .placeholder(R.color.colorPrimaryDarkWhite)
                    .centerCrop()
                    .into(postImage);*/

            fileName.setText(rName[position]);
            deptName.setText("Dept.: " + rDept[position]);
            courseName.setText("Course: " + rCourse[position]);

            // ========= Permform Click Event ===================
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(rLink[position]));
                    startActivity(i);
                }
            });

            return row;
        }
    }

}
