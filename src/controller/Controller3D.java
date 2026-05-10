package controller;

import config.Config;
import raster.Texture;
import raster.ZBuffer;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import rasterize.TriangleRasterizer;
import renderer.RendererSolid;
import solid.Arrow;
import solid.Cone;
import solid.Cube;
import solid.Solid;
import solid.Sphere;
import transforms.*;
import view.Panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;


public class Controller3D {
    private final Panel panel;
    private final ZBuffer zBuffer;
    private final LineRasterizer lineRasterizer;
    private final TriangleRasterizer triangleRasterizer;
    private final RendererSolid renderer;
    private Projection projection = Projection.PERSPECTIVE;

    private final Arrow arrowX, arrowY, arrowZ;
    private final Solid sphere, cube, cone;
    private final Mat4 perspProj, orthoProj;
    private final Mat4 hudProj;
    private Camera camera;

    private ArrayList<Solid> selectableSolids = new ArrayList<Solid>();
    private Integer selectedSolidIndex = 0;

    private TransformOp transformOp = TransformOp.TRANSLATE;
    private TransformAxis transformAxis = TransformAxis.X;

    public Controller3D(Panel panel) {
        this.panel = panel;
        this.zBuffer = new ZBuffer(panel.getRaster());
        this.lineRasterizer = new LineRasterizerGraphics(panel.getRaster());
        this.triangleRasterizer = new TriangleRasterizer(zBuffer);

        double fov = Math.toRadians(Config.FOV_DEGREES);

        perspProj = new Mat4PerspRH(
                fov,
                panel.getRaster().getHeight() / (double) panel.getRaster().getWidth(),
                Config.NEAR_CLIP,
                Config.FAR_CLIP
        );

        double h = 6 * Math.tan(fov / 2);
        double w = h * panel.getRaster().getWidth() / (double) panel.getRaster().getHeight();

        orthoProj = new Mat4OrthoRH(
                w, h, Config.NEAR_CLIP, Config.FAR_CLIP
        );
        hudProj = new Mat4OrthoRH(18, 18, -10, 10)
                .mul(new Mat4Transl(-0.8, 0, 0));
        this.camera = createCamera();
        Mat4 view = this.camera.getViewMatrix();
        Mat4 proj = projection == Projection.PERSPECTIVE ? perspProj : orthoProj;
        this.renderer = new RendererSolid(
                lineRasterizer, triangleRasterizer, view, proj,
                panel.getRaster().getWidth(), panel.getRaster().getHeight()
        );

        this.arrowX = new Arrow(new Col(0xff4040), "X");
        this.arrowY = new Arrow(new Col(0x40ff40), "Y");
        this.arrowY.setModel(new Mat4RotZ(Math.toRadians(90)));
        this.arrowZ = new Arrow(new Col(0x4080ff), "Z");
        this.arrowZ.setModel(new Mat4RotY(Math.toRadians(-90)));

        this.sphere = new Sphere(new Vec3D(0, 0, 0), 1);
        try {
            sphere.setTexture(new Texture("res/textures/jupiter-rect.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.cube = new Cube(new Vec3D(0.5, 0, 0.5), 0.5);
        this.cone = new Cone(new Vec3D(-0.5, 0.3, -1), 0.5, 3);
        this.cone.setModel(new Mat4RotX(Math.toRadians(45)));

        selectableSolids.add(sphere);
        selectableSolids.add(cube);
        selectableSolids.add(cone);

        initListeners();

        drawScene();
    }

    private void initListeners() {
        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                boolean redraw = false;

                if (e.getKeyCode() == KeyEvent.VK_W) {
                    camera = camera.forward(Config.CAMERA_SPEED);
                    redraw = true;
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    camera = camera.backward(Config.CAMERA_SPEED);
                    redraw = true;
                } else if (e.getKeyCode() == KeyEvent.VK_A) {
                    camera = camera.left(Config.CAMERA_SPEED);
                    redraw = true;
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    camera = camera.right(Config.CAMERA_SPEED);
                    redraw = true;
                } else if (e.getKeyCode() == KeyEvent.VK_M) {
                    renderer.toggleRenderMode();
                    redraw = true;
                } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    selectedSolidIndex = (selectedSolidIndex + 1) % selectableSolids.size();
                    redraw = true;
                } else if (e.getKeyCode() == KeyEvent.VK_P) {
                    projection = projection == Projection.PERSPECTIVE ? Projection.ORTHOGRAPHIC : Projection.PERSPECTIVE;
                    redraw = true;
                } else if (e.getKeyCode() == KeyEvent.VK_T) {
                    TransformOp[] ops = TransformOp.values();
                    transformOp = ops[(transformOp.ordinal() + 1) % ops.length];
                    redraw = true;
                } else if (e.getKeyCode() == KeyEvent.VK_X) {
                    TransformAxis[] axes = TransformAxis.values();
                    transformAxis = axes[(transformAxis.ordinal() + 1) % axes.length];
                    redraw = true;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    applyStep(-1);
                    redraw = true;
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    applyStep(+1);
                    redraw = true;
                }

                if (redraw) {
                    renderer.setView(camera.getViewMatrix());
                    drawScene();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        MouseAdapter mouseAdapter = new MouseAdapter() {
            private int lastX, lastY;

            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;
                lastX = e.getX();
                lastY = e.getY();

                camera = camera
                        .addAzimuth(-dx * Config.MOUSE_SENSITIVITY)
                        .addZenith(-dy * Config.MOUSE_SENSITIVITY);
                renderer.setView(camera.getViewMatrix());
                drawScene();
            }
        };
        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);
    }

    private void applyStep(int sign) {
        Solid s = selectableSolids.get(selectedSolidIndex);
        s.setModel(s.getModel().mul(buildDelta(sign)));
    }

    private Mat4 buildDelta(int sign) {
        switch (transformOp) {
            case TRANSLATE: {
                double t = sign * Config.MODEL_TRANSLATE_STEP;
                switch (transformAxis) {
                    case X: return new Mat4Transl(t, 0, 0);
                    case Y: return new Mat4Transl(0, t, 0);
                    case Z: return new Mat4Transl(0, 0, t);
                }
            }
            case ROTATE: {
                double r = sign * Config.MODEL_ROTATE_STEP;
                switch (transformAxis) {
                    case X: return new Mat4RotX(r);
                    case Y: return new Mat4RotY(r);
                    case Z: return new Mat4RotZ(r);
                }
            }
            case SCALE: {
                double f = sign > 0 ? Config.SCALE_UP_FACTOR : Config.SCALE_DOWN_FACTOR;
                return new Mat4Scale(f, f, f);
            }
        }
        return new Mat4Identity();
    }

    private void drawScene() {
        zBuffer.clear();

        renderer.setProj(Projection.PERSPECTIVE == projection ? perspProj : orthoProj);

        for (Solid solid : selectableSolids) {
            renderer.render(solid);
        }

        Mat4 gView = hudView();
        renderArrowAxis(arrowX, gView);
        renderArrowAxis(arrowY, gView);
        renderArrowAxis(arrowZ, gView);

        drawHud();

        panel.repaint();
    }

    private void renderArrowAxis(Arrow arrow, Mat4 gView) {
        renderer.renderHud(arrow, gView, hudProj);
        Graphics2D g = (Graphics2D) panel.getRaster().getImage().getGraphics();
        boolean selected = arrow == arrowForAxis(transformAxis);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, selected ? 18 : 14));
        drawLabel(g, arrow, gView, selected);
    }

    private void drawLabel(Graphics2D g, Arrow arrow, Mat4 gView, boolean selected) {
        Point3D tip = arrow.getTipPosition().mul(arrow.getModel()).mul(gView).mul(hudProj);
        if (tip.getW() == 0) return;
        int sx = (int) ((tip.getX() / tip.getW() + 1) * 0.5 * panel.getRaster().getWidth());
        int sy = (int) ((1 - tip.getY() / tip.getW()) * 0.5 * panel.getRaster().getHeight());

        g.setColor(new Color(arrow.getColor().getRGB()));
        g.drawString(selected ? "[" + arrow.getLabel() + "]" : arrow.getLabel(), sx, sy);
    }

    private Arrow arrowForAxis(TransformAxis axis) {
        switch (axis) {
            case X: return arrowX;
            case Y: return arrowY;
            case Z: return arrowZ;
        }
        return arrowX;
    }

    private void drawHud() {
        Graphics2D g = (Graphics2D) panel.getRaster().getImage().getGraphics();
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g.setColor(Color.WHITE);

        g.drawString("[WASD + mouse] Camera movement", 8, 20);

        g.drawString("[P] Projection: " + projection.name(), 8, 60);
        g.drawString("[M] Render mode: " + renderer.getRenderMode().name(), 8, 80);

        g.drawString("[TAB] Selected solid: " + selectableSolids.get(selectedSolidIndex).getClass().getSimpleName(), 8, 120);

        g.drawString("[T] Transformation operation: " + transformOp.name(), 8, 160);
        g.drawString("[X] Transformation axis: " + transformAxis.name(), 8, 180);
        g.drawString("[UP] Transform +", 8, 200);
        g.drawString("[DOWN] Transform -", 8, 220);
    }

    private Mat4 hudView() {
        return new Camera()
                .withAzimuth(camera.getAzimuth())
                .withZenith(camera.getZenith())
                .withFirstPerson(true)
                .getViewMatrix();
    }

    private Camera createCamera() {
        return new Camera()
                .withPosition(new Vec3D(Config.CAMERA_INIT_X, Config.CAMERA_INIT_Y, Config.CAMERA_INIT_Z))
                .withAzimuth(Math.toRadians(Config.CAMERA_INIT_AZIMUTH)) // - -> look right, + -> look left
                .withZenith(Math.toRadians(Config.CAMERA_INIT_ZENITH)) // - -> look down, + -> look up
                .withFirstPerson(true);
    }

    private enum TransformOp { TRANSLATE, ROTATE, SCALE }

    private enum TransformAxis { X, Y, Z }

    private enum Projection {
        ORTHOGRAPHIC,
        PERSPECTIVE
    }
}
