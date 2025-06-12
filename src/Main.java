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
        Material shinyMetal = new Material(new Color(0.9, 0.8, 0.7), 0.1, 1.0);
        Material mattePlastic = new Material(new Color(1, 1, 0), 0.9, 0.0);  // Yellow plastic

        // SPHERE: Centered and scaled
        Matrix4 sphereQ = new Matrix4(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, -1
        );
        Quadric sphere = new Quadric(sphereQ, shinyMetal);
        Matrix4 sphereTransform = Matrix4.translation(0, 0, -3).multiply(Matrix4.scaling(1.2, 1.2, 1.2));
        sphere.applyTransformation(sphereTransform);

        // CYLINDER: Translated and rotated
        Matrix4 cylinderQ = new Matrix4(
                1, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, -1
        );
        Quadric cylinder = new Quadric(cylinderQ, mattePlastic);
        Matrix4 cylinderTransform = Matrix4.translation(0.5, 0, -3).multiply(Matrix4.rotationZ(Math.toRadians(45)));
        cylinder.applyTransformation(cylinderTransform);

        // SCENE OBJECTS LIST
        List<SceneObject> scene = new ArrayList<>();

        // Add CSG operation result to scene
        // You can change between UNION, INTERSECTION, DIFFERENCE
        scene.add(new CSG(cylinder, sphere, CSG.Operation.UNION));

        // LIGHT
        Light light = new Light(new Vector3(3, 3, 0), 2.0, new Color(1.0, 1.0, 1.0));

        // RENDER LOOP
        for (int y = 0; y < resY; y++) {
            for (int x = 0; x < resX; x++) {
                Ray ray = camera.generateRay(x, y, resX, resY);
                Color color = traceRay(ray, scene, light);
                Color corrected = color.applyGamma(2.2);
                pixels[y * resX + x] = corrected.toRGB();
            }
        }

        mis.newPixels(); // Refresh image
    }

    public static Color traceRay(Ray ray, List<SceneObject> scene, Light light) {
        double closest = Double.POSITIVE_INFINITY;
        Quadric hitObject = null;

        for (SceneObject obj : scene) {
            FinalRayHit result = obj.intersect(ray);
            if (result != null && result.t > 0 && result.t < closest) {
                closest = result.t;
                hitObject = result.hitObject;
            }
        }

        if (hitObject != null) {
            Vector3 hitPoint = ray.getPoint(closest);
            Vector3 normal = hitObject.getNormal(hitPoint);
            Vector3 lightDir = light.position.subtract(hitPoint).normalize();
            Vector3 viewDir = ray.origin.subtract(hitPoint).normalize();

            return Lighting.cookTorrance(normal, viewDir, lightDir, light.color, light.intensity, hitObject.material);
        }

        return new Color(0.1, 0.1, 0.1); // background
    }
}
