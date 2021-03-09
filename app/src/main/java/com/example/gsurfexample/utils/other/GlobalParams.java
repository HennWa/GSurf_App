package com.example.gsurfexample.utils.other;

import com.example.gsurfexample.utils.algorithms.Matrix;

public class GlobalParams {

    private static GlobalParams instance;

    // ResampleFilter
    public static final long sampleR = 100;  // [ms]
    public static final float sampleRs = sampleR/1e3f;  // [s]

    // Madgwick algorithm
    public static final float betaMadgwick = 0.1f; // [-]

    // Highpass filter
    //public static final float cutoff = 0.1f; // [Hz]

    // KalmanFilter
    double dt = (double)sampleR/1000;
    double s2_x = 70*70;
    double s2_y = 70*70;
    double lambda2 = 10*10;
    public static final int maxIntervalLength = 20;
    public static Matrix A = new Matrix(4,4);
    public static Matrix B = new Matrix(4,2);
    public static Matrix Q = new Matrix(4,4);
    public static Matrix H = new Matrix(2,4);
    public static Matrix R = new Matrix(2,2);
    public static Matrix x0 = new Matrix(4,1);
    public static Matrix P0 = new Matrix(4,4);

    // state categorization
    public static final float waveThresholdVelocity = 3.5f;  // [m/s]
    public enum States{
        NOTONWAVE,
        SURFINGWAVE,
        PADDELING
    }

    // Singleton class
    public static GlobalParams getInstance() {
        if (instance == null)
            instance = new GlobalParams();
        return instance;
    }

    // Constructor
    private GlobalParams() {

        // KalmanFilter
        // A
        A.setData(1,  0,  dt,  0,
                  0,  1,   0, dt,
                  0,  0,   1,  0,
                  0,  0,   0,  1);

        // B
        B.setData(0.5*dt*dt,         0,
                          0, 0.5*dt*dt,
                         dt,         0,
                          0,        dt);

        // Q
        Q.setData(s2_x*dt*dt*dt/3,              0, s2_x*dt*dt/2,               0,
                                0,s2_y*dt*dt*dt/3,            0,  s2_y * dt*dt/2,
                     s2_x*dt*dt/2,              0,      s2_x*dt,               0,
                                0,   s2_y*dt*dt/2,            0,         s2_y*dt);

        // H
        H.setData(1, 0, 0, 0,
                  0, 1, 0, 0);

        // R
        R.setData(lambda2,       0,
                        0, lambda2);

        // Initial conditions
        x0.setData(0,
                   0,
                   0,
                   0);

        P0.setData(0,  0,  0,  0,
                   0,  0,  0,  0,
                   0,  0,  0,  0,
                   0,  0,  0,  0);
    }

    public static Matrix getA() {
        return A;
    }

    public static Matrix getB() {
        return B;
    }

    public static Matrix getQ() {
        return Q;
    }

    public static Matrix getH() {
        return H;
    }

    public static Matrix getR() {
        return R;
    }

    public static Matrix getX0() {
        return x0;
    }

    public static Matrix getP0() {
        return P0;
    }

}