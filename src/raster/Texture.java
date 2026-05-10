package raster;

import transforms.Col;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Texture {
    private final int[] pixels;
    private final int width, height;

    public Texture(String path) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        width = img.getWidth();
        height = img.getHeight();
        pixels = new int[width * height];
        img.getRGB(0, 0, width, height, pixels, 0, width);
    }

    public Col sample(double u, double v) {
        int tx = Math.floorMod((int)(u * width),  width);
        int ty = Math.floorMod((int)((1-v) * height), height);
        return new Col(pixels[ty * width + tx]);
    }
}
