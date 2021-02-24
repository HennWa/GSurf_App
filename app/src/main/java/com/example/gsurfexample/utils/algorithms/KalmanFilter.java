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

    /*auxiliary matrices KalmanFilter*/
    public Matrix auxBxU;
    public Matrix auxSDxSD;
    public Matrix auxSDxMD;
    public Matrix KYk;

    /*auxiliary matrices for interval update*/
    private int count; // counting interval length without update
    private final double dt; // constant sample rate
    private final Matrix IntData; //storage for states in interval without update
    private final Matrix LastStateOfLastInterval; // storage for last state of last interval
    //used for velocity calculation

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
                        Matrix R, Matrix x0, Matrix P0, double dt) throws Exception{

        // maximal Storage size
        int maxIntDataLength = 20;

        int n = A.shape()[0];
        int v = H.shape()[0];
        if((n != A.shape()[1]) ||
        (n != B.shape()[0]) ||
        (n != H.shape()[1]) ||
        (n != Q.shape()[0]) ||
        (n != Q.shape()[0]) ||
        (n != x0.shape()[0]) ||
        (n != P0.shape()[0]) ||
        (n != P0.shape()[1]) ||
        (v != R.shape()[0]) ||
        (v != R.shape()[1]) ||
        (1 != x0.shape()[1])){
            throw new Exception("Dimension of matrices are incompatible!");
        }

        this.dt = dt;
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
        this.KYk = new Matrix(stateDimension, 1);

        this.IntData = new Matrix(stateDimension, maxIntDataLength);
        this.LastStateOfLastInterval = new Matrix(stateDimension,1);
    }

    /**
     * Calculates estimated state based on input. Assumption: constant sample rate.
     * @param Uk System input of one sample time.
     */
    public void predict(Matrix Uk) throws Exception {

        if((B.shape()[1] != Uk.shape()[0])||
        (Uk.shape()[1] != 1)){
            throw new Exception("Dimension of matrices are incompatible!");
        }

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

        //Pk_k = Pk_km1
        Matrix.matrixCopy(Pk_km1, Pk_k);

        // Save state in interval data for later deviation compensation
        IntData.setDataByIndex(0, Xk_k.shape()[0]-1, count, count, this.getState());

        // Increase counter
        count += 1;
    }

    /**
     * Update of estimated state by including measurement information.
     * @param Zk Measurement (GPS here).
     */
    public void update(Matrix Zk) throws Exception {

        if((H.shape()[0] != Zk.shape()[0]) ||
        (Zk.shape()[1] != 1)){
            throw new Exception("Dimension of matrices are incompatible!");
        }

        //Vk = Zk - Hk*Xk|k-1
        Matrix.matrixMultiply(H, Xk_km1, Vk);
        Matrix.matrixSubtract(Zk, Vk, Vk);        // self.V = z - self.H @ self._x

        //Sk = Rk + Hk*Pk|k-1*Hk(t)
        Matrix.matrixMultiplyByTranspose(Pk_km1, H, auxSDxMD);
        Matrix.matrixMultiply(H, auxSDxMD, Sk);
        Matrix.matrixAdd(R, Sk, Sk);              // self.S = self.H @ self._P @ self.H.transpose() + self.R

        //Kk = Pk|k-1*Hk(t)*Sk(inv)
        if (!(Matrix.matrixDestructiveInvert(Sk, SkInv))) {    // if matrix can no
            throw new Exception("Inversion of matrix Sk failed!");
        }
        Matrix.matrixMultiply(auxSDxMD, SkInv, K);  // self.K = self._P @ self.H.transpose() @ np.linalg.inv(self.S)

        //xk|k = xk|k-1 + Kk*Yk
        Matrix.matrixMultiply(K, Vk, KYk);        // only used for storage
        Matrix.matrixAdd(Xk_km1, KYk, Xk_k);      // self._x = self._x + self.K @ self.V   // Xk_k is new state, prediction in Xk_km1

        //Pk|k = (I - Kk*Hk) * Pk|k-1 - SEE WIKI!!!   self._P = self._P - self.K @ self.S @ self.K.transpose()
        Matrix.matrixMultiply(K, H, auxSDxSD);
        Matrix.matrixSubtractFromIdentity(auxSDxSD);
        Matrix.matrixMultiply(auxSDxSD, Pk_km1, Pk_k);  // self._P = self._P - self.K @ self.S @ self.K.transpose()

        // Save updated state in data for later deviation compensation
        IntData.setDataByIndex(0, Xk_k.shape()[0]-1, count-1, count-1, this.getState()); // count-1 since has been increased in predict
    }

    /**
     * Updates all states in the interval between two updates with measurements
     * by subtracting an error term assuming a quadratic shift error of position estimations
     * from accelerometer. This function is specific to the state estimation (X,Y,dX,dY).
     * @return double[][] with all updated states in interval between last interval update
     * 		and last Kalman update. In first column first state ... .
     */
    public double[][] updateKalmanResultsInInterval() throws Exception {

        if(this.count < 1){    // count equal to number of predictions
            throw new Exception("Predict has to be invoked before call of this method!");
        }

        // Calculate deviation Coefficients for quadratic correction (delta = 1/2 c dt^2)
        double dtUpdate = this.count * this.dt;
        Matrix delta = new Matrix(2, 1);  // Correct X, Y
        Matrix.matrixSubtract(this.Xk_km1.getSubMatrix(0, 1, 0, 0), this.Xk_k.getSubMatrix(0, 1, 0, 0), delta);

        Matrix devCoef = new Matrix(2, 1);
        Matrix.matrixMultiply(2/(dtUpdate*dtUpdate), delta, devCoef);  // shape n x 1

        //Calculate updated interval data of position
        double[] helperTimesWeights_ = new double[this.count];
        for (int i=0; i<this.count-1; ++i){helperTimesWeights_[i] = 0.5 * ((i+1)*this.dt)*((i+1)*this.dt);} // note: last entry = 0
        Matrix TimesWeights = new Matrix(1, this.count);
        TimesWeights.setData(helperTimesWeights_);

        Matrix devEstimation = new Matrix(2, this.count);
        Matrix.matrixMultiply(devCoef, TimesWeights, devEstimation);

        Matrix Xi_hatPos = new Matrix(2, this.count);
        Matrix.matrixSubtract(IntData.getSubMatrix(0, 1, 0, this.count-1), devEstimation, Xi_hatPos);

        //Calculate updated interval data of velocity
        Matrix Xi_hatPosExt = new Matrix(2,this.count+1);
        Matrix.matrixConcatenate(this.LastStateOfLastInterval.getSubMatrix(0, 1, 0, 0), Xi_hatPos, Xi_hatPosExt, 1);
        Matrix Xi_hatVel = new Matrix(2,this.count);
        Matrix.matrixSubtract(Xi_hatPosExt.getSubMatrix(0, 1, 1, this.count),
                Xi_hatPosExt.getSubMatrix(0, 1, 0, this.count-1), Xi_hatVel); // Diff operation
        Matrix.matrixMultiply(1/this.dt, Xi_hatVel, Xi_hatVel);

        Matrix Xi_hat = new Matrix(4, this.count);
        Matrix.matrixConcatenate(Xi_hatPos, Xi_hatVel, Xi_hat, 0);

        // store last state for next iteration and set counter back
        this.LastStateOfLastInterval.setData(
                Xi_hat.getSubMatrix(0, 3, this.count-1, this.count-1).getDataFlat());
        this.count = 0;

        return Xi_hat.getData();
    }

    /**
     * Combines call of update[] and updateKalmanResultsInInterval().
     * @param 	Zk Measurement (GPS here).
     * @return 	double[][] with all updated states in interval between last interval update
     * 			and last Kalman update. In first column first state ... .
     */
    public double[][] updateAndGetResults(Matrix Zk){
        try {
            this.update(Zk);
            return updateKalmanResultsInInterval();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
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