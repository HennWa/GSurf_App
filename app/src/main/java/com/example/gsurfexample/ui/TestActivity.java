package com.example.gsurfexample.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.example.gsurfexample.source.local.live.TimeSample;
import com.example.gsurfexample.utils.algorithms.Quaternion;
import com.example.gsurfexample.utils.factory.ProcessedDataViewModelFactory;
import com.example.gsurfexample.utils.factory.TestViewModelFactory;
import com.example.gsurfexample.utils.other.GlobalParams;
import com.example.gsurfexample.utils.services.DataRecordManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    public static final int ADD_SURFSESSION_REQUEST = 1;
    private SurfSessionViewModel surfSessionViewModel;
    private String sessionID;

    private ProcessedDataViewModel processedDataViewModel;
    private TextView xValue;
    private Activity activityContext;

    // For plot
    float xOffset;
    float yOffset;
    private LineChart mChart;
    private LineChart mChartTeta;
    private LineChart mChartVelo;
    Thread thread;
    boolean plotData = false;

    ScatterChart scatterChart;
    ScatterData scatterData;
    ScatterDataSet scatterDataSet;
    ScatterDataSet scatterDataSet2;
    ArrayList scatterEntries;
    ArrayList scatterEntries2;
    private ArrayList<ProcessedData> processedDataCache;
    int plottetDataSize;


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
        scatterEntries2 = new ArrayList<>();
        scatterEntries2.add(new BarEntry(0f, 0));
        scatterDataSet = new ScatterDataSet(scatterEntries, "");
        scatterDataSet2 = new ScatterDataSet(scatterEntries, "");
        scatterData = new ScatterData(scatterDataSet);
        scatterChart.setData(scatterData);
        scatterDataSet.setValueTextColor(Color.BLACK);
        scatterDataSet.setValueTextSize(18f);
        scatterChart.setBackgroundColor(Color.WHITE);
        scatterChart.setGridBackgroundColor(Color.WHITE);

        mChartVelo = (LineChart) findViewById(R.id.chartVelocity);
        mChartVelo.getDescription().setEnabled(true);
        mChartVelo.getDescription().setText("Velocity");
        mChartVelo.setTouchEnabled(false);
        mChartVelo.setDragEnabled(false);
        mChartVelo.setScaleEnabled(false);
        mChartVelo.setPinchZoom(false);
        mChartVelo.setBackgroundColor(Color.WHITE);
        LineData dataVelo = new LineData();
        dataVelo.setValueTextColor(Color.WHITE);
        mChartVelo.setData(dataVelo);

        LineData data4 = mChartVelo.getData();
        ILineDataSet bondaryLines = createSetLimitLines();
        data4.addDataSet(bondaryLines);
        data4.addEntry(new Entry( -10, -10), 0);
        data4.addEntry(new Entry( 10, 10), 0);


        processedDataCache = new ArrayList<ProcessedData>();
        plottetDataSize = 0;



        // Instantiate and connect surfSessionViewModel to Live Data
        TestViewModelFactory viewModelFactory;
        viewModelFactory = new TestViewModelFactory(this.getApplication());
        surfSessionViewModel = new ViewModelProvider(this, viewModelFactory).get(SurfSessionViewModel.class);
        surfSessionViewModel.getAllSurfSessions().observe(this, new Observer<List<SurfSession>>() {
            @Override
            public void onChanged(@Nullable List<SurfSession> surfSessions) {
            }
        });


        // Instantiate and connect timeSampleViewModel to Live Data
        ProcessedDataViewModelFactory processedDataViewModelFactory;
        processedDataViewModelFactory = new ProcessedDataViewModelFactory(this.getApplication());
        processedDataViewModel = new ViewModelProvider(this, processedDataViewModelFactory).get(ProcessedDataViewModel.class);


        // Wennn gesamtes times sample database beobachtet werden soll
        processedDataViewModel.getAllProcessedData().observe(this, new Observer<List<ProcessedData>>() {
            @Override
            public void onChanged(@Nullable List<ProcessedData> processedDataList) {
                if (processedDataList.size() > 0) {

                    if(plottetDataSize==0){
                        sessionID = processedDataList.get(processedDataList.size()-1).
                                getSession_id();
                    }

                    for(int k = plottetDataSize; k<processedDataList.size(); k++) {
                        ProcessedData processedData = processedDataList.get(k);

                        if (processedData != null) {
                            xValue.setText("xValue: " + (float) processedData.getX());
                            if (xOffset == 0) {
                                xOffset = (float) processedData.getX();
                            }
                            if (yOffset == 0) {
                                yOffset = (float) processedData.getY();
                            }

                            // add value to chart
                            if (true) {   // if plotData

                                // 1st figure
                                //addChartEntry((float)processedData.getX()-xOffset);
                                if (Math.abs(Math.sqrt(processedData.getDXFilt() * processedData.getDXFilt() +
                                        processedData.getDYFilt() * processedData.getDYFilt())) > 50) {
                                    addChartEntry(0f);
                                } else {
                                    addChartEntry((float) Math.sqrt(processedData.getDXFilt() * processedData.getDXFilt() +
                                            processedData.getDYFilt() * processedData.getDYFilt()));
                                }

                                // 2nd figure
                                // Caluclate angle from quaternion
                                Quaternion quaternion = new Quaternion(processedData.getQ0(),
                                        processedData.getQ1(), processedData.getQ2(), processedData.getQ3());
                                addChartEntry2((float) (quaternion.toEulerAngles()[2] / Math.PI * 180));
                        //if(Math.abs(Math.sqrt(processedData.getDXFilt() * processedData.getDXFilt() +
                        //        processedData.getDYFilt() * processedData.getDYFilt()))>200){
                        //    addChartEntry2(0f);
                        //}else{
                        //    addChartEntry2((float)Math.sqrt(processedData.getDXFilt() * processedData.getDXFilt() +
                        //            processedData.getDYFilt() * processedData.getDYFilt()));
                        //}

                                //Log.i("TestActivity", "Plottet Data k "  + k + "  "+
                                //        processedData.getTimeStamp());

                                // 3rd figure Location plot
                                // here just a cache for now to sort data for plot
                                processedDataCache.add(processedData);
                                scatterEntries = new ArrayList<>();
                                scatterEntries2 = new ArrayList<>();
                                // sort processdata according to x

                        //Collections.sort(processedDataCache, ProcessedData.xSort);
                        //for(int i = 0; i<processedDataCache.size(); i++){
                        //    if(processedDataCache.get(i).getState() == GlobalParams.States.valueOf("FLOATING").ordinal()){
                        //        scatterEntries.add(new BarEntry((int)(processedDataCache.get(i).getX()-xOffset)*10,   // unit dm
                        //                (int)((processedDataCache.get(i).getY()-yOffset))*10));
                        //    }else{

                        //        scatterEntries2.add(new BarEntry((int)(processedDataCache.get(i).getX()-xOffset)*10,   // unit dm
                        //                (int)((processedDataCache.get(i).getY()-yOffset))*10));
                        //    }
                        //}
                        //plotScatter();

                                // 4th figure
                                // Velocity plot
                                addChartVelo((float) processedData.getDXFilt(), (float) processedData.getDYFilt());
                                plotDataVelo();

                            }
                        }
                    }
                    plotData();
                    plotData2();
                    plottetDataSize = processedDataList.size();
                    plotData = false;

                }
            }
        });

