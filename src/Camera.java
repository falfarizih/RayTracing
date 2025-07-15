public class Camera {
    public Vector3 position;
    public Vector3 forward, right, up;
    public double image_plane_width, image_plane_height;

    // Depth of Field parameters    
    public double apertureSize = 0.05;
    public double focusDistance = 5.0;

    public Camera(Vector3 position, Vector3 point_to_look_at, double image_width, double image_height) {

        this.position = position;                                       // c
        this.forward = point_to_look_at.subtract(position).normalize();  // v = a - c
        this.right = new Vector3(0, 1, 0).cross(forward).normalize(); // r = v x o
        this.up = forward.cross(right).normalize();    // u = r x v

        this.image_plane_width = image_width;
        this.image_plane_height = image_height;
    }

    public Ray generateRay(double x, double y, int res_x, int res_y) {

        //pixel step (image_plane_width / resolution)
        double u = (x + 0.5) / res_x - 0.5;
        double v = (y + 0.5) / res_y - 0.5;

        Vector3 point_on_plane = position
                .add(forward)                                       //forward
                .add(right.multiply(u * image_plane_width))     // right offset
                .add(up.multiply(v * image_plane_height));      // left offset


        Vector3 direction = point_on_plane.subtract(position).normalize();
        Vector3 focal_point = position.add(direction.multiply(focusDistance));

        Vector3 lens_offset = Vector3.randomPointOnDisk(apertureSize);
        Vector3 aperture_point = position.add(right.multiply(lens_offset.x)).add(up.multiply(lens_offset.y));

        Vector3 new_direction = focal_point.subtract(aperture_point).normalize();
        return new Ray(aperture_point, new_direction);
    }
}
