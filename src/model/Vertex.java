package model;

import transforms.Col;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Vec2D;

public class Vertex implements Vectorizable<Vertex> {
    private final Point3D position;
    private final Col color;
    private Vec2D uv = new Vec2D(0, 0);
    private double one = 1;
    // další atributy: normála

    public Vertex(Point3D position, Col color, Vec2D uv, double one) {
        this.position = position;
        this.color = color;
        this.uv = uv;
        this.one = one;
    }

    public Vertex(Point3D position, Col color, Vec2D uv) {
        this.position = position;
        this.color = color;
        this.uv = uv;
    }

    public Vertex(Point3D position, Col color) {
        this.position = position;
        this.color = color;
    }

    public Vertex(Point3D position) {
        this.position = position;
        this.color = new Col(0xffffff);
    }

    public Vertex(double x, double y, double z) {
        this.position = new Point3D(x, y, z);
        this.color = new Col(0xffffff);
    }

    public Vertex(double x, double y, double z, Col color) {
        this.position = new Point3D(x, y, z);
        this.color = color;
    }

    public Point3D getPosition() {
        return position;
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public double getZ() {
        return position.getZ();
    }

    public double getW() {
        return position.getW();
    }

    public Col getColor() {
        return color;
    }

    @Override
    public Vertex mul(double d) {
        return new Vertex(position.mul(d), color.mul(d), uv.mul(d), one * d);
    }

    @Override
    public Vertex add(Vertex v) {
        return new Vertex(position.add(v.getPosition()), color.add(v.getColor()), uv.add(v.getUV()), one + v.getOne());
    }

    public Vertex transform(Mat4 matrix) {
        return new Vertex(position.mul(matrix), color, uv, one);
    }

    public Vertex dehomog() {
        return new Vertex(position.mul(1 / position.getW()), color, uv, one);
    }

    public double getOne() {
        return one;
    }

    public Vec2D getUV() {
        return uv;
    }

    public void setUv(Vec2D uv) {
        this.uv = uv;
    }
}
