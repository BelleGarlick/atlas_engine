package joji.blocks;

import org.joml.Vector3f;

import atlas.objects.Entity;
import atlas.objects.entityComponents.Material;
import joji.blocks.Blocks.BlockData;

public class Block {
	private final String id;
	
	boolean transparent = false;
	
	Entity top = null;
	Entity bottom = null;
	Entity left = null;
	Entity right = null;
	Entity front = null;
	Entity back = null;
	
	public Block(BlockData bd) {
		this.id = bd.id;
		this.transparent = bd.transparent;
		
		top = new Entity(Blocks.plane);
		top.setMaterial(new Material(BlockTextures.getTexture("jomiTextures/" + bd.textureUp + ".png")));
		top.setScale(0.5f);
		
		bottom = new Entity(Blocks.plane);
		bottom.setMaterial(new Material(BlockTextures.getTexture("jomiTextures/" + bd.textureDown + ".png")));
		bottom.getRotation().z = 180;
		bottom.setScale(0.5f);

		back = new Entity(Blocks.plane);
		back.setMaterial(new Material(BlockTextures.getTexture("jomiTextures/" + bd.textureBack + ".png")));
		back.getRotation().z = -90;
		back.setScale(0.5f);
		
		front = new Entity(Blocks.plane);
		front.setMaterial(new Material(BlockTextures.getTexture("jomiTextures/" + bd.textureFront + ".png")));
		front.getRotation().z = 90;
		front.setScale(0.5f);
		
		right = new Entity(Blocks.plane);
		right.setMaterial(new Material(BlockTextures.getTexture("jomiTextures/" + bd.textureRight + ".png")));
		right.getRotation().x = -90;
		right.setScale(0.5f);
		
		left = new Entity(Blocks.plane);
		left.setMaterial(new Material(BlockTextures.getTexture("jomiTextures/" + bd.textureLeft + ".png")));
		left.getRotation().x = 90;
		left.setScale(0.5f);
		
		setPosition(0,0,0);
	}
	
	public void setPosition(Vector3f pos) {
		setPosition(pos.x, pos.y, pos.z);
	}
	
	public void setPosition(float x, float y, float z) {
		top.setPosition(x, y+1, z);
		bottom.setPosition(x, y, z);
		front.setPosition(x + 0.5f, y + 0.5f, z);
		back.setPosition(x - 0.5f, y + 0.5f, z);
		left.setPosition(x, y + 0.5f, z - 0.5f);
		right.setPosition(x, y + 0.5f, z + 0.5f);
	}

	public Entity getTop() {return this.top;}
	public Entity getBottom() {return this.bottom;}
	public Entity getFront() {return this.front;}
	public Entity getBack() {return this.back;}
	public Entity getLeft() {return this.left;}
	public Entity getRight() {return this.right;}

	public boolean isTransparent() {
		return this.transparent;
	}
	
	public String id() {
		return this.id;
	}
}
