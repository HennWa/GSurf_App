package com.example.gsurfexample.utils.algorithms;

public class Integrator {

    float samplePeriod;
    float[] cumtrapz;
    float[] lastSample;
    boolean start;

    public Integrator(float samplePeriod) {
        this.samplePeriod = samplePeriod;   // not used yet
        this.cumtrapz = new float[3];
        this.lastSample = new float[3];
        this.start = true;
    }

    public float[] cumTrapzIntegration(float valsXYZ[]) {

        if(start){
            cumtrapz[0] = 0f; // Initialization with 0
            cumtrapz[1] = 0f;
            cumtrapz[2] = 0f;
            start = false;
        }else{
            cumtrapz[0] = cumtrapz[0] + 0.5f * (lastSample[0] + valsXYZ[0]) * samplePeriod;
            cumtrapz[1] = cumtrapz[1] + 0.5f * (lastSample[1] + valsXYZ[1]) * samplePeriod;
            cumtrapz[2] = cumtrapz[2] + 0.5f * (lastSample[2] + valsXYZ[2]) * samplePeriod;
        }
        lastSample[0] = valsXYZ[0];
        lastSample[1] = valsXYZ[1];
        lastSample[2] = valsXYZ[2];
        return cumtrapz;
    }
}
