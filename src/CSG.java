public class CSG implements SceneObject {
    public enum Operation {
        UNION, INTERSECTION, DIFFERENCE
    }

    private Quadric A, B;
    private Operation operation;

    public CSG(Quadric A, Quadric B, Operation op) {
        this.A = A;
        this.B = B;
        this.operation = op;
    }

    @Override
    public FinalRayHit intersect(Ray ray) {
        IndividualHitPoints intervalA = A.intersectIndividual(ray);
        IndividualHitPoints intervalB = B.intersectIndividual(ray);

        if (intervalA == null && intervalB == null) return null;

        switch (operation) {
            case UNION:
                return CSGLogic.union(ray, A, B);
            case INTERSECTION:
                return CSGLogic.intersection(ray, A, B);
            case DIFFERENCE:
                return CSGLogic.difference(ray, A, B);
        }
        return null;
    }

    public Material getMaterial() {
        return null;
    }
}
