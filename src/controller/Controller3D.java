package controller;

import config.Config;
import raster.ZBuffer;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import rasterize.TriangleRasterizer;
import renderer.RendererSolid;
import solid.Arrow;
import solid.Solid;
import solid.Sphere;
import transforms.*;
import view.Panel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class Controller3D {
    private final Panel panel;
    private final ZBuffer zBuffer;
    private final LineRasterizer lineRasterizer;
    private final TriangleRasterizer triangleRasterizer;
    private final RendererSolid renderer;
    private final Projection projection = Projection.PERSPECTIVE;

    private final Solid arrow;
    private final Solid sphere;
    private final Mat4 perspProj, orthoProj;
    private Camera camera;

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
        this.camera = createCamera();
        Mat4 view = this.camera.getViewMatrix();
        Mat4 proj = projection == Projection.PERSPECTIVE ? perspProj : orthoProj;
        this.renderer = new RendererSolid(
                lineRasterizer, triangleRasterizer, view, proj,
                panel.getRaster().getWidth(), panel.getRaster().getHeight()
        );

        this.arrow = new Arrow();
        this.sphere = new Sphere(new Vec3D(0, 0, 0), 1);

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

    private void drawScene() {
        zBuffer.clear();

        renderer.render(arrow);
        renderer.render(sphere);

        panel.repaint();
    }

    private Camera createCamera() {
        return new Camera()
                .withPosition(new Vec3D(Config.CAMERA_INIT_X, Config.CAMERA_INIT_Y, Config.CAMERA_INIT_Z))
                .withAzimuth(Math.toRadians(Config.CAMERA_INIT_AZIMUTH)) // - -> look right, + -> look left
                .withZenith(Math.toRadians(Config.CAMERA_INIT_ZENITH)) // - -> look down, + -> look up
                .withFirstPerson(true);
    }

    private enum Projection {
        ORTHOGRAPHIC,
        PERSPECTIVE
    }
}
