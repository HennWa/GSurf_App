package com.example.gsurfexample.utils.algorithms;

import java.util.Arrays;

/**
 * Shifting Average Filter which can be applied to float array.
 */
public class ShiftingAverageFilter {

    float[] memoryOfSamples;

    public ShiftingAverageFilter(int averageLength) {
        this.memoryOfSamples = new float[averageLength];
    }

    /**
     * Apply Shifting Average Filter to a sequence of arrays, whereas no time vector is used.
     * Formel: y[n]=(y[n−1]+y[n−2]+…+y[n−N])/N
     *
     * @param x signal to be filtered (one array of sequence).
     */
    public float[] filterSequence(float[] x){

        float[] y = new float[x.length];
        float meanSum = 0f;

        // Append entries of memory
        float[] xPlusMemory = Arrays.copyOf(memoryOfSamples, memoryOfSamples.length + x.length);
        System.arraycopy(x, 0, xPlusMemory, memoryOfSamples.length, x.length);

        // Fill results array
        for(int i=0; i<x.length; i++){
            for(int j=0; j<memoryOfSamples.length; j++){
                meanSum += xPlusMemory[i+j];
            }
            y[i] = meanSum / memoryOfSamples.length;
            meanSum = 0f;
        }

        // Store last N entries in memory
        System.arraycopy(xPlusMemory, xPlusMemory.length-memoryOfSamples.length,
                memoryOfSamples, 0, memoryOfSamples.length);
        return y;
    }
}
