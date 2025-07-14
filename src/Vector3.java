public class Vector3 {
    public double x, y, z;

    // constructor
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // add two vectors
    public Vector3 add(Vector3 v) {
        return new Vector3(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    // subtract two vectors
    public Vector3 subtract(Vector3 v) {
        return new Vector3(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    // multiply vector by a scalar
    public Vector3 multiply(double scalar) {
        return new Vector3(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    // dot product
    public double dot(Vector3 v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    // cross product
    public Vector3 cross(Vector3 v) {
        return new Vector3(
                this.y * v.z - this.z * v.y,
                this.z * v.x - this.x * v.z,
                this.x * v.y - this.y * v.x
        );
    }

    // normalize vector (length = 1)
    public Vector3 normalize() {
        double length = Math.sqrt(this.dot(this));
        if (length == 0) return new Vector3(0, 0, 0);  // Avoid division by zero
        return this.multiply(1.0 / length);
    }

    public Vector3 negate() {
        return new Vector3(-this.x, -this.y, -this.z);
    }

    //get length
    public double length() {
        return Math.sqrt(this.dot(this));
    }
    
}
