package atlas.graphical;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL13;
import org.lwjglx.debug.opengl.GL11;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Texture {

	private int id;
	private int width;
	private int height;
	
	//How many rows & cols there are, size 4 -> 4 rows & 4 columns == 16 textures
	private int atlasSize = 1;

	public Texture(InputStream is) throws Exception {
        try {
            // Load Texture file
            PNGDecoder decoder = new PNGDecoder(is);

            this.width = decoder.getWidth();
            this.height = decoder.getHeight();

            // Load texture contents into a byte buffer
            ByteBuffer buf = ByteBuffer.allocateDirect(
                    4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
            buf.flip();

            // Create a new OpenGL texture 
            this.id = glGenTextures();
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, this.id);

            // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            // Upload the texture data
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
            // Generate Mip Map
            glGenerateMipmap(GL_TEXTURE_2D);

            is.close();
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

	public Texture(InputStream resourceAsStream, int i) throws Exception {
		this(resourceAsStream);
		this.atlasSize = i;
	}

	public int getId() {
		return this.id;
	}

	public int getAtlasSize() {
		return this.atlasSize;
	}

	public void setAtlasSize(int i) {
		this.atlasSize = i;
	}
	
	public void scaleLinear(){
        glBindTexture(GL_TEXTURE_2D, this.id);
		GL11.glTexParameteri(GL13.GL_TEXTURE_2D, GL13.GL_TEXTURE_MAG_FILTER, GL13.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_2D, GL13.GL_TEXTURE_MIN_FILTER, GL13.GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void scaleDiscrete(){
        glBindTexture(GL_TEXTURE_2D, this.id);
		GL11.glTexParameteri(GL13.GL_TEXTURE_2D, GL13.GL_TEXTURE_MAG_FILTER, GL13.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_2D, GL13.GL_TEXTURE_MIN_FILTER, GL13.GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
	}

    public void cleanUp() {
        glDeleteTextures(id);
    }
	
}
