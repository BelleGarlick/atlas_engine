package atlas.objects;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import atlas.objects.entityComponents.BlendMap;
import atlas.objects.entityComponents.Material;
import atlas.objects.entityComponents.Mesh;
import atlas.utils.Maths;
import atlas.utils.Utils;

public class Terrain {

	private Mesh mesh = null;
	private Mesh lowMesh = null;
	private Mesh minMesh = null;
	private Material material = new Material();
	private float[][] heights = null;
	
	private Vector3f position = new Vector3f();
	public final float xSize;
	public final float zSize;
	private boolean visible = true;
	
	private ArrayList<BlendMap> blendMaps = new ArrayList<>();
	private float tiling = 1f;
	
	public Terrain(float[][] heights, float xSize, float zSize) {
		this.xSize = xSize;
		this.zSize = zSize;
		this.heights = new float[heights.length - 2][heights[0].length - 2];

		float[][] decimHeights = decimateHeights(heights);
		lowMesh = buildTerrainMesh(xSize, zSize, decimHeights);
		minMesh = buildTerrainMesh(xSize, zSize, decimateHeights(decimHeights));
		mesh = buildTerrainMesh(xSize, zSize, heights);
	}
	
	private Mesh buildTerrainMesh (float x_size, float z_size, float[][] heights) {
		float incx = (x_size) / (heights[0].length - 3);
        float incz = (z_size) / (heights.length - 3);

        int width = heights.length - 2;
        int height = heights[0].length - 2;

        List<Float> positions = new ArrayList<>();
        List<Float> textCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // Create vertex for current position
                positions.add(col * incx); // x

                this.heights[col][row] = heights[col+1][row+1];
                positions.add(heights[col+1][row+1]); //y
                positions.add(row * incz); //z

                // Set texture coordinates
                textCoords.add((float) 1 * (float) col / (float) width);
                textCoords.add((float) 1 * (float) row / (float) height);

                // Create indices
                if (col < width - 1 && row < height - 1) {
                    int leftTop = row * width + col;
                    int leftBottom = (row + 1) * width + col;
                    int rightBottom = (row + 1) * width + col + 1;
                    int rightTop = row * width + col + 1;

                    indices.add(leftTop);
                    indices.add(leftBottom);
                    indices.add(rightTop);

                    indices.add(rightTop);
                    indices.add(leftBottom);
                    indices.add(rightBottom);
                }
            }
        }
        float[] posArr = Utils.listToArray(positions);
        int[] indicesArr = indices.stream().mapToInt(i -> i).toArray();
        float[] textCoordsArr = Utils.listToArray(textCoords);
        float[] normalsArr = calcNormals(heights);
        return new Mesh(posArr, textCoordsArr, normalsArr, indicesArr);
	}

	private float[] calcNormals(float[][] positions) {
//		int width = positions.length;
		Vector3f normal = new Vector3f();
        Vector3f v0 = new Vector3f();
        Vector3f v1 = new Vector3f();
        Vector3f v2 = new Vector3f();
        Vector3f v3 = new Vector3f();
        Vector3f v4 = new Vector3f();
        Vector3f v12 = new Vector3f();
        Vector3f v23 = new Vector3f();
        Vector3f v34 = new Vector3f();
        Vector3f v41 = new Vector3f();
        List<Float> normals = new ArrayList<>();
        for (int row = 1; row < positions.length - 1; row++) {
            for (int col = 1; col < positions[0].length - 1; col++) {            	            	
//        		Vector3f tN = new Vector3f(positions[col+1][row]-positions[col-1][row],2f,positions[col][row+1]-positions[col][row-1]);
//        		tN.normalize();
//                normals.add(tN.x);
//                normals.add(tN.y);
//                normals.add(tN.z);
            	
            	int r = row;
        		int c = col;
        		
//            	int i0 = row*width*3 + col*3;
                v0.x = r*10;
                v0.y = positions[col][row];
                v0.z = c*10;

//            	int i1 = row*width*3 + (col-1)*3;
                v1.x = r*10;
                v1.y = positions[col-1][row];
                v1.z = (c-1)*10;                    
                v1 = v1.sub(v0);

//                int i2 = (row+1)*width*3 + col*3;
                v2.x = (r+1)*10;
                v2.y = positions[col][row+1];
                v2.z = (c)*10;    
                v2 = v2.sub(v0);

                v3.x = r*10;
                v3.y = positions[col+1][row];
                v3.z = (c+1)*10;    
                v3 = v3.sub(v0);

//                int i4 = (row-1)*width*3 + col*3;
                v4.x = (r-1)*10;
                v4.y = positions[col][row-1];
                v4.z = (c)*10;    
                v4 = v4.sub(v0);
                
                v1.cross(v2, v12);
                v12.normalize();

                v2.cross(v3, v23);
                v23.normalize();
                
                v3.cross(v4, v34);
                v34.normalize();
                
                v4.cross(v1, v41);
                v41.normalize();
                
                normal = v12.add(v23).add(v34).add(v41);
                normal.normalize();
            normals.add(normal.x);
            normals.add(-normal.y);
            normals.add(normal.z);
            }
        }
        return Utils.listToArray(normals);
    }
	
	float lastHeight = 0; 
    public float getHeightOfTerrain(float worldX, float worldZ){
    	float terrainX = worldX - this.position.x;
		float terrainZ = worldZ - this.position.z;
		float gridSquareSizeX = this.xSize / (float)(heights[0].length - 1);
		float gridSquareSizeZ = this.zSize / (float)(heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSizeX);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSizeZ);
		if (gridX >= heights.length-1 || gridZ>=heights.length-1 || gridX <0 || gridZ < 0){
			return lastHeight;
		}
		float xCoord = (terrainX % gridSquareSizeX)/gridSquareSizeX;
		float zCoord = (terrainZ % gridSquareSizeZ)/gridSquareSizeZ;
		float answer;
		if (xCoord <= (1-zCoord)) {
			answer = Utils
					.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ], 0), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			answer = Utils
					.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}

		
		lastHeight = answer;
		return answer;
	}
    
    private float[][] decimateHeights(float[][] heights) {
    	int decimWidth = (int) (Math.ceil((heights.length-4) / 2f) + 4);
    	int decimHeight = (int) (Math.ceil((heights[0].length-4) / 2f) + 4);
    	
    	float[][] newHeights = new float[decimWidth][decimHeight];
    	for (int dw = 0; dw < decimWidth; dw++) { 
            for (int dh = 0; dh < decimHeight; dh++) { 
            	int tableLocW = dw;
            	int tableLocH = dh;
                if (dw > 1) {tableLocW = (dw - 1) * 2;}
                if (dw == decimWidth-2) {tableLocW -= 1;}
                if (dw == decimWidth-1) {tableLocW -= 2;}
                if (dh > 1) {tableLocH = (dh - 1) * 2;}
                if (dh == decimHeight-2) {tableLocH -= 1;}
                if (dh == decimHeight-1) {tableLocH -= 2;}
                newHeights[dw][dh] = heights[tableLocW][tableLocH];
            }
    	}
    	return newHeights;
    }

	public Mesh getMesh() {return this.mesh;}
	public Mesh getLowMesh() {return this.lowMesh;}
	public Mesh getMinMesh() {return this.minMesh;}

	public void setPosition(Vector3f pos) {this.position = pos;}
	public Vector3f getPosition() {return position;}
	
	public ArrayList<BlendMap> getBlendMaps() {return this.blendMaps;}
	public boolean addBlendMap(BlendMap blendMap) {
		if (this.blendMaps.size() < 3) {
			this.blendMaps.add(blendMap);
			return true;
		}else{
			return false;
		}
	}
	
	public float getDistance(Vector3f target) {
		Vector3f bl = this.position;
		Vector3f br = new Vector3f(this.position);br.z += this.zSize;
		Vector3f tl = new Vector3f(this.position);tl.x += this.xSize;
		Vector3f tr = new Vector3f(this.position);tr.z += this.zSize;tr.x += this.xSize;

		float bDist = Math.min(Maths.pythag3D(target, bl), Maths.pythag3D(target, br));
		float tDist = Math.min(Maths.pythag3D(target, tl), Maths.pythag3D(target, tr));
		return Math.min(tDist, bDist);
	}
	
	public Material getMaterial() {return this.material;}
	public void setMaterial(Material m) {this.material = m;}
	
	public boolean getVisibility(){return this.visible;}
	public void setVisibility(boolean v){this.visible = v;}

	public void setTiling(float tiles) {this.tiling = tiles;}
	public float getTiling() {return this.tiling;}
}
