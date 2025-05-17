public class Vector4 {
    public double x, y, z, w;

    // Constructors
    public Vector4(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    // Constructor to convert from Vector3
    public Vector4(Vector3 v, double w) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = w;
    }

    // Dot Product: this • other
    public double dot(Vector4 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
    }

    // Matrix4 * Vector4 multiplication
    public Vector4 multiply(Matrix4 mat) {
        double[] res = new double[4];
        for (int row = 0; row < 4; row++) {
            res[row] = mat.m[row][0] * x + mat.m[row][1] * y + mat.m[row][2] * z + mat.m[row][3] * w;
        }
        return new Vector4(res[0], res[1], res[2], res[3]);
    }

    // Convert back to Vector3 (dividing by w if w != 0 for perspective divide)
    public Vector3 toVector3() {
        if (w != 0) {
            return new Vector3(x / w, y / w, z / w);
        } else {
            return new Vector3(x, y, z);
        }
    }

    // String representation for debugging
    @Override
    public String toString() {
        return "Vector4(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
}
