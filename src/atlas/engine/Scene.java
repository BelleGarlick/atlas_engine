package atlas.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import atlas.objects.Camera;
import atlas.objects.Entity;
import atlas.objects.Fog;
import atlas.objects.Skybox;
import atlas.objects.Terrain;
import atlas.objects.entityComponents.EntityModel;
import atlas.objects.lights.DirectionalLight;
import atlas.objects.lights.PointLight;
import atlas.objects.lights.SpotLight;
import atlas.objects.particles.ParticleEmitter;
import atlas.renderer.Renderer;
import atlas.utils.Loader;

public abstract class Scene {

	protected Game game;
	protected ArrayList<Camera> cameras = new ArrayList<>();
	
	private HashMap<EntityModel, HashSet<Entity>> entities = new HashMap<EntityModel, HashSet<Entity>>();
	private HashSet<Terrain> terrains = new HashSet<>();
	
	//max 16 lights
	private HashSet<SpotLight> spotLights = new HashSet<>();
	private HashSet<PointLight> pointLights = new HashSet<>();
	public Vector3f ambientLight = new Vector3f(0.1f,0.1f,0.1f);
	public DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1,1,1), new Vector3f(1,1,0));
	
	public Skybox skybox = null;
	public Fog fog = new Fog(new Vector3f(0.27f,0.64f,0.84f));
	
	public HashSet<ParticleEmitter> particleEmitters = new HashSet<>();
	
	public final void _init(Game game) {
		try {
			this.game = game;
			
			Camera c = new Camera();
			this.cameras.add(c);
			
			String side = "skyboxDefault/sides.png";
			String top = "skyboxDefault/top.png";
			String bottom = "skyboxDefault/bottom.png";
			this.skybox = new Skybox(Loader.getSkyboxTexture(
						side,
						side,
						top,
						bottom,
						side,
						side
					));

			this.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected abstract void init() throws Exception;

	public void update(float interval){}
	
	public void fixedUpdate(){}
	
	public final void _render(Window window, Renderer renderer) {
		GL11.glClearColor(0f, 0f, 0f, 1f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		for (Camera camera : cameras) {
			camera.updateProjectionMatrix(window);
			camera.updateViewMatrix();
			GL11.glViewport((int)(camera.viewport.x * window.getWidth()),
					(int)(camera.viewport.y * window.getHeight()),
					(int)(camera.viewport.z * window.getWidth()),
					(int)(camera.viewport.w * window.getHeight()));
			renderer.render(window, this, camera);
		}
				
	}

	public abstract void cleanUp();

	
	public void addEntity(Entity e) {
		if (entities.containsKey(e.getModel())) {
			this.entities.get(e.getModel()).add(e);
		} else {
			HashSet<Entity> eList = new HashSet<>();
			eList.add(e);
			this.entities.put(e.getModel(), eList);
		}
	}
	public void removeEntity(Entity e) {
		if (this.entities.containsKey(e.getModel())) {
			HashSet<Entity> eList = entities.get(e.getModel());
			eList.remove(e);
			if (eList.isEmpty()) {
				this.entities.remove(e.getModel());
			}
		} 
	}
	public HashMap<EntityModel, HashSet<Entity>> getEntities() {return this.entities;}

	public Camera getCamera() {return this.cameras.get(0);}
	
	public boolean addTerrain(Terrain t) {return this.terrains.add(t);}
	public boolean removeTerrain(Terrain t) {return this.terrains.remove(t);}
	public HashSet<Terrain> getTerrains() {return this.terrains;}
	
	//Add lighting 
	public boolean addSpotLight(SpotLight sl) {return this.spotLights.add(sl);}
	public boolean removeSpotLight(SpotLight sl) {return this.spotLights.remove(sl);}
	public HashSet<SpotLight> getSpotLights() {return this.spotLights;}
	
	public boolean addPointLight(PointLight pl) {return this.pointLights.add(pl);}
	public boolean removePointLight(PointLight pl) {return this.pointLights.remove(pl);}
	public HashSet<PointLight> getPointLights() {return this.pointLights;}

	public Skybox getSkybox() {
		return this.skybox;
	}
}
