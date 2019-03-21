package atlas.objects;

import org.joml.Vector3f;
import org.joml.Quaternionf;

import atlas.objects.entityComponents.EntityModel;

public class Entity {

	private EntityModel model = null;
	
	private Vector3f position = new Vector3f(0f,0f,0f);
	private Vector3f scale = new Vector3f(1f,1f,1f);
	public Quaternionf rotation = new Quaternionf();
	private int selectedAtlas = 0;

//	private final HashSet<Entity> children = new HashSet<Entity>();
	
	public Entity(EntityModel model) {
		this.model = model;
	}
		
	public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public Vector3f getScale() {return scale;}
    public void setScale(float scale) {this.scale.x = scale;this.scale.y = scale;this.scale.z = scale;}
    public void setScale(Vector3f scale) {this.scale = scale;}

    public EntityModel getModel() {return this.model;}
    public void setModel(EntityModel model) {this.model = model;}
	
	public void setSelectedTextureAtlas(int atl) {
		this.selectedAtlas  = atl;
	}
	public int getSelectedTextureAtlas() {
		return this.selectedAtlas;
	}

//	public boolean addChild(Entity e) {return children.add(e);}
//	public HashSet<Entity> getChildren() {return children;}
//	public boolean removeChild(Entity e) {return children.remove(e);}
}
