package atlas.audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL11;

import static org.lwjgl.openal.AL10.*;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.ALCCapabilities;

import static org.lwjgl.system.MemoryUtil.NULL;

public class Audio {

    private static long device;    
    private static long context;

    public static AudioListener listener;
    
    public static void init() throws Exception {
        device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);

        listener = new AudioListener();
        
        alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);
    }
    
    
    public static void cleanUp() {
        if (context != NULL) {
            alcDestroyContext(context);
        }
        if (device != NULL) {
            alcCloseDevice(device);
        }
    }
}
