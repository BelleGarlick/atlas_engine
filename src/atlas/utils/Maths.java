package atlas.utils;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import com.sun.javafx.geom.Line2D;

public class Maths {
	
	public static float getAngle(Vector2f target, Vector2f origin) {
		float deltaY = target.y - origin.y;
		float deltaX = target.x - origin.x;
	    float angle = (float) Math.toDegrees(Math.atan2(deltaY, target.x - origin.x));
		if (deltaX < 0) {
//			angle += 180;
		} if (angle < 0) {
			angle += 360;
		}
	    return angle;
	}

	public static boolean areLinesIntersecting(Vector2f a1, Vector2f a2, Vector2f b1, Vector2f b2) {
		return Line2D.linesIntersect(a1.x, a1.y, a2.x, a2.y, b1.x, b1.y, b2.x, b2.y);
	}
	
	public static float pythag3D (Vector3f a, Vector3f b) {
		float deltaX = a.x - b.x;
		float deltaY = a.y - b.y;
		float deltaZ = a.z - b.z;
		
		float pythagorusHorz = (float) Math.hypot(deltaX, deltaZ);
		float pythagorusVert = (float) Math.hypot(pythagorusHorz,deltaY);
		return pythagorusVert;
	}

    public static Matrix4f updateGenericViewMatrix(Vector3f position, Vector3f rotation) {
        // First do the rotation so camera rotates over its position
        return new Matrix4f().rotationX((float) ((float)(rotation.x)))
                .rotateY((float) ((float)(rotation.y)))
                .rotateZ((float) ((float)(rotation.z)))
                     .translate(-position.x, -position.y, -position.z);
    }
}
