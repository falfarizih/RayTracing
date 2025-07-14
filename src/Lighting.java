import java.util.ArrayList;
import java.util.List;

public class Lighting {
    public static Color cookTorrance(Vector3 normal, Vector3 viewDir, Vector3 lightDir, Color lightColor, double lightIntensity, Material material)
    {
        double NdotL = Math.max(0, normal.dot(lightDir)); // N · L → Light incidence
        double NdotV = Math.max(0, normal.dot(viewDir));    // N · V → View direction
        Vector3 halfVector = lightDir.add(viewDir).normalize(); // H = (L + V) / |L + V|
        double NdotH = Math.max(0, normal.dot(halfVector)); // N · H → Halfway vector
        double VdotH = Math.max(0, viewDir.dot(halfVector)); // V · H → View-Halfway
        double r = material.roughness;








        // D (GGX)
        double r2 = r * r;
        double denomD = NdotH * NdotH * (r2 - 1.0) + 1.0; // (N·H²)(r² - 1) + 1
        double D = r2 / (Math.PI * denomD * denomD);    // D = r² / [π * denom²]

        // F (Schlick)
        // F0 = Mix between dielectric constant (0.04) and material.albedo based on metalness
        //F0= (1 –metalness) * 0,04 + metalness* albedo
        Color F0 = new Color(0.04, 0.04, 0.04).multiply(1 - material.metalness)
                .add(material.albedo.multiply(material.metalness));
        // F = F0 + (1 - F0) * (1 - N·V)^5
        Color F = F0.add(Color.one().subtract(F0).multiply(Math.pow(1 - NdotV, 5)));

        // G (Smith-Schlick GGX)
        double k = r / 2.0;     // Simplified approximation factor
        double G_V = NdotV / (NdotV * (1.0 - k) + k);     // G(V)
        double G_L = NdotL / (NdotL * (1.0 - k) + k);   // G(L)
        double G = G_V * G_L;                           // Final G term

        // Specular ks = D * F * G
        Color ks = F.multiply(D * G);

        // Diffuse kd = (1 - ks) * (1 - metalness)
        Color kd = Color.one().subtract(ks).multiply(1.0 - material.metalness);

        // Final Color
        return lightColor.multiply(lightIntensity)      // Light color and intensity
                .multiply(NdotL)                        // Lambert cosine factor (N·L)
                .multiply(kd.multiply(material.albedo).add(ks));        // Final color = Diffuse + Specular
    }

    public static boolean isInShadow(Vector3 point, Light light, List<SceneObject> objects) {
        Vector3 toLight = light.position.subtract(point);
        Vector3 dir = toLight.normalize();
        Ray shadowRay = new Ray(point.add(dir.multiply(0.001)), dir);

        for (SceneObject obj : objects) {
            FinalRayHit hit = obj.intersect(shadowRay);
            if (hit != null && hit.t < toLight.length()) {
                if (hit.hitObject.material.transparency > 0) {
                    continue; // Fully transparent objects don't block light
                }
                return true;
            }
        }
        return false; // Light is visible
    }

    public static double sampleOcclusion(Vector3 origin, List<Vector3> sampleDirections, List<SceneObject> objects, double maxDistance) {
        int unblocked = 0;
        for (Vector3 dir : sampleDirections) {
            Ray sampleRay = new Ray(origin.add(dir.multiply(0.001)), dir);
            boolean blocked = false;
            for (SceneObject obj : objects) {
                FinalRayHit hit = obj.intersect(sampleRay);
                if (hit != null && hit.t > 0 && hit.t < maxDistance && hit.hitObject.material.transparency < 1.0) {
                    blocked = true;
                    break;
                }
            }
            if (!blocked) {
                unblocked++;
            }
        }
        return (double) unblocked / sampleDirections.size();
    }

    public static List<Vector3> generateHemisphereSamples(Vector3 normal, int samples) {
        List<Vector3> directions = new ArrayList<>();
        for (int i = 0; i < samples; i++) {
            Vector3 randomDir = Vector3.randomHemisphereDirection(normal);
            directions.add(randomDir);
        }
        return directions;
    }

    public static List<Vector3> generateLightSamples(Vector3 point, Light light, int samples, double lightSize) {
        List<Vector3> directions = new ArrayList<>();
        for (int i = 0; i < samples; i++) {
            Vector3 randomOffset = Vector3.randomUnitVector().multiply(lightSize);
            Vector3 lightSample = light.position.add(randomOffset);
            Vector3 dir = lightSample.subtract(point).normalize();
            directions.add(dir);
        }
        return directions;
    }

}
