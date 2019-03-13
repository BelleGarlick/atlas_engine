package joji;

import org.joml.Vector3f;

import atlas.engine.Scene;
import atlas.userInput.Keys;
import atlas.userInput.UserInput;
import joji.blocks.BlockTextures;
import joji.blocks.Blocks;
import joji.map.Map;

public class MainScene extends Scene {

	private Map m = null;
	
	@Override
	protected void init() throws Exception {
		Blocks.init();	
		m = new Map(this);
		
		UserInput.disableCursor();
		
		this.ambientLight = new Vector3f(1f,1f,1f);
		
		this.getCamera().getPosition().y = 40;
		this.getCamera().getPosition().x = -20;
	}
	
	@Override public void update(float interval) {
		m.update(interval, this);
		
		if (UserInput.keyDown(Keys.KEY_LEFT_CONTROL)) {
			System.out.println(this.getEntities().size());
		}
		
//		this.getCamera().getRotation().y += 10 * interval;
		this.getCamera().getRotation().x += UserInput.getDisplVec().y / 10;
		this.getCamera().getRotation().y += UserInput.getDisplVec().x / 10;

		float camRot = (float) (this.getCamera().getRotation().y / 180f * Math.PI);
		
		if (UserInput.keyDown(Keys.KEY_SPACE)) {
			this.getCamera().getPosition().y += 5 * interval;
		}
		if (UserInput.keyDown(Keys.KEY_LEFT_SHIFT)) {
			this.getCamera().getPosition().y -= 5 * interval;
		}

		if (UserInput.keyDown(Keys.KEY_W)) {
			this.getCamera().getPosition().x += 5 * interval * Math.cos(camRot);
			this.getCamera().getPosition().z += 5 * interval * Math.sin(camRot);
		}
		if (UserInput.keyDown(Keys.KEY_S)) {
			this.getCamera().getPosition().x -= 5 * interval;
		}

		if (UserInput.keyDown(Keys.KEY_A)) {
			this.getCamera().getPosition().z -= 5 * interval;
		}
		if (UserInput.keyDown(Keys.KEY_D)) {
			this.getCamera().getPosition().z += 5 * interval;
		}
	}

	@Override
	public void cleanup() {
		BlockTextures.cleanUp();
	}

}
