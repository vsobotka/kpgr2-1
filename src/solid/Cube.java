package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;
import transforms.Vec3D;

public class Cube extends Solid {
    public Cube(Vec3D start, double size) {
        super();
        Col col = new Col(53, 126, 199);

        double x = start.getX(), y = start.getY(), z = start.getZ();

        // Bottom (-Z)
        addFace(new Point3D(x, y, z), new Point3D(x, y + size, z),
                new Point3D(x + size, y + size, z), new Point3D(x + size, y, z),
                col, new Vec3D(0, 0, -1));
        // Top (+Z)
        addFace(new Point3D(x, y, z + size), new Point3D(x + size, y, z + size),
                new Point3D(x + size, y + size, z + size), new Point3D(x, y + size, z + size),
                col, new Vec3D(0, 0, 1));
        // Front (-Y)
        addFace(new Point3D(x, y, z), new Point3D(x + size, y, z),
                new Point3D(x + size, y, z + size), new Point3D(x, y, z + size),
                col, new Vec3D(0, -1, 0));
        // Back (+Y)
        addFace(new Point3D(x + size, y + size, z), new Point3D(x, y + size, z),
                new Point3D(x, y + size, z + size), new Point3D(x + size, y + size, z + size),
                col, new Vec3D(0, 1, 0));
        // Left (-X)
        addFace(new Point3D(x, y + size, z), new Point3D(x, y, z),
                new Point3D(x, y, z + size), new Point3D(x, y + size, z + size),
                col, new Vec3D(-1, 0, 0));
        // Right (+X)
        addFace(new Point3D(x + size, y, z), new Point3D(x + size, y + size, z),
                new Point3D(x + size, y + size, z + size), new Point3D(x + size, y, z + size),
                col, new Vec3D(1, 0, 0));
    }

    private void addFace(Point3D a, Point3D b, Point3D c, Point3D d, Col col, Vec3D normal) {
        double uvMin = 0.5 / 15.0;
        double uvMax = 4.5 / 15.0;

        int base = vertexBuffer.size();
        int strip = indexBuffer.size();
        vertexBuffer.add(new Vertex(a, col, new Vec2D(uvMin, uvMin), normal));
        vertexBuffer.add(new Vertex(b, col, new Vec2D(uvMax, uvMin), normal));
        vertexBuffer.add(new Vertex(c, col, new Vec2D(uvMax, uvMax), normal));
        vertexBuffer.add(new Vertex(d, col, new Vec2D(uvMin, uvMax), normal));
        addIndices(base, base + 1, base + 3, base + 2);
        partBuffer.add(new Part(TopologyType.TRIANGLE_STRIP, strip, 4));
    }
}
