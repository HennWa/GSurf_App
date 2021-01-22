package com.example.gsurfexample;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Simple Test of Digital filter.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DigitalButterworthFilterTest {
    @Test
    public void testFilter() {

        float testNominator[] = {0.00782021f, 0.01564042f, 0.00782021f};
        float testDenominator[] = {1.f, -1.73472577f,  0.7660066f};
        float x[] = {1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f, 10f, 10f, 11f, 12f, 13f, 14f, 15};
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
    }
}