package com.example.gsurfexample;

public class Integrator {

    float samplePeriod;
    float[] cumtrapz;
    float[] lastSample;

    public Integrator(float samplePeriod) {
        this.samplePeriod = samplePeriod;
        this.cumtrapz = new float[3];
        this.lastSample = new float[3];
    }

    public float[] cumTrapzIntegration(float valsXYZ[]) {

        cumtrapz[0] = cumtrapz[0] + 0.5f * (lastSample[0] + valsXYZ[0]);
        cumtrapz[1] = cumtrapz[1] + 0.5f * (lastSample[1] + valsXYZ[1]);
        cumtrapz[2] = cumtrapz[2] + 0.5f * (lastSample[2] + valsXYZ[2]);
        lastSample[0] = valsXYZ[0];
        lastSample[1] = valsXYZ[1];
        lastSample[2] = valsXYZ[2];
        return cumtrapz;
    }
}
