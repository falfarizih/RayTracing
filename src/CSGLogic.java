public class CSGLogic {
    public static FinalRayHit union(Ray ray, Quadric A, Quadric B) {
        double tA = A.intersectFirstHit(ray);
        double tB = B.intersectFirstHit(ray);

        if (tA > 0 && (tB <= 0 || tA < tB)) {
            return new FinalRayHit(tA, A);
        }
        if (tB > 0) {
            return new FinalRayHit(tB, B);
        }
        return null;
    }

    public static FinalRayHit intersection(Ray ray, Quadric A, Quadric B) {
        IndividualHitPoints intervalA = A.intersectIndividual(ray);
        IndividualHitPoints intervalB = B.intersectIndividual(ray);

        if (intervalA == null || intervalB == null) {
            return null;
        }

        double A_in = intervalA.t_enter;
        double B_in = intervalB.t_enter;
        double entry = Math.max(A_in, B_in);

        Quadric hit_object = (A_in > B_in) ? A : B;
        return new FinalRayHit(entry, hit_object);
    }

    public static FinalRayHit difference(Ray ray, Quadric A, Quadric B) {
        IndividualHitPoints intervalA = A.intersectIndividual(ray);
        IndividualHitPoints intervalB = B.intersectIndividual(ray);

        if (intervalA == null) return null;

        if (intervalB == null) {
            return new FinalRayHit(intervalA.t_enter, A);
        }

        double A_in = intervalA.t_enter;
        double A_out = intervalA.t_exit;
        double B_in = intervalB.t_enter;
        double B_out = intervalB.t_exit;

        if (B_in > A_out) {
            return new FinalRayHit(A_in, A);
        }

        if (B_in < A_in && B_out > A_out) {
            return null;
        }

        if (B_in < A_in && B_out < A_out) {
            return new FinalRayHit(B_out, A);
        }

        if (B_in > A_in && B_out < A_out) {
            return new FinalRayHit(A_in, A);
        }

        return new FinalRayHit(A_in, A);
    }
}
