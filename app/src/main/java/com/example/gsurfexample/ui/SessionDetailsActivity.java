package com.example.gsurfexample.ui;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


public class SessionDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Attributes for data access
    private Activity activityContext;
    private ProcessedDataViewModel processedDataViewModel;
    private PolylineIdentifier polylineIdentifier;

    // Attributes for scrollable layout
    private LinearLayout linearLayout;
    private String[] textContent = {"Session Time", "Surfed Waves", "Highest Wave",
            "Paddle Dist.", "Wave Time", "Surfed Distance", "Duck Dives", "Score"};
    private int[] icons = {R.drawable.icon_time, R.drawable.icon_counter, R.drawable.icon_wave,
            R.drawable.icon_paddle_distance, R.drawable.icon_wave_time,
            R.drawable.icon_distance, R.drawable.icon_counter, R.drawable.icon_score};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);

        // Styling
        getSupportActionBar().hide();  // hide title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        TextView textViewTitle = findViewById(R.id.text_view_title);
        Shader textShader = new LinearGradient(0, 0, 0, 100,
                new int[]{ContextCompat.getColor(this, R.color.light_blue_transition),
                        ContextCompat.getColor(this, R.color.water_blue)},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        textViewTitle.getPaint().setShader(textShader);

        // Activity context
        activityContext = this;

        // Map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Scrollable Layout
        linearLayout = findViewById(R.id.scrollable_linear_layout);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        for(int i=0; i<textContent.length; i++) {
            View view = layoutInflater.inflate(R.layout.icon_item, linearLayout, false);

            ImageView imageView = view.findViewById(R.id.icon_image);
            imageView.setImageResource(icons[i]);

            TextView textView = view.findViewById(R.id.description);
            textView.setText(textContent[i]);

            linearLayout.addView(view);
        }


        // Instantiate and connect processedDataViewModel to Live Data
        ProcessedDataViewModelFactory processedDataViewModelFactory;
        processedDataViewModelFactory = new ProcessedDataViewModelFactory(this.getApplication());
        processedDataViewModel = new ViewModelProvider(this, processedDataViewModelFactory).
                get(ProcessedDataViewModel.class);
    }

    @Override
    // onMapReady called when map is ready
    public void onMapReady(GoogleMap googleMap) {

        // Get Text views
        //TextView timeView = findViewById(R.id.value_time);
        //TextView numberOfWavesView = findViewById(R.id.value_number_of_waves);
        //TextView paddleDistanceView = findViewById(R.id.value_paddle_distance);


        // Observe Live data for polyline plots
        processedDataViewModel.getAllProcessedData().observe(this,
                new ObserverPolylinePlot(googleMap, getResources()));

        // Here change activity
        googleMap.setOnPolylineClickListener(polyline -> {


            //here get old wave and paint old color


            // highlight plotline
            polyline.setColor(getResources().getColor(R.color.light_orange));




            // Get identifier
            polylineIdentifier = (PolylineIdentifier)polyline.getTag();
        });

        // Buttons
        TextView buttonWaveDetails = findViewById(R.id.text_wave_details);
        buttonWaveDetails.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SessionDetailsActivity.this, WaveDetailsActivity.class);
                if(polylineIdentifier!=null){
                    intent.putExtra("startIndex", polylineIdentifier.getStartIndex());
                    intent.putExtra("endIndex", polylineIdentifier.getEndIndex());
                    intent.putExtra("waveIdentifier", polylineIdentifier.getWaveIdentifier());
                    startActivity(intent);
                }else{
                    Toast.makeText(activityContext, "Select a wave", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


