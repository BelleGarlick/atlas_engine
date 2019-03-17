package joji.map;

import atlas.engine.Scene;
import atlas.utils.Noise;
import joji.blocks.Block;
import joji.blocks.Blocks.BlockData;
import joji.terrainGeneration.TerrainGeneration;

public class Chunk {
	private final Scene scene;
	private final Map map;
	
	private final int chunkWorldX;
	private final int chunkWorldZ;
	private Block[][][] blocks = new Block[Map.CHUNK_SIZE][Map.MAX_HEIGHT][Map.CHUNK_SIZE];
	
	public Chunk(Scene s, Map m, int x, int z) {
		this.map = m;
		this.scene = s;
		this.chunkWorldX = x * Map.CHUNK_SIZE;
		this.chunkWorldZ = z * Map.CHUNK_SIZE;
	}

	public void init(Noise random) {
		for (int i = 0; i < Map.CHUNK_SIZE; i++) {
			for (int j = 0; j < Map.CHUNK_SIZE; j++) {
				for (int k = 0; k < Map.MAX_HEIGHT; k++) {
					BlockData bd = TerrainGeneration.getBlock(random, i + (chunkWorldX), k, j + (chunkWorldZ));
					if (bd != null){ 
						this.placeBlock(scene, new Block(bd), i + (chunkWorldX), k, j + (chunkWorldZ));
					}
				}
			}
		}
	}

	public void placeBlock(Scene s, Block block, int worldX, int y, int worldZ) {
		int x = worldX - chunkWorldX;
		int z = worldZ - chunkWorldZ;
		
		if (blocks[x][y][z] == null) {
			blocks[x][y][z] = block;
			
			Block above = map.getBlock(worldX, y + 1, worldZ);
			if (above == null || above.isTransparent()) {
				s.addEntity(block.getTop());
			} else {
				s.removeEntity(above.getBottom());
			}
			
			Block below = map.getBlock(worldX,y-1,worldZ);
			if ((below == null || below.isTransparent())) {
				if (y > 0) {s.addEntity(block.getBottom());}
			} else {
				s.removeEntity(below.getTop());
			}
			

			Block front = map.getBlock(worldX+1,y,worldZ);
			if (front == null || front.isTransparent()) {
				s.addEntity(block.getFront());
			} else {
				s.removeEntity(front.getBack());
			}

			Block back = map.getBlock(worldX-1,y,worldZ);
			if (back == null || back.isTransparent()) {
				s.addEntity(block.getBack());
			} else {
				s.removeEntity(back.getFront());
			}

			Block left = map.getBlock(worldX,y,worldZ-1);
			if (left == null || left.isTransparent()) {
				s.addEntity(block.getLeft());
			} else {
				s.removeEntity(left.getRight());
			}

			Block right = map.getBlock(worldX,y,worldZ+1);
			if (right == null || right.isTransparent()) {
				s.addEntity(block.getRight());
			} else {
				s.removeEntity(right.getLeft());
			}
			block.setPosition(worldX,y,worldZ);
		}
	}

	public Block[][][] getBlockArray() {
		return this.blocks;
	}
	
}
