package com.example.gsurfexample.ui;

import android.content.res.Resources;

import com.example.gsurfexample.R;
import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.utils.other.GlobalParams;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class PolylineDrawer {

    private Polyline polyline1;
    private final GoogleMap googleMap;
    private int waveCounter;
    private int type;
    private int waveIdentifier;
    private final Resources resources;
    private final int resamplePlotFactor;

    public PolylineDrawer(GoogleMap googleMap, Resources resources) {
        this.type = -1;
        this.waveCounter = 1;
        this.googleMap = googleMap;
        this.resources = resources;
        this.resamplePlotFactor = 1;
    }

    public void drawPolylines(List<? extends ProcessedData> sessionData){
        if (sessionData.size() > 0) {

            List<LatLng> pointsPolyline1 = new ArrayList<>();

            for(int k = 0; k<sessionData.size(); k++) {

                ProcessedData processedData = sessionData.get(k);

                // Check if new data point is of a different type (state)
                // if so, plot new polyline
                if((polyline1 == null) || (processedData.getState() != type)){

                    if(polyline1 != null){
                        // Draw last polyline and empty point list
                        polyline1.setPoints(pointsPolyline1);
                        pointsPolyline1 = new ArrayList<>();
                    }

                    // Add polyline to the map.
                    polyline1 = googleMap.addPolyline(new PolylineOptions().clickable(true));

                    // Store a data object with the polyline, used here to indicate an arbitrary type.
                    switch (GlobalParams.States.values()[processedData.getState()].toString()) {
                        // If no type is given, allow the API to use the default.
                        case "SURFINGWAVE":
                            type = GlobalParams.States.valueOf("SURFINGWAVE").ordinal();
                            waveCounter += 1;
                            waveIdentifier = waveCounter;
                            break;
                        case "FLOATING":
                            type = GlobalParams.States.valueOf("FLOATING").ordinal();
                            waveIdentifier = 0;
                            break;
                    }
                    PolylineIdentifier polylineIdentifier = new PolylineIdentifier(type,
                            waveIdentifier, k, 0);
                    polyline1.setTag(polylineIdentifier);

                    // Adapt styling
                    stylePolyline(polyline1);
                }

                // Set last index of polyline
                PolylineIdentifier polylineIdentifier = (PolylineIdentifier) polyline1.getTag();
                polylineIdentifier.setEndIndex(k-1);
                polyline1.setTag(polylineIdentifier);

                // Add new entries to last polyline in list
                if(k%resamplePlotFactor == 0){
                    LatLng newPoint = new LatLng(processedData.getLat(),
                            processedData.getLon());
                    pointsPolyline1.add(newPoint);
                }
            }
            // Draw last polyline of iteration
            polyline1.setPoints(pointsPolyline1);
        }
    }

    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    public void stylePolyline(Polyline polyline) {

        // Style parameter
        int POLYLINE_STROKE_WIDTH_PX = 12;

        PolylineIdentifier polylineIdentifier = (PolylineIdentifier)polyline.getTag();

        // State specific plotting
        if(polylineIdentifier.getType()== GlobalParams.States.valueOf("FLOATING").ordinal()) {
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
