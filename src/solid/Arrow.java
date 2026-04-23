package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Col;

public class Arrow extends Solid {
    public Arrow() {
        vertexBuffer.add(new Vertex(-1,   0,   0)); // v0
        vertexBuffer.add(new Vertex( 1,   0,   0)); // v1
        vertexBuffer.add(new Vertex( 1,  -0.3, 0, new Col(0xff0000))); // v2
        vertexBuffer.add(new Vertex( 1.4, 0,   0)); // v3
        vertexBuffer.add(new Vertex( 1,   0.3, 0)); // v4

        addIndices(0, 1); // lines
        addIndices(4, 3, 2); // triangles

        partBuffer.add(new Part(TopologyType.LINES, 0, 1));
        partBuffer.add(new Part(TopologyType.TRIANGLES, 2, 1));
    }
}
