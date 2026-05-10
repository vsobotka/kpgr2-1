package shader;

import model.Vertex;
import raster.Texture;
import transforms.Col;

public class ShaderTexture implements Shader {
    private final Texture texture;

    public ShaderTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public Col getColor(Vertex pixel) {
        return texture.sample(pixel.getUV().getX(), pixel.getUV().getY());
    }
}