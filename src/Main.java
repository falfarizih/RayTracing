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

        // SETTING THE CAMERA (poition, view direction, image plane width, image plane height)

        Camera camera = new Camera(
                new Vector3(0, 0, 0),
                new Vector3(0, 0, -1),
                4,
                4
        );


        // CREATING OBJECTS IN THE SCENE


        //Material Setup
        Material shinyMetal = new Material(new Color(0.9, 0.8, 0.7), 0.1, 1.0);  // Smooth metal surface
        Material mattePlastic = new Material(new Color(0.7, 0.1, 0.1), 0.9, 0.0); // Rough plastic material


        //Sphere centered at (0, 0, -5)
        Matrix4 sphereQ = new Matrix4(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
               0, 0, 0, -1
        );
        Quadric sphere = new Quadric(sphereQ, shinyMetal); // Red Sphere

        // Cylinder along Y-axis centered at (0, 0, -5)
        Matrix4 cylinderQ = new Matrix4(
                1, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 1, 0,
               0, 0, 0, -1
        );
        Quadric cylinder = new Quadric(cylinderQ, mattePlastic); // Green Cylinder

        // Move both objects back along Z-axis
        Matrix4 translateSphere = Matrix4.translation(0, 0, -3);
        Matrix4 translateCylinder = Matrix4.translation(1.5, 0, -3);

        sphere.applyTransformation(translateSphere);
        cylinder.applyTransformation(translateCylinder);

        // Union: Sphere ∪ Cylinder
        CSGUnion unionObj = new CSGUnion(sphere, cylinder);

        // Intersection: Sphere ∩ Cylinder
        CSGIntersection intersectObj = new CSGIntersection(sphere, cylinder);

        // Difference: Sphere − Cylinder
        CSGDifference diffObj = new CSGDifference(sphere, cylinder);


        // ADD TO SCENE
        // Keep quadrics list for individual objects (optional)
        List<Quadric> quadrics = new ArrayList<>();

        // CSG Object Lists
        List<CSGUnion> unions = new ArrayList<>();
        List<CSGIntersection> intersections = new ArrayList<>();
        List<CSGDifference> differences = new ArrayList<>();

        // Choose which CSG to render:
        quadrics.add(sphere);
        quadrics.add(cylinder);
        //unions.add(unionObj);          // To show Union result
        //intersections.add(intersectObj); // Uncomment to show Intersection result
        //differences.add(diffObj);        // Uncomment to show Difference result




        // CREATE LIGHT (position, color object)
        Light light = new Light(new Vector3(3, 3, 0), 2.0, new Color(1.0, 1.0, 1.0));


        // RENDER
        for (int y = 0; y < resY; y++) {
            for (int x = 0; x < resX; x++) {
                Ray ray = camera.generateRay(x, y, resX, resY);
                Color color = traceRay(ray, quadrics, unions, intersections, differences, light);
                Color corrected = color.applyGamma(2.2);  // Apply gamma correction before converting to RGB
                pixels[y * resX + x] = corrected.toRGB();
            }
        }

        // DISPLAY THE IMAGE
        mis.newPixels();
    }

    //
    public static Color traceRay(Ray ray,
                                 List<Quadric> quadrics,
                                 List<CSGUnion> unions,
                                 List<CSGIntersection> intersections,
                                 List<CSGDifference> differences,
                                 Light light) {

        double closest_distance = -1;
        Quadric hit_quadric = null;
        boolean found_closer_hit = false;

        // Step 1: Check regular Quadrics
        for (Quadric quadric : quadrics) {
            double t = quadric.intersect(ray);
            if (t > 0 && (!found_closer_hit || t < closest_distance)) {
                found_closer_hit = true;
                closest_distance = t;
                hit_quadric = quadric;
            }
        }

        // Step 2: Check CSG Unions
        for (CSGUnion union : unions) {
            IntersectionResult result = union.intersect(ray);
            if (result != null && (!found_closer_hit || result.t < closest_distance)) {
                found_closer_hit = true;
                closest_distance = result.t;
                hit_quadric = result.hitObject;
            }
        }

        // Step 3: Check CSG Intersections
        for (CSGIntersection inter : intersections) {
            IntersectionResult result = inter.intersect(ray);
            if (result != null && (!found_closer_hit || result.t < closest_distance)) {
                found_closer_hit = true;
                closest_distance = result.t;
                hit_quadric = result.hitObject;
            }
        }

        // Step 4: Check CSG Differences
        for (CSGDifference diff : differences) {
            IntersectionResult result = diff.intersect(ray);
            if (result != null && (!found_closer_hit || result.t < closest_distance)) {
                found_closer_hit = true;
                closest_distance = result.t;
                hit_quadric = result.hitObject;
            }
        }

        // Step 5: Compute lighting if any object was hit
        if (found_closer_hit) {
            Vector3 hit_point = ray.getPoint(closest_distance);
            Vector3 normal = hit_quadric.getNormal(hit_point);

            Vector3 light_direction = light.position.subtract(hit_point).normalize();
            double diffuse_factor = Math.max(0, normal.dot(light_direction));

            Vector3 viewDir = ray.origin.subtract(hit_point).normalize();
            return Lighting.cookTorrance(
                    normal, viewDir, light_direction,
                    light.color, light.intensity,
                    hit_quadric.material);
        }

        // Step 6: No hit found, return background color
        return new Color(0.1, 0.1, 0.1);
    }
}
