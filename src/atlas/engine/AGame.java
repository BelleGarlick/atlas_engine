package atlas.engine;

import atlas.renderer.Renderer;

public abstract class AGame {

	private Scene scene;
	private Renderer renderer;
	
	public AGame() {
		renderer = new Renderer();
	}
	
	public final void init(Window window) throws Exception {
		renderer.init();
		
		//init sub class
		this.init();
	}	
	protected abstract void init();

	final void render(Window window){
		if (scene != null) {
			scene._render(window, renderer);
		} else {
			//must set scene on game init
		}
	}
	final void update(float interval) {
		if (scene != null) {
			scene.update(interval);
		}
	}
	
	public void setScene(Scene s) {
		if (this.scene != null) {
			this.scene.cleanup();
		}
		s._init(this);
		this.scene = s;
	}

	public void cleanup(){
		if (scene!=null) {
			this.scene.cleanup();
		}
		renderer.cleanup();
	}

	final void fixedUpdate() {
		if (scene!=null) {
			scene.fixedUpdate();			
		}
	}
	
}
