package com.example.gsurfexample.utils.algorithms;

import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * Digital Butterworth Filter with fixed coefficients, which can be applied to float array.
 * (assumed parameters are determined for given equidistant sample rate of values)
 */
public class DigitalButterworthFilter {
    float samplePeriod;
    float[] nominator;
    float[] denominator;

    /**
     * Creates Digital Butterworth Filter with given coefficients.
     * (Coefficients result from design for specific sample rate.)
     * @param nominator of nominator
     * @param denominator of denominator
     */
    public DigitalButterworthFilter(float[] nominator, float[] denominator) {
        this.nominator = nominator;
        this.denominator = denominator;
    }

    /**
     * Apply Digital Butterworth Filter to an array, whereas no time vector is used. Instead
     * samples are assumed to be equidistant and comply with sample rate for which the filter
     * is designed.
     * Formel: y[n]=−a1y[n−1]−a2y[n−2]−…−aNy[n−N]+
     *              +b0x[n]+b1x[n−1]+…+bNx[n−N]
     *
     * @param x signal to be filtered.
     */
    public float[] filter(float[] x){

        float[] y = new float[x.length];

        for(int i=0; i<x.length; i++){
            for(int j=0; j<nominator.length; j++){
                if(i-j>=0){
                    y[i] += nominator[j] * x[i-j];
                }
            }
            for(int j=0; j<denominator.length-1; j++){     // first denominator always 1 -> skipped
                if(i-(j+1)>=0){
                    y[i] -= denominator[j+1] * y[i-(j+1)]; // first denominator always 1 -> skipped
                }
            }
        }
        return y;
    }

}
