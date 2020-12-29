package com.example.gsurfexample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    private TimeSampleViewModel timeSampleViewModel;
    private TextView xValue;
    private Activity activityContext;

    // For plot
    private LineChart mChart;
    Thread thread;
    boolean plotData = false;


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

        // start thread for frame control of plot
        //startPlot();

        // Instantiate and connect timeSampleViewModel to Live Data
        TimeSampleViewModelFactory timeSampleViewModelFactory;
        timeSampleViewModelFactory = new TimeSampleViewModelFactory(this.getApplication());
        timeSampleViewModel = new ViewModelProvider(this, timeSampleViewModelFactory).get(TimeSampleViewModel.class);

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
        timeSampleViewModel.getLastTimeSample().observe(this, new Observer<TimeSample>() {
            @Override
            public void onChanged(@Nullable TimeSample timeSample) {

                if (timeSample != null) {
                    xValue.setText("xValue: " + (float) timeSample.getDdx());

                    // add value to chart
                    if(plotData) {

                        addChartEntry((float) timeSample.getDdx());
                        //addChartEntry(1);
                        plotData();
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
                    timeSampleViewModel.sensorDataFetch();
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
        timeSampleViewModel.stopSensorDataFetch();
        timeSampleViewModel.deleteAllTimeSamples();
        timeSampleViewModel.deleteAllProcessedData();
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

    // Add Entry and plot in one method, now in two methods
    /*private void addChartEntry(float entry){
        LineData data = mChart.getData();

        if(data !=null){
            ILineDataSet set = data.getDataSetByIndex(0);
            if(set==null){
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry( set.getEntryCount(), entry), 0);
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();   // both for data update necessary

            //sets the visible number in the chart at first view to 5
            mChart.setVisibleXRangeMaximum(5);
            // enables drag to left/right
            mChart.setDragEnabled(true);
            // moves chart to the latest entry
            mChart.moveViewToX(data.getEntryCount());
            // do not forget to invalidate()
            mChart.invalidate();
            plotData = false;
        }
    } */




}
























