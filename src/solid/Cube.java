package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Vec3D;

public class Cube extends Solid {
    public Cube(Vec3D start, double size) {
        super();

        vertexBuffer.add(new Vertex(start.getX(), start.getY(), start.getZ())); // 0 Bottom-Front-Left
        vertexBuffer.add(new Vertex(start.getX() + size, start.getY(), start.getZ())); // 1 Bottom-Front-Right
        vertexBuffer.add(new Vertex(start.getX() + size, start.getY() + size, start.getZ())); // 2 Bottom-Back-Right
        vertexBuffer.add(new Vertex(start.getX(), start.getY() + size, start.getZ())); // 3 Bottom-Back-Left

        vertexBuffer.add(new Vertex(start.getX(), start.getY(), start.getZ() + size)); // 4 Top-Front-Left
        vertexBuffer.add(new Vertex(start.getX() + size, start.getY(), start.getZ() + size)); // 5 Top-Front-Right
        vertexBuffer.add(new Vertex(start.getX() + size, start.getY() + size, start.getZ() + size)); // 6 Top-Back-Right
        vertexBuffer.add(new Vertex(start.getX(), start.getY() + size, start.getZ() + size)); // 7 Top-Back-Left

        indexBuffer.add(0);
        indexBuffer.add(1);
        indexBuffer.add(3);
        indexBuffer.add(2);
        indexBuffer.add(6);
        indexBuffer.add(1);
        indexBuffer.add(5);
        indexBuffer.add(0);
        indexBuffer.add(4);
        indexBuffer.add(3);
        indexBuffer.add(7);
        indexBuffer.add(6);
        indexBuffer.add(4);
        indexBuffer.add(5);

        partBuffer.add(new Part(TopologyType.TRIANGLE_STRIP, 0, 14));
    }
}
