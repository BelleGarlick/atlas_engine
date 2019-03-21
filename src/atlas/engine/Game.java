package atlas.engine;

import atlas.renderer.Renderer;

public abstract class Game {

	private Scene scene;
	private Renderer renderer;
	
	public Game() {
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
			this.scene.cleanUp();
		}
		s._init(this);
		this.scene = s;
	}

	public void cleanUp(){
		if (scene!=null) {
			this.scene.cleanUp();
		}
		renderer.cleanUp();
	}

	final void fixedUpdate() {
		if (scene!=null) {
			scene.fixedUpdate();			
		}
	}
	
}
