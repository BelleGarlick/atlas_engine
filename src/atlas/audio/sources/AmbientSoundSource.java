package atlas.audio.sources;

import static org.lwjgl.openal.AL10.AL_SOURCE_RELATIVE;
import static org.lwjgl.openal.AL10.AL_TRUE;
import static org.lwjgl.openal.AL10.alSourcei;

import org.joml.Vector3f;

import atlas.audio.Audio;
import atlas.audio.AudioListener;
import atlas.audio.Sound;

public class AmbientSoundSource extends SoundSource {

	private float ambientVolume = 0;

	public Vector3f position = new Vector3f();
	public Vector3f size = new Vector3f();
	public Vector3f padding = new Vector3f();
	
	public AmbientSoundSource(Sound soundbuffer, Vector3f position, Vector3f size, Vector3f padding) {
		super(soundbuffer);
		
		this.position = position;
		this.size = size;
		this.padding = padding;
		
        alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE);
	}

	@Override
	public void setVolume(float volume) {
		this.sourceVolume = volume;
		this.setGain(volume * ambientVolume); 
	}
	
	private void setAmbientVolume(float amVol){
		this.ambientVolume = amVol;
		this.setGain(this.sourceVolume * this.ambientVolume);
	}

	
	public void update() {
		float volume = calcAmbientSoundVolume();
		this.setAmbientVolume(volume);
		
		if (volume == 0) {
			this.pause();
		} else { 
			this.play();
		}
	}
	
	
	private float calcAmbientSoundVolume() {
		AudioListener listener = Audio.listener;
		float xMin = position.x - size.x/2f, xMax = position.x + size.x/2f;
		float xVol = getVolumeMultiplyerFromPlane(listener.getPositionByValue().x, 
				xMin - padding.x, xMin, xMax, xMax + padding.x);
		
		float yMin = position.y - size.y/2f, yMax = position.y + size.y/2f;
		float yVol = getVolumeMultiplyerFromPlane(listener.getPositionByValue().y, 
				yMin - padding.y, yMin, yMax, yMax + padding.y);

		float zMin = position.z - size.z/2f, zMax = position.z + size.z/2f;
		float zVol = getVolumeMultiplyerFromPlane(listener.getPositionByValue().z, 
				zMin - padding.z, zMin, zMax, zMax + padding.z);

		System.out.println(listener.getPositionByValue());
		return xVol * yVol * zVol;
	}
	


	//if value in box, value = 1; if point in paddingRange, padding is proportional to how close it is to box
	private float getVolumeMultiplyerFromPlane(float value, float minPadding, float minBox, float maxBox, float maxPadding){
		if (value <= minPadding || value >= maxPadding){
			//Value in padding
			return 0;
		}else if (value >= minBox && value <= maxBox){
			//value in box
			return 1;
		}else if (value >= minPadding && value<= minBox){
			//value less that box but in padding
			float divisor = (minBox-minPadding);
			if (divisor==0){divisor=0.0001f;}
			return (value-minPadding)/divisor;
		}else if (value <= maxPadding && value>= maxBox){
			//value greater that box but in padding
			float divisor = (maxBox-maxPadding);
			if (divisor==0){divisor=0.0001f;}
			return (value-maxPadding)/divisor;
		}
		return 0;
	}
}
