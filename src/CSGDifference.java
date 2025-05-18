public class CSGDifference {
    public Quadric A;
    public Quadric B;

    public CSGDifference(Quadric A, Quadric B) {
        this.A = A;
        this.B = B;
    }

    public IntersectionResult intersect(Ray ray) {
        double tAin = A.intersect(ray);
        double tAout = computeExit(A, ray, tAin);

        double tBin = B.intersect(ray);
        double tBout = computeExit(B, ray, tBin);

        if (tAin < 0) return null; // No hit with A at all

        if (tBin < 0 || tBin > tAout) {
            // B is not overlapping or behind A, simple case
            return new IntersectionResult(tAin, A);
        } else if (tBout > tAout) {
            // B starts before A but doesn't fully cover it
            return new IntersectionResult(tBout, A);
        }

        return null; // B completely cuts out the intersection
    }

    private double computeExit(Quadric obj, Ray ray, double tEntry) {
        if (tEntry < 0) return -1;
        Vector3 entryPoint = ray.getPoint(tEntry);
        Vector3 normal = obj.getNormal(entryPoint);
        Vector3 reverseDir = ray.direction.multiply(-1);

        Ray reverseRay = new Ray(entryPoint.add(normal.multiply(1e-6)), reverseDir);
        return obj.intersect(reverseRay);
    }
}
