package atlas.objects.lights;

import org.joml.Vector3f;

public class DirectionalLight extends ALight {
	
	private Vector3f direction;

	public DirectionalLight(Vector3f colour, Vector3f direction) {
		super(colour);
		this.direction = direction;
	}
	
	public Vector3f getDirection() {
		return this.direction;
	}
	public void setDirection(Vector3f d) {
		this.direction = d;
	}
}
