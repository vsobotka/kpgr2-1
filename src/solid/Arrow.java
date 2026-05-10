package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Col;
import transforms.Point3D;

public class Arrow extends Solid {
    private final Col color;
    private final String label;

    public Arrow(Col color, String label) {
        this.color = color;
        this.label = label;

        vertexBuffer.add(new Vertex(0,   0,   0, color)); // v0
        vertexBuffer.add(new Vertex( 1,   0,   0, color)); // v1
        vertexBuffer.add(new Vertex( 1,  -0.3, 0, color)); // v2
        vertexBuffer.add(new Vertex( 1.4, 0,   0, color)); // v3
        vertexBuffer.add(new Vertex( 1,   0.3, 0, color)); // v4

        addIndices(0, 1); // lines
        addIndices(4, 3, 2); // triangles

        partBuffer.add(new Part(TopologyType.LINES, 0, 1));
        partBuffer.add(new Part(TopologyType.TRIANGLES, 2, 1));
    }

    public Col getColor() {
        return color;
    }
    public String getLabel() {
        return label;
    }
    public Point3D getTipPosition() {
        return new Point3D(1.4, 0, 0);
    }
}