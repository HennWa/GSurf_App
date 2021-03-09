package com.example.gsurfexample.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.gsurfexample.R;
import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.source.local.live.TimeSample;
import com.example.gsurfexample.utils.algorithms.Quaternion;
import com.example.gsurfexample.utils.factory.ProcessedDataViewModelFactory;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class TestActivity extends AppCompatActivity {

    private ProcessedDataViewModel processedDataViewModel;
    private TextView xValue;
    private Activity activityContext;

    // For plot
    float xOffset;
    float yOffset;
    private LineChart mChart;
    private LineChart mChartTeta;
    Thread thread;
    boolean plotData = false;

    ScatterChart scatterChart;
    ScatterData scatterData;
    ScatterDataSet scatterDataSet;
    ArrayList scatterEntries;
    private ArrayList<ProcessedData> processedDataCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Test Activity");

        activityContext = this;

        // TextViews
        xValue = (TextView) findViewById(R.id.xValue);

        // Plot chart and its configuration
        mChart = (LineChart) findViewById(R.id.chartX);
        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText("x Acceleration");
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setPinchZoom(false);
        mChart.setBackgroundColor(Color.WHITE);
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        mChartTeta = (LineChart) findViewById(R.id.chartTeta);
        mChartTeta.getDescription().setEnabled(true);
        mChartTeta.getDescription().setText("Orientation");
        mChartTeta.setTouchEnabled(false);
        mChartTeta.setDragEnabled(false);
        mChartTeta.setScaleEnabled(false);
        mChartTeta.setPinchZoom(false);
        mChartTeta.setBackgroundColor(Color.WHITE);
        LineData data2 = new LineData();
        data2.setValueTextColor(Color.WHITE);
        mChartTeta.setData(data2);

        scatterChart = findViewById(R.id.scatterChart);
        scatterEntries = new ArrayList<>();
        scatterEntries.add(new BarEntry(0f, 0));
        scatterDataSet = new ScatterDataSet(scatterEntries, "");
        scatterData = new ScatterData(scatterDataSet);
        scatterChart.setData(scatterData);
        scatterDataSet.setValueTextColor(Color.BLACK);
        scatterDataSet.setValueTextSize(18f);
        scatterChart.setBackgroundColor(Color.WHITE);
        scatterChart.setGridBackgroundColor(Color.WHITE);


        processedDataCache = new ArrayList<ProcessedData>();





        // start thread for frame control of plot
        //startPlot();

        // Instantiate and connect timeSampleViewModel to Live Data
        ProcessedDataViewModelFactory processedDataViewModelFactory;
        processedDataViewModelFactory = new ProcessedDataViewModelFactory(this.getApplication());
        processedDataViewModel = new ViewModelProvider(this, processedDataViewModelFactory).get(ProcessedDataViewModel.class);

        // Wennn gesamtes times sample database beobachtet werden soll
        /*timeSampleViewModel.getAllTimeSamples().observe(this, new Observer<List<TimeSample>>() {
            @Override
            public void onChanged(@Nullable List<TimeSample> timeSamples) {
                if (timeSamples.size() > 0) {

                    xValue.setText("xValue: " + timeSamples.size());
                    //float ddx = (float) timeSamples.get(timeSamples.size() - 1).getDdx();
                    //xValue.setText("xValue: " + ddx);

                    // add value to chart
                    if(plotData) {


                        //for(int i = mChart.getData().getEntryCount(); i<timeSamples.size(); i++){
                        //    addChartEntry((float) timeSamples.get(i).getDdx());
                        //}

                        //addChartEntry(ddx);
                        //addChartEntry(1);
                        //plotData();
                    }

                    plotData = false;
                }
            }
        });*/

        // Wenn nur letzte pubkt beobachtet werden soll
        processedDataViewModel.getLastProcessedDataSample().observe(this, new Observer<ProcessedData>() {
            @Override
            public void onChanged(@Nullable ProcessedData processedData) {

                if (processedData != null) {
                    xValue.setText("xValue: " + (float) processedData.getX());
                    if(xOffset==0){
                        xOffset = (float) processedData.getX();
                    }
                    if(yOffset==0){
                        yOffset = (float) processedData.getY();
                    }

                    //Log.i("Test Activity       ", "X Position  "+ "    " + Float.toString((float)processedData.getX()-xOffset));


                    // add value to chart
                    if(plotData) {

                        addChartEntry((float)processedData.getX()-xOffset);

                        // Caluclate angle from quaternion
                        Quaternion quaternion = new Quaternion(processedData.getQ0(),
                                processedData.getQ1(), processedData.getQ2(), processedData.getQ3());
                        addChartEntry2((float)(quaternion.toEulerAngles()[1]/Math.PI*180));
                        //addChartEntry(1);
                        plotData();
                        plotData2();


                        processedDataCache.add(processedData);
                        scatterEntries = new ArrayList<>();

                        // sort processdata according to x
                        Collections.sort(processedDataCache, ProcessedData.xSort);
                        for(int i = 0; i<processedDataCache.size(); i++){
                            scatterEntries.add(new BarEntry((int)(processedDataCache.get(i).getX()-xOffset)*10,   // unit dm
                                    (int)((processedDataCache.get(i).getY()-yOffset))*10));
                        }
                        plotScatter();
                        //Log.i("TEstActivity", "plotScatter called    ");

                    }
                    plotData = false;
                }
            }
        });

        // Button to get back to MainActivity
        FloatingActionButton buttonAddNote2 = findViewById(R.id.button_test2);
        buttonAddNote2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getBack();
            }
        });

        // Button for starting data fetch
        FloatingActionButton buttonTest3 = findViewById(R.id.button_test3);
        buttonTest3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // Permissions and execution
                if(ContextCompat.checkSelfPermission(activityContext,Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    // Start thread for frame control of plot
                    startPlot();
                    // Start sensor data fetch
                    processedDataViewModel.sensorDataFetch();
                }else{
                    ActivityCompat.requestPermissions(activityContext,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                }
            }
        });

        // Button for stopping data fetch
        FloatingActionButton buttonStop = findViewById(R.id.button_stop);
        buttonStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        if (thread != null){
            thread.interrupt();
        }
        mChart.setData(null);
        processedDataViewModel.stopSensorDataFetch();
        processedDataViewModel.deleteAllProcessedData();
        super.onDestroy();
    }

    private void getBack(){
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }


    private void addChartEntry(float entry){
        LineData data = mChart.getData();

        if(data !=null){
            ILineDataSet set = data.getDataSetByIndex(0);
            if(set==null){
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry( set.getEntryCount(), entry), 0);
        }
    }

    private void addChartEntry2(float entry){
        LineData data2 = mChartTeta.getData();

        if(data2 !=null){
            ILineDataSet set2 = data2.getDataSetByIndex(0);
            if(set2==null){
                set2 = createSet();
                data2.addDataSet(set2);
            }
            data2.addEntry(new Entry( set2.getEntryCount(), entry), 0);
        }
    }


    private void plotScatter(){

        scatterDataSet = new ScatterDataSet(scatterEntries, "");
        //scatterDataSet.setColor(Color.RED);
        scatterData = new ScatterData(scatterDataSet);
        scatterChart.setData(scatterData);

        scatterData.notifyDataChanged();
        scatterChart.notifyDataSetChanged();
        scatterChart.invalidate();
    }


    private void plotData(){
        LineData data = mChart.getData();

        if(data !=null){
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();   // both for data update necessary

            //sets the visible number in the chart at first view to 5
            mChart.setVisibleXRangeMaximum(300);
            // enables drag to left/right
            mChart.setDragEnabled(true);
            // moves chart to the latest entry
            mChart.moveViewToX(data.getEntryCount());
            // do not forget to invalidate()
            mChart.invalidate();
            plotData = false;
        }
    }

    private void plotData2(){
        LineData data2 = mChartTeta.getData();

        if(data2 !=null){
            data2.notifyDataChanged();
            mChartTeta.notifyDataSetChanged();   // both for data update necessary

            //sets the visible number in the chart at first view to 5
            mChartTeta.setVisibleXRangeMaximum(300);
            // enables drag to left/right
            mChartTeta.setDragEnabled(true);
            // moves chart to the latest entry
            mChartTeta.moveViewToX(data2.getEntryCount());
            // do not forget to invalidate()
            mChartTeta.invalidate();
            plotData = false;
        }
    }

    // Set for plot
    private LineDataSet createSet(){
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setDrawCircles(false);
        return set;
    }

    private void startPlot(){
        if (thread != null){
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(50);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

}
























