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
        Material shinyMetal = new Material(new Color(0.9, 0.8, 0.7), 0.1, 1.0, 0.75);
        Material mattePlasticRed = new Material(new Color(1, 0, 0), 0.9, 0.0, 0.5); //red plastic
        Material mattePlasticYellow = new Material(new Color(1, 1, 0), 0.9, 0.0, 0);// Yellow plastic

        // SPHERE: Centered and scaled
        Matrix4 sphereQ = new Matrix4(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, -1
        );
        Quadric sphere = new Quadric(sphereQ, shinyMetal);
        Matrix4 sphereTransform = Matrix4.translation(0.6, 0, -3.5).multiply(Matrix4.scaling(0.5, 0.5, 0.5));
        sphere.applyTransformation(sphereTransform);

        // CYLINDER: Translated and rotated
        Matrix4 cylinderQ = new Matrix4(
                1, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, -1
        );
        Quadric cylinder = new Quadric(cylinderQ, mattePlasticYellow);
        Matrix4 cylinderTransform = Matrix4.translation(0, 0, -4).multiply(Matrix4.scaling(0.3, 1.0, 0.3));
        cylinder.applyTransformation(cylinderTransform);

        // SCENE OBJECTS LIST
        List<SceneObject> scene = new ArrayList<>();

        // ADD TO SCENE
        scene.add(new CSG(cylinder, sphere, CSG.Operation.UNION));

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


            // recursion
            Vector3 reflectDir = ray.direction.subtract(normal.multiply(2 * ray.direction.dot(normal))).normalize();
            Ray reflectRay = new Ray(hitPoint.add(reflectDir.multiply(0.001)), reflectDir);
            Color reflectedColor = traceRay(reflectRay, scene, light, depth - 1);




            if(!inShadow) {
                Vector3 lightDir = light.position.subtract(hitPoint).normalize();
                localColor = Lighting.cookTorrance(normal, viewDir, lightDir, light.color, light.intensity, hitObject.material);
            }

            // localColor + reflected
            return localColor.multiply(1 - material.reflectivity)
                    .add(reflectedColor.multiply(material.reflectivity));

        }

        //
        return new Color(0.1, 0.1, 0.1); // background
    }
}
