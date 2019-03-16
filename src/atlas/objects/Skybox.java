package atlas.objects;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL13;
import org.lwjglx.debug.opengl.GL11;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Skybox {

	private SkyboxTexture skyboxTexture;
	private SkyboxTexture secondSkyboxTexture = null;
	private float overlayAlpha = 0;
	
	private float rotation = 0;
	private float radius = 1150;
	
	public Skybox(SkyboxTexture texture) throws Exception {
		this.skyboxTexture = texture;
	}
	
	public SkyboxTexture getTexture() {
		return skyboxTexture;
	}

	public float getRotation() {return this.rotation;}
	public void setRotation(float rot) {this.rotation = rot;}

	public float getRadius() {return this.radius;}
	public void setRadius(float radius) {this.radius = radius;}

	public void setSkyboxTexture(SkyboxTexture st) {this.skyboxTexture = st;}
	public SkyboxTexture getSkyboxTexture() {return this.skyboxTexture;}

	public SkyboxTexture getSkyboxOverlay() {return this.secondSkyboxTexture;}
	public float getSkyboxOverlayAlpha() {return this.overlayAlpha;}
	public void setSkyboxOverlayAlpha(float a) {this.overlayAlpha = a;}
	public void clearOverlay() {this.secondSkyboxTexture = null;this.overlayAlpha = 0;}
	public void setSkyboxOverlay(SkyboxTexture overlay, float alpha) {
		this.secondSkyboxTexture = overlay;
		this.overlayAlpha = alpha;
		if (this.overlayAlpha < 0) {
			overlayAlpha = 0;
		}
		if (this.overlayAlpha > 1) {
			overlayAlpha = 1;
		}
	}
	
	
	public static class SkyboxTexture {		
//		private ByteBuffer[] buffer = new ByteBuffer[6];
		private final int skyboxID; 

		public SkyboxTexture(InputStream[] iStreams) throws IOException {
			ByteBuffer[] buffer = new ByteBuffer[6];
			
			skyboxID = glGenTextures();
    		GL13.glActiveTexture(GL13.GL_TEXTURE0);
    		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, skyboxID);
    		
    		for (int i = 0; i < 6; i++) {
                PNGDecoder decoder = new PNGDecoder(iStreams[i]);
                // Load texture contents into a byte buffer
                buffer[i] = ByteBuffer.allocateDirect(
                        4 * decoder.getWidth() * decoder.getHeight());
                decoder.decode(buffer[i], decoder.getWidth() * 4, Format.RGBA);
                buffer[i].flip();
                
    			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL13.GL_RGBA, decoder.getWidth(), decoder.getHeight(),
    					0, GL13.GL_RGBA, GL13.GL_UNSIGNED_BYTE, buffer[i]);
    		}

    		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_MAG_FILTER, GL13.GL_LINEAR);
    		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_MIN_FILTER, GL13.GL_LINEAR);

    		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_EDGE);
    		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_EDGE);
		}
		
		public void cleanUp() {
	        glDeleteTextures(skyboxID);
		}

		public int getId() {
			return skyboxID;
		}
	}
}
