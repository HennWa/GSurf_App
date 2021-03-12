package com.example.gsurfexample;

import android.util.Log;

import com.example.gsurfexample.utils.algorithms.DigitalButterworthFilter;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Simple Test of Digital filter.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DigitalButterworthFilterTest {
    @Test
    public void testFilter() {

        // Test 1
        float testNominator[] = {0.00782021f, 0.01564042f, 0.00782021f};
        float testDenominator[] = {1.f, -1.73472577f,  0.7660066f};
        float x[] = {1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f, 10f, 10f, 11f, 12f, 13f, 14f, 15f};
        float y[] ={7.82020803e-03f, 4.48467485e-02f, 1.34368144e-01f, 2.92581472e-01f,
                    5.29745063e-01f, 8.51247233e-01f, 1.25857729e+00f, 1.75019128e+00f,
                    2.32227006e+00f, 2.96937113e+00f, 3.67715854e+00f, 4.41711210e+00f,
                    5.16637901e+00f, 5.92280292e+00f, 6.69232841e+00f, 7.47909924e+00f,
                    8.28575008e+00f};
        DigitalButterworthFilter testFilter = new DigitalButterworthFilter(testNominator,testDenominator);
        float[] yRes = testFilter.filter(x);

        for(int i=0; i<y.length; i++){
            assertEquals("Digital ButterWorthFilter error",y[i], yRes[i], 1e-5f);
        }

        // Test 2
        DigitalButterworthFilter testFilter2 = new DigitalButterworthFilter(testNominator,testDenominator);
        float xSeq1[] = {1f, 2f};
        float xSeq2[] = {3f, 4f, 5f, 6f, 7f, 8f};
        float xSeq3[] = {9f, 10f, 10f, 10f, 11f, 12f, 13f, 14f, 15f};

        float[] yResSeq1 = testFilter2.filterSequence(xSeq1);
        float[] yResSeq2 = testFilter2.filterSequence(xSeq2);
        float[] yResSeq3 = testFilter2.filterSequence(xSeq3);

        float[] yResSeq12 = Arrays.copyOf(yResSeq1, yResSeq1.length + yResSeq2.length);
        System.arraycopy(yResSeq2, 0, yResSeq12, yResSeq1.length, yResSeq2.length);
        float[] yRes2 = Arrays.copyOf(yResSeq12, yResSeq12.length + yResSeq3.length);
        System.arraycopy(yResSeq3, 0, yRes2, yResSeq12.length, yResSeq3.length);

        for(int i=0; i<y.length; i++){
            assertEquals("Digital ButterWorthFilter error",y[i], yRes2[i], 1e-5f);
        }
    }
}