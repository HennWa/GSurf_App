package com.example.gsurfexample.ui;

import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.gsurfexample.R;
import com.example.gsurfexample.source.local.historic.ProcessedDataHistoric;
import com.example.gsurfexample.utils.factory.ProcessedDataHistoricViewModelFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.List;


public class SessionDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Polyline selectedPolyline;
    private List<ProcessedDataHistoric> sessionData;
    private String sessionID;

    private final String[] textContent = {"Session Time", "Surfed Waves", "Highest Wave",
            "Paddle Dist.", "Wave Time", "Surfed Distance", "Duck Dives", "Score"};
    private final int[] icons = {R.drawable.icon_time, R.drawable.icon_counter, R.drawable.icon_wave,
            R.drawable.icon_paddle_distance, R.drawable.icon_wave_time,
            R.drawable.icon_distance, R.drawable.icon_counter, R.drawable.icon_score};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);

        // Get parameter
        Bundle b = getIntent().getExtras();
        sessionID = b.getString("sessionID");

        // Get data from processedDataHistoricViewModel
        ProcessedDataHistoricViewModelFactory processedDataHistoricViewModelFactory;
        processedDataHistoricViewModelFactory = new ProcessedDataHistoricViewModelFactory(this.getApplication());
        // Attributes for data access
        ProcessedDataHistoricViewModel processedDataHistoricViewModel = new ViewModelProvider(this, processedDataHistoricViewModelFactory).
                get(ProcessedDataHistoricViewModel.class);
        try{
            sessionData = processedDataHistoricViewModel.
                    getProcessedDataHistoricSyncBySessionsId(sessionID);
        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"Can not load data",Toast.LENGTH_SHORT).show();
        }


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


        // Map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Scrollable Layout
        // Attributes for scrollable layout
        LinearLayout linearLayout = findViewById(R.id.scrollable_linear_layout);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        for(int i=0; i<textContent.length; i++) {
            View view = layoutInflater.inflate(R.layout.icon_item, linearLayout, false);

            ImageView imageView = view.findViewById(R.id.icon_image);
            imageView.setImageResource(icons[i]);

            TextView textView = view.findViewById(R.id.description);
            textView.setText(textContent[i]);

            // Get Text views
            TextView valueView = view.findViewById(R.id.value);
            valueView.setText(CalculatorStats.calculatePeriod(sessionData));

            linearLayout.addView(view);
        }
    }

    @Override
    // onMapReady called when map is ready
    public void onMapReady(GoogleMap googleMap) {

        // Draw polylines
        PolylineDrawer polylineDrawer = new PolylineDrawer(googleMap, getResources());
        polylineDrawer.drawPolylines(sessionData);


        // move camera to last position
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(sessionData.get(sessionData.size() - 1).getLat(),
                        sessionData.get(sessionData.size() - 1).getLon()), 20));


        // Polyline listener for selection
        googleMap.setOnPolylineClickListener(polyline -> {
            // highlight polyline
            if(selectedPolyline!=null){
                polylineDrawer.stylePolyline(selectedPolyline);
            }
            polyline.setColor(getResources().getColor(R.color.light_orange));

            // Get identifier
            selectedPolyline = polyline;
        });


        // Buttons
        TextView buttonWaveDetails = findViewById(R.id.text_wave_details);
        buttonWaveDetails.setOnClickListener(v -> {
            Intent intent = new Intent(SessionDetailsActivity.this, WaveDetailsActivity.class);
            PolylineIdentifier polylineIdentifier = (PolylineIdentifier)selectedPolyline.getTag();
            if(polylineIdentifier!=null){
                intent.putExtra("sessionID", sessionID);
                intent.putExtra("startIndex", polylineIdentifier.getStartIndex());
                intent.putExtra("endIndex", polylineIdentifier.getEndIndex());
                intent.putExtra("waveIdentifier", polylineIdentifier.getWaveIdentifier());
                startActivity(intent);
            }else{
                Toast.makeText(this, "Select a wave", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


