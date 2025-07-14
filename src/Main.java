import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        int resX = 800;
        int resY = 800;

        int[] pixels = new int[resX * resY];
        MemoryImageSource mis = new MemoryImageSource(resX, resY,
                new DirectColorModel(24, 0xff0000, 0xff00, 0xff),
                pixels, 0, resX);
        mis.setAnimated(true);
        Image image = Toolkit.getDefaultToolkit().createImage(mis);

        JFrame frame = new JFrame("Render Window");
        frame.add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // CAMERA SETUP
        Camera camera = new Camera(
                new Vector3(0, 0, 0),
                new Vector3(0, 0, -1),
                4,
                4
        );

        // MATERIALS
        Material mirror = new Material(new Color(1.0, 1.0, 1.0), 0.1, 0, 1.0, 1.0, 0.0);
        Material glass = new Material(new Color(1.0, 1.0, 1.0), 0.0, 0.0, 0.0, 1.5, 1.0);
        Material translucentGlass = new Material(new Color(0.9, 0.9, 1.0), 1, 0.0, 0.0, 1.5, 0.8);
        Material matteRed = new Material(new Color(1.0, 0.2, 0.2), 0.9, 0.0, 0.0, 1.0, 0.0);

        // SPHERE QUADRIC
        Matrix4 sphereQ = new Matrix4(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, -1
        );
        //SPHERE A
        Quadric sphere_a = new Quadric(sphereQ, matteRed);
        Matrix4 sphereATransform = Matrix4.translation(0.5, 0.2, -2).multiply(Matrix4.scaling(1, 1, 1));
        sphere_a.applyTransformation(sphereATransform);

        // SPHERE B
        Quadric sphere_b = new Quadric(sphereQ, matteRed);
        Matrix4 sphereBTransform = Matrix4.translation(0.5, -1, -1.5).multiply(Matrix4.scaling(0.5, 0.25, 0.5));
        sphere_b.applyTransformation(sphereBTransform);

        // CYLINDER QUADRIC
        Matrix4 cylinderQ = new Matrix4(
                1, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, -1
        );

        // CYLINDER
        Quadric cylinder = new Quadric(cylinderQ, matteRed);
        Matrix4 cylinderTransform = Matrix4.translation(-1, 0, -2).multiply(Matrix4.scaling(0.3, 1.0, 0.3));
        cylinder.applyTransformation(cylinderTransform);


        // FLAT SPHERE
        Quadric sphere_flat = new Quadric(sphereQ, matteRed);
        Matrix4 groundTransform = Matrix4.translation(0, 1, -4).multiply(Matrix4.scaling(5.0, 0.1, 5.0));
        sphere_flat.applyTransformation(groundTransform);



        // SCENE OBJECTS LIST
        List<SceneObject> scene = new ArrayList<>();

        // ADD TO SCENE
        //scene.add(new CSG(cylinder, sphere, CSG.Operation.UNION));
        scene.add(sphere_a);
        scene.add(sphere_b);
        scene.add(sphere_flat);
        scene.add(cylinder);


        // LIGHT
        Light light = new Light(new Vector3(2, 0, 0), 2.0, new Color(1.0, 1.0, 1.0));

        // RENDER LOOP
        for (int y = 0; y < resY; y++) {
            for (int x = 0; x < resX; x++) {
                Ray ray = camera.generateRay(x, y, resX, resY);
                Color color = traceRay(ray, scene, light, 3);
                Color corrected = color.applyGamma(2.2);
                pixels[y * resX + x] = corrected.toRGB();
            }
        }

        // DISPLAY IMAGE
        mis.newPixels();
    }

    public static Color traceRay(Ray ray, List<SceneObject> scene, Light light, int depth) {
        if (depth <= 0) return new Color(0, 0, 0);

        double closest = Double.POSITIVE_INFINITY;
        Quadric hitObject = null;

        // check all object in the scene
        for (SceneObject obj : scene) {
            FinalRayHit result = obj.intersect(ray);
            if (result != null && result.t > 0 && result.t < closest) {
                closest = result.t;
                hitObject = result.hitObject;
            }
        }

        // calculate lighting if any object was hit
        if (hitObject != null) {
            Color localColor = new Color(0, 0, 0);
            Vector3 hitPoint = ray.getPoint(closest);
            Material material = hitObject.material;
            Vector3 viewDir = ray.origin.subtract(hitPoint).normalize();
            Vector3 normal = hitObject.getNormal(hitPoint);

            // Soft Shadow Sampling
            List<Vector3> shadowSamples = Lighting.generateLightSamples(hitPoint, light, 16, 0.1);
            double visibility = Lighting.sampleOcclusion(hitPoint, shadowSamples, scene, 100.0);

            // Ambient Occlusion
            List<Vector3> aoSamples = Lighting.generateHemisphereSamples(normal, 16);
            double ao = Lighting.sampleOcclusion(hitPoint, aoSamples, scene, 1.0);



            // reflection
            Vector3 reflectDir = ray.direction.subtract(normal.multiply(2 * ray.direction.dot(normal))).normalize(); //r = v - 2(n · v)n
            Ray reflectRay = new Ray(hitPoint.add(reflectDir.multiply(0.001)), reflectDir);
            Color reflectedColor = traceRay(reflectRay, scene, light, depth - 1);


            // refraction
            Color refractedColor = new Color(0, 0, 0);
            double fresnel = 1.0;

            if (material.transparency > 0.0) {
                double i1 = 1.0;               // ior of air
                double i2 = material.ior;      // ior of material
                Vector3 n = normal;

                double a = -n.dot(ray.direction); // a = -v1.n = cos(w) = angle between ray and normal
                if (a < 0) {             //if ray is inside the object
                    n = n.negate();         // flip normal
                    double temp = i1;
                    i1 = i2;
                    i2 = temp;                  // swap the air ior and material
                    a = -n.dot(ray.direction);
                }

                double i = i1 / i2;           // i = i1/i2
                double sinT2 = i * i * (1 - a * a);       // sin^2(θ_t)

                if (sinT2 <= 1.0) { //no internal reflection
                    double b = Math.sqrt(1 - sinT2);     // b = sqrt(1-i^2(1-a^)) = angle adter refraction

                    Vector3 refractDir = ray.direction.multiply(i).add(n.multiply(i * a - b)).normalize(); //  v2 = i*v1 + (i*a – b)n
                    Ray refractRay = new Ray(hitPoint.add(refractDir.multiply(0.001)), refractDir);
                    refractedColor = traceRay(refractRay, scene, light, depth - 1);

                    //Fresnel via Schlick approximation
                    fresnel = Math.pow(1 - a, 5); // F = (1-cosI)^5
                } else {   //total internal reflection
                    fresnel = 1.0;
                }
            }

            // diffuse lighting
            if (material.transparency >= 1.0) {
                localColor = new Color(0, 0, 0);
            } else {
                Vector3 lightDir = light.position.subtract(hitPoint).normalize();
                Color contribution = Lighting.cookTorrance(
                        normal,
                        viewDir,
                        lightDir,
                        light.color,
                        light.intensity * visibility,
                        material
                );
                localColor = localColor.add(contribution.multiply(ao));
            }

            // localColor + reflected + refraction
            Color finalColor;
            if (material.transparency > 0.0) {
                // mix reflection & refraction  (1 – T) * localColor + T * (F * reflectedColor + (1 – F) * refractedColor)
                Color reflectionRefraction = reflectedColor.multiply(fresnel)
                        .add(refractedColor.multiply(1 - fresnel)); //(F * reflected + (1 – F) * refracted)
                finalColor = localColor.multiply(1 - material.transparency) // (1 – T) * localColor
                        .add(reflectionRefraction.multiply(material.transparency)); //T * Rr
            } else {
                // mix local + reflection via reflectivity  (1−R)⋅localColor+R⋅reflectedColor
                finalColor = localColor.multiply(1 - material.reflectivity) //localColor * (1-R)
                        .add(reflectedColor.multiply(material.reflectivity)); // reflected * R
            }

            return finalColor.clamp();
        }

        //
        return new Color(0.1, 0.1, 0.1); // background
    }
}
