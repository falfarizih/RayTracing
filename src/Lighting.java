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


}
