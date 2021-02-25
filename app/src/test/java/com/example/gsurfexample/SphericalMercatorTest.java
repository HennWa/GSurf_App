package com.example.gsurfexample;

import com.example.gsurfexample.utils.algorithms.SphericalMercator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SphericalMercatorTest {
    @Test
    public void testSphericalMercator() {

        // Inputs
        double longitude = 11.588413497;
        double latitude = 48.14841167;

        double[] shift = new double[]{1.0, 5.0, 10.0, 25.0, 100.0, 1000.0, 10000.0};

        double[] xShift = new double[shift.length];
        double[] yShift = new double[shift.length];

        double[] latRes = new double[shift.length];
        double[] lonRes = new double[shift.length];

        // Targets
        double xTarget = 1290016.2895879385;
        double yTarget = 6131580.8591697365;

        double[] lonTarget = new double[] {11.588422480152838, 11.588458412764204, 11.58850332852841,
                11.588638075821027, 11.589311812284118, 11.597396649841194,
                11.678245025411949};
        double[] latTarget = new double[] {48.148417663589925, 48.14844163794258, 48.148471605867655,
                48.148561509537885, 48.149011025526654, 48.15440491028445,
                48.20831257705365};

        // Projections
        double xRes = SphericalMercator.lon2x(longitude);
        double yRes = SphericalMercator.lat2y(latitude);
        for(int i=0; i<xShift.length; i++) {
            lonRes[i] = SphericalMercator.x2lon(shift[i] + xRes);
            latRes[i] = SphericalMercator.y2lat(shift[i] + yRes);
        }

        // Assert
        assertEquals("Error in mercator projection" ,
                xTarget, xRes, 1e-7f);
        assertEquals("Error in mercator projection" ,
                yTarget, yRes, 1e-7f);

        for(int i=0; i<lonRes.length; i++) {
            assertEquals("Error in mercator projection in " + i,
                    lonTarget[i], lonRes[i], 1e-7f);
            assertEquals("Error in mercator projection in " + i,
                    latTarget[i], latRes[i], 1e-7f);
        }

    }
}
