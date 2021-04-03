package com.example.gsurfexample.ui;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.gsurfexample.R;

import com.example.gsurfexample.source.local.historic.SurfSession;
import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.utils.algorithms.Quaternion;
import com.example.gsurfexample.utils.factory.ProcessedDataViewModelFactory;
import com.example.gsurfexample.utils.factory.TestViewModelFactory;
import com.example.gsurfexample.utils.other.GlobalParams;
import com.example.gsurfexample.utils.services.DataRecordManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SessionActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Attributes for session handling
    public static final int ADD_SURFSESSION_REQUEST = 1;
    private SurfSessionViewModel surfSessionViewModel;
    private String sessionID;

    // Attributes for data access
    private Activity activityContext;
    private ProcessedDataViewModel processedDataViewModel;

    // Attributes for plotting polylines
    private int plottedDataSize;
    private Polyline polyline1;
    private ArrayList<Polyline> wavePolylines;

    // plot sidebar
    private ImageView compassRose;
    private ImageView compassArrow;
    private ImageView stopButton;
    private float psi;
    private float vAbs;
    private float rotationAngleDegArrowCompass;

    // to be deleted...
    Thread thread;
    boolean plotPolyline = false;

    // Style parameter
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        // Styling
        getSupportActionBar().hide();  // hide title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Activity context
        activityContext = this;

        //Get image views
        compassRose = (ImageView)findViewById(R.id.compass_rose);
        compassArrow = (ImageView)findViewById(R.id.compass_arrow);

        // Map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Stop button
        stopButton = (ImageView)findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                processedDataViewModel.stopSensorDataFetch();

                Intent intent = new Intent(SessionActivity.this, AddEditSurfSessionActivity.class);
                startActivityForResult(intent, ADD_SURFSESSION_REQUEST);

                // Stop service  //############################################################################
                //Intent intent2 = new Intent(activityContext, DataRecordManager.class);
                //startService(intent2);
            }
        });

        // Initialize state of plots
        plottedDataSize = 0;
        wavePolylines = new ArrayList<Polyline>();

        // Instantiate and connect surfSessionViewModel to Live Data
        TestViewModelFactory viewModelFactory;
        viewModelFactory = new TestViewModelFactory(this.getApplication());
        surfSessionViewModel = new ViewModelProvider(this, viewModelFactory).get(SurfSessionViewModel.class);

        // Instantiate and connect processedDataViewModel to Live Data
        ProcessedDataViewModelFactory processedDataViewModelFactory;
        processedDataViewModelFactory = new ProcessedDataViewModelFactory(this.getApplication());
        processedDataViewModel = new ViewModelProvider(this, processedDataViewModelFactory).get(ProcessedDataViewModel.class);
    }

    @Override
    // onMapReady called when map is ready
    public void onMapReady(GoogleMap googleMap) {

        // Permissions and execution of data fetching
        if(ContextCompat.checkSelfPermission(activityContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            // Start thread for frame control of plot
            //startPlot();
            // Start sensor data fetch
            processedDataViewModel.sensorDataFetch();
        }else{
            ActivityCompat.requestPermissions(activityContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        // Observe Live data
        processedDataViewModel.getAllProcessedData().observe(this, new Observer<List<ProcessedData>>() {
            @Override
            public void onChanged(@Nullable List<ProcessedData> processedDataList) {

                if(true){// to be deleted
                    if (processedDataList.size() > 0) {

                        if(plottedDataSize==0){
                            sessionID = processedDataList.get(processedDataList.size()-1).
                                    getSession_id();
                        }

                        for(int k = plottedDataSize; k<processedDataList.size(); k++) {

                            ProcessedData processedData = processedDataList.get(k);

                            if (processedData != null) {

                                Log.i("SessionActivity", "k:   " +  k +  "  State: " +  processedData.getState());

                                // Check if new data point is of a different type (state)
                                // if so, plot new ployline
                                if((polyline1 == null) ||
                                        (GlobalParams.States.values()[processedData.getState()].toString() !=
                                        polyline1.getTag().toString())){

                                    Log.i("SessionActivity", "State of processed data" + GlobalParams.States.values()[processedData.getState()].toString());

                                    //LatLng startPoint = new LatLng(processedData.getLat(),
                                    //        processedData.getLon());
                                    // Add polyline to the map.
                                    polyline1 = googleMap.addPolyline(new PolylineOptions()
                                            .clickable(true));
                                            //.add(startPoint));

                                    // Store a data object with the polyline, used here to indicate an arbitrary type.
                                    switch (GlobalParams.States.values()[processedData.getState()].toString()) {
                                        // If no type is given, allow the API to use the default.
                                        case "SURFINGWAVE":
                                            polyline1.setTag("SURFINGWAVE");
                                            break;
                                        case "FLOATING":
                                            polyline1.setTag("FLOATING");
                                            break;
                                    }

                                    // Adapt styling
                                    stylePolyline(polyline1);

                                    // Store in list
                                    wavePolylines.add(polyline1);
                                }

                                // Add new entries to last polyline in list
                                List<LatLng> pointsPolyline1 = polyline1.getPoints();
                                LatLng newPoint = new LatLng(processedData.getLat(),
                                        processedData.getLon());
                                pointsPolyline1.add(newPoint);
                                polyline1.setPoints(pointsPolyline1);

                                // Rotate compass rose
                                psi = (float)(new Quaternion(processedData.getQ0(),
                                        processedData.getQ1(), processedData.getQ2(),
                                        processedData.getQ3()).toEulerAngles()[2] / Math.PI * 180);
                                compassRose.setRotation(psi); // function requires clockwise rotation

                                // Update arrow of image view
                                vAbs = (float)Math.sqrt((processedData.getDYFilt()*
                                        processedData.getDYFilt() +
                                        processedData.getDXFilt()*processedData.getDXFilt()));

                                if(Math.abs(vAbs) < GlobalParams.getInstance().eps){
                                    rotationAngleDegArrowCompass = 0;
                                }else{
                                    rotationAngleDegArrowCompass =
                                            (float)(Math.asin(processedData.getDYFilt()/vAbs)
                                                    / Math.PI * 180
                                                    -psi);
                                }
                                compassArrow.setRotation(rotationAngleDegArrowCompass); // function requires clockwise rotation
                                int width = 40;
                                int height = 90;
                                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
                                //iv.setLayoutParams(parms);
                            }
                        }

                        // move camera after every few points
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(processedDataList.get(processedDataList.size()-1).getLat(),
                                processedDataList.get(processedDataList.size()-1).getLon()), 18));
                    }
                    plottedDataSize = processedDataList.size();
                }
            }
        });

    }

    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "SURFINGWAVE":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.endcap_polyline), 150));
                polyline.setEndCap(
                        new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.endcap_polyline), 150));
                polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
                polyline.setColor(getResources().getColor(R.color.light_green));
                polyline.setJointType(JointType.ROUND);
                break;
            case "FLOATING":
                // Use a round cap at the start of the line.
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.endcap_polyline), 150));
                polyline.setEndCap(
                        new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.endcap_polyline), 150));
                polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
                polyline.setColor(getResources().getColor(R.color.dark_orange));
                polyline.setJointType(JointType.ROUND);
                break;
        }

    }

    /**
     * Store session data in session db.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(sessionID == null){
            Toast.makeText(this,"No data of session available",Toast.LENGTH_SHORT).show();
        }else if(requestCode == ADD_SURFSESSION_REQUEST && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddEditSurfSessionActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditSurfSessionActivity.EXTRA_LOCATION);
            String date = data.getStringExtra(AddEditSurfSessionActivity.EXTRA_DATE);
            SurfSession surfSession = new SurfSession(sessionID,
                    title, description, date);
            surfSessionViewModel.insert(surfSession);
            Toast.makeText(this,"Session saved",Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this,"Failed to save session",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (thread != null){
            thread.interrupt();
        }
        processedDataViewModel.stopSensorDataFetch();
        processedDataViewModel.deleteAllProcessedData();
        super.onDestroy();
    }


    // can probably delete, so far no performance issues
    private void startPlot(){
        if (thread != null){
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    plotPolyline = true;
                    try {
                        Thread.sleep(100);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }



}
























