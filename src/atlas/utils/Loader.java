package atlas.utils;

import static org.lwjgl.BufferUtils.createByteBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;

import atlas.graphical.OBJFileLoader;
import atlas.graphical.Texture;
import atlas.objects.Skybox.SkyboxTexture;
import atlas.objects.entityComponents.Mesh;
import atlas.objects.entityComponents.animation.AnimatedModel;
import atlas.objects.entityComponents.animation.assimp.AnimMeshesLoader;

public class Loader {

	public static InputStream getStream(ClassLoader cl, String loc) { 
		if (Loader.class.getClassLoader().getResource(loc) == null) {
			System.err.println("Error loading: " + loc);
		}
		return cl.getResourceAsStream(loc);
	}

	public static InputStream getStream(String loc) { 
		ClassLoader cl = Loader.class.getClassLoader();
		if (cl.getResource(loc) == null) {
			System.err.println("Error loading: " + loc);
		}
		return cl.getResourceAsStream(loc);
	}
	
	public static Texture getTexture(String loc) throws Exception {
		Texture texture = new Texture(getStream(loc));
		return texture;
	}
	
	public static Mesh getMesh(ClassLoader cl, String loc) throws Exception {
		Mesh mesh = OBJFileLoader.loadOBJ(getStream(cl, loc));
//		System.out.println(oc);
//		Mesh mesh = StaticMeshesLoader.load(loc);
		return mesh;
	}
	
	public static AnimatedModel getAnimatedModel(ClassLoader cl, String loc) throws Exception {
		AnimatedModel anim = AnimMeshesLoader.loadAnimGameItem(loc);
		return anim;
	}
	
	public static ByteBuffer ioResourceToByteBuffer(ClassLoader cl, String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
                while (fc.read(buffer) != -1) ;
            }
        } else {
            try (
                    InputStream source = getStream(cl, resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)) {
                buffer = createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                    }
                }
            }
        }

        buffer.flip();
        return buffer;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

	public static SkyboxTexture getSkyboxTexture(String front, String back, String top, 
			String bottom, String left, String right) throws IOException {
			InputStream[] is = new InputStream[6];
			is[0] = Loader.getStream(front);
			is[1] = Loader.getStream(back);
			is[2] = Loader.getStream(top);
			is[3] = Loader.getStream(bottom);
			is[4] = Loader.getStream(left);
			is[5] = Loader.getStream(right);
		return new SkyboxTexture(is);
	}
}
