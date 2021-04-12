package com.example.gsurfexample.ui;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.gsurfexample.R;

import com.example.gsurfexample.source.local.historic.ProcessedDataHistoric;
import com.example.gsurfexample.source.local.historic.SurfSession;
import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.utils.factory.ProcessedDataHistoricViewModelFactory;
import com.example.gsurfexample.utils.factory.ProcessedDataViewModelFactory;
import com.example.gsurfexample.utils.factory.TestViewModelFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;


public class SessionActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Attributes for session handling
    public static final int ADD_SURFSESSION_REQUEST = 1;
    private SurfSessionViewModel surfSessionViewModel;

    // Attributes for data access
    private Activity activityContext;
    private ProcessedDataViewModel processedDataViewModel;
    private ProcessedDataHistoricViewModel processedDataHistoricViewModel;

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

        // Map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Stop button
        ImageView stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(v -> {

            processedDataViewModel.stopSensorDataFetch();

            Intent intent = new Intent(SessionActivity.this, AddEditSurfSessionActivity.class);
            startActivityForResult(intent, ADD_SURFSESSION_REQUEST);

            // Stop service  //############################################################################
            //Intent intent2 = new Intent(activityContext, DataRecordManager.class);
            //startService(intent2);
        });

        // Instantiate and connect surfSessionViewModel to Live Data
        TestViewModelFactory viewModelFactory;
        viewModelFactory = new TestViewModelFactory(this.getApplication());
        surfSessionViewModel = new ViewModelProvider(this, viewModelFactory).get(SurfSessionViewModel.class);

        // Instantiate and connect processedDataViewModel to Live Data
        ProcessedDataViewModelFactory processedDataViewModelFactory;
        processedDataViewModelFactory = new ProcessedDataViewModelFactory(this.getApplication());
        processedDataViewModel = new ViewModelProvider(this, processedDataViewModelFactory).
                get(ProcessedDataViewModel.class);

        // Instantiate and connect processedDataHistoricViewModel
        ProcessedDataHistoricViewModelFactory processedDataHistoricViewModelFactory;
        processedDataHistoricViewModelFactory = new ProcessedDataHistoricViewModelFactory(
                this.getApplication());
        processedDataHistoricViewModel = new ViewModelProvider(this,
                processedDataHistoricViewModelFactory).get(ProcessedDataHistoricViewModel.class);
    }

    @Override
    // onMapReady called when map is ready
    public void onMapReady(GoogleMap googleMap) {

        //Get image views
        ImageView compassRose = findViewById(R.id.compass_rose);
        ImageView compassArrow = findViewById(R.id.compass_arrow);

        // Get Text views
        TextView timeView = findViewById(R.id.value_time);
        TextView numberOfWavesView = findViewById(R.id.value_number_of_waves);
        TextView paddleDistanceView = findViewById(R.id.value_paddle_distance);

        // Permissions and execution of data fetching
        if(ContextCompat.checkSelfPermission(activityContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){

            // Start sensor data fetch
            processedDataViewModel.sensorDataFetch();
        }else{
            ActivityCompat.requestPermissions(activityContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        // Observe Live data for polyline plots
        processedDataViewModel.getAllProcessedData().observe(this,
                new ObserverPolylinePlot(googleMap, getResources()));

        // Observe Live data for polyline plots
        processedDataViewModel.getAllProcessedData().observe(this,
                new ObserverSideBarSessionView(compassRose, compassArrow,
                        timeView, numberOfWavesView, paddleDistanceView));
    }

    /**
     * Store session data in session db.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Here still spagetti code

        if(requestCode == ADD_SURFSESSION_REQUEST && resultCode == RESULT_OK){

            // Copy data in historic data db
            List<ProcessedData> currentProcessedData;
            try {
                currentProcessedData = processedDataViewModel.getAllProcessedDataSync();
                if(currentProcessedData != null){
                    for(int j = 0; j<currentProcessedData.size(); j++) {
                        processedDataHistoricViewModel.insert(new ProcessedDataHistoric(currentProcessedData.get(j)));
                    }

                    // new entry in surfSession db with sessionID
                    String title = data.getStringExtra(AddEditSurfSessionActivity.EXTRA_TITLE);
                    String description = data.getStringExtra(AddEditSurfSessionActivity.EXTRA_LOCATION);
                    String date = data.getStringExtra(AddEditSurfSessionActivity.EXTRA_DATE);
                    SurfSession surfSession =
                            new SurfSession(currentProcessedData.get(currentProcessedData.size()-1)
                                    .getSession_id(), title, description, date);
                    surfSessionViewModel.insert(surfSession);

                    Toast.makeText(this,"Session saved",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"Failed to save session1",Toast.LENGTH_SHORT).show();
                }
            }catch(Exception e) {
                e.printStackTrace();
                Toast.makeText(this,"Failed to save session2",Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this,"Failed to save session3",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        processedDataViewModel.stopSensorDataFetch();
        processedDataViewModel.deleteAllProcessedData();
        super.onDestroy();
    }
}


