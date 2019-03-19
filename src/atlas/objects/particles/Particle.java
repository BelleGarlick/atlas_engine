package atlas.objects.particles;

import org.joml.Vector3f;

import atlas.objects.entityComponents.Mesh;

public class Particle {

	private Vector3f position = new Vector3f();
    private Vector3f speed = new Vector3f();
    private float scale = 1;

    /**
     * Time to live for particle in milliseconds.
     */
    private long ttl;


    public Particle(Vector3f speed, long ttl) {
        this.speed = new Vector3f(speed);
        this.ttl = ttl;
    }

    public Particle(Particle baseParticle) {
        Vector3f aux = baseParticle.getPosition();
        setPosition(aux.x, aux.y, aux.z);
        setScale(baseParticle.getScale());
        this.speed = new Vector3f(baseParticle.speed);
        this.ttl = baseParticle.geTtl();
    }

    public Vector3f getSpeed() {
        return speed;
    }

    public void setSpeed(Vector3f speed) {
        this.speed = speed;
    }

    public long geTtl() {
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
    public long updateTtl(long elapsedTime) {
        this.ttl -= elapsedTime;
        return this.ttl;
    }

	public Vector3f getPosition() {return this.position;}
	public void setPosition(float x, float y, float z) {this.position.x = x;this.position.y = y; this.position.z = z;}

	public float getScale() {return scale;}
	public void setScale(float s) {scale = s;}
}