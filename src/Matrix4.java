public class Matrix4 {
    public double[][] m = new double[4][4];

    // Constructor to directly set matrix values (row-major order)
    public Matrix4(double... values) {
        if (values.length != 16) throw new IllegalArgumentException("Need 16 values.");
        for (int i = 0; i < 16; i++) {
            m[i / 4][i % 4] = values[i];
        }
    }

    // Identity Matrix
    public static Matrix4 identity() {
        return new Matrix4(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
    }

    // Translation Matrix
    public static Matrix4 translation(double tx, double ty, double tz) {
        return new Matrix4(
                1, 0, 0, tx,
                0, 1, 0, ty,
                0, 0, 1, tz,
                0, 0, 0, 1
        );
    }

    // Scaling Matrix
    public static Matrix4 scaling(double sx, double sy, double sz) {
        return new Matrix4(
                sx, 0, 0, 0,
                0, sy, 0, 0,
                0, 0, sz, 0,
                0, 0, 0, 1
        );
    }

    // Rotation around Z-axis
    public static Matrix4 rotationZ(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Matrix4(
                cos, -sin, 0, 0,
                sin,  cos, 0, 0,
                0,    0,   1, 0,
                0,    0,   0, 1
        );
    }

    // Matrix Multiplication
    // Matrix × Matrix → Matrix4
    public Matrix4 multiply(Matrix4 other) {
        Matrix4 result = new Matrix4(new double[16]);
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                for (int k = 0; k < 4; k++) {
                    result.m[row][col] += this.m[row][k] * other.m[k][col];
                }
            }
        }
        return result;
    }

    // Matrix × Vector4 → Vector4
    public Vector4 multiply(Vector4 vec) {
        double[] res = new double[4];
        for (int row = 0; row < 4; row++) {
            res[row] = m[row][0] * vec.x + m[row][1] * vec.y + m[row][2] * vec.z + m[row][3] * vec.w;
        }
        return new Vector4(res[0], res[1], res[2], res[3]);
    }

    // Transpose
    public Matrix4 transpose() {
        Matrix4 result = new Matrix4(new double[16]);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.m[i][j] = this.m[j][i];
            }
        }
        return result;
    }

    //For simplicity, assume it's always invertible and affine transform

    public Matrix4 inverse() {
        Matrix4 result = new Matrix4(new double[16]);

        // Extract the upper-left 3x3 rotation/scale matrix
        double[][] R = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                R[i][j] = this.m[i][j];
            }
        }

        // Compute inverse of the rotation/scale part (assumes no shearing)
        double det = R[0][0] * (R[1][1] * R[2][2] - R[1][2] * R[2][1]) -
                R[0][1] * (R[1][0] * R[2][2] - R[1][2] * R[2][0]) +
                R[0][2] * (R[1][0] * R[2][1] - R[1][1] * R[2][0]);

        if (Math.abs(det) < 1e-8) {
            throw new RuntimeException("Matrix is singular and cannot be inverted.");
        }

        double invDet = 1.0 / det;

        // Compute the inverse of the 3x3 matrix manually (Cofactor method)
        result.m[0][0] = invDet * (R[1][1] * R[2][2] - R[1][2] * R[2][1]);
        result.m[0][1] = invDet * (R[0][2] * R[2][1] - R[0][1] * R[2][2]);
        result.m[0][2] = invDet * (R[0][1] * R[1][2] - R[0][2] * R[1][1]);

        result.m[1][0] = invDet * (R[1][2] * R[2][0] - R[1][0] * R[2][2]);
        result.m[1][1] = invDet * (R[0][0] * R[2][2] - R[0][2] * R[2][0]);
        result.m[1][2] = invDet * (R[0][2] * R[1][0] - R[0][0] * R[1][2]);

        result.m[2][0] = invDet * (R[1][0] * R[2][1] - R[1][1] * R[2][0]);
        result.m[2][1] = invDet * (R[0][1] * R[2][0] - R[0][0] * R[2][1]);
        result.m[2][2] = invDet * (R[0][0] * R[1][1] - R[0][1] * R[1][0]);

        // Inverse translation: -R⁻¹ * T
        double tx = this.m[0][3];
        double ty = this.m[1][3];
        double tz = this.m[2][3];

        result.m[0][3] = -(result.m[0][0] * tx + result.m[0][1] * ty + result.m[0][2] * tz);
        result.m[1][3] = -(result.m[1][0] * tx + result.m[1][1] * ty + result.m[1][2] * tz);
        result.m[2][3] = -(result.m[2][0] * tx + result.m[2][1] * ty + result.m[2][2] * tz);

        // Last row remains [0, 0, 0, 1]
        result.m[3][0] = 0;
        result.m[3][1] = 0;
        result.m[3][2] = 0;
        result.m[3][3] = 1;

        return result;
    }

}
