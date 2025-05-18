public class CSGUnion {
    public Quadric A;
    public Quadric B;

    public CSGUnion(Quadric A, Quadric B) {
        this.A = A;
        this.B = B;
    }

    public IntersectionResult intersect(Ray ray) {
        double tA = A.intersect(ray);
        double tB = B.intersect(ray);

        if (tA > 0 && (tB <= 0 || tA < tB)) {
            return new IntersectionResult(tA, A);
        }
        if (tB > 0) {
            return new IntersectionResult(tB, B);
        }
        return null;
    }
}
