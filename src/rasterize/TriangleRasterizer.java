package rasterize;

import model.Vertex;
import raster.ZBuffer;
import transforms.Col;
import util.Lerp;

public class TriangleRasterizer {
    private final ZBuffer zBuffer;

    public TriangleRasterizer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }

    public void rasterize(Vertex a, Vertex b, Vertex c) {
        Vertex v1 = a, v2 = b, v3 = c;
        if (v1.getY() > v2.getY()) { Vertex t = v1; v1 = v2; v2 = t; }
        if (v2.getY() > v3.getY()) { Vertex t = v2; v2 = v3; v3 = t; }
        if (v1.getY() > v2.getY()) { Vertex t = v1; v1 = v2; v2 = t; }

        Lerp<Vertex> lerp = new Lerp<>();

        // 1. část
        for (int y = (int) v1.getY(); y <= (int) v2.getY(); y++) {
            // Hrana V12
            double tAB = (y - v1.getY()) / (v2.getY() - v1.getY());
            Vertex v12 = lerp.lerp(v1, v2, tAB);

            // Hrana V13
            double tAC = (y - v1.getY()) / (v3.getY() - v1.getY());
            Vertex v13 = lerp.lerp(v1, v3, tAC);

            Vertex x1 = v12, x2 = v13;
            if (x1.getX() > x2.getX()) { Vertex t = x1; x1 = x2; x2 = t; }

            for (int x = (int) Math.round(x1.getX()); x <= (int) Math.round(x2.getX()); x++) {
                double t = (x - x1.getX()) / (x2.getX() - x1.getX());
                Vertex pixel = lerp.lerp(x1, x2, t);

                zBuffer.setPixelWithZTest(x, y, pixel.getZ(), pixel.getColor());
            }
        }

        // 2. část
        for (int y = (int) v2.getY(); y <= (int) v3.getY(); y++) {
            // Hrana V13 (dlouhá, aktivní v obou polovinách)
            double tAC = (y - v1.getY()) / (v3.getY() - v1.getY());
            Vertex v13 = lerp.lerp(v1, v3, tAC);

            // Hrana V23 (krátká, spodní polovina)
            double tBC = (y - v2.getY()) / (v3.getY() - v2.getY());
            Vertex v23 = lerp.lerp(v2, v3, tBC);

            Vertex x1 = v13, x2 = v23;
            if (x1.getX() > x2.getX()) { Vertex t = x1; x1 = x2; x2 = t; }

            for (int x = (int) Math.round(x1.getX()); x <= (int) Math.round(x2.getX()); x++) {
                double t = (x - x1.getX()) / (x2.getX() - x1.getX());
                Vertex pixel = lerp.lerp(x1, x2, t);

                zBuffer.setPixelWithZTest(x, y, pixel.getZ(), pixel.getColor());
            }
        }
    }
}
