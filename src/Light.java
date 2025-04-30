public class Light {
    public Vector3 position;
    public double intensity;
    public Color color;

    public Light(Vector3 position, double intensity, Color color) {
        this.position = position;
        this.intensity = intensity;
        this.color = color;
    }
}