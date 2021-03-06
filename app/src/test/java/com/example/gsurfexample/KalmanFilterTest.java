package com.example.gsurfexample;

import com.example.gsurfexample.utils.algorithms.KalmanFilter;
import com.example.gsurfexample.utils.algorithms.Matrix;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Simple Test of KalmanFilter.
 *
 */
public class KalmanFilterTest {

    @Test
    public void testKalmanFilter() {

        // Preparations
        int maxIntervals = 20;
        double dt = 0.1;
        double s2_x = 70*70;
        double s2_y = 70*70;
        double lambda2 = 10*10;

        // Matrices for KalmanFilter as used in algorithm
        // A
        Matrix A = new Matrix(4,4);
        A.setData(1,  0,  dt,  0,
                  0,  1,   0, dt,
                  0,  0,   1,  0,
                  0,  0,   0,  1);

        // B
        Matrix B = new Matrix(4,2);
        B.setData(0.5*dt*dt,         0,
                          0, 0.5*dt*dt,
                         dt,         0,
                          0,        dt);

        // Q
        Matrix Q = new Matrix(4,4);
        Q.setData(s2_x*dt*dt*dt/3,              0, s2_x*dt*dt/2,               0,
                                0,s2_y*dt*dt*dt/3,            0,  s2_y * dt*dt/2,
                     s2_x*dt*dt/2,              0,      s2_x*dt,               0,
                                0,   s2_y*dt*dt/2,            0,         s2_y*dt);

        // H
        Matrix H = new Matrix(2,4);
        H.setData(1, 0, 0, 0,
                  0, 1, 0, 0);

        // R
        Matrix R = new Matrix(2,2);
        R.setData(lambda2,       0,
                        0, lambda2);

        // Initial conditions
        Matrix x0 = new Matrix(4,1);
        x0.setData(0,
                0,
                0,
                0);

        Matrix P0 = new Matrix(4,4);
        P0.setData(0,  0,  0,  0,
                   0,  0,  0,  0,
                   0,  0,  0,  0,
                   0,  0,  0,  0);

        // Test1

        // Inputs
        Matrix Uk = new Matrix(2,1);
        double[][] u = {{ 0.04148839,  0.00676059,  0.00710022,  0.03073782, -0.05194414,
                         -0.14910391, -0.09373991, -0.03601638, -0.1023279 , -0.07476155,
                          0.13621953,  0.23829263,  0.07131373, -0.13309719, -0.13651066,
                         -0.00990951,  0.05626858,  0.0273886 , -0.01013763},
                        {-0.03760459, -0.03634708,  0.01739064,  0.07525632,  0.00694988,
                         -0.14468787, -0.17698628, -0.078811  , -0.05518062, -0.10710129,
                         -0.01600083,  0.16811877,  0.13964883, -0.07828563, -0.15792558,
                         -0.04375271,  0.03911996,  0.02125967, -0.02700419}};

        Matrix Zk = new Matrix(2,1);
        double[][] z = {{0.72796264, 0.73019129, 0.73241994, 0.7346486 , 0.73687725,
                         0.7391059 , 0.74133455, 0.7435632 , 0.74579185, 0.72729602,
                         0.7088002 , 0.69030438, 0.67180855, 0.65331273, 0.63481691,
                         0.61632108, 0.59782526, 0.57932944, 0.56083362},
                        {1.25330209, 1.24092472, 1.22854735, 1.21616999, 1.20379262,
                         1.19141525, 1.17903789, 1.16666052, 1.15428315, 1.15095282,
                         1.14762249, 1.14429216, 1.14096183, 1.1376315 , 1.13430117,
                         1.13097084, 1.12764051, 1.12431018, 1.12097985}};
        int n = u[0].length;

        // expected outputs
        double[][] targetEstPred = {{2.07441953e-04, -1.88022944e-04,  4.14883907e-03, -3.76045887e-03},
                {2.98952276e-02,  4.96158024e-02,  1.80259491e-01, 2.94774473e-01},
                { 1.88162610e-01,  3.17691578e-01,  7.82724297e-01, 1.32018891e+00},
                { 4.97757158e-01,  8.36964586e-01,  1.56608368e+00, 2.63358146e+00},
                { 7.89933915e-01,  1.31778515e+00,  1.94874290e+00, 3.25513551e+00},
                { 9.50867378e-01,  1.57125497e+00,  1.84818862e+00, 3.05666038e+00},
                { 1.00085996e+00,  1.63503688e+00,  1.50811099e+00, 2.44577371e+00},
                { 9.86946267e-01,  1.59010700e+00,  1.10449020e+00, 1.73503915e+00},
                { 9.42371748e-01,  1.49450974e+00,  7.17650756e-01, 1.07428751e+00},
                { 8.88439468e-01,  1.38450185e+00,  4.03063684e-01, 5.32051521e-01},
                { 8.26224195e-01,  1.28805281e+00,  1.62868120e-01, 1.62587667e-01},
                { 7.68246293e-01,  1.21491222e+00,  8.46497542e-04, -4.28642262e-02},
                { 7.18506887e-01,  1.16585738e+00, -1.15646823e-01, -1.40910752e-01},
                { 6.76185623e-01,  1.13533288e+00, -2.03073391e-01, -1.88252072e-01},
                { 6.40452746e-01,  1.11719965e+00, -2.53029272e-01, -2.00396169e-01},
                { 6.11467388e-01,  1.10796497e+00, -2.62965064e-01, -1.77628999e-01},
                { 5.88580947e-01,  1.10522740e+00, -2.49635064e-01, -1.37205156e-01},
                { 5.69713324e-01,  1.10606080e+00, -2.32224908e-01, -9.95082016e-02},
                { 5.52638788e-01,  1.10773869e+00, -2.17977063e-01, -7.32452825e-02}};

        double[][] targetEstState = {{0.01190308,  0.01995662,  0.17958343,  0.29840918},
                { 0.10992568,  0.18575964,  0.78201427,  1.31844985},
                { 0.34130248,  0.57398272,  1.56300989,  2.62605583},
                { 0.5947999 ,  0.99230635,  1.95393732,  3.25444052},
                { 0.765303  ,  1.26486549,  1.86309901,  3.07112917},
                { 0.84958016,  1.38957458,  1.51748498,  2.46347234},
                { 0.87631717,  1.41620903,  1.10809183,  1.74292025},
                { 0.87009503,  1.38680509,  0.72788355,  1.07980557},
                { 0.84775929,  1.33076119,  0.41053984,  0.54276165},
                { 0.81061848,  1.27171404,  0.14924617,  0.16418775},
                { 0.76935311,  1.22003924, -0.02298276, -0.0596761 },
                { 0.73042814,  1.1806467 , -0.1227782 , -0.15487564},
                { 0.69582748,  1.15376666, -0.18976367, -0.18042351},
                { 0.66507312,  1.13644964, -0.23937821, -0.18460361},
                { 0.63771435,  1.12550911, -0.26197411, -0.17325373},
                { 0.6138258 ,  1.11914351, -0.25526192, -0.14111715},
                { 0.59307276,  1.11611792, -0.23496377, -0.10163417},
                { 0.57438581,  1.1149282 , -0.2169633 , -0.07054486},
                { 0.55662069,  1.11417262, -0.20497093, -0.05223005}};

        // Instantiate
        KalmanFilter kalmanFilter;
        try{
            kalmanFilter = new KalmanFilter(A, B, H, Q, R, x0, P0, dt, maxIntervals);

            // Storage allocation for results
            double[][] estPred = new double[n][4];
            double[][] estState = new double[n][4];

            for(int i = 0; i<n; i++) {
                Uk.setData(u[0][i], u[1][i]);
                kalmanFilter.predict(Uk);
                estPred[i] = kalmanFilter.getState();
                Zk.setData(z[0][i], z[1][i]);
                try {
                    kalmanFilter.update(Zk);
                }catch(Exception e){
                    e.printStackTrace();
                }
                estState[i] = kalmanFilter.getState();
            }

            // Assert
            for(int i=0; i<n; i++) {
                for(int j=0; j<4; j++) {
                    assertEquals("KalmanFilter error in prediction " + i +" state " + j,
                            targetEstPred[i][j], estPred[i][j], 1e-5f);
                    assertEquals("KalmanFilter error in update " + i +" state " + j,
                            targetEstState[i][j], estState[i][j], 1e-5f);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        // Test 2

        // target data for run with GPS update 0,4,8...
        double[][] targetProcState =     {{ 0.62944622,  1.17899486,  6.29446216, 11.78994862},
                { 0.64320065,  1.1938317,   0.1375443,   0.14836835},
                { 0.66380107,  1.20392799,  0.20600423,  0.10096289},
                { 0.69136737,  1.20984175,  0.27566303,  0.0591376 },
                { 0.72560433,  1.21152077,  0.34236961,  0.01679026},
                { 0.74383562,  1.21611708,  0.18231287,  0.04596306},
                { 0.75455302,  1.21006858,  0.10717397, -0.06048498},
                { 0.75832196,  1.19370466,  0.03768946, -0.16363919},
                { 0.75509952,  1.16763435, -0.03222445, -0.26070311},
                { 0.75646987,  1.15836243,  0.01370353, -0.09271917},
                { 0.7454111,   1.14970881, -0.11058766, -0.08653621},
                { 0.72348849,  1.14304959, -0.21922614, -0.06659224},
                { 0.6903775,   1.13916301, -0.33110991, -0.03886579},
                { 0.67815021,  1.13421196, -0.1222729,  -0.04951048},
                { 0.65942038,  1.12994258, -0.18729834, -0.04269383},
                { 0.63480394,  1.12652753, -0.24616439, -0.03415053},
                { 0.60526479,  1.12495203, -0.29539149, -0.01575496},
                { 0.,          0.,          0.,          0.        },
                { 0.,          0.,          0.,          0.        },
                { 0.,          0.,          0.,          0.        }};

        try{
            KalmanFilter kalmanFilter2 = new KalmanFilter(A, B, H, Q, R, x0, P0, 0.1, maxIntervals);

            double[][] estProc = new double[n][4];
            double[][] updatedInterval;

            for(int i = 0; i<n; i++) {
                Uk.setData(u[0][i], u[1][i]);
                kalmanFilter2.predict(Uk);
                Zk.setData(z[0][i], z[1][i]);

                if (i%4 == 0) {  // in this particular test gps update only every 4 samples
                    updatedInterval = kalmanFilter2.updateAndGetResults(Zk);
                    // copy solution
                    for(int p=0; p<updatedInterval.length; p++) {
                        for(int m=0; m<updatedInterval[0].length; m++) {
                            estProc[m+i-updatedInterval[0].length+1][p] = updatedInterval[p][m];  // Note: Transposition
                        }
                    }
                }
            }

            // Assert
            for(int i=0; i<n; i++) {
                for(int j=0; j<4; j++) {
                    assertEquals("KalmanFilter error in processing " + i +" state " + j,
                            targetProcState[i][j], estProc[i][j], 1e-5f);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
