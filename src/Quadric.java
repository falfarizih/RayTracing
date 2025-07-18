public class Quadric implements SceneObject{
    public Matrix4 Q;         // Quadric Matrix
    public Material material;

    public Quadric(Matrix4 Q, Material material) {
        this.Q = Q;
        this.material = material;
    }

    // Apply Transformation: Q' = (M⁻¹)ᵗ * Q * M⁻¹
    public void applyTransformation(Matrix4 M) {
        Matrix4 M_inv = M.inverse();
        Matrix4 M_inv_T = M_inv.transpose();
        this.Q = M_inv_T.multiply(this.Q).multiply(M_inv);
    }

    public FinalRayHit intersect(Ray ray) {
        IndividualHitPoints interval = this.intersectIndividual(ray);
        if (interval == null) return null;
        return new FinalRayHit(interval.t_enter, this);
    }

    public double intersectFirstHit(Ray ray) {
        Vector4 p = new Vector4(ray.origin, 1);   // because its a point, w=1
        Vector4 v = new Vector4(ray.direction, 0); // because its a direction, w=0

        // (p + t·v)ᵀ · Q · (p + t·v) = 0
        // At² + Bt + C = 0
        // Quadratic coefficients: A, B, C
        double A = v.dot(Q.multiply(v));        // vᵀQv
        double B = 2 * p.dot(Q.multiply(v));    // 2pᵀQv
        double C = p.dot(Q.multiply(p));        // p^t . Q . p

        double discriminant = B * B - 4 * A * C;

        if (discriminant < 0) return -1; // No intersection

        double sqrt_disc = Math.sqrt(discriminant);
        double t1 = (-B - sqrt_disc) / (2 * A);
        double t2 = (-B + sqrt_disc) / (2 * A);

        // Return the smallest positive solution
        if (t1 > 0) return t1;
        if (t2 > 0) return t2;

        return -1; // Both intersections behind the ray origin
    }

    // returns hit points both entry and exit
    public IndividualHitPoints intersectIndividual(Ray ray) {
        Vector4 p = new Vector4(ray.origin, 1);     // Ray origin in homogeneous coordinates
        Vector4 v = new Vector4(ray.direction, 0);  // Ray direction, w = 0

        double A = v.dot(Q.multiply(v));        // vᵀQv
        double B = 2 * p.dot(Q.multiply(v));    // 2pᵀQv
        double C = p.dot(Q.multiply(p));        // p^t . Q . p

        double discriminant = B * B - 4 * A * C;
        if (discriminant < 0) return null;

        double sqrtDisc = Math.sqrt(discriminant);
        double t1 = (-B - sqrtDisc) / (2 * A);
        double t2 = (-B + sqrtDisc) / (2 * A);

        if (t1 > t2) {
            double temp = t1;
            t1 = t2;
            t2 = temp;
        }

        if (t2 < 0) return null; // both hits behind camera

        return new IndividualHitPoints(t1, t2);
    }

//partial ableitung
    public Vector3 getNormal(Vector3 point) {
        double x = point.x;
        double y = point.y;
        double z = point.z;

        // Extract coefficients from matrix Q
        double a = Q.m[0][0];
        double b = Q.m[1][1];
        double c = Q.m[2][2];
        double d = Q.m[0][1];
        double e = Q.m[0][2];
        double f = Q.m[1][2];
        double g = Q.m[0][3];
        double h = Q.m[1][3];
        double i = Q.m[2][3];

        Vector3 normal = new Vector3(
                a * x + d * y + e * z + g,     // ∂F/∂x = ax + dy + ez + g
                b * y + d * x + f * z + h,      // ∂F/∂y = by + dx + fz + h
                c * z + e * x + f * y + i       // ∂F/∂z = cz + ex + fy + i
        );

        return normal.normalize();
    }

    public Material getMaterial() {
        return material;
    }
}