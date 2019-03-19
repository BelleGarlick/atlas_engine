package atlas.audio;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import atlas.utils.Maths;

import static org.lwjgl.openal.AL10.*;

public class AudioListener {

	private Vector3f position = new Vector3f();
	
    public AudioListener() {
        this(new Vector3f(0, 0, 0));
    }

    public AudioListener(Vector3f position) {
    	this.position = position;
        alListener3f(AL_POSITION, position.x, position.y, position.z);
        alListener3f(AL_VELOCITY, 0, 0, 0);
    }
    
    public void setVelocity(Vector3f speed) {
        alListener3f(AL_VELOCITY, speed.x, speed.y, speed.z);
    }

    public void setPosition(Vector3f position) {
    	this.position = position;
        alListener3f(AL_POSITION, position.x, position.y, position.z);
    }

    public Vector3f getPositionByValue() {
    	return this.position;
    }
           
    public void updateListenerPosition(Vector3f position, Vector3f orientation) {
        // Update camera matrix with camera data
        Matrix4f cameraMatrix = Maths.updateGenericViewMatrix(position, (new Vector3f(orientation)));
        setPosition(position);
        
        

        Vector3f at = new Vector3f();
        cameraMatrix.positiveX(at);//.negate();
        Vector3f up = new Vector3f();
        cameraMatrix.positiveY(up);
        setOrientation(at, up);
    }
            
    public void setOrientation(Vector3f at, Vector3f up) {
        float[] data = new float[6];
        data[0] = at.x;
        data[1] = at.y;
        data[2] = at.z;
        data[3] = up.x;
        data[4] = up.y;
        data[5] = up.z;
        alListenerfv(AL_ORIENTATION, data);
    }    
}
