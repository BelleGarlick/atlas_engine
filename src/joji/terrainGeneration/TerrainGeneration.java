package joji.terrainGeneration;

import atlas.utils.Noise;
import joji.blocks.Blocks;
import joji.blocks.Blocks.BlockData;

public class TerrainGeneration {
	
	public static BlockData getBlock(Noise random, int x, int y, int z) {
		if (y < random.eval(x/10f, z/10f) * 20f + 30) {
			return Blocks.getBlock("cobblestone");
		}
		return null;
	}
}
