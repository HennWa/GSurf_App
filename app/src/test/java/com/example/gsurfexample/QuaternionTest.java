package com.example.gsurfexample;

import com.example.gsurfexample.utils.algorithms.Quaternion;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Simple Test of Quaternion.
 */
public class QuaternionTest {
    @Test
    public void testQuaternion() {

        // Test rotateVector
        Quaternion q1 = new Quaternion(0.597f, -0.007f, 0.796f, 0.100f);
        float[] v1 = {1,2,3};
        float[] resultsTarget = {2.2445339f, -2.20057916f, 2.02965981f};

        float[] results = q1.rotateVector(v1);

        assertEquals("Quaternion rotation error in X", resultsTarget[0], results[0], 1e-3f);
        assertEquals("Quaternion rotation error in Y", resultsTarget[1], results[1], 1e-3f);
        assertEquals("Quaternion rotation error in Y", resultsTarget[2], results[2], 1e-3f);
        // numerical difference to scipy

        // Test toEulerAngles
        float[] resultsTarget2 = {1.2499022f, 0.42667839f, 2.58158735f};
        float[] results2 = q1.toEulerAngles();

        assertEquals("Error in Euler angle pitch", resultsTarget2[0], results2[0], 1e-3f);
        assertEquals("Error in Euler angle roll", resultsTarget2[1], results2[1], 1e-3f);
        assertEquals("Error in Euler angle yaw", resultsTarget2[2], results2[2], 1e-3f);
        // numerical difference to scipy
    }
}