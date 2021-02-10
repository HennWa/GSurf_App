package com.example.gsurfexample;

import android.util.Log;

import com.example.gsurfexample.utils.algorithms.MadgwickAHRS;
import com.example.gsurfexample.utils.algorithms.Quaternion;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Simple Test of Madgwick AHRS.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MadgwickAHRSTest {
    @Test
    public void testMadgwickAHRS() {

                                   //wx             wy           wz
        float samples[][] = {{  -0.00031111f, -0.00017778f, -0.00035556f,
                                    //ax            ay           az
                                 0.02167778f, 0.0115f    , 1.01453333f,
                                    // mx            my          mz
                                 21.16666667f,   5.45333333f, -36.16777778f},

                             {  -0.00031111f, -0.00017778f, -0.00035556f,
                                 0.02167778f, 0.0115f    , 1.01453333f,
                                21.16666667f,   5.45333333f, -36.16777778f},
                             {  -0.00031111f, -0.00017778f, -0.00035556f,
                                 0.02167778f, 0.0115f    , 1.01453333f,
                                     21.16666667f,   5.45333333f, -36.16777778f}
                             };

        float quatTarget[][] = {{0.99994976f, -0.00796868f, -0.00284633f, -0.00537378f},
                                {0.9997997f,  -0.0152829f,  -0.00567317f, -0.01161081f},
                                {0.99955579f, -0.02112509f, -0.00843744f, -0.01925525f}};

        float[][] results = new float[quatTarget.length][4];

        MadgwickAHRS madgwickAHRS = new MadgwickAHRS(0.1f, 0.1f);
        for(int i = 0; i<samples.length; i++){
            madgwickAHRS.update(samples[i][0], samples[i][1], samples[i][2],
                                samples[i][3], samples[i][4], samples[i][5],
                                samples[i][6], samples[i][7], samples[i][8]);

            results[i] = madgwickAHRS.getQuaternion().clone();
        }

        for(int i = 0; i<samples.length; i++){
            assertEquals("Madgwick error in X run " + i,
                    quatTarget[i][0], results[i][0], 1e-5f);
            assertEquals("Madgwick error in Y run " + i,
                    quatTarget[i][1], results[i][1], 1e-5f);
            assertEquals("Madgwick error in Z run " + i,
                    quatTarget[i][2], results[i][2], 1e-5f);
            assertEquals("Madgwick error in W run " + i,
                    quatTarget[i][3], results[i][3], 1e-5f);
        }
    }
}