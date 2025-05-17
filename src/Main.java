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

        // setting the camera (poition, view direction, image plane width, image plane height)
        Camera camera = new Camera(
                new Vector3(0, 0, 0),
                new Vector3(0, 0, -1),
                4,
                4
        );

        // creating spheres object in the scene

        Matrix4 quadric_ellipsoid = new Matrix4(
                1,   0, 0, 0,
                0, 0.25, 0, 0,    // b = 1 / (2²) = 0.25
                0,   0, 1, 0,
                0,   0, 0, -1
        );



        List<Quadric> quadrics = new ArrayList<>();
        //spheres.add(new Sphere(new Vector3(1, 0, -2), 1.0, new Color(1, 0, 0)));
        //spheres.add(new Sphere(new Vector3(0, -1, -3), 2.0, new Color(0, 1, 1)));
        //spheres.add(new Sphere(new Vector3(0, 0, -1), 0.5, new Color(1, 1, 0)));

        Quadric ellipsoid = new Quadric(quadric_ellipsoid, new Color(1, 1, 0));

        Matrix4 scaling = Matrix4.scaling(1, 2, 1);                          // Stretch along Y-axis
        Matrix4 rotation = Matrix4.rotationZ(Math.toRadians(45));            // Rotate 45° around Z-axis
        Matrix4 translation = Matrix4.translation(0, 0, -5);                 // Move back along Z-axis

        Matrix4 transform = translation.multiply(rotation).multiply(scaling);
        ellipsoid.applyTransformation(transform);

        // 4. Add to Scene
        quadrics.add(ellipsoid);





        // create light (position, color object)
        Light light = new Light(new Vector3(3, 3, 0), 2.0, new Color(1.0, 1.0, 1.0));

        // render
        for (int y = 0; y < resY; y++) {
            for (int x = 0; x < resX; x++) {
                Ray ray = camera.generateRay(x, y, resX, resY);
                Color color = traceRay(ray, quadrics, light);
                pixels[y * resX + x] = color.toRGB();
            }
        }

        // diplay the image
        mis.newPixels();
    }

    //
    public static Color traceRay(Ray ray, List<Quadric> quadrics, Light light) {
        double closest_distance = 0;
        Quadric hit_quadric = null;
        boolean found_closer_hit = false;

        // find closest sphere hit
        for (int i = 0; i < quadrics.size(); i++) {
            Quadric current_quadric = quadrics.get(i);
            double intersection_distance = current_quadric.intersect(ray); // find the intersection ditance s

            if (intersection_distance > 0) {
                if (!found_closer_hit || intersection_distance < closest_distance) {
                    found_closer_hit = true;
                    closest_distance = intersection_distance;
                    hit_quadric = current_quadric;
                }
            }
        }

        if (found_closer_hit) {
            Vector3 hit_point = ray.getPoint(closest_distance); // extract the hit point by using hit_point = p+s*v
            Vector3 normal =  hit_quadric.getNormal(hit_point);// (s-c)/r normal in der objekt class machen

            Vector3 light_direction = light.position.subtract(hit_point).normalize(); // From hit point to light: l = (L - p)
            double diffuse_factor = Math.max(0, normal.dot(light_direction)); //n·(-r) = |n| |r| cos(θ) (a.b = cos(θ)) (we dont negative it because the vector r points from point to light)

            Color diffuse = hit_quadric.color
                    .multiply(light.color)
                    .multiply(diffuse_factor * light.intensity); // color * n·(-r) * brightness

            return diffuse;
        }

        // Background color (e.g., dark gray)
        return new Color(0.1, 0.1, 0.1);
    }
}
