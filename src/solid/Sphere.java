package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;
import transforms.Vec3D;

public class Sphere extends Solid {
    private static final int M = 50;
    private static final int N = 30;

    public Sphere(Vec3D center, double r) {
        int stride = M + 1;
        for (int j = 0; j <= N; j++) {
            double elevation = -Math.PI / 2 + Math.PI * j / N;
            double cosE = Math.cos(elevation);
            double sinE = Math.sin(elevation);

            for (int i = 0; i <= M; i++) {
                double azimuth = 2 * Math.PI * i / M;
                double nx = Math.cos(azimuth) * cosE;
                double ny = Math.sin(azimuth) * cosE;
                double nz = sinE;

                double x = center.getX() + r * nx;
                double y = center.getY() + r * ny;
                double z = center.getZ() + r * nz;

                Col color = new Col((nx + 1) / 2, (ny + 1) / 2, (nz + 1) / 2);
                double u = (double) i / M;
                double v = (double) j / N;
                vertexBuffer.add(new Vertex(new Point3D(x, y, z), color, new Vec2D(u, v), new Vec3D(nx, ny, nz)));
            }
        }

        for (int j = 0; j < N; j++) {
            for (int i = 0; i < M; i++) {
                int i0 = j * stride + i;
                int i1 = j * stride + i + 1;
                int i2 = (j + 1) * stride + i;
                int i3 = (j + 1) * stride + i + 1;

                addIndices(i0, i1, i3);
                addIndices(i0, i3, i2);
            }
        }

        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, M * N * 2));
    }
}
