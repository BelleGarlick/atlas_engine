package atlas.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Utils {
	
    public static float[] listToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static int[] listIntToArray(List<Integer> list) {
        int[] result = list.stream().mapToInt((Integer v) -> v).toArray();
        return result;
	}
	
	public static void restrictCallerAccess(String c, String des) throws Exception {
		//Utils.restrictCallerAccess(Loader.class.getCanonicalName(), "Do not create a new Texture, use Loader.getTexture() instead.");
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (!(stackTraceElements[3].getClassName().equals(c))) {
        	throw new Exception("Access Restricted. " + des);
        }
	}
	
	public static List<String> inputStreamToList(InputStream is) throws IOException{
        List<String> lines = new ArrayList<>();
        String l = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((l = br.readLine()) != null) {
        	lines.add(l);
        }
        return lines;
	}
}
