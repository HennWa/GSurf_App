package com.example.gsurfexample.ui;


import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.utils.algorithms.Quaternion;
import com.example.gsurfexample.utils.other.GlobalParams;



import java.util.List;

public class ObserverSideBarSessionView implements Observer<List<ProcessedData>> {

    public ProcessedData processedData;
    private int plottedDataSize;

    private float psi;
    private float vAbs;
    private float rotationAngleDegArrowCompass;
    private float paddleDistance;
    private int waveCounter;
    private int stateOfLastProcessedData;

    private final ImageView compassRose;
    private final ImageView compassArrow;
    private final TextView timeView;
    private final TextView numberOfWavesView;
    private final TextView paddleDistanceView;

    private Thread thread;
    private boolean updateText = false;

    long startTime = System.nanoTime();




    ObserverSideBarSessionView(ImageView compassRose, ImageView compassArrow,
                               TextView timeView, TextView numberOfWavesView,
                               TextView paddleDistanceView){

        this.compassRose = compassRose;
        this.compassArrow = compassArrow;
        this.timeView = timeView;
        this.numberOfWavesView = numberOfWavesView;
        this.paddleDistanceView = paddleDistanceView;

        plottedDataSize = 0;
        waveCounter = 0;
        paddleDistance = 0f;

        startPlot();
    }

    @Override
    public void onChanged(@Nullable List<ProcessedData> processedDataList) {
        if (processedDataList.size() > 0) {

            // All calculations that need every data point
            for(int k = plottedDataSize; k<processedDataList.size(); k++) {

                processedData = processedDataList.get(k);

                if (processedData != null) {  // necessary here?? to make code more stable?

                    // Calculate paddle distance
                    if(GlobalParams.States.values()[processedData.
                            getState()].toString().equals("FLOATING") && k>0){
                        paddleDistance +=
                                Math.sqrt(Math.pow(processedData.getX()-
                                        processedDataList.get(k-1).getX(),2) +
                                        Math.pow(processedData.getY()-
                                                processedDataList.get(k-1).getY(),2));
                    }

                    // Update wave counter
                    // Check if new data point is of a different type (state)
                    if(stateOfLastProcessedData != processedData.getState()) {

                        // Store a data object with the polyline, used here to indicate an arbitrary type.
                        switch (GlobalParams.States.values()[processedData.getState()].toString()) {
                            // If no type is given, allow the API to use the default.
                            case "SURFINGWAVE":
                                waveCounter += 1;
                                break;
                            case "FLOATING":
                                break;
                        }
                    }
                    stateOfLastProcessedData = processedData.getState();
                }
            }

            // All calculations that only need last data point
            processedData = processedDataList.get(processedDataList.size()-1);

            // Rotate compass rose
            psi = (float)(new Quaternion(processedData.getQ0(),
                    processedData.getQ1(), processedData.getQ2(),
                    processedData.getQ3()).toEulerAngles()[2] / Math.PI * 180);
            compassRose.setRotation(psi); // function requires clockwise rotation

            // Update arrow of image view
            vAbs = (float)Math.sqrt((processedData.getDYFilt()*
                    processedData.getDYFilt() +
                    processedData.getDXFilt()*processedData.getDXFilt()));

            if(Math.abs(vAbs) < GlobalParams.getInstance().eps){
                rotationAngleDegArrowCompass = 0;
            }else{
                rotationAngleDegArrowCompass =
                        (float)(Math.asin(processedData.getDYFilt()/vAbs)
                                / Math.PI * 180
                                -psi);
            }
            compassArrow.setRotation(rotationAngleDegArrowCompass); // function requires clockwise rotation

            // Update text in regularly
            if(updateText){
                numberOfWavesView.setText(" " + waveCounter);
                paddleDistanceView.setText(" " + (int)paddleDistance + " m");
                updateText = false;

                // Timer
                long elapsedTimeHr = (long)((System.nanoTime() - startTime)/1e9/60/60);
                String elapsedTimeHrStr;
                if(elapsedTimeHr<10){
                    elapsedTimeHrStr = "0"+elapsedTimeHr;
                }else{
                    elapsedTimeHrStr = ""+elapsedTimeHr;
                }
                long elapsedTimeMin = (long)((System.nanoTime() - startTime
                        - elapsedTimeHr*60*60*1e9)/1e9/60);
                String elapsedTimeMinStr;
                if(elapsedTimeMin<10){
                    elapsedTimeMinStr = "0"+elapsedTimeMin;
                }else{
                    elapsedTimeMinStr = ""+elapsedTimeMin;
                }
                long elapsedTimeSec = (long)((System.nanoTime() - startTime
                        - elapsedTimeHr*60*60*1e9 -
                        -elapsedTimeMin*1e9*60)/1e9%60);
                String elapsedTimeSecStr;
                if(elapsedTimeSec<10){
                    elapsedTimeSecStr = "0"+elapsedTimeSec;
                }else{
                    elapsedTimeSecStr = ""+elapsedTimeSec;
                }

                timeView.setText(elapsedTimeHrStr + ":" +
                        elapsedTimeMinStr + ":" + elapsedTimeSecStr);
            }

        }
        plottedDataSize = processedDataList.size();
    }

    // can probably delete, so far no performance issues
    private void startPlot(){
        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(() -> {
            while (true){
                updateText = true;
                try {
                    Thread.sleep(500);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
