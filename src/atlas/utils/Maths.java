package atlas.utils;

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
/*
 <svg height="500" width="500" viewBox="-100 -100 200 200">
<line x1='-19.223928' y1='23.474354' x2='464.5539' y2='198.51251' style='stroke:rgb(255,0,0);stroke-width:10' />
<line x1='50.0' y1='50.0' x2='50.0' y2='-50.0' style='stroke:rgb(0,0,255);stroke-width:10' />
</svg>
 */
}
