package joji.blocks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import atlas.objects.entityComponents.Mesh;
import atlas.utils.Loader;
import atlas.utils.MeshDefaults;

public class Blocks {
	
	public static Mesh plane = null;
	private static HashMap<String, BlockData> blockData = new HashMap<>();
	
	public static void init() {
		try { 
			plane = MeshDefaults.loadPlane();
			
			String blockText = "";
			BufferedReader br = new BufferedReader(new InputStreamReader(Loader.getStream("blocks.txt")));
			String l = "";
			while ((l = br.readLine()) != null) {blockText += l;}br.close();
						
			parseBlocks(blockText);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void parseBlocks(String text) {
		for (String blocks : text.split(";")) {
			BlockData newBlock = new BlockData();
			for (String att : blocks.split(",")) {
				String blockAttribute = att.replaceAll(" ", "");
				String[] attributes = blockAttribute.split(":");
				String data = attributes[0], value = attributes[1];
				
				if (data.equals("id")) {newBlock.id = value;}				

				if (data.equals("textureUp")) {newBlock.textureUp = value;}
				if (data.equals("textureDown")) {newBlock.textureDown = value;}
				if (data.equals("textureLeft")) {newBlock.textureLeft = value;}
				if (data.equals("textureRight")) {newBlock.textureRight = value;}
				if (data.equals("textureFront")) {newBlock.textureFront = value;}
				if (data.equals("textureBack")) {newBlock.textureBack = value;}

				if (data.equals("textureAll")) {
					newBlock.textureUp = value;
					newBlock.textureDown = value;
					newBlock.textureLeft = value;
					newBlock.textureRight = value;
					newBlock.textureFront = value;
					newBlock.textureBack = value;
				}

				if (data.equals("textureSides")) {
					newBlock.textureLeft = value;
					newBlock.textureRight = value;
					newBlock.textureFront = value;
					newBlock.textureBack = value;
				}				
			}
			System.out.println(newBlock.id);
			blockData.put(newBlock.id, newBlock);
		}
	}

	public static BlockData getBlock(String string) {
		return blockData.get(string);
	}
	
	public static class BlockData { 
		public String id = "unnamed";
		public boolean transparent = false;

		public String textureUp = "";
		public String textureDown = "";
		public String textureLeft = "";
		public String textureRight = "";
		public String textureFront = "";
		public String textureBack = "";
	}

}
