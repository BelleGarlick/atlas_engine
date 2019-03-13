package joji.blocks;

import java.util.HashMap;

import atlas.graphical.Texture;
import atlas.utils.Loader;

public class BlockTextures {

	private static HashMap<String, Texture> texturemap = new HashMap<>();
	
	public static Texture getTexture(String texture) {
		try {
			if (texturemap.containsKey(texture)) {
				return texturemap.get(texture);
			} else {
				Texture newTexture = Loader.getTexture(texture);
				newTexture.scaleDiscrete();
				texturemap.put(texture, newTexture);
				return newTexture;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void cleanUp() {
		for (Texture t : texturemap.values()) {
			t.cleanUp();
		}
	}
	
}
