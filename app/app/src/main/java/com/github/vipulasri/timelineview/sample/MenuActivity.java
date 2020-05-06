package com.github.vipulasri.timelineview.sample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;


import com.github.vipulasri.timelineview.sample.model.Orientation;
import com.google.gson.JsonObject;
import com.shinelw.library.ColorArcProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.github.vipulasri.timelineview.sample.MainActivity.EXTRA_ORIENTATION;
import static com.github.vipulasri.timelineview.sample.MainActivity.EXTRA_WITH_LINE_PADDING;


public class MenuActivity extends AppCompatActivity {

    private ColorArcProgressBar bar2;
    private JobScheduler jobScheduler;
    private ComponentName componentName;
    private JobInfo jobInfo;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private JSONArray location_array=new JSONArray();
    private JSONArray blue_arry=new JSONArray();
    private int final_socre=0;
    private TextView tilte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_menu);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        permissions.add(Manifest.permission.BLUETOOTH);
        permissions.add(Manifest.permission.BLUETOOTH_ADMIN);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }


        bar2 = (ColorArcProgressBar) findViewById(R.id.bar2);
        tilte=(TextView)findViewById(R.id.text_view_id);




        findViewById(R.id.case_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.timeline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, TimeLineActivity.class);
                intent.putExtra(EXTRA_ORIENTATION, Orientation.VERTICAL);
                intent.putExtra(EXTRA_WITH_LINE_PADDING, false);
                startActivity(intent);
            }
        });

        findViewById(R.id.leaderboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, Leaderboard.class);
                intent.putExtra("Score", String.valueOf(final_socre));
                startActivity(intent);
            }
        });

        findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, Setting.class);
                startActivity(intent);
            }
        });


        File path=getApplicationContext().getExternalFilesDir(null);
        File file = new File(path,"/location.txt");
        ArrayList<String> list=new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String[] parts;
            while ((line = br.readLine()) != null)
                //System.out.println(line.split("\\|"));
                list.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<list.size();i++){

            List c=Arrays.asList(list.get(i).split("\\|"));
            JSONObject tmp = new JSONObject();
            try {
                tmp.put("time", c.get(0));
                tmp.put("place", c.get(1));
                tmp.put("address", c.get(2));
                location_array.put(tmp);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }




        ArrayList<String> list2=new ArrayList<>();;
        File file2 = new File(path,"/bluetooth.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file2));
            String line;
            while ((line = br.readLine()) != null)
                list2.add(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<list2.size();i++){
            List g=Arrays.asList(list2.get(i).split("\\|"));
            System.out.println(g.get(1));
            JSONObject t=new JSONObject();
            try {
                t.put("count",g.get(1));
                blue_arry.put(t);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        System.out.println(location_array);


        new AsyncTaskParseJson().execute();
        //StartBackgroundTask();


    }
    public class AsyncTaskParseJson extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        final String TAG = "AsyncTaskParseJson.java";

        // set your json string url here
        String yourJsonStringUrl ="http://192.168.60.186:8888/send";
        JSONArray dataJsonArr = null;
        JSONObject score = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MenuActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                // instantiate our json parser
                JSONParser jParser = new JSONParser();
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("location", location_array);
                jsonParam.put("ble", blue_arry);
                System.out.println(jsonParam);
                score=jParser.postURL(yourJsonStringUrl,jsonParam);
                // get json string from url
                dataJsonArr = jParser.getJSONFromUrl(yourJsonStringUrl);
                System.out.println(score);
                final_socre=Integer.parseInt(score.getString("score"));



            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {
            if(final_socre>80){
                tilte.setText("Beware!");
                tilte.setTextColor(Color.RED);
            }else if(final_socre>50){
                tilte.setText("Should go home!");
                tilte.setTextColor(Color.GREEN);
            }
            bar2.setCurrentValues(final_socre);

            pDialog.dismiss();

        }
    }

    @SuppressLint("NewApi")
    public void StartBackgroundTask() {
        jobScheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
        componentName = new ComponentName(getApplicationContext(), MyService.class);
        jobInfo = new JobInfo.Builder(1, componentName)
                .setMinimumLatency(20*60*1000) //10 sec interval
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setRequiresCharging(false).build();
        jobScheduler.schedule(jobInfo);
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }



}
