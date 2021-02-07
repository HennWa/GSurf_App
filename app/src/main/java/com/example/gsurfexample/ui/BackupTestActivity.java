package com.example.gsurfexample.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.gsurfexample.R;
import com.example.gsurfexample.utils.factory.TimeSampleViewModelFactory;
import com.example.gsurfexample.source.local.live.TimeSample;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class BackupTestActivity extends AppCompatActivity {

    private TimeSampleViewModel timeSampleViewModel;
    TextView xValue;
    private LineChart mChart;


    Thread thread;
    boolean plotData = false;
    float ww = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Test Activity");
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



        //startPlot();



        // Instantiate and connect timeSampleViewModel to Live Data
        TimeSampleViewModelFactory timeSampleViewModelFactory;
        timeSampleViewModelFactory = new TimeSampleViewModelFactory(this.getApplication());
        timeSampleViewModel = new ViewModelProvider(this, timeSampleViewModelFactory).get(TimeSampleViewModel.class);
        timeSampleViewModel.getAllTimeSamples().observe(this, new Observer<List<TimeSample>>() {
            @Override
            public void onChanged(@Nullable List<TimeSample> timeSamples) {
                if (timeSamples.size() > 0) {

                    //xValue.setText("xValue: " + timeSamples.size());
                    float ddx = (float) timeSamples.get(timeSamples.size() - 1).getDdx();
                    xValue.setText("xValue: " + ddx);


                    /*
                    if (plotData) {
                        ILineDataSet set = data.getDataSetByIndex(0);
                        if(set==null){
                            set = createSet();
                            data.addDataSet(set);
                        }
                        data.addEntry(new Entry( 2, 5), 0);
                        Log.i("hh","++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        data.notifyDataChanged();
                        //mChart.setData(data);
                    } */


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
                timeSampleViewModel.sensorDataFetch();
            }
        });

    }

    @Override
    protected void onDestroy() {
        timeSampleViewModel.getAllTimeSamples();
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

            //data.addEntry(new Entry( set.getEntryCount(), entry+5), 0);
            data.addEntry(new Entry( 2, 5), 0);
            data.notifyDataChanged();
            mChart.setMaxVisibleValueCount(150);
            mChart.moveViewToX(data.getEntryCount());

            plotData = false;
            ww += 1;

        }
    }

    // Set for plot
    private LineDataSet createSet(){
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
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


                    LineData data = mChart.getData();
                    if(data !=null) {
                        ILineDataSet set = data.getDataSetByIndex(0);
                        if (set == null) {
                            set = createSet();
                            data.addDataSet(set);
                        }
                        //data.addEntry(new Entry( set.getEntryCount(), entry+5), 0);
                        data.addEntry(new Entry(2, 5), 0); // läuft aber kein chart
                        //data.notifyDataChanged();
                        mChart.notifyDataSetChanged();
                    }


                    try {
                        Thread.sleep(1000);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

}
























