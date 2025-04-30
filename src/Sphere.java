public class Sphere {
    public Vector3 center;
    public double radius;
    public Color color;

    public Sphere(Vector3 center, double radius, Color color) {
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    // Returns distance t if hit, otherwise -1
    public double intersect(Ray ray) {
        // |(p+s*v)-c|^2 = r^2
        //  s^2(v.v)  + 2*(v.(p-c)) + (p-c).(p-c)-r^2  = 0
        Vector3 L = ray.origin.subtract(center);  // (p-c) center to origin

        double a = 1.0;                             // (v.v)since ray.direction is normalized
        double b = 2.0 * ray.direction.dot(L);      // 2*(v.(p-c))
        double c = L.dot(L) - radius * radius;      // (p-c).(p-c)-r^2

        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) return -1;

        double t1 = (-b - Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b + Math.sqrt(discriminant)) / (2 * a);

        if (t1 > 0) return t1;
        if (t2 > 0) return t2;
        return -1;
    }
}
