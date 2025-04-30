public class Color {
    public double r, g, b;

    public Color(double r, double g, double b) {
        this.r = r; this.g = g; this.b = b;
    }

    public Color multiply(double scalar) {
        return new Color(r * scalar, g * scalar, b * scalar);
    }

    public Color multiply(Color other) {
        return new Color(r * other.r, g * other.g, b * other.b);
    }

    public int toRGB() {
        int ir = (int)(Math.min(1.0, r) * 255);
        int ig = (int)(Math.min(1.0, g) * 255);
        int ib = (int)(Math.min(1.0, b) * 255);
        return (ir << 16) | (ig << 8) | ib;
    }
}
