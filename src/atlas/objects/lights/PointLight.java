package atlas.objects.lights;

import org.joml.Vector3f;

public class PointLight extends ALight {

	private Vector3f position;
	
	public PointLight(Vector3f position, Vector3f colour) {
		super(colour);
		this.position = position;
	}
	
	public void setPosition(Vector3f pos) {this.position = pos;}
	public Vector3f getPosition() {return this.position;};

}
