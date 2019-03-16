package atlas.objects;

import org.joml.Vector3f;

public class Fog {

	private float density = 0.02f;
	private boolean active = true;
	private Vector3f colour = new Vector3f(0,0,0);
	private FogMode fogMode = FogMode.Radial;
	public enum FogMode {
		Radial,
		Cylindrical 
	};
	
	public Fog(Vector3f c) {
		this.setColour(c);
	}
	
	public void setFogMode(FogMode fogMode) {this.fogMode = fogMode;}
	public FogMode getFogMode() {return this.fogMode;}

	public void setColour(Vector3f colour) {this.colour = colour;}
	public Vector3f getColour() {return this.colour;}
	
	public void setDensity(float density) {this.density = density;}
	public float getDensity(){return this.density;}
	
	public void activate() {this.active = true;}
	public void deactivate() {this.active = false;}
	public boolean isActive() {
		return this.active;
	}
}
