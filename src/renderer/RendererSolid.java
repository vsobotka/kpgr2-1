package renderer;

import model.Part;
import model.Vertex;
import rasterize.LineRasterizer;
import rasterize.TriangleRasterizer;
import solid.RenderMode;
import solid.Solid;
import transforms.Mat4;
import transforms.Point3D;
import util.Lerp;

import java.util.ArrayList;
import java.util.List;

public class RendererSolid {
    private final LineRasterizer lineRasterizer;
    private final TriangleRasterizer triangleRasterizer;
    private Mat4 view;
    private final Mat4 proj;
    private final int width, height;
    private final Lerp<Vertex> lerp = new Lerp<>();
    private RenderMode renderMode = RenderMode.FILL;

    public RendererSolid(LineRasterizer lineRasterizer, TriangleRasterizer triangleRasterizer,
                         Mat4 view, Mat4 proj, int width, int height) {
        this.lineRasterizer = lineRasterizer;
        this.triangleRasterizer = triangleRasterizer;
        this.view = view;
        this.proj = proj;
        this.width = width;
        this.height = height;
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public RenderMode getRenderMode() {
        return renderMode;
    }

    public void toggleRenderMode() {
        renderMode = renderMode == RenderMode.FILL ? RenderMode.WIRE : RenderMode.FILL;
    }

    public void render(Solid solid) {
        renderInternal(solid, solid.getModel().mul(view).mul(proj));
    }

    public void renderHud(Solid solid, Mat4 hudView, Mat4 hudProj) {
        renderInternal(solid, solid.getModel().mul(hudView).mul(hudProj));
    }

    private void renderInternal(Solid solid, Mat4 mvp) {
        for (Part part : solid.getPartBuffer()) {
            switch (part.getType()) {
                case LINES:
                    int index = part.getStartIndex();
                    for (int i = 0; i < part.getCount(); i++) {
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);

                        Vertex a = solid.getVertexBuffer().get(indexA).transform(mvp);
                        Vertex b = solid.getVertexBuffer().get(indexB).transform(mvp);

                        drawEdge(a, b);
                    }
                    break;
                case TRIANGLES:
                    index = part.getStartIndex();
                    for (int i = 0; i < part.getCount(); i++) {
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);
                        int indexC = solid.getIndexBuffer().get(index++);

                        Vertex a = solid.getVertexBuffer().get(indexA).transform(mvp);
                        Vertex b = solid.getVertexBuffer().get(indexB).transform(mvp);
                        Vertex c = solid.getVertexBuffer().get(indexC).transform(mvp);

                        if (renderMode == RenderMode.WIRE) {
                            drawEdge(a, b);
                            drawEdge(b, c);
                            drawEdge(c, a);
                        } else {
                            List<Vertex> clipped = clipTriangleNear(a, b, c);
                            if (clipped.size() < 3) continue;

                            List<Vertex> screen = new ArrayList<>(clipped.size());
                            for (Vertex v : clipped) screen.add(toScreen(v.dehomog()));

                            // fan-triangulate (clipped polygon has 3 or 4 vertices)
                            for (int k = 1; k < screen.size() - 1; k++) {
                                triangleRasterizer.rasterize(screen.get(0), screen.get(k), screen.get(k + 1));
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void drawEdge(Vertex a, Vertex b) {
        Vertex[] clipped = clipLineNear(a, b);
        if (clipped == null) return;

        Vertex sa = toScreen(clipped[0].dehomog());
        Vertex sb = toScreen(clipped[1].dehomog());

        lineRasterizer.setColor(sa.getColor());
        lineRasterizer.rasterize(
                (int) Math.round(sa.getX()),
                (int) Math.round(sa.getY()),
                (int) Math.round(sb.getX()),
                (int) Math.round(sb.getY())
        );
    }

    private Vertex[] clipLineNear(Vertex a, Vertex b) {
        boolean aIn = a.getZ() >= 0;
        boolean bIn = b.getZ() >= 0;
        if (aIn && bIn) return new Vertex[]{a, b};
        if (!aIn && !bIn) return null;
        if (aIn) {
            double t = a.getZ() / (a.getZ() - b.getZ());
            return new Vertex[]{a, lerp.lerp(a, b, t)};
        } else {
            double t = b.getZ() / (b.getZ() - a.getZ());
            return new Vertex[]{lerp.lerp(b, a, t), b};
        }
    }

    private List<Vertex> clipTriangleNear(Vertex a, Vertex b, Vertex c) {
        List<Vertex> input = List.of(a, b, c);
        List<Vertex> output = new ArrayList<>(4);
        int n = input.size();
        for (int i = 0; i < n; i++) {
            Vertex s = input.get(i);
            Vertex e = input.get((i + 1) % n);
            boolean sIn = s.getZ() >= 0;
            boolean eIn = e.getZ() >= 0;
            if (eIn) {
                if (!sIn) {
                    double t = s.getZ() / (s.getZ() - e.getZ());
                    output.add(lerp.lerp(s, e, t));
                }
                output.add(e);
            } else if (sIn) {
                double t = s.getZ() / (s.getZ() - e.getZ());
                output.add(lerp.lerp(s, e, t));
            }
        }
        return output;
    }

    private Vertex toScreen(Vertex v) {
        double x = (v.getX() + 1) * 0.5 * width;
        double y = (1 - v.getY()) * 0.5 * height;
        return new Vertex(new Point3D(x, y, v.getZ()), v.getColor());
    }
}
