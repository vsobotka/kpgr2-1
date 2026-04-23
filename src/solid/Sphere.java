package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Col;
import transforms.Vec3D;

public class Sphere extends Solid {
    private static final int M = 20;
    private static final int N = 10;

    public Sphere(Vec3D center, double r) {
        for (int j = 0; j <= N; j++) {
            double elevation = -Math.PI / 2 + Math.PI * j / N;
            double cosE = Math.cos(elevation);
            double sinE = Math.sin(elevation);

            for (int i = 0; i < M; i++) {
                double azimuth = 2 * Math.PI * i / M;
                double nx = Math.cos(azimuth) * cosE;
                double ny = Math.sin(azimuth) * cosE;
                double nz = sinE;

                double x = center.getX() + r * nx;
                double y = center.getY() + r * ny;
                double z = center.getZ() + r * nz;

                Col color = new Col((nx + 1) / 2, (ny + 1) / 2, (nz + 1) / 2);
                vertexBuffer.add(new Vertex(x, y, z, color));
            }
        }

        for (int j = 0; j < N; j++) {
            for (int i = 0; i < M; i++) {
                int i0 = j * M + i;
                int i1 = j * M + (i + 1) % M;
                int i2 = (j + 1) * M + i;
                int i3 = (j + 1) * M + (i + 1) % M;

                addIndices(i0, i1, i3);
                addIndices(i0, i3, i2);
            }
        }

        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, M * N * 2));
    }
}
