public class Ray {
    public Vector3 origin;
    public Vector3 direction;

    public Ray(Vector3 origin, Vector3 direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }

    public Vector3 getPoint(double s) {
        return origin.add(direction.multiply(s)); // p(s)= p + s*v
    }
}
