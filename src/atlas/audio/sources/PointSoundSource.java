package atlas.audio.sources;

import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alSource3f;

import org.joml.Vector3f;

import atlas.audio.Sound;

public class PointSoundSource extends SoundSource {

	public PointSoundSource(Sound soundbuffer, Vector3f position) {
		super(soundbuffer);
	    this.setPosition(position);
	}

    public void setPosition(Vector3f position) {
        alSource3f(sourceId, AL_POSITION, position.x, position.y, position.z);
    }

    public void setSpeed(Vector3f speed) {
        alSource3f(sourceId, AL_VELOCITY, speed.x, speed.y, speed.z);
    }
}
