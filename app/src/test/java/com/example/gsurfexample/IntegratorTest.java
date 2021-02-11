package com.example.gsurfexample;

import com.example.gsurfexample.utils.algorithms.Integrator;
import com.example.gsurfexample.utils.algorithms.Quaternion;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Simple Test of Integrator.
 */
public class IntegratorTest {
    @Test
    public void testIntegrator() {
        float SAMPLEPERIOD = 0.1f;
        float[] ddX = {2f, 3.3f, 6f, 2f, 1f, 7f};
        float[] ddY = {8.122f, 3.3f, 9f, 2f, 5f, 7f};
        float[] ddZ = {7f, 53f, 9.9f, 2f, 1f, 7f};

        float[] results_dX = new float[ddX.length];
        float[] results_dY = new float[ddX.length];
        float[] results_dZ = new float[ddX.length];
        float[] results = new float[ddX.length];

        float[] dXTarget = {0f,    0.265f, 0.73f,  1.13f,  1.28f,  1.68f };
        float[] dYTarget = {0f,    0.5711f, 1.1861f, 1.7361f, 2.0861f, 2.6861f};
        float[] dZTarget = {0f,    3f,    6.145f, 6.74f,  6.89f,  7.29f};

        Integrator integratorAcceleration = new Integrator(SAMPLEPERIOD);

        for(int i = 0; i < ddX.length; i++){
            results = integratorAcceleration.cumTrapzIntegration(new float[]{ddX[i], ddY[i], ddZ[i]});
            results_dX[i] = results[0];
            results_dY[i] = results[1];
            results_dZ[i] = results[2];
        }

        for(int i = 0; i < ddX.length; i++){
            assertEquals("Integration error in X" + i, dXTarget[i], results_dX[i], 1e-5f);
            assertEquals("Integration error in Y" + i, dYTarget[i], results_dY[i], 1e-5f);
            assertEquals("Integration error in Z" + i, dZTarget[i], results_dZ[i], 1e-5f);
        }
    }
}