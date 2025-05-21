public class CSGUnion {
    public Quadric A;
    public Quadric B;

    public CSGUnion(Quadric A, Quadric B) {
        this.A = A;
        this.B = B;
    }

    public IntersectionResult intersect(Ray ray) {
        double tA = A.intersect(ray);   // Find intersection with object A
        double tB = B.intersect(ray);   // Find intersection with object B

        if (tA > 0 && (tB <= 0 || tA < tB)) {   // A is hit first, or B is not hit at all, if so then return the hit from A
            return new IntersectionResult(tA, A);
        }
        if (tB > 0) {
            return new IntersectionResult(tB, B);   // Otherwise if B is hit (and A is not, or B is closer), return B
        }
        return null;
    }
}
