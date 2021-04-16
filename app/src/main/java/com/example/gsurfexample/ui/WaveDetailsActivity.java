package com.example.gsurfexample.ui;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import com.example.gsurfexample.source.local.historic.SurfSession;
import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.utils.algorithms.Quaternion;
import com.example.gsurfexample.utils.factory.ProcessedDataViewModelFactory;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class WaveDetailsActivity extends AppCompatActivity {

    // Attributes for data access
    private Activity activityContext;
    private ProcessedDataViewModel processedDataViewModel;

    private LineChart mChart;
    private LineChart mChart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_details);

        // Get parameter
        Bundle b = getIntent().getExtras();
        int startIndex = b.getInt("startIndex");
        int endIndex = b.getInt("endIndex");


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
        TextView textViewSubTitle = findViewById(R.id.text_view_subtitle);
        textViewSubTitle.getPaint().setShader(textShader);


        // Buttons
        TextView buttonWaveDetails = findViewById(R.id.text_wave_stats);
        buttonWaveDetails.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WaveDetailsActivity.this, WaveAnimationActivity.class);
                startActivity(intent);
            }
        });

        // Instantiate and connect processedDataViewModel to Live Data
        ProcessedDataViewModelFactory processedDataViewModelFactory;
        processedDataViewModelFactory = new ProcessedDataViewModelFactory(this.getApplication());
        processedDataViewModel = new ViewModelProvider(this, processedDataViewModelFactory).
                get(ProcessedDataViewModel.class);

        // Get Wave specific data
        List<ProcessedData> currentProcessedData;
        List<ProcessedData> waveProcessedData = new ArrayList();
        try {
            currentProcessedData = processedDataViewModel.getAllProcessedDataSync();
            if(currentProcessedData != null){

                // here still a proper data access by sql
                waveProcessedData = currentProcessedData.subList(startIndex, endIndex + 1);

            }else{
                Toast.makeText(this,"Cannot load wave data",Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"Cannot load wave data",Toast.LENGTH_SHORT).show();
        }

        // Calculate statistics


        // Plot
        // Plot chart and its configuration
        mChart = (LineChart) findViewById(R.id.chart_speed);
        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText("");
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getLegend().setEnabled(false);
        mChart.getXAxis().setTextColor(Color.WHITE);
        mChart.getAxisLeft().setTextColor(Color.WHITE);
        //mChart.setBackgroundColor(Color.MAGENTA);
        YAxis yAxisRight = mChart.getAxisRight();
        yAxisRight.setEnabled(false);
        LineData data = new LineData();
        data.setValueTextColor(R.color.water_blue);
        mChart.setData(data);

        mChart2 = (LineChart) findViewById(R.id.chart_angle);
        mChart2.getDescription().setEnabled(true);
        mChart2.getDescription().setText("");
        mChart2.setTouchEnabled(true);
        mChart2.setDragEnabled(true);
        mChart2.setScaleEnabled(true);
        mChart2.setPinchZoom(true);
        mChart2.getXAxis().setDrawGridLines(false);
        mChart2.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart2.getLegend().setEnabled(false);
        mChart2.getXAxis().setTextColor(Color.WHITE);
        mChart2.getAxisLeft().setTextColor(Color.WHITE);
        //mChart.setBackgroundColor(Color.MAGENTA);
        YAxis yAxisRight2 = mChart2.getAxisRight();
        yAxisRight2.setEnabled(false);
        LineData data2 = new LineData();
        data2.setValueTextColor(R.color.water_blue);
        mChart2.setData(data2);

        for(int k = 0; k<waveProcessedData.size(); k++){
            ProcessedData processedData = waveProcessedData.get(k);
            double dt = (processedData.getTimeStamp()-waveProcessedData.get(0).getTimeStamp());

            addChartEntry(mChart, (float)dt,
                    (float) Math.sqrt(processedData.getDXFilt() * processedData.getDXFilt() +
                    processedData.getDYFilt() * processedData.getDYFilt()));

            Quaternion quaternion = new Quaternion(processedData.getQ0(),
                    processedData.getQ1(), processedData.getQ2(), processedData.getQ3());

            addChartEntry(mChart2, (float)dt, (float)(quaternion.toEulerAngles()[1] / Math.PI * 180));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void addChartEntry(LineChart chart, float x, float y){
        LineData data = chart.getData();

        if(data !=null){
            LineDataSet set = (LineDataSet)data.getDataSetByIndex(0);
            if(set==null){
                set = new LineDataSet(null, "");
                set.setAxisDependency(YAxis.AxisDependency.LEFT);
                set.setLineWidth(2f);
                set.setColor(Color.TRANSPARENT);
                set.setMode(LineDataSet.Mode.LINEAR);
                set.setDrawCircles(false);
                set.setDrawFilled(true);
                if (Utils.getSDKInt() >= 18) {
                    // fill drawable only supported on api level 18 and above
                    Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
                    set.setFillDrawable(drawable);
                }
                else {
                    set.setFillColor(Color.BLACK);
                }
                data.addDataSet(set);
            }
            data.addEntry(new Entry(x, y), 0);
        }
    }




}


