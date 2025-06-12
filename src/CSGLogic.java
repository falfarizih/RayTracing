public class CSGLogic {
    public static FinalRayHit union(Ray ray, Quadric A, Quadric B) {
        double tA = A.intersectFirstHit(ray);   // find intersection with object A
        double tB = B.intersectFirstHit(ray);   // find intersection with object B

        if (tA > 0 && (tB <= 0 || tA < tB)) { // A is hit first, or B is not hit at all, then return A and the hit point
            return new FinalRayHit(tA, A);
        }
        if (tB > 0) {
            return new FinalRayHit(tB, B);  // otherwise if B is hit (and A is not, or B is closer) then return B and its hit point
        }
        return null;
    }

    public static FinalRayHit intersection(Ray ray, Quadric A, Quadric B) {
        // get the interval (entry/exit points) for both objects
        IndividualHitPoints intervalA = A.intersectIndividual(ray);
        IndividualHitPoints intervalB = B.intersectIndividual(ray);

        // if ray misses either object, there is no intersection
        if (intervalA == null || intervalB == null) {
            return null;
        }

        double A_in = intervalA.t_enter;
        double B_in = intervalB.t_enter;

        // determine the entry point
        double entry = Math.max(A_in, B_in); // compare entry point of oth object, take the furthest one because we need the second hit

        //determine which object we are entering second
        Quadric hit_object;
        if (A_in > B_in) {
            hit_object = A;
        } else {
            hit_object = B;
        }

        // return the hit point and the object that was hit
        return new FinalRayHit(entry, hit_object);
    }

    public static FinalRayHit difference(Ray ray, Quadric A, Quadric B) {
        IndividualHitPoints intervalA = A.intersectIndividual(ray);
        IndividualHitPoints intervalB = B.intersectIndividual(ray);

        // if no hit with A at all, we are definitely not inside the result
        if (intervalA == null) return null;


        // case 1 : A only (no substraction : keep A entry)
        if (intervalB == null) {
            return new FinalRayHit(intervalA.t_enter, A);
        }

        double A_in = intervalA.t_enter;
        double A_out = intervalA.t_exit;
        double B_in = intervalB.t_enter;
        double B_out = intervalB.t_exit;

        // case 2 : A_in -> A_out -> B_in -> B_out (B fully behind A: keep A entry)
        if (B_in > A_out) {
            return new FinalRayHit(A_in, A);
        }

        // case 3 :  B_in -> A_in -> A_out -> B_out (B fully removes A: no hit)
        if (B_in < A_in && B_out > A_out) {
            return null;
        }

        // case 4 : B_in -> A_in -> B_out -> A_out (optimal case : keep the first B exit)
        if (B_in < A_in && B_out < A_out) {
            return new FinalRayHit(B_out, A);
        }

        // case 5: B is fully inside A and starts after A_in
        if (B_in > A_in && B_out < A_out) {
            return new FinalRayHit(A_in, A);
        }

        // Default to just A
        return new FinalRayHit(A_in, A);
    }
}
