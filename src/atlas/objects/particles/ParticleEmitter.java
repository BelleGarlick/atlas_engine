package atlas.objects.particles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joml.Vector3f;

public class ParticleEmitter {

    private int maxParticles;

    private boolean active;

    private final List<Particle> particles;
    private final Particle baseParticle;

    private float creationPeriodMillis;

    private float lastCreationTime;

    private float speedRndRange;

    private float positionRndRange;

    private float scaleRndRange;

    public ParticleEmitter(Particle baseParticle, int maxParticles, float creationPeriodMillis) {
        particles = new ArrayList<>();
        this.baseParticle = baseParticle;
        this.maxParticles = maxParticles;
        this.active = false;
        this.lastCreationTime = 0;
        this.creationPeriodMillis = creationPeriodMillis;
    }

    public Particle getBaseParticle() {
        return baseParticle;
    }

    public float getCreationPeriodMillis() {
        return creationPeriodMillis;
    }

    public int getMaxParticles() {
        return maxParticles;
    }

    public List<Particle> getParticles() {return particles;}

    public float getPositionRndRange() {
        return positionRndRange;
    }

    public float getScaleRndRange() {
        return scaleRndRange;
    }

    public float getSpeedRndRange() {
        return speedRndRange;
    }

    public void setCreationPeriodMillis(long creationPeriodMillis) {
        this.creationPeriodMillis = creationPeriodMillis;
    }

    public void setMaxParticles(int maxParticles) {
        this.maxParticles = maxParticles;
    }

    public void setPositionRndRange(float positionRndRange) {
        this.positionRndRange = positionRndRange;
    }

    public void setScaleRndRange(float scaleRndRange) {
        this.scaleRndRange = scaleRndRange;
    }

    public boolean isActive() {return active;}
    public void setActive(boolean active) {this.active = active;}

    public void setSpeedRndRange(float speedRndRange) {this.speedRndRange = speedRndRange;}

    public void update(float interval) {
        Iterator<? extends Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle particle = (Particle) it.next();
            if (particle.updateTtl(interval) < 0) {
                it.remove();
            } else {
                updatePosition(particle, interval);
            }
        }

        int length = this.getParticles().size();

        lastCreationTime += interval;
        if (lastCreationTime >= this.creationPeriodMillis && length < maxParticles) {
            createParticle();
            this.lastCreationTime = 0;
        }
    }

    private void createParticle() {
        Particle particle = new Particle(this.getBaseParticle());
        // Add a little bit of randomness of the particle
        float sign = Math.random() > 0.5d ? -1.0f : 1.0f;
        float speedInc = sign * (float)Math.random() * this.speedRndRange;
        float posInc = sign * (float)Math.random() * this.positionRndRange;        
        float scaleInc = sign * (float)Math.random() * this.scaleRndRange;        
        particle.getPosition().add(posInc, posInc, posInc);
        particle.getSpeed().add(speedInc, speedInc, speedInc);
        particle.setScale(particle.getScale() + scaleInc);
        particles.add(particle);
    }

    /**
     * Updates a particle position
     * @param particle The particle to update
     * @param elapsedTime Elapsed time in milliseconds
     */
    public void updatePosition(Particle particle, float elapsedTime) {
        Vector3f speed = particle.getSpeed();
        float dx = speed.x * elapsedTime;
        float dy = speed.y * elapsedTime;
        float dz = speed.z * elapsedTime;
        Vector3f pos = particle.getPosition();
        particle.setPosition(pos.x + dx, pos.y + dy, pos.z + dz);
    }


    
    public void cleanup() {
//        for (Particle particle : getParticles()) {
        	System.out.println("Clean up particles");
//        }
    }
}
