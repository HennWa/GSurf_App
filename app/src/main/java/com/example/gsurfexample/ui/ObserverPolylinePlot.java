package com.example.gsurfexample.ui;


import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.example.gsurfexample.R;
import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.utils.other.GlobalParams;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class ObserverPolylinePlot implements Observer<List<ProcessedData>> {

    private int plottedDataSize;
    private Polyline polyline1;
    private final GoogleMap googleMap;
    private final Resources resources;

    // Style parameter
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;

    ObserverPolylinePlot(GoogleMap googleMap, Resources resources){
        plottedDataSize = 0;
        this.googleMap = googleMap;
        this.resources = resources;

    }

    @Override
    public void onChanged(@Nullable List<ProcessedData> processedDataList) {
        if (processedDataList.size() > 0) {

            for(int k = plottedDataSize; k<processedDataList.size(); k++) {

                ProcessedData processedData = processedDataList.get(k);

                if (processedData != null) {

                    // Check if new data point is of a different type (state)
                    // if so, plot new polyline
                    if((polyline1 == null) ||
                            (!GlobalParams.States.values()[processedData.getState()].toString().
                                    equals(polyline1.getTag().toString()))){

                        //LatLng startPoint = new LatLng(processedData.getLat(),
                        //        processedData.getLon());
                        // Add polyline to the map.
                        polyline1 = googleMap.addPolyline(new PolylineOptions()
                                .clickable(true));

                        // Store a data object with the polyline, used here to indicate an arbitrary type.
                        switch (GlobalParams.States.values()[processedData.getState()].toString()) {
                            // If no type is given, allow the API to use the default.
                            case "SURFINGWAVE":
                                polyline1.setTag("SURFINGWAVE");
                                break;
                            case "FLOATING":
                                polyline1.setTag("FLOATING");
                                break;
                        }

                        // Adapt styling
                        stylePolyline(polyline1);

                    }

                    // Add new entries to last polyline in list
                    List<LatLng> pointsPolyline1 = polyline1.getPoints();
                    LatLng newPoint = new LatLng(processedData.getLat(),
                            processedData.getLon());
                    pointsPolyline1.add(newPoint);
                    polyline1.setPoints(pointsPolyline1);

                }
            }
            // move camera after every few points
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(processedDataList.get(processedDataList.size()-1).getLat(),
                            processedDataList.get(processedDataList.size()-1).getLon()), 20));
        }
        plottedDataSize = processedDataList.size();
    }

    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "SURFINGWAVE":

                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.endcap_polyline), 150));
                polyline.setEndCap(
                        new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.endcap_polyline), 150));
                polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
                polyline.setColor(resources.getColor(R.color.light_green));
                polyline.setJointType(JointType.ROUND);
                break;
            case "FLOATING":
                // Use a round cap at the start of the line.
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.endcap_polyline), 150));
                polyline.setEndCap(
                        new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.endcap_polyline), 150));
                polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
                polyline.setColor(resources.getColor(R.color.dark_orange));
                polyline.setJointType(JointType.ROUND);
                break;
        }
    }
}