/*
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

                    // add value to chart
                    if(plotData) {

                        // 1st figure
                        //addChartEntry((float)processedData.getX()-xOffset);
                        if(Math.abs(Math.sqrt(processedData.getDXFilt() * processedData.getDXFilt() +
                                processedData.getDYFilt() * processedData.getDYFilt()))>200){
                            addChartEntry(0f);
                        }else{
                            addChartEntry((float)Math.sqrt(processedData.getDXFilt() * processedData.getDXFilt() +
                                    processedData.getDYFilt() * processedData.getDYFilt()));
                        }


                        // 2nd figure
                        // Caluclate angle from quaternion
                        Quaternion quaternion = new Quaternion(processedData.getQ0(),
                                processedData.getQ1(), processedData.getQ2(), processedData.getQ3());
                        addChartEntry2((float)(quaternion.toEulerAngles()[2]/Math.PI*180));
                        //if(Math.abs(Math.sqrt(processedData.getDXFilt() * processedData.getDXFilt() +
                        //        processedData.getDYFilt() * processedData.getDYFilt()))>200){
                        //    addChartEntry2(0f);
                        //}else{
                        //    addChartEntry2((float)Math.sqrt(processedData.getDXFilt() * processedData.getDXFilt() +
                        //            processedData.getDYFilt() * processedData.getDYFilt()));
                        //}

                        Log.i("TestActivity", "Plottet Data" +
                                processedData.getTimeStamp());

                        plotData();
                        plotData2();

                        // 3rd figure Location plot
                        // here just a cache for now to sort data for plot
                        processedDataCache.add(processedData);
                        scatterEntries = new ArrayList<>();
                        scatterEntries2 = new ArrayList<>();
                        // sort processdata according to x

                        //Collections.sort(processedDataCache, ProcessedData.xSort);
                        //for(int i = 0; i<processedDataCache.size(); i++){
                        //    if(processedDataCache.get(i).getState() == GlobalParams.States.valueOf("FLOATING").ordinal()){
                        //        scatterEntries.add(new BarEntry((int)(processedDataCache.get(i).getX()-xOffset)*10,   // unit dm
                        //                (int)((processedDataCache.get(i).getY()-yOffset))*10));
                        //    }else{

                        //        scatterEntries2.add(new BarEntry((int)(processedDataCache.get(i).getX()-xOffset)*10,   // unit dm
                        //                (int)((processedDataCache.get(i).getY()-yOffset))*10));
                        //    }
                        //}
                        //plotScatter();

                        // 4th figure
                        // Velocity plot
                        addChartVelo((float)processedData.getDXFilt(), (float)processedData.getDYFilt());
                        plotDataVelo();

                    }
                    plotData = false;
                }
            }
        }); */

        // Button to get back to MainActivity
        FloatingActionButton buttonAddNote2 = findViewById(R.id.button_test2);
        buttonAddNote2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                processedDataViewModel.stopSensorDataFetch();
                //getBack();
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

                // Test service  //############################################################################
                Intent intent = new Intent(activityContext, DataRecordManager.class);
                startService(intent);
            }
        });

        // Button for stopping data fetch
        FloatingActionButton buttonSave = findViewById(R.id.button_save);
        buttonSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, AddEditSurfSessionActivity.class);
                startActivityForResult(intent, ADD_SURFSESSION_REQUEST);

                // Start and Stop service  //############################################################################
                Intent intent2 = new Intent(activityContext, DataRecordManager.class);
                startService(intent2);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(sessionID == null){
            Toast.makeText(this,"No data of session available",Toast.LENGTH_SHORT).show();
        }else if(requestCode == ADD_SURFSESSION_REQUEST && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddEditSurfSessionActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditSurfSessionActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditSurfSessionActivity.EXTRA_PRIORITY, 1);
            SurfSession surfSession = new SurfSession(sessionID, title, description, priority);
            surfSessionViewModel.insert(surfSession);
            Toast.makeText(this,"Session saved",Toast.LENGTH_SHORT).show();
        }
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

    private void addChartVelo(float dX, float dY){
        LineData data4 = mChartVelo.getData();

        data4.removeDataSet(1);
        ILineDataSet set4 = createSet();
        data4.addDataSet(set4);

        if (dX<0){
            data4.addEntry(new Entry( dX, dY), 1);
            data4.addEntry(new Entry( 0, 0), 1);
        }else{
            data4.addEntry(new Entry( 0, 0), 1);
            data4.addEntry(new Entry( dX, dY), 1);
        }
    }

    private void plotScatter(){

        scatterDataSet = new ScatterDataSet(scatterEntries, "");
        scatterDataSet2 = new ScatterDataSet(scatterEntries2, "Fast");

        scatterDataSet2.setScatterShapeHoleColor(Color.MAGENTA);
        scatterDataSet2.setColor(Color.MAGENTA);
        scatterDataSet2.setScatterShape(ScatterChart.ScatterShape.CIRCLE);

        ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
        dataSets.add(scatterDataSet); // add the data sets
        dataSets.add(scatterDataSet2);
        scatterData = new ScatterData(dataSets);
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


    private void plotDataVelo(){
        LineData dataVelo = mChartVelo.getData();

        if(dataVelo !=null){
            dataVelo.notifyDataChanged();
            mChartVelo.notifyDataSetChanged();   // both for data update necessary

            // enables drag to left/right
            mChartVelo.setDragEnabled(true);
            // moves chart to the latest entry
            //mChartVelo.moveViewToX(-10);


            // do not forget to invalidate()
            mChartVelo.invalidate();
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

    private LineDataSet createSetLimitLines(){
        LineDataSet set = new LineDataSet(null, "Boundaries");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(1f);
        set.setColor(Color.WHITE);
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
                        Thread.sleep(20);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

}
























