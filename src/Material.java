public class Material {
    public Color albedo;
    public double roughness;
    public double metalness;
    public double reflectivity;
    public double ior;
    public double transparency;

    public Material(Color albedo, double roughness, double metalness, double reflectivity, double ior, double transparency) {
        this.albedo = albedo;
        this.roughness = roughness;
        this.metalness = metalness;
        this.reflectivity = reflectivity;
        this.ior = ior;
        this.transparency = transparency;
    }
}
