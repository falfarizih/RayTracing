public class CSGIntersection {
    public Quadric A;
    public Quadric B;

    public CSGIntersection(Quadric A, Quadric B) {
        this.A = A;
        this.B = B;
    }

    public IntersectionResult intersect(Ray ray) {
        double tAin = A.intersect(ray);
        double tBout = B.intersect(ray);

        if (tAin < 0 || tBout < 0) return null; // Must intersect both

        // Take the max of the entry points (second entry point into both objects)
        double tEntry = Math.max(tAin, tBout);

        if (tEntry > 0) {
            Quadric hitObj = (tAin > tBout) ? A : B;
            return new IntersectionResult(tEntry, hitObj);
        }

        return null; // No valid intersection where ray is inside both
    }
}
