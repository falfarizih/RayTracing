public interface SceneObject {
    FinalRayHit intersect(Ray ray);

    Material getMaterial();
}
