public class Material {
    public Color albedo;
    public double roughness;
    public double metalness;
    public double reflectivity;

    public Material(Color albedo, double roughness, double metalness, double reflectivity) {
        this.albedo = albedo;
        this.roughness = roughness;
        this.metalness = metalness;
        this.reflectivity = reflectivity;
    }
}
