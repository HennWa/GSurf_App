package com.example.gsurfexample.utils.other;

import com.example.gsurfexample.utils.algorithms.Matrix;

public class GlobalParams {

    private static GlobalParams instance;

    // General
    public float eps = 1e-6f;

    // ResampleFilter
    public final long sampleR = 100;  // [ms]
    public final float sampleRs = sampleR/1e3f;  // [s]

    // Madgwick algorithm
    public final float betaMadgwick = 0.1f; // [-]

    // Highpass filter
    //public static final float cutoff = 0.1f; // [Hz]

    // Low pass filter speed
    // cut-off frequency 0.3 Hz
    //public float nominatorLPVelocity[] = {0.39133577f, 0.78267155f, 0.39133577f};
    //public float denominatorLPVelocity[] = {1.f,   0.36952738f, 0.19581571f};
    // cut-off frequency 0.2 Hz
    //public float nominatorLPVelocity[] = {0.00362168f, 0.00724336f, 0.00362168f};
    //public float denominatorLPVelocity[] = {1.f,     -1.82269493f,  0.83718165f};
    // cut-off frequency 0.1 Hz
    public float nominatorLPVelocity[] = {0.00094469f, 0.00188938f, 0.00094469f};
    public float denominatorLPVelocity[] = {1.f,      -1.91119707f, 0.91497583f};


    // KalmanFilter
    double dt = (double)sampleR/1000;
    double s2_x = 70*70;
    double s2_y = 70*70;
    double lambda2 = 10*10;
    public final int maxIntervalLength = 20;
    public Matrix A = new Matrix(4,4);
    public Matrix B = new Matrix(4,2);
    public Matrix Q = new Matrix(4,4);
    public Matrix H = new Matrix(2,4);
    public Matrix R = new Matrix(2,2);
    public Matrix x0 = new Matrix(4,1);
    public Matrix P0 = new Matrix(4,4);

    // state categorization
    public final float waveThresholdVelocity = 3.5f;  // [m/s]
    public enum States{
        FLOATING,
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

    public Matrix getA() {
        return A;
    }

    public Matrix getB() {
        return B;
    }

    public Matrix getQ() {
        return Q;
    }

    public Matrix getH() {
        return H;
    }

    public Matrix getR() {
        return R;
    }

    public Matrix getX0() {
        return x0;
    }

    public Matrix getP0() {
        return P0;
    }

}