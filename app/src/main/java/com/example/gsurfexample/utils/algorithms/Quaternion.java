package com.example.gsurfexample.utils.algorithms;


public class Quaternion {

    private final float x;
    private final float y;
    private final float z;
    private final float w;

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getW() {
        return w;
    }

    public float[][] asMatrix(){
        float x2 = x * x;
        float y2 = y * y;
        float z2 = z * z;
        float w2 = w * w;

        float xy = x * y;
        float zw = z * w;
        float xz = x * z;
        float yw = y * w;
        float yz = y * z;
        float xw = x * w;

        float[][] matrix = new float[3][3];

        matrix[0][0] = x2 - y2 - z2 + w2;
        matrix[1][0] = 2 * (xy + zw);
        matrix[2][0] = 2 * (xz - yw);

        matrix[0][1] = 2 * (xy - zw);
        matrix[1][1] = - x2 + y2 - z2 + w2;
        matrix[2][1] = 2 * (yz + xw);

        matrix[0][2] = 2 * (xz + yw);
        matrix[1][2] = 2 * (yz - xw);
        matrix[2][2] = - x2 - y2 + z2 + w2;

        return matrix;
    }

    public float[] rotateVector(float[] vec){

        float[] res = new float[3];
        float[][] matrix = this.asMatrix();

        res[0] = matrix[0][0] * vec[0] + matrix[0][1] * vec[1] + matrix[0][2] * vec[2];
        res[1] = matrix[1][0] * vec[0] + matrix[1][1] * vec[1] + matrix[1][2] * vec[2];
        res[2] = matrix[2][0] * vec[0] + matrix[2][1] * vec[1] + matrix[2][2] * vec[2];

        return res;
    }

    public float[] toEulerAngles() {
        float pitch, yaw, roll;

        roll = (float)Math.atan2(-2*( y*z - w*x), w*w - x*x - y*y + z*z);
        pitch = (float)Math.asin(2*( x*z + w*y));
        yaw = (float)Math.atan2(-2*( x*y - w*z), w*w + x*x - y*y - z*z);

        /* Alte methode passt nicht zu Scipy
        float pitch = (float)Math.asin(2 * x * y + 2 * w * z);
        if(Math.abs(x * y + z * w - 0.5) < 1e-8){
            roll = 0;
            yaw = 2 * (float)Math.atan2(x, w);
        }else if(Math.abs(x * y + z * w + 0.5) < 1e-8){
            roll = -2 * (float)Math.atan2(x, w);
            yaw = 0;
        }else{
        roll = (float)Math.atan2(2 * w * x - 2 * y * z, 1 - 2 * x * x - 2 * z * z);
        yaw = (float)Math.atan2(2 * w * y - 2 * x * z, 1 - 2 * y * y - 2 * z * z);
        } */

        return new float[]{pitch, roll, yaw};
    }
}


