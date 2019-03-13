package atlas.objects.entityComponents;

import atlas.graphical.Texture;

public class BlendMap {
	
	private Texture blendMap = null;
	private Texture rTexture = null;
	private Texture gTexture = null;
	private Texture bTexture = null;

	public BlendMap(Texture blendMap) {this.blendMap = blendMap;}
	public BlendMap(Texture blendMap, Texture rTexture) {this.blendMap = blendMap;this.rTexture = rTexture;}
	public BlendMap(Texture blendMap, Texture rTexture, Texture gTexture) {this.blendMap = blendMap;this.rTexture = rTexture;this.gTexture = gTexture;}
	public BlendMap(Texture blendMap, Texture rTexture, Texture gTexture, Texture bTexture) {this.blendMap = blendMap;this.rTexture = rTexture;this.gTexture = gTexture;this.bTexture = bTexture;}

	public Texture getBlendMap() {return this.blendMap;}
	public void setBlendMap(Texture blendMap) {this.blendMap = blendMap;}
	
	public Texture getRTexture() {return this.rTexture;}
	public void setRTexture(Texture rTexture) {this.rTexture = rTexture;}
	
	public Texture getGTexture() {return this.gTexture;}
	public void setGTexture(Texture gTexture) {this.gTexture = gTexture;}
	
	public Texture getBTexture() {return this.bTexture;}
	public void setBTexture(Texture bTexture) {this.bTexture = bTexture;}
	
}
