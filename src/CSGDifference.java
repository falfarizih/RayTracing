public class CSGDifference {
    public Quadric A;
    public Quadric B;

    public CSGDifference(Quadric A, Quadric B) {
        this.A = A;
        this.B = B;
    }

    public FinalRayHit intersectDifference(Ray ray) {
        IndividualHitPoints intervalA = A.intersectIndividual(ray);
        IndividualHitPoints intervalB = B.intersectIndividual(ray);

        // If no hit with A at all, we are definitely not inside the result
        if (intervalA == null) return null;

        // Case 1: A only  (no substraction : keep A entry)
        if (intervalB == null) {
            return new FinalRayHit(intervalA.t_enter, A);
        }

        double A_in = intervalA.t_enter;
        double A_out = intervalA.t_exit;
        double B_in = intervalB.t_enter;
        double B_out = intervalB.t_exit;


        // Case 2: A_in -> Aout -> Bin -> Bout (B fully behind A : Keep A entry)
        if (B_in > A_out) {
            return new FinalRayHit(A_in, A);
        }

        // Case 3: Bin -> Ain -> Aout -> Bout (B fully removes A : no hit)
        if (B_in < A_in && B_out > A_out) {
            return null; // A is fully subtracted
        }

        // Case 4: B_in -> A_in -> B_out -> A_out (optimal case : keep the first B exit)
        if (B_in < A_in && B_out < A_out) {
            return new FinalRayHit(B_out, A);
        }

        // Case 5: B is fully inside A and starts after Ain
        if (B_in > A_in && B_out < A_out) {
            return new FinalRayHit(A_in, A); // Could also return two segments if needed
        }

        // Fallback: Default to just A if B is behind or not overlapping
        return new FinalRayHit(A_in, A);
    }
}