package atlas.objects.entityComponents;

public class BillboardMesh extends Mesh {

	public BillboardMesh(float width, float height) {
		super(new float[]{
		        -width/2f, height, 0.0f,
		        -width/2f, 0f, 0.0f,
		         width/2f, 0f, 0.0f,
		         width/2f, height, 0.0f
		    }, new float[]{
		        0f, 0f, 
		        0f, 1f, 
		        1f, 1f,
		        1f, 0f
		    }, new float[]{
		        0f, 1f, 0f,
		        0f, 1f, 0f
		    }, new int[]{
		        0, 1, 3, 3, 1, 2,
		});
	}

}
