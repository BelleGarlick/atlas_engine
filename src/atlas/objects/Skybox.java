package atlas.objects;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL13;
import org.lwjglx.debug.opengl.GL11;

import static org.lwjgl.opengl.GL11.glGenTextures;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Skybox {
	
	private int skyboxTexture;
	private float rotation = 0;
	private float radius = 1150;
	
	public Skybox(SkyboxTexture[] textures) throws Exception {
		this.skyboxTexture = createCubeMap(textures);
	}
	
	
	private int createCubeMap(SkyboxTexture[] textures) {
		int texId = glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texId);
		
		for (int i = 0; i < 6; i++) {
			SkyboxTexture t = textures[i];
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL13.GL_RGBA, t.getWidth(), t.getHeight(),
					0, GL13.GL_RGBA, GL13.GL_UNSIGNED_BYTE, t.getBuffer());
		}

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_MAG_FILTER, GL13.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_MIN_FILTER, GL13.GL_LINEAR);

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_EDGE);

		return texId;
	}

	
	public int getTexture() {
		return skyboxTexture;
	}
	
	public static class SkyboxTexture {		
		private ByteBuffer buffer;
		private int width;
		private int height;
		
		public SkyboxTexture(InputStream is) throws IOException {
			// Load Texture file
            PNGDecoder decoder = new PNGDecoder(is);

            this.width = decoder.getWidth();
            this.height = decoder.getHeight();

            // Load texture contents into a byte buffer
            buffer = ByteBuffer.allocateDirect(
                    4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buffer, decoder.getWidth() * 4, Format.RGBA);
            buffer.flip();
		}

		public ByteBuffer getBuffer() {
			return buffer;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}

	public float getRotation() {return this.rotation;}
	public void setRotation(float rot) {this.rotation = rot;}


	public float getRadius() {return this.radius;}
	public void setRadius(float radius) {this.radius = radius;}
}
