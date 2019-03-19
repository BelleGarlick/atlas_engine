package atlas.objects.particles;

import org.joml.Vector3f;

import atlas.graphical.Texture;

public class Particle {

	private Vector3f position = new Vector3f();
    private Vector3f speed = new Vector3f();
    private float scale = 1;

    /**
     * Time to live for particle in milliseconds.
     */
    private float ttl;
	private Texture texture;


    public Particle(Texture t, Vector3f speed, float ttl) {
        this.speed = new Vector3f(speed);
        this.ttl = ttl;
        this.texture = t;
    }

    public Particle(Particle baseParticle) {
        Vector3f aux = baseParticle.getPosition();
        setPosition(aux.x, aux.y, aux.z);
        setScale(baseParticle.getScale());
        this.speed = new Vector3f(baseParticle.speed);
        this.ttl = baseParticle.geTtl();
        this.texture = baseParticle.texture;
    }

    public Vector3f getSpeed() {
        return speed;
    }

    public void setSpeed(Vector3f speed) {
        this.speed = speed;
    }

    public float geTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    /**
     * Updates the Particle's TTL
     * @param elapsedTime Elapsed Time in milliseconds
     * @return The Particle's TTL
     */
    public float updateTtl(float elapsedTime) {
        this.ttl -= elapsedTime;
        return this.ttl;
    }

	public Vector3f getPosition() {return this.position;}
	public void setPosition(float x, float y, float z) {this.position.x = x;this.position.y = y; this.position.z = z;}

	public float getScale() {return scale;}
	public void setScale(float s) {scale = s;}

	public void setTexture(Texture t) {this.texture = t;}
	public Texture getTexture() {return this.texture;}
}