package com.example.gsurfexample.utils.algorithms;

/**
 * KalmanFilter class. Taken from GitHub
 *
 * @see <a
 *      href=https://github.com/maddevsio/mad-location-manager/blob/master/madlocationmanager/src/main/java/mad/location/manager/lib/Filters/KalmanFilter.java</a>
 *
 * Note: Comments and line predict method added
 */
public class KalmanFilter {
    /*these matrices should be provided by user*/
    public Matrix A; //state transition model
    public Matrix H; //observation model
    public Matrix B; //control matrix
    public Matrix Q; //process noise covariance
    public Matrix R; //observation noise covariance

    /*these matrices will be updated by user*/
    public Matrix Uk; //control vector
    public Matrix Zk; //actual values (measured)
    public Matrix Xk_km1; //predicted state estimate
    public Matrix Pk_km1; //predicted estimate covariance
    public Matrix Vk; //measurement innovation

    public Matrix Sk; //innovation covariance
    public Matrix SkInv; //innovation covariance inverse

    public Matrix K; //Kalman gain (optimal)
    public Matrix Xk_k; //updated (current) state
    public Matrix Pk_k; //updated estimate covariance
    public Matrix Yk_k; //post fit residual

    /*auxiliary matrices*/
    public Matrix auxBxU;
    public Matrix auxSDxSD;
    public Matrix auxSDxMD;

    /**
     * Contructor
     * @param A System matrix for prediction. Dimension (n x n), where n is the dimension of the
     *          system state.
     * @param B System input matrix for prediction. Dimension (n x m), where n is the dimension of
     *          the system state and m the dimension of the input.
     * @param H Matrix of the observation model. Dimension (v x n), where v is the dimension of
     *          the measurements and n is the dimension of state.
     * @param Q Covariance matrix of process noise. Dimension (n x n), where n is the dimension of
     *          of the state.
     * @param R Covariance matrix of observation noise. Dimension (v x v), where n is the dimension of
     *          of the measurement.
     * @param x0 Initial state of Filter. Dimension (n x 1).
     * @param P0 Initial covariance matrix. Dimension (n x n), where n is the dimension of
     *           of the state.
     */
    public KalmanFilter(Matrix A, Matrix B, Matrix H, Matrix Q,
                        Matrix R, Matrix x0, Matrix P0) {
        this.A = A;
        this.H = H;
        this.Q = Q;
        this.R = R;

        this.B = B;

        this.Xk_k = x0;
        this.Pk_k = P0;

        int[] shapeB = B.shape();
        int controlDimension = shapeB[1];

        int[] shapeH = H.shape();
        int measureDimension = shapeH[0];

        int[] shapeX = x0.shape();
        int stateDimension = shapeX[0];

        this.Uk = new Matrix(controlDimension, 1);

        this.Zk = new Matrix(measureDimension, 1);

        this.Xk_km1 = new Matrix(stateDimension, 1);
        this.Pk_km1 = new Matrix(stateDimension, stateDimension);

        this.Vk = new Matrix(measureDimension, 1);
        this.Sk = new Matrix(measureDimension, measureDimension);
        this.SkInv = new Matrix(measureDimension, measureDimension);

        this.K = new Matrix(stateDimension, measureDimension);


        this.Yk_k = new Matrix(measureDimension, 1);

        this.auxBxU = new Matrix(stateDimension, 1);
        this.auxSDxSD = new Matrix(stateDimension, stateDimension);
        this.auxSDxMD = new Matrix(stateDimension, measureDimension);
    }

    /**
     * Calculates estimated state based on input. Assumption: constant sample rate.
     * @param Uk System input of one sample time.
     */
    public void predict(Matrix Uk) {
        //Xk|k-1 = Ak*Xk-1|k-1 + Bk*Uk
        Matrix.matrixMultiply(A, Xk_k, Xk_km1);
        Matrix.matrixMultiply(B, Uk, auxBxU);
        Matrix.matrixAdd(Xk_km1, auxBxU, Xk_km1);  // self._x = self.A @ self._x + self.B @ u

        //Pk|k-1 = Ak*Pk-1|k-1*Fk(t) + Qk
        Matrix.matrixMultiply(A, Pk_k, auxSDxSD);
        Matrix.matrixMultiplyByTranspose(auxSDxSD, A, Pk_km1);
        Matrix.matrixAdd(Pk_km1, Q, Pk_km1);       // self._P = self.A @ self._P @ self.A.transpose() + self.Q

        //Xk_k = Xk_km1
        Matrix.matrixCopy(Xk_km1, Xk_k);
    }

    /**
     * Update of estimated state by including measurement information.
     * @param Zk Measurement of one sample time.
     */
    public void update(Matrix Zk) {
        //Vk = Zk - Hk*Xk|k-1
        Matrix.matrixMultiply(H, Xk_km1, Vk);
        Matrix.matrixSubtract(Zk, Vk, Vk);        // self.V = z - self.H @ self._x

        //Sk = Rk + Hk*Pk|k-1*Hk(t)
        Matrix.matrixMultiplyByTranspose(Pk_km1, H, auxSDxMD);
        Matrix.matrixMultiply(H, auxSDxMD, Sk);
        Matrix.matrixAdd(R, Sk, Sk);              // self.S = self.H @ self._P @ self.H.transpose() + self.R

        //Kk = Pk|k-1*Hk(t)*Sk(inv)
        if (!(Matrix.matrixDestructiveInvert(Sk, SkInv)))
            return; //matrix hasn't inversion
        Matrix.matrixMultiply(auxSDxMD, SkInv, K);  // self.K = self._P @ self.H.transpose() @ np.linalg.inv(self.S)

        //xk|k = xk|k-1 + Kk*Yk
        Matrix.matrixMultiply(K, Vk, Xk_k);        // only storage used here in Xk_k!
        Matrix.matrixAdd(Xk_km1, Xk_k, Xk_k);      // self._x = self._x + self.K @ self.V   // Xk_k is new state, prediction in Xk_km1

        //Pk|k = (I - Kk*Hk) * Pk|k-1 - SEE WIKI!!!
        Matrix.matrixMultiply(K, H, auxSDxSD);
        Matrix.matrixSubtractFromIdentity(auxSDxSD);
        Matrix.matrixMultiply(auxSDxSD, Pk_km1, Pk_k);  // self._P = self._P - self.K @ self.S @ self.K.transpose()
    }

    /**
     * Get state.
     * @return Get current estimated state.
     */
    public double[] getState() {
        double[] flatXk_k = new double[this.Xk_k.shape()[0]];
        for(int i=0; i<this.Xk_k.shape()[0]; i++){
            flatXk_k[i] = this.Xk_k.getData()[i][0];
        }
        return flatXk_k;
    }
}

