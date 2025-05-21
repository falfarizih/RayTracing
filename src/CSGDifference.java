public class CSGDifference {
    public Quadric A, B;

    public CSGDifference(Quadric A, Quadric B) {
        this.A = A;
        this.B = B;
    }

    public IntersectionResult intersect(Ray ray) {
        HitInterval a = A.intersectInterval(ray);
        if (a == null) return null;

        HitInterval b = B.intersectInterval(ray);

        if (b == null || b.tEnter > a.tExit || b.tExit < a.tEnter) {
            // B doesn't affect A
            return (a.tEnter > 0) ? new IntersectionResult(a.tEnter, A) : null;
        }

        if (b.tEnter > a.tEnter && b.tEnter < a.tExit) {
            // B enters after A
            return (a.tEnter > 0) ? new IntersectionResult(a.tEnter, A) : null;
        }

        if (b.tExit > a.tEnter && b.tExit < a.tExit) {
            // B exits before A ends
            return (b.tExit > 0) ? new IntersectionResult(b.tExit, A) : null;
        }

        return null; // Fully subtracted
    }
}