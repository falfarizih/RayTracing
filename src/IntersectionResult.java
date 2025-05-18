public class IntersectionResult {
    public double t;
    public Quadric hitObject;

    public IntersectionResult(double t, Quadric hitObject) {
        this.t = t;
        this.hitObject = hitObject;
    }
}
