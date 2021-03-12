package com.example.gsurfexample.utils.algorithms;


import java.util.Arrays;

/**
 * Digital Butterworth Filter with fixed coefficients, which can be applied to float array.
 * (assumed parameters are determined for given equidistant sample rate of values)
 */
public class DigitalButterworthFilter {
    float[] nominator;
    float[] denominator;

    float[] xMemo;  // Input memory
    float[] yMemo;  // Output memory

    /**
     * Creates Digital Butterworth Filter with given coefficients.
     * (Coefficients result from design for specific sample rate.)
     * @param nominator of nominator
     * @param denominator of denominator
     */
    public DigitalButterworthFilter(float[] nominator, float[] denominator) {
        this.nominator = nominator;
        this.denominator = denominator;
        this.xMemo = new float[nominator.length];
        this.yMemo = new float[nominator.length];
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

    /**
     * Apply Digital Butterworth Filter to a sequence of arrays, whereas no time vector is used.
     * Instead samples are assumed to be equidistant and comply with sample rate for which the
     * filter is designed.
     * Formel: y[n]=−a1y[n−1]−a2y[n−2]−…−aNy[n−N]+
     *              +b0x[n]+b1x[n−1]+…+bNx[n−N]
     *
     * @param x signal to be filtered (one array of sequence).
     */
    public float[] filterSequence(float[] x){

        float[] y = new float[x.length];

        // Append entries of memory
        float[] xMemoX = Arrays.copyOf(xMemo, xMemo.length + x.length);
        System.arraycopy(x, 0, xMemoX, xMemo.length, x.length);
        float[] yMemoY = Arrays.copyOf(yMemo, yMemo.length + y.length);
        System.arraycopy(y, 0, yMemoY, yMemo.length, y.length);

        for(int i=xMemo.length; i<xMemoX.length; i++){
            for(int j=0; j<nominator.length; j++){
                yMemoY[i] += nominator[j] * xMemoX[i-j];
            }
            for(int j=0; j<denominator.length-1; j++){     // first denominator always 1 -> skipped
                yMemoY[i] -= denominator[j+1] * yMemoY[i-(j+1)]; // first denominator always 1 -> skipped
            }
        }

        // Store last N entries in memory
        System.arraycopy(xMemoX, xMemoX.length-xMemo.length, xMemo, 0, xMemo.length);
        System.arraycopy(yMemoY, yMemoY.length-yMemo.length, yMemo, 0, yMemo.length);

        // Cut out result
        System.arraycopy(yMemoY, yMemoY.length-y.length, y, 0, y.length);
        return y;
    }
}
