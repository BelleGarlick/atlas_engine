package joji.terrainGeneration;

import atlas.utils.Noise;
import joji.blocks.Blocks;
import joji.blocks.Blocks.BlockData;

public class TerrainGeneration {
	
	public static BlockData getBlock(Noise random, int x, int y, int z) {
		if (y == 0) {
			return Blocks.getBlock("corundum");
		}
		if (y < random.eval(x/100f, z/100f) * 20f + 10) {
			return Blocks.getBlock("cobblestone");
		}
		return null;
	}
}
