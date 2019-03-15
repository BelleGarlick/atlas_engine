package atlas.objects.lights;

import org.joml.Vector3f;

public class ALight {
	
	protected Vector3f colour = new Vector3f();
	protected float intensity = 1f;
	
	protected Attenuation attenuation = new Attenuation();
	
	public ALight(Vector3f colour) {
		this.colour = colour;
	}

	public void setIntensity(float intensity) {this.intensity = intensity;}
	public float getIntensity() {return this.intensity;}

	public void setColour(Vector3f colour) {this.colour = colour;}
	public Vector3f getColour() {return this.colour;}

	public void setAttenuation(Attenuation atten) {this.attenuation = atten;}
	public Attenuation getAttenuation() {return this.attenuation;}
	
	public static class Attenuation {
		public float constant = 0f;
		public float linear = 0.5f;
		public float exponent = 0;
	}
}
