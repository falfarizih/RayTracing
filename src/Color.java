public class Color {
    public double r, g, b;

    public Color(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    // Add method: Adds two colors component-wise
    public Color add(Color other) {
        return new Color(this.r + other.r, this.g + other.g, this.b + other.b);
    }

    // Subtract method: Subtracts component-wise
    public Color subtract(Color other) {
        return new Color(this.r - other.r, this.g - other.g, this.b - other.b);
    }


    public Color multiply(double scalar) {
        return new Color(r * scalar, g * scalar, b * scalar);
    }

    public Color multiply(Color other) {
        return new Color(r * other.r, g * other.g, b * other.b);
    }

    // Clamp color values between 0 and 1
    public Color clamp() {
        return new Color(
                Math.min(1.0, Math.max(0.0, r)),
                Math.min(1.0, Math.max(0.0, g)),
                Math.min(1.0, Math.max(0.0, b))
        );
    }

    // Convenience method for white color (used for Color.one())
    public static Color one() {
        return new Color(1.0, 1.0, 1.0);
    }

    public Color applyGamma(double gamma) {
        double inverted_gamma = 1.0 / gamma;
        return new Color(
                Math.pow(this.r, inverted_gamma),
                Math.pow(this.g, inverted_gamma),
                Math.pow(this.b, inverted_gamma)
        );
    }


    public int toRGB() {
        int ir = (int)(Math.min(1.0, r) * 255);
        int ig = (int)(Math.min(1.0, g) * 255);
        int ib = (int)(Math.min(1.0, b) * 255);
        return (ir << 16) | (ig << 8) | ib;
    }


}
