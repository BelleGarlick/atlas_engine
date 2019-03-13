package atlas.objects.entityComponents;

import org.joml.Vector3f;

import atlas.graphical.Texture;

public class Material {

	private boolean useTexture = false;
	private Texture texture = null;
	private Texture normalMap = null;
	private Vector3f color = new Vector3f(1f,0f,0.5f);
	
	private float reflectance = 1;
	
	public Material() {}
	
	public Material(Vector3f c) {
		this.color = c;
	}
	
	public Material(Texture t) {
		this.useTexture = true;
		this.texture = t;
	}
	
	public boolean useTexture() {
		return this.useTexture;
	}

	public Texture getTexture() {
		return this.texture;
	}

	public Vector3f getColor() {
		return this.color;
	}
	
	public float getReflectance() {
		return this.reflectance;
	}
	public void setReflectance(float r) {
		this.reflectance = r;
	}
	
	public void setNormalMap(Texture map) {this.normalMap = map;}
	public Texture getNormalMap() {return this.normalMap;}
}
