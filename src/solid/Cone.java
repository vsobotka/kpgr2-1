package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Col;
import transforms.Vec3D;

public class Cone extends Solid {
    private static final int M = 20;

    public Cone(Vec3D base, double radius, double height) {
        Col baseCol = new Col(0.2, 0.4, 1.0);
        Col apexCol = new Col(1.0, 0.5, 0.1);

        vertexBuffer.add(new Vertex(
                base.getX(), base.getY(), base.getZ() + height, apexCol)); // 0 apex
        vertexBuffer.add(new Vertex(
                base.getX(), base.getY(), base.getZ(), baseCol)); // 1 base center

        for (int i = 0; i < M; i++) {
            double a = 2 * Math.PI * i / M;
            vertexBuffer.add(new Vertex(
                    base.getX() + radius * Math.cos(a),
                    base.getY() + radius * Math.sin(a),
                    base.getZ(),
                    baseCol));
        }

        for (int i = 0; i < M; i++) {
            int r0 = 2 + i;
            int r1 = 2 + (i + 1) % M;
            addIndices(0, r0, r1);
        }
        for (int i = 0; i < M; i++) {
            int r0 = 2 + i;
            int r1 = 2 + (i + 1) % M;
            addIndices(1, r1, r0);
        }

        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, M));
        partBuffer.add(new Part(TopologyType.TRIANGLES, M * 3, M));
    }
}