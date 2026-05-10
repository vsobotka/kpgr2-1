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

        // 1. část - ceil(v1.y) .. ceil(v2.y)-1, fill rule zabrání extrapolaci u tenkých trojúhelníků
        int y1Start = (int) Math.ceil(v1.getY());
        int y1End = (int) Math.ceil(v2.getY()) - 1;
        for (int y = y1Start; y <= y1End; y++) {
            double tAB = (y - v1.getY()) / (v2.getY() - v1.getY());
            Vertex v12 = lerp.lerp(v1, v2, tAB);

            double tAC = (y - v1.getY()) / (v3.getY() - v1.getY());
            Vertex v13 = lerp.lerp(v1, v3, tAC);

            Vertex x1 = v12, x2 = v13;
            if (x1.getX() > x2.getX()) { Vertex t = x1; x1 = x2; x2 = t; }

            drawScanline(y, x1, x2);
        }

        // 2. část - ceil(v2.y) .. ceil(v3.y)-1, žádné překrytí s 1. částí
        int y2Start = (int) Math.ceil(v2.getY());
        int y2End = (int) Math.ceil(v3.getY()) - 1;
        for (int y = y2Start; y <= y2End; y++) {
            double tAC = (y - v1.getY()) / (v3.getY() - v1.getY());
            Vertex v13 = lerp.lerp(v1, v3, tAC);

            double tBC = (y - v2.getY()) / (v3.getY() - v2.getY());
            Vertex v23 = lerp.lerp(v2, v3, tBC);

            Vertex x1 = v13, x2 = v23;
            if (x1.getX() > x2.getX()) { Vertex t = x1; x1 = x2; x2 = t; }

            drawScanline(y, x1, x2);
        }
    }

    private void drawScanline(int y, Vertex x1, Vertex x2) {
        int xStart = (int) Math.round(x1.getX());
        int xEnd = (int) Math.round(x2.getX());
        double dx = x2.getX() - x1.getX();
        Lerp<Vertex> lerp = new Lerp<>();
        for (int x = xStart; x <= xEnd; x++) {
            double t = dx == 0 ? 0 : (x - x1.getX()) / dx;
            Vertex pixel = lerp.lerp(x1, x2, t);
            Col color = pixel.getColor().mul(1.0 / pixel.getW());
            zBuffer.setPixelWithZTest(x, y, pixel.getZ(), color);
        }
    }
}
