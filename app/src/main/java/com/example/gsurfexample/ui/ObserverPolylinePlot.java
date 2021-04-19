package com.example.gsurfexample.ui;


import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.example.gsurfexample.source.local.live.ProcessedData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class ObserverPolylinePlot implements Observer<List<ProcessedData>> {

    private int plottedDataSize;
    private final GoogleMap googleMap;
    private final PolylineDrawer polylineDrawer;

    ObserverPolylinePlot(GoogleMap googleMap, Resources resources){
        this.googleMap = googleMap;
        this.polylineDrawer = new PolylineDrawer(googleMap, resources);
    }

    @Override
    public void onChanged(@Nullable List<ProcessedData> processedDataList) {

        assert processedDataList != null;
        if (processedDataList.size() > 0) {

            // Draw polylines
            List<ProcessedData> processedDataListSubList =
                    processedDataList.subList(plottedDataSize, processedDataList.size());
            polylineDrawer.drawPolylines(processedDataListSubList);

            plottedDataSize = processedDataList.size();

            // move camera after every few points
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(processedDataList.get(processedDataList.size() - 1).getLat(),
                            processedDataList.get(processedDataList.size() - 1).getLon()), 20));
        }
    }
}
