public class FinalRayHit {
    //this class contains the final to be rendered point and its respective object

    public double t;
    public Quadric hitObject;
    public boolean flipNormal;

    public FinalRayHit(double t, Quadric hitObject) {
        this.t = t;
        this.hitObject = hitObject;
    }

}
