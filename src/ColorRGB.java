public class ColorRGB {
    public double r, g, b;

    public ColorRGB(double r, double g, double b) {
        this.r = r; this.g = g; this.b = b;
    }

    public ColorRGB multiply(double scalar) {
        return new ColorRGB(r * scalar, g * scalar, b * scalar);
    }

    public ColorRGB multiply(ColorRGB other) {
        return new ColorRGB(r * other.r, g * other.g, b * other.b);
    }

    public int toRGB() {
        int ir = (int)(Math.min(1.0, r) * 255);
        int ig = (int)(Math.min(1.0, g) * 255);
        int ib = (int)(Math.min(1.0, b) * 255);
        return (ir << 16) | (ig << 8) | ib;
    }
}
