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
	
	public Terrain(float xSize, float zSize, float[][] heights) {
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
		System.out.println("Need to do more work to check this is correct");
        List<Float> normals = new ArrayList<>();
        for (int row = 1; row < positions.length - 1; row++) {
            for (int col = 1; col < positions[0].length - 1; col++) {            	            	
        		Vector3f tN = new Vector3f(positions[col+1][row]-positions[col-1][row],2f,positions[col][row+1]-positions[col][row-1]);
        		tN.normalize();
                normals.add(tN.x);
                normals.add(tN.y);
                normals.add(tN.z);
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
