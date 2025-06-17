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
        Material frostedGlass = new Material(new Color(0.9, 0.9, 1.0), 0.5, 0.0, 0.1, 1.5, 0.8);
        Material matteRed = new Material(new Color(1.0, 0.2, 0.2), 0.9, 0.0, 0.0, 1.0, 0.0);

        // SPHERE: Centered and scaled
        Matrix4 sphereQ = new Matrix4(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, -1
        );
        Quadric sphere = new Quadric(sphereQ, glass);
        Matrix4 sphereTransform = Matrix4.translation(0.5, 0.2, -2).multiply(Matrix4.scaling(1, 1, 1));
        sphere.applyTransformation(sphereTransform);

        // CYLINDER: Translated and rotated
        Matrix4 cylinderQ = new Matrix4(
                1, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, -1
        );


        Quadric cylinder = new Quadric(cylinderQ, matteRed);
        Matrix4 cylinderTransform = Matrix4.translation(0, 0, -5).multiply(Matrix4.scaling(0.3, 1.0, 0.3));
        cylinder.applyTransformation(cylinderTransform);


        // FLAT SPHERE
        Matrix4 sphereQ2 = new Matrix4(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, -1
        );
        Quadric sphere2 = new Quadric(sphereQ2, matteRed);
        Matrix4 groundTransform = Matrix4.translation(0, 1, -4).multiply(Matrix4.scaling(5.0, 0.1, 5.0));
        sphere2.applyTransformation(groundTransform);



        // SCENE OBJECTS LIST
        List<SceneObject> scene = new ArrayList<>();

        // ADD TO SCENE
        //(scene.add(new CSG(cylinder, sphere, CSG.Operation.UNION));
        scene.add(sphere);
        scene.add(sphere2);
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

            Vector3 normal = hitObject.getNormal(hitPoint);
            boolean inShadow = Lighting.isInShadow(hitPoint, light, scene);

            Vector3 viewDir = ray.origin.subtract(hitPoint).normalize();


            // reflection
            Vector3 reflectDir = ray.direction.subtract(normal.multiply(2 * ray.direction.dot(normal))).normalize();
            Ray reflectRay = new Ray(hitPoint.add(reflectDir.multiply(0.001)), reflectDir);
            Color reflectedColor = traceRay(reflectRay, scene, light, depth - 1);






            // refraction
            Color refractedColor = new Color(0, 0, 0);
            double fresnel = 1.0;

            if (material.transparency > 0.0) {
                double n1 = 1.0;               // air
                double n2 = material.ior;      // object
                Vector3 n = normal;

                double cosI = -n.dot(ray.direction);
                if (cosI < 0) {
                    // Ray is inside object, flip normal and invert IORs
                    n = n.negate();
                    double temp = n1;
                    n1 = n2;
                    n2 = temp;
                    cosI = -n.dot(ray.direction);
                }

                double eta = n1 / n2;
                double sinT2 = eta * eta * (1 - cosI * cosI);

                if (sinT2 <= 1.0) {
                    double cosT = Math.sqrt(1 - sinT2);
                    Vector3 refractDir = ray.direction.multiply(eta)
                            .add(n.multiply(eta * cosI - cosT))
                            .normalize();
                    Ray refractRay = new Ray(hitPoint.add(refractDir.multiply(1e-4)), refractDir);
                    refractedColor = traceRay(refractRay, scene, light, depth - 1);

                    // --- Fresnel via Schlick approximation ---
                    fresnel = Math.pow(1 - cosI, 5);
                } else {
                    // Total internal reflection
                    fresnel = 1.0;
                }
            }

            // diffuse lighting
            if(!inShadow) {
                Vector3 lightDir = light.position.subtract(hitPoint).normalize();
                localColor = Lighting.cookTorrance(normal, viewDir, lightDir, light.color, light.intensity, hitObject.material);
            }

            // localColor + reflected + refraction
            Color finalColor;

            if (material.transparency > 0.0) {
                // Transparent surface: mix reflection & refraction
                Color reflectionRefraction = reflectedColor.multiply(fresnel)
                        .add(refractedColor.multiply(1 - fresnel));
                finalColor = localColor.multiply(1 - material.transparency)
                        .add(reflectionRefraction.multiply(material.transparency));
            } else {
                // Opaque surface: mix local + reflection via reflectivity
                finalColor = localColor.multiply(1 - material.reflectivity)
                        .add(reflectedColor.multiply(material.reflectivity));
            }

            return finalColor.clamp();
        }

        //
        return new Color(0.1, 0.1, 0.1); // background
    }
}
