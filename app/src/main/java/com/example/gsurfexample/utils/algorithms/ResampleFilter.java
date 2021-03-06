package com.example.gsurfexample.utils.algorithms;

import android.util.Log;

import com.example.gsurfexample.source.local.live.TimeSample;

import java.util.ArrayList;
import java.util.List;

/**
 * ResampleFilter resamples TimeSamples. It stores a list of TimeSamples and calculates the mean
 * of every features over the whole list once a sample time is above a given period. This time
 * threshold is updated after every iteration.
 * Requirements: TimeSample in sequential order.
 */
public class ResampleFilter{

    // Attributes
    private final long sampleRate; // in ms
    private long nextSampleTime;
    private float meanDdx, meanDdy, meanDdz;
    private float meanGFx, meanGFy, meanGFz;
    private float meanBx, meanBy, meanBz;
    private float meanWx, meanWy, meanWz;
    private float meanLat, meanLon, meanHeight;
    private float meanXGPS, meanYGPS;
    private final List<TimeSample> filterCache;

    /**
     * Constructor.
     * @param sampleR  Sample rate used.
     */
    // Constructor
    public ResampleFilter(long sampleR) {
        sampleRate = sampleR;
        nextSampleTime = 0;
        meanDdx = meanDdy = meanDdz = 0;
        meanGFx = meanGFy = meanGFz = 0;
        meanBx = meanBy = meanBz = 0;
        meanWx = meanWy = meanWz = 0;
        meanLat = meanLon = meanHeight = 0;
        meanXGPS = meanYGPS = 0;
        filterCache = new ArrayList<>();
    }

    // Methods
    /**
     * Takes a new TimeSample into a list and carries out resampling.
     * @param timeSample  Next TimeSample to be resampled.
     * @return Resampled TimeSample or null if period not complete.
     */
    public TimeSample filterElement(TimeSample timeSample){
        filterCache.add(timeSample);
        if(filterCache.size()==1){
            nextSampleTime = filterCache.get(0).getTimeStamp() + sampleRate;
        }

        TimeSample resampledTimeSampleData;
        if(timeSample.getTimeStamp() > nextSampleTime){
            int numElem = filterCache.size();
            for(int i = 0; i < numElem-1; i++){
                meanDdx += filterCache.get(0).getDdx();
                meanDdy += filterCache.get(0).getDdy();
                meanDdz += filterCache.get(0).getDdz();
                meanGFx += filterCache.get(0).getGFx();
                meanGFy += filterCache.get(0).getGFy();
                meanGFz += filterCache.get(0).getGFz();
                meanBx += filterCache.get(0).getBx();
                meanBy += filterCache.get(0).getBy();
                meanBz += filterCache.get(0).getBz();
                meanWx += filterCache.get(0).getWx();
                meanWy += filterCache.get(0).getWy();
                meanWz += filterCache.get(0).getWz();
                meanLat += (float) filterCache.get(0).getLat();
                meanLon += (float) filterCache.get(0).getLon();
                meanHeight += (float) filterCache.get(0).getHeight();
                meanXGPS += (float) filterCache.get(0).getXGPS();
                meanYGPS += (float) filterCache.get(0).getYGPS();
                filterCache.remove(0);
            }
            meanDdx /= (numElem-1);
            meanDdy /= (numElem-1);
            meanDdz /= (numElem-1);
            meanGFx /= (numElem-1);
            meanGFy /= (numElem-1);
            meanGFz /= (numElem-1);
            meanBx /= (numElem-1);
            meanBy /= (numElem-1);
            meanBz /= (numElem-1);
            meanWx /= (numElem-1);
            meanWy /= (numElem-1);
            meanWz /= (numElem-1);
            meanLat /= (numElem-1);
            meanLon /= (numElem-1);
            meanHeight /= (numElem-1);
            meanXGPS /= (numElem-1);
            meanYGPS /= (numElem-1);


            resampledTimeSampleData = new TimeSample( nextSampleTime-sampleRate/2,
                                                                meanDdx, meanDdy, meanDdz,
                                                                meanGFx, meanGFy, meanGFz,
                                                                meanBx, meanBy, meanBz,
                                                                meanWx, meanWy, meanWz,
                                                                meanLat, meanLon, meanHeight,
                                                                meanXGPS, meanYGPS);

            meanDdx = meanDdy = meanDdz = 0;
            meanGFx = meanGFy = meanGFz = 0;
            meanBx = meanBy = meanBz = 0;
            meanWx = meanWy = meanWz = 0;
            meanLat = meanLon = meanHeight = 0;
            meanXGPS = meanYGPS = 0;
            nextSampleTime += sampleRate;
        }else{
            resampledTimeSampleData = null;
        }
        return resampledTimeSampleData;
    }
}
