package atlas.objects;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import atlas.engine.Window;

public class Camera {
	
	public static float FOV = 70;
	public static float NEAR = 0.1f;
	public static float FAR = 2000f;

	//left, top, width, height
	public Vector4f viewport = new Vector4f(0,0,1,1);
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f viewMatrix = new Matrix4f();
	
	private Vector3f position = new Vector3f(0, 0, 0);
	private final Vector3f rotation = new Vector3f(0,0,0);
	
	
	public Camera() {
		
	}
	
	
	public void updateProjectionMatrix(Window window) {
		float aspectRatio = (float) (window.getWidth() * viewport.z()) / (window.getHeight() * viewport.w());
		projectionMatrix = new Matrix4f().perspective((float)Math.toRadians(FOV), aspectRatio,
				NEAR, FAR);
	}	
	public Matrix4f getProjectionMatrix() {
		return this.projectionMatrix;
	}
	
	public void updateViewMatrix() {
		viewMatrix.identity();
		viewMatrix = viewMatrix.rotate((float)Math.toRadians(this.rotation.x),
				new Vector3f(1,0,0)).rotate((float) Math.toRadians(rotation.y + 90), new Vector3f(0,1,0));
		
		viewMatrix = viewMatrix.translate(-position.x, -position.y, -position.z);
	}
	public Matrix4f getViewMatrix() {
		return this.viewMatrix;
	}
	
	
	
	public Vector3f getPosition() {return this.position;}
	public void setPosition(Vector3f pos) {this.position = pos;}
	public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if ( offsetZ != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if ( offsetX != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }
	
	
	
	public void setPitch(float y) {this.rotation.y = y;}	
	public void setRoll(float x) {this.rotation.x = x;}	
	public void setYaw(float z) {this.rotation.z = z;}
	public Vector3f getRotation() {return this.rotation;}


	//Returns Horizontal FOV in radians
//	public float getHFOV(int screenWidth, int screenHeight) {
//		float width = ((float)screenWidth) * viewport.z;
//		float height = ((float)screenHeight) * viewport.w;
//		float ratio = width / height;
//
//		float radHFOV = (float) (2f * Math.atan(Math.tan(FOV / 2) * ratio));
//		return radHFOV;
//	}
}
