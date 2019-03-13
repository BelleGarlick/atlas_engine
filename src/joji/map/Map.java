package joji.map;

import java.util.HashMap;

import atlas.engine.Scene;
import atlas.utils.Noise;
import joji.blocks.Block;

public class Map {

	public final static int CHUNK_SIZE = 16;
	public final static int MAX_HEIGHT = 255;
	private HashMap<Integer, HashMap<Integer, Chunk>> loadedChunks = new HashMap<>();
	
	public Map(Scene s) {
		Chunk c = new Chunk(s, this, 0, 0);
		loadedChunks.put(0, new HashMap<Integer, Chunk>());
		loadedChunks.get(0).put(0, c);
		c.init(new Noise(10000l));
	}
	
	public void placeBlock(Scene s, Block block, int x, int y, int z) {
		int chunkX = ((int)Math.floor(x / (float)CHUNK_SIZE));
		int chunkZ = ((int)Math.floor(z / (float)CHUNK_SIZE));
		if (!loadedChunks.containsKey(chunkX)){
			loadedChunks.put(chunkX, new HashMap<Integer, Chunk>());
		}
		if (!loadedChunks.get(chunkX).containsKey(chunkZ)){
			loadedChunks.get(chunkX).put(chunkZ, new Chunk(s, this, chunkX, chunkZ));
		}
		
		loadedChunks.get(chunkX).get(chunkZ).placeBlock(s,block,x,y,z);
	}
	
	public Chunk getChunkByChunkCoords(int chunkX, int chunkZ){
		return loadedChunks.get(chunkX).get(chunkZ);
	}
	
	public Chunk getChunkByBlockCoords(int blockX, int blockZ){
		return getChunkByChunkCoords((int)Math.floor(blockX / CHUNK_SIZE), (int)Math.floor(blockZ / CHUNK_SIZE));
	}

	public Block getBlock(int x, int y, int z) {
		if (y < 0) {
			return null;
		}
		if (y > MAX_HEIGHT) {
			return null;
		}
		int chunkX = ((int)Math.floor(x / (float)CHUNK_SIZE));
		int chunkZ = ((int)Math.floor(z / (float)CHUNK_SIZE));

		if (!loadedChunks.containsKey(chunkX)){
			return null;
		}
		if (!loadedChunks.get(chunkX).containsKey(chunkZ)){
			return null;
		}
		return loadedChunks.get(chunkX).get(chunkZ).getBlockArray()[x-chunkX][y][z-chunkZ];
	}
}
