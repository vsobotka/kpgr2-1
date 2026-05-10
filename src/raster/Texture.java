package raster;

import transforms.Col;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Texture {
    private final BufferedImage img;

    public Texture(String path) throws IOException {
        img = ImageIO.read(new File(path));
    }

    public Col sample(double u, double v) {
        int tx = Math.floorMod((int)(u * img.getWidth()),  img.getWidth());
        int ty = Math.floorMod((int)((1-v) * img.getHeight()), img.getHeight());
        return new Col(img.getRGB(tx, ty));
    }
}
