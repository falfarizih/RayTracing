public class Light {
    public Vector3 position;
    public double intensity;
    public ColorRGB color;

    public Light(Vector3 position, double intensity, ColorRGB color) {
        this.position = position;
        this.intensity = intensity;
        this.color = color;
    }
}