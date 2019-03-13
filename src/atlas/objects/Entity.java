package atlas.objects;

import java.util.HashSet;

import org.joml.Vector3f;

import atlas.objects.entityComponents.Material;
import atlas.objects.entityComponents.Mesh;
import atlas.objects.entityComponents.animation.AnimatedModel;

public class Entity {

	private Mesh mesh = null; private AnimatedModel animModel = null; private boolean animated = false;
	private Material material = new Material();
	
	private Vector3f position = new Vector3f(0f,0f,0f);
	private Vector3f scale = new Vector3f(1f,1f,1f);
	private Vector3f rotation = new Vector3f(0f,0f,0f);
	private int selectedAtlas = 0;

	private final HashSet<Entity> children = new HashSet<Entity>();
	
	public Entity(Mesh mesh) {
		this.mesh = mesh;
	}
	public Entity(AnimatedModel animModel) {
		this.animModel = animModel;
		this.animated = true;
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

    
    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public boolean animated() {return this.animated;}
    public Mesh getMesh() {return mesh;}
    public AnimatedModel getAnimation() {return this.animModel;}
    public void setMesh(Mesh m) {this.mesh = m; this.animModel = null; this.animated = false;}
    public void seAnimation(AnimatedModel m) {this.animModel = m; this.mesh = null; this.animated = true;}

	public Material getMaterial() {
		return this.material;
	}

	public void setMaterial(Material material) {this.material = material;}
	
	public void setSelectedTextureAtlas(int atl) {
		this.selectedAtlas  = atl;
	}
	public int getSelectedTextureAtlas() {
		return this.selectedAtlas;
	}

	public boolean addChild(Entity e) {return children.add(e);}
	public HashSet<Entity> getChildren() {return children;}
	public boolean removeChild(Entity e) {return children.remove(e);}
}
