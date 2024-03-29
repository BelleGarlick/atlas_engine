package atlas.renderer; 

import atlas.engine.Scene;
import atlas.engine.Window;
import atlas.graphical.Transformation;
import atlas.objects.Camera;

public class Renderer {

	Transformation transformation;

	private EntityRenderer entityRenderer = new EntityRenderer();
	private TerrainRenderer terrainRenderer = new TerrainRenderer();
	private SkyboxRenderer skyboxRenderer = new SkyboxRenderer();
	
	public Renderer() {
		transformation = new Transformation();
	}

	public void init() throws Exception {
		entityRenderer.init();  
		terrainRenderer.init();  
		skyboxRenderer.init();
	}
	
	public void render(Window window, Scene scene, Camera camera) {
		entityRenderer.render(scene, camera);
		terrainRenderer.render(window, scene, camera);
		skyboxRenderer.render(scene, camera);
	}
	
	public void cleanup() {
	    entityRenderer.cleanUp();
	    terrainRenderer.cleanUp();
	    skyboxRenderer.cleanUp();
	}
}
