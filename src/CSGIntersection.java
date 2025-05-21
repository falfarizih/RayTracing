public class CSGIntersection {
    public Quadric A;
    public Quadric B;

    public CSGIntersection(Quadric A, Quadric B) {
        this.A = A;
        this.B = B;
    }

    public FinalRayHit intersectIntersection(Ray ray) {
        // Get the interval (entry/exit points) for both objects
        IndividualHitPoints intervalA = A.intersectIndividual(ray);
        IndividualHitPoints intervalB = B.intersectIndividual(ray);

        // If the ray misses either object, there is no intersection
        if (intervalA == null || intervalB == null) {
            return null;
        }

        double A_in = intervalA.t_enter;
        double B_in = intervalB.t_enter;

        // determine the entry point
        double entry = Math.max(A_in, B_in); //compare entry point of both object, take the furthest one, because we need the second hit


        // determine which object we are entering second
        Quadric hit_object;
        if (A_in > B_in) { //takes the object of the second entry
            hit_object = A;
        } else {
            hit_object = B;
        }

            // return the first hit point and the object that was hit (the second object)
        return new FinalRayHit(entry, hit_object);
    }
}