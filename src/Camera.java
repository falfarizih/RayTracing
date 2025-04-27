public class Camera {
    public Vector3 position;
    public Vector3 forward, right, up;
    public double imagePlaneWidth, imagePlaneHeight;

    public Camera(Vector3 position, Vector3 lookAt, double imgWidth, double imgHeight) {
        this.position = position;
        this.forward = lookAt.subtract(position).normalize();
        this.right = new Vector3(0, 1, 0).cross(forward).normalize();
        this.up = forward.cross(right).normalize();
        this.imagePlaneWidth = imgWidth;
        this.imagePlaneHeight = imgHeight;
    }

    public Ray generateRay(int x, int y, int resX, int resY) {
        double u = (x + 0.5) / resX - 0.5;
        double v = (y + 0.5) / resY - 0.5;

        Vector3 pointOnPlane = position
                .add(forward)
                .add(right.multiply(u * imagePlaneWidth))
                .add(up.multiply(v * imagePlaneHeight));

        Vector3 dir = pointOnPlane.subtract(position);
        return new Ray(position, dir);
    }
}
