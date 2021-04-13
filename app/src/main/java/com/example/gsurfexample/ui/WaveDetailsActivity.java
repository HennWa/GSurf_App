package com.example.gsurfexample.ui;


import android.app.Activity;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.gsurfexample.R;
import com.example.gsurfexample.utils.factory.ProcessedDataViewModelFactory;



public class WaveDetailsActivity extends AppCompatActivity {

    // Attributes for data access
    private Activity activityContext;
    private ProcessedDataViewModel processedDataViewModel;

    private LinearLayout linearLayout;
    private String[] textContent = {"Session Time", "Surfed Waves", "Highest Wave",
                                    "Paddle Distance", "Wave Time", "Surfed Distance", "Duck Dives", "Score"};
    private int[] icons = {R.drawable.icon_time, R.drawable.icon_counter, R.drawable.icon_wave,
                          R.drawable.icon_paddle_distance, R.drawable.icon_wave_time,
                          R.drawable.icon_distance, R.drawable.icon_counter, R.drawable.icon_score};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_details);



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

        // Scrollable Layout
        linearLayout = findViewById(R.id.scrollable_linear_layout);
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        for(int i=0; i<textContent.length; i++){
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
    protected void onDestroy() {
        super.onDestroy();
    }
}


