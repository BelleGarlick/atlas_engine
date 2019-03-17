package atlas.audio.sources;

import org.lwjgl.openal.AL10;
import atlas.audio.Sound;
import static org.lwjgl.openal.AL10.*;

public abstract class SoundSource {

    protected int sourceId = 0;
    
    protected float sourceVolume = 1f;    
	private float pitchVariation = 0;

    public SoundSource(Sound soundbuffer) {
        setUp();     
        
        setBuffer(soundbuffer.getBufferId());
    }
    
    public void setUp(){
        this.sourceId = alGenSources();   
    	AL10.alSourcef(this.sourceId, AL10.AL_ROLLOFF_FACTOR, 1);
    	AL10.alSourcef(this.sourceId, AL10.AL_REFERENCE_DISTANCE, 6);
    	AL10.alSourcef(this.sourceId, AL10.AL_MAX_DISTANCE, 30);
    }
    
    public void updateVolume(){
    	this.setGain(sourceVolume);
    }

    public void setBuffer(int bufferId) {
        stop();
        alSourcei(sourceId, AL_BUFFER, bufferId);
    }
    
    public void setVolume(float v){
    	this.sourceVolume = v;
    	setGain(v);
    }
    public float getVolume() {return this.sourceVolume;}    
    protected void setGain(float gain) {alSourcef(sourceId, AL_GAIN, gain);}

//    public void setProperty(int param, float value) {
//        alSourcef(sourceId, param, value);
//    }
        
    public void setPitchVariation(float variation){
    	this.pitchVariation  = variation;
    }
    
    private void pitchToVeriation(){
    	float sign = Math.random() < 0.5d ? 1 : -1;
    	float newPitch = (float) (1 + (sign*Math.random()*pitchVariation));
    	alSourcef(sourceId, AL_PITCH, newPitch);
    }
    
    public void play() {
    	pitchToVeriation();
    	if (!this.isPlaying()){
            alSourcePlay(sourceId);
    	}
    }

    public boolean isPlaying() {
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public void pause() {
        alSourcePause(sourceId);
    }

    public void stop() {
        alSourceStop(sourceId);
    }

    public void cleanup() {
        stop();
        alDeleteSources(sourceId);
    }

	public void setLooping(boolean shouldLoop) {
		if (shouldLoop){
			alSourcei(sourceId,AL_LOOPING,1);
		}else{
			alSourcei(sourceId,AL_LOOPING,0);
		}
	}
}
