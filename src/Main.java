import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main {

    public static void main(String[] args) {
        // set Resolution
        int resX = 1024;
        int resY = 768;

        int[] pixels = new int[resX * resY];

        // connects the pixels array to an image object
        MemoryImageSource mis = new MemoryImageSource(resX, resY, new DirectColorModel(24, 0xff0000, 0xff00, 0xff), pixels, 0, resX);
        mis.setAnimated(true);
        Image image = Toolkit.getDefaultToolkit().createImage(mis);

        // creates simple GUI window to display the image
        JFrame frame = new JFrame("Mein H...");
        frame.add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);


        //logic starts here
        for (int y = 0; y < 200; ++y) {
            for (int x = 0; x < 200; ++x) {
                pixels[y * resX + x] = 0x37f341;
            }
        }
        mis.newPixels();
    }
}