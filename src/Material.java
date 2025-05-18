public class Material {
    public Color albedo;
    public double roughness;
    public double metalness;

    public Material(Color albedo, double roughness, double metalness) {
        this.albedo = albedo;
        this.roughness = roughness;
        this.metalness = metalness;
    }
}
