package com.example.gsurfexample.ui;


import android.content.res.Resources;
import android.util.Log;

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
    private int waveCounter;
    private int type;
    private int waveIdentifier;

    // Style parameter
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;

    ObserverPolylinePlot(GoogleMap googleMap, Resources resources){
        plottedDataSize = 0;
        type = -1;
        waveCounter = 1;
        this.googleMap = googleMap;
        this.resources = resources;
    }

    @Override
    public void onChanged(@Nullable List<ProcessedData> processedDataList) {

        assert processedDataList != null;
        if (processedDataList.size() > 0) {

            for(int k = plottedDataSize; k<processedDataList.size(); k++) {

                ProcessedData processedData = processedDataList.get(k);

                if (processedData != null) {

                    // Check if new data point is of a different type (state)
                    // if so, plot new polyline
                    if((polyline1 == null) || (processedData.getState() != type)){

                        if(polyline1!=null){
                            // Set last index of polyline
                            PolylineIdentifier polylineIdentifier = (PolylineIdentifier) polyline1.getTag();
                            assert polylineIdentifier != null;
                            polylineIdentifier.setEndIndex(k-1);
                            polyline1.setTag(polylineIdentifier);
                        }

                        // Add polyline to the map.
                        polyline1 = googleMap.addPolyline(new PolylineOptions()
                                .clickable(true));

                        // Store a data object with the polyline, used here to indicate an arbitrary type.
                        switch (GlobalParams.States.values()[processedData.getState()].toString()) {
                            // If no type is given, allow the API to use the default.
                            case "SURFINGWAVE":
                                //polyline1.setTag("SURFINGWAVE");
                                //polyline1.setTag(waveIdentifier);
                                type = GlobalParams.States.valueOf("SURFINGWAVE").ordinal();
                                waveCounter += 1;
                                waveIdentifier = waveCounter;
                                break;
                            case "FLOATING":
                                //polyline1.setTag("FLOATING");
                                type = GlobalParams.States.valueOf("SURFINGWAVE").ordinal();
                                waveIdentifier = 0;
                                break;
                        }
                        PolylineIdentifier polylineIdentifier = new PolylineIdentifier(type,
                                waveIdentifier, k, 0);
                        polyline1.setTag(polylineIdentifier);

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

        PolylineIdentifier polylineIdentifier = (PolylineIdentifier)polyline.getTag();

        // State specific plotting
        if(polylineIdentifier.getType()==GlobalParams.States.valueOf("FLOATING").ordinal()) {
            // If no type is given, allow the API to use the default.

            // Use a round cap at the start of the line.
            // Use a custom bitmap as the cap at the start of the line.
            polyline.setStartCap(
                    new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.endcap_polyline), 150));
            polyline.setEndCap(
                    new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.endcap_polyline), 150));
            polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
            polyline.setColor(resources.getColor(R.color.dark_orange));
            polyline.setJointType(JointType.ROUND);
        }else { // if Surfing wave
                // Use a custom bitmap as the cap at the start of the line.
            polyline.setStartCap(
                    new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.endcap_polyline), 150));
            polyline.setEndCap(
                    new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.endcap_polyline), 150));
            polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
            polyline.setColor(resources.getColor(R.color.light_green));
            polyline.setJointType(JointType.ROUND);
        }
    }
}
