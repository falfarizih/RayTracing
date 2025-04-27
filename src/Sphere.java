public class Sphere {
    public Vector3 center;
    public double radius;
    public Material material;

    public Sphere(Vector3 center, double radius, Material material) {
        this.center = center;
        this.radius = radius;
        this.material = material;
    }

    // Returns distance t if hit, otherwise -1
    public double intersect(Ray ray) {
        Vector3 L = ray.origin.subtract(center);
        double a = 1.0;  // since ray.direction is normalized
        double b = 2.0 * ray.direction.dot(L);
        double c = L.dot(L) - radius * radius;

        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) return -1;

        double t1 = (-b - Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b + Math.sqrt(discriminant)) / (2 * a);

        if (t1 > 0) return t1;
        if (t2 > 0) return t2;
        return -1;
    }
}
