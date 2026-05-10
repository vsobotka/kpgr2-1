package model;

import transforms.Col;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Vec2D;
import transforms.Vec3D;

public class Vertex implements Vectorizable<Vertex> {
    private final Point3D position;
    private final Col color;
    private Vec2D uv = new Vec2D(0, 0);
    private Vec3D normal = new Vec3D(0, 0, 0);
    private double one = 1;

    public Vertex(Point3D position, Col color, Vec2D uv, Vec3D normal, double one) {
        this.position = position;
        this.color = color;
        this.uv = uv;
        this.normal = normal;
        this.one = one;
    }

    public Vertex(Point3D position, Col color, Vec2D uv, Vec3D normal) {
        this.position = position;
        this.color = color;
        this.uv = uv;
        this.normal = normal;
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
        return new Vertex(position.mul(d), color.mul(d), uv.mul(d), normal.mul(d), one * d);
    }

    @Override
    public Vertex add(Vertex v) {
        return new Vertex(position.add(v.getPosition()), color.add(v.getColor()), uv.add(v.getUV()), normal.add(v.getNormal()), one + v.getOne());
    }

    public Vertex transform(Mat4 matrix) {
        return new Vertex(position.mul(matrix), color, uv, normal, one);
    }

    public Vertex dehomog() {
        return new Vertex(position.mul(1 / position.getW()), color, uv, normal, one);
    }

    public double getOne() {
        return one;
    }

    public Vec2D getUV() {
        return uv;
    }

    public Vec3D getNormal() {
        return normal;
    }
}
