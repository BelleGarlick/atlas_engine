package atlas.audio.sources;

import static org.lwjgl.openal.AL10.AL_SOURCE_RELATIVE;
import static org.lwjgl.openal.AL10.AL_TRUE;
import static org.lwjgl.openal.AL10.alSourcei;

import atlas.audio.Sound;

public class RelativeSoundSource extends SoundSource {

	public RelativeSoundSource(Sound soundbuffer) {
		super(soundbuffer);
        alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE);
	}

}
