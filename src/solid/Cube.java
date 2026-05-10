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
                new Point3D(x + size, y + size, z), new Point3D(x + size, y, z), col);
        // Top (+Z)
        addFace(new Point3D(x, y, z + size), new Point3D(x + size, y, z + size),
                new Point3D(x + size, y + size, z + size), new Point3D(x, y + size, z + size), col);
        // Front (-Y)
        addFace(new Point3D(x, y, z), new Point3D(x + size, y, z),
                new Point3D(x + size, y, z + size), new Point3D(x, y, z + size), col);
        // Back (+Y)
        addFace(new Point3D(x + size, y + size, z), new Point3D(x, y + size, z),
                new Point3D(x, y + size, z + size), new Point3D(x + size, y + size, z + size), col);
        // Left (-X)
        addFace(new Point3D(x, y + size, z), new Point3D(x, y, z),
                new Point3D(x, y, z + size), new Point3D(x, y + size, z + size), col);
        // Right (+X)
        addFace(new Point3D(x + size, y, z), new Point3D(x + size, y + size, z),
                new Point3D(x + size, y + size, z + size), new Point3D(x + size, y, z + size), col);

        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, 12));
    }

    private void addFace(Point3D a, Point3D b, Point3D c, Point3D d, Col col) {
        double uvMin = 0.5 / 15.0;
        double uvMax = 4.5 / 15.0;

        int base = vertexBuffer.size();
        vertexBuffer.add(new Vertex(a, col, new Vec2D(uvMin, uvMin)));
        vertexBuffer.add(new Vertex(b, col, new Vec2D(uvMax, uvMin)));
        vertexBuffer.add(new Vertex(c, col, new Vec2D(uvMax, uvMax)));
        vertexBuffer.add(new Vertex(d, col, new Vec2D(uvMin, uvMax)));
        addIndices(base, base + 1, base + 2);
        addIndices(base, base + 2, base + 3);
    }
}