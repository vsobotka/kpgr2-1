package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;
import transforms.Vec3D;

public class Cone extends Solid {
    private static final int M = 50;

    public Cone(Vec3D base, double radius, double height) {
        Col col = new Col(0.2, 0.3, 0.4);
        double bx = base.getX(), by = base.getY(), bz = base.getZ();
        double slant = Math.hypot(radius, height);
        double rise = height / slant;
        double radial = radius / slant;
        Vec3D baseNormal = new Vec3D(0, 0, -1);

        for (int i = 0; i < M; i++) {
            double a = 2 * Math.PI * (i + 0.5) / M;
            Vec3D n = new Vec3D(Math.cos(a) * rise, Math.sin(a) * rise, radial);
            vertexBuffer.add(new Vertex(
                    new Point3D(bx, by, bz + height), col, new Vec2D((i + 0.5) / M, 1), n));
        }

        for (int i = 0; i <= M; i++) {
            double a = 2 * Math.PI * i / M;
            Vec3D n = new Vec3D(Math.cos(a) * rise, Math.sin(a) * rise, radial);
            vertexBuffer.add(new Vertex(
                    new Point3D(bx + radius * Math.cos(a), by + radius * Math.sin(a), bz),
                    col, new Vec2D((double) i / M, 0), n));
        }

        vertexBuffer.add(new Vertex(new Point3D(bx, by, bz), col, new Vec2D(0.5, 0.5), baseNormal));

        for (int i = 0; i < M; i++) {
            double a = 2 * Math.PI * i / M;
            double cosA = Math.cos(a), sinA = Math.sin(a);
            vertexBuffer.add(new Vertex(
                    new Point3D(bx + radius * cosA, by + radius * sinA, bz),
                    col, new Vec2D((cosA + 1) / 2, (sinA + 1) / 2), baseNormal));
        }

        int sideRing = M;
        int baseCenter = sideRing + M + 1;
        int baseRing = baseCenter + 1;

        for (int i = 0; i < M; i++) {
            addIndices(i, sideRing + i, sideRing + i + 1);
            addIndices(baseCenter, baseRing + (i + 1) % M, baseRing + i);
        }

        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, 2 * M));
    }
}