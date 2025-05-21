public class CSGUnion {
    public Quadric A;
    public Quadric B;

    public CSGUnion(Quadric A, Quadric B) {
        this.A = A;
        this.B = B;
    }

    public FinalRayHit intersectUnion(Ray ray) {
        double tA = A.intersectFirstHit(ray);   // Find intersection with object A
        double tB = B.intersectFirstHit(ray);   // Find intersection with object B

        if (tA > 0 && (tB <= 0 || tA < tB)) {   // A is hit first, or B is not hit at all, then return A
            return new FinalRayHit(tA, A);
        }
        if (tB > 0) {
            return new FinalRayHit(tB, B);   // Otherwise if B is hit (and A is not, or B is closer), return B
        }
        return null;
    }
}
