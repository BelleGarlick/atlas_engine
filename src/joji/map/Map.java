package joji.map;

import java.util.HashMap;

import atlas.engine.Scene;
import atlas.utils.Noise;
import joji.blocks.Block;

public class Map {

	public final static int CHUNK_SIZE = 16;
	public final static int MAX_HEIGHT = 255;
	private HashMap<Integer, HashMap<Integer, Chunk>> loadedChunks = new HashMap<>();

	private final Noise seed;
	
	public Map(Scene s) {
		seed = new Noise(1000l);
	}
	
	float count = 1;
	public void update(float interval, Scene s) {
		count += interval;
		if (count > 1) {
			loadLocalChunks(s);
		}
	}
	
	private void loadLocalChunks(Scene s) {
		int camX = (int) s.getCamera().getPosition().x;
		int camZ = (int) s.getCamera().getPosition().z;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				loadChunk(s, i * CHUNK_SIZE + camX, j * CHUNK_SIZE + camZ);
			}
		}
	}
	
	private void loadChunk(Scene s, int worldX, int worldZ) {
		int chunkX = ((int)Math.floor(worldX / (float)CHUNK_SIZE));
		int chunkZ = ((int)Math.floor(worldZ / (float)CHUNK_SIZE));

		if (!loadedChunks.containsKey(chunkX)){
			loadedChunks.put(chunkX, new HashMap<Integer, Chunk>());
		}
		if (!loadedChunks.get(chunkX).containsKey(chunkZ)){
			Chunk c = new Chunk(s, this, chunkX, chunkZ);
			loadedChunks.get(chunkX).put(chunkZ, c);
			c.init(seed);
		}
	}
	
//	public void placeBlock(Scene s, Block block, int x, int y, int z) {
//		int chunkX = ((int)Math.floor(x / (float)CHUNK_SIZE));
//		int chunkZ = ((int)Math.floor(z / (float)CHUNK_SIZE));
//		if (!loadedChunks.containsKey(chunkX)){
//			loadedChunks.put(chunkX, new HashMap<Integer, Chunk>());
//		}
//		if (!loadedChunks.get(chunkX).containsKey(chunkZ)){
//			loadedChunks.get(chunkX).put(chunkZ, new Chunk(s, this, chunkX, chunkZ));
//		}
//		
//		loadedChunks.get(chunkX).get(chunkZ).placeBlock(s,block,x,y,z);
//	}
	
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
		return loadedChunks.get(chunkX).get(chunkZ).getBlockArray()[x-(chunkX * CHUNK_SIZE)][y][z-(chunkZ * CHUNK_SIZE)];
	}
}
