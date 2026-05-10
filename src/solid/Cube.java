package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Col;
import transforms.Vec3D;

public class Cube extends Solid {
    public Cube(Vec3D start, double size) {
        super();
        Col col = new Col(53, 126, 199);

        vertexBuffer.add(new Vertex(start.getX(), start.getY(), start.getZ(), col)); // 0 Bottom-Front-Left
        vertexBuffer.add(new Vertex(start.getX() + size, start.getY(), start.getZ(), col)); // 1 Bottom-Front-Right
        vertexBuffer.add(new Vertex(start.getX() + size, start.getY() + size, start.getZ(), col)); // 2 Bottom-Back-Right
        vertexBuffer.add(new Vertex(start.getX(), start.getY() + size, start.getZ(), col)); // 3 Bottom-Back-Left

        vertexBuffer.add(new Vertex(start.getX(), start.getY(), start.getZ() + size, col)); // 4 Top-Front-Left
        vertexBuffer.add(new Vertex(start.getX() + size, start.getY(), start.getZ() + size, col)); // 5 Top-Front-Right
        vertexBuffer.add(new Vertex(start.getX() + size, start.getY() + size, start.getZ() + size, col)); // 6 Top-Back-Right
        vertexBuffer.add(new Vertex(start.getX(), start.getY() + size, start.getZ() + size, col)); // 7 Top-Back-Left

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
