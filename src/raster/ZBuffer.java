package raster;

import transforms.Col;

import java.util.Optional;

public class ZBuffer {
    private final Raster<Col> imageBuffer;
    private final Raster<Double> depthBuffer;

    public ZBuffer(Raster<Col> imageBuffer) {
        this.imageBuffer = imageBuffer;
        this.depthBuffer = new DepthBuffer(imageBuffer.getWidth(), imageBuffer.getHeight());
    }

    public void setPixelWithZTest(int x, int y, double z, Col color) {
        Optional<Double> oldZ = depthBuffer.getValue(x, y);
        if (oldZ.isEmpty() || z >= oldZ.get()) {
            return;
        }

        imageBuffer.setValue(x, y, color);
        depthBuffer.setValue(x, y, z);
    }

    public void clear() {
        imageBuffer.clear();
        depthBuffer.clear();
    }
}
