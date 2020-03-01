package com.joyti.baiusthub;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.expansionpanel.ExpansionLayout;
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;

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
    private String[] mRating = null;
    private ListView listView;
    private RatingBar ratingBar;
    private String user_id, rdepartment, rcourse, rteacher, rcategory;

    private ExpansionLayout ex0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_file);

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        user_id = sharedPreferences.getString("id", null);

        listView = findViewById(R.id.listView);
        ex0 = findViewById(R.id.expansionLayout0);

        final ExpansionLayoutCollection expansionLayoutCollection = new ExpansionLayoutCollection();
        expansionLayoutCollection.add(ex0);
        //ex0.toggle(true);

        ex0.addListener(new ExpansionLayout.Listener() {
            @Override
            public void onExpansionChanged(ExpansionLayout expansionLayout, boolean expanded) {

            }
        });



        Spinner mySpinner1 = findViewById(R.id.spinner1);
        final ArrayAdapter<String> myAdapter1 = new ArrayAdapter<>(ExploreFileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.department));

        myAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner1.setAdapter(myAdapter1);

        mySpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //record = myAdapter.getItem(position);
                rdepartment = myAdapter1.getItem(position);

                new FetchData(getApplicationContext(), listView).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //record = "All";

            }
        });



        Spinner mySpinner2 = findViewById(R.id.spinner2);
        final ArrayAdapter<String> myAdapter2 = new ArrayAdapter<>(ExploreFileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.course));

        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner2.setAdapter(myAdapter2);

        mySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //record = myAdapter.getItem(position);

                rcourse = myAdapter2.getItem(position);

                new FetchData(getApplicationContext(), listView).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //record = "All";

            }
        });



        Spinner mySpinner4 = findViewById(R.id.spinner4);
        final ArrayAdapter<String> myAdapter4 = new ArrayAdapter<>(ExploreFileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.file_category));

        myAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner4.setAdapter(myAdapter4);

        mySpinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //record = myAdapter.getItem(position);

                rcategory = myAdapter4.getItem(position);

                new FetchData(getApplicationContext(), listView).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //record = "All";

            }
        });

        new FetchData(getApplicationContext(), listView).execute();
    }

    public void backBtn(View view) {
        onBackPressed();
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


            try {
                URL url = new URL(check_user_url);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, StandardCharsets.UTF_8));
                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");
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
            ArrayList<String> ratingList = new ArrayList<>();

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
                    String category = JO.getString("category");
                    String rating = JO.getString("rating");


                    if(rdepartment.equals(department)){

                        if(rcourse.equals(course)){

                            if(rcategory.equals(category)){

                                idList.add(id);
                                nameList.add(filename);
                                deptList.add(department);
                                courseList.add(course);
                                linkList.add(link);
                                ratingList.add(rating);
                            }

                            else if(rcategory.equals("")){

                                idList.add(id);
                                nameList.add(filename);
                                deptList.add(department);
                                courseList.add(course);
                                linkList.add(link);
                                ratingList.add(rating);
                            }
                        }

                        else if(rcourse.equals("")){
                            if(rcategory.equals(category)){

                                idList.add(id);
                                nameList.add(filename);
                                deptList.add(department);
                                courseList.add(course);
                                linkList.add(link);
                                ratingList.add(rating);
                            }

                            else if(rcategory.equals("")){

                                idList.add(id);
                                nameList.add(filename);
                                deptList.add(department);
                                courseList.add(course);
                                linkList.add(link);
                                ratingList.add(rating);
                            }

                        }
                    }


                    else if(rdepartment.equals("")){

                        if(rcourse.equals(course)){

                            if(rcategory.equals(category)){

                                idList.add(id);
                                nameList.add(filename);
                                deptList.add(department);
                                courseList.add(course);
                                linkList.add(link);
                                ratingList.add(rating);
                            }

                            else if(rcategory.equals("")){

                                idList.add(id);
                                nameList.add(filename);
                                deptList.add(department);
                                courseList.add(course);
                                linkList.add(link);
                                ratingList.add(rating);
                            }
                        }

                        else if(rcourse.equals("")){
                            if(rcategory.equals(category)){

                                idList.add(id);
                                nameList.add(filename);
                                deptList.add(department);
                                courseList.add(course);
                                linkList.add(link);
                                ratingList.add(rating);
                            }

                            else if(rcategory.equals("")){

                                idList.add(id);
                                nameList.add(filename);
                                deptList.add(department);
                                courseList.add(course);
                                linkList.add(link);
                                ratingList.add(rating);
                            }

                        }
                    }



                    count++;
                }


                mId =idList.toArray(new String[0]);
                mFileName =nameList.toArray(new String[0]);
                mDeptName =deptList.toArray(new String[0]);
                mCourseName =courseList.toArray(new String[0]);
                mDownloadLink =linkList.toArray(new String[0]);
                mRating =ratingList.toArray(new String[0]);




                // Save the ListView state (= includes scroll position) as a Parceble

                Parcelable state = listView.onSaveInstanceState();
                MyAdapter adapter = new MyAdapter(context, mId, mFileName, mDeptName, mCourseName, mDownloadLink, mRating);
                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
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
        String[] rRating;

        MyAdapter(Context context, String[] id, String[] name, String[] dept, String[] course, String[] link, String[] rating){
            super(context,R.layout.filelist,R.id.fileName,name);

            this.context = context;
            this.rId = id;
            this.rName = name;
            this.rDept = dept;
            this.rCourse = course;
            this.rLink = link;
            this.rRating = rating;
        }


        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("ViewHolder") View row = Objects.requireNonNull(layoutInflater).inflate(R.layout.filelist, parent, false);

            TextView fileName = row.findViewById(R.id.fileName);
            TextView deptName = row.findViewById(R.id.deptName);
            TextView courseName = row.findViewById(R.id.courseName);
            Button downloadBtn = row.findViewById(R.id.downloadBtn);
            Button ratingBtn = row.findViewById(R.id.ratingBtn);
            TextView ratingText = row.findViewById(R.id.ratingText);


            fileName.setText(rName[position]);
            deptName.setText("Dept.: " + rDept[position]);
            courseName.setText("Course: " + rCourse[position]);
            ratingText.setText("Rating: (" + rRating[position] + ")");

            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(rLink[position]));
                    startActivity(i);
                }
            });

            ratingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    RatingDialogClass rdd = new RatingDialogClass(ExploreFileActivity.this, rId[position]);
                    rdd.show();

                }
            });

            // ========= Permform Click Event ===================
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(rLink[position]));
                    startActivity(i);*/
                }
            });

            return row;
        }
    }


    private class RatingDialogClass extends Dialog implements
            android.view.View.OnClickListener {

        private Activity c;
        private TextView textView;
        private Button yes, no;
        private String id;

        private RatingDialogClass(Activity a, String id) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
            this.id = id;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.rating);
            textView = findViewById(R.id.txt_dialog);
            yes = findViewById(R.id.btn_yes);
            yes.setOnClickListener(this);
            ratingBar = findViewById(R.id.myRating);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:

                    //String rating = "Rating is :" + ratingBar.getRating();
                    //Toast.makeText(getActivity(), rating, Toast.LENGTH_LONG).show();

                    new SendRatings(c).execute(user_id, String.valueOf(ratingBar.getRating()),id);
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }



    private class SendRatings extends AsyncTask<String, Void,String> {


        Context context;

        SendRatings(Context context) {
            this.context = context;
        }


        @Override
        protected void onPreExecute() {
            //dialog = new AlertDialog.Builder(context).create();
            //dialog.setTitle("Please wait..");
        }



        @Override
        protected String doInBackground(String... parameter) {

            String check_user_url = "http://baiusthub.mygamesonline.org/update_rating.php";

            String uid = parameter[0];
            String rating = parameter[1];
            String file_id = parameter[2];
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(check_user_url);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, StandardCharsets.UTF_8));
                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(uid, "UTF-8")
                        +"&&"+URLEncoder.encode("rating", "UTF-8") + "=" + URLEncoder.encode(rating, "UTF-8")
                        +"&&"+URLEncoder.encode("file_id", "UTF-8") + "=" + URLEncoder.encode(file_id, "UTF-8");
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
                result = new StringBuilder(Objects.requireNonNull(e.getMessage()));
            }


            return result.toString();
        }


        @Override
        protected void onPostExecute(String s) {

            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();



        }

    }

    private static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int j = 0; j < listAdapter.getCount(); j++) {
            View listItem = listAdapter.getView(j, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
