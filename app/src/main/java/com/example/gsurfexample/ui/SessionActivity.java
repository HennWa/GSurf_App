package com.example.gsurfexample.ui;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.gsurfexample.R;

import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.utils.algorithms.Quaternion;
import com.example.gsurfexample.utils.factory.ProcessedDataViewModelFactory;
import com.example.gsurfexample.utils.other.GlobalParams;
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

    // Attributes for data access
    private Activity activityContext;
    private ProcessedDataViewModel processedDataViewModel;

    // Attributes for plotting polylines
    private int plottedDataSize;
    private Polyline polyline1;
    private ArrayList<Polyline> wavePolylines;

    // to be deleted...
    Thread thread;
    boolean plotPolyline = false;

    // Style parameter
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        // activity context
        activityContext = this;

        // mapFragment for google map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize state of plots
        plottedDataSize = 0;
        wavePolylines = new ArrayList<Polyline>();

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
























