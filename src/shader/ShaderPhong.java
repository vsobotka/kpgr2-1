package shader;

import model.Vertex;
import transforms.Col;
import transforms.Vec3D;

public class ShaderPhong implements Shader {
    private final Shader baseColor;
    private final Vec3D lightPos;
    private final Col lightColor;
    private final double ambient = 0.15;

    public ShaderPhong(Shader baseColor, Vec3D lightPos, Col lightColor) {
        this.baseColor = baseColor;
        this.lightPos = lightPos;
        this.lightColor = lightColor;
    }

    @Override
    public Col getColor(Vertex pixel) {
        Col albedo = baseColor.getColor(pixel);

        Vec3D n = pixel.getNormal().normalized().orElse(new Vec3D(0, 0, 1));
        Vec3D worldPos = new Vec3D(pixel.getWorldPos().getX(), pixel.getWorldPos().getY(), pixel.getWorldPos().getZ());
        Vec3D l = lightPos.sub(worldPos).normalized().orElse(new Vec3D(0, 0, 1));

        double diff = Math.max(0, n.dot(l));

        return new Col(
                Math.min(1, albedo.getR() * (ambient + lightColor.getR() * diff)),
                Math.min(1, albedo.getG() * (ambient + lightColor.getG() * diff)),
                Math.min(1, albedo.getB() * (ambient + lightColor.getB() * diff)));
    }
}