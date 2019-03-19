package test;

import org.joml.Vector3f;

import atlas.audio.Audio;
import atlas.audio.Sound;
import atlas.audio.sources.PointSoundSource;
import atlas.audio.sources.RelativeSoundSource;
import atlas.engine.Scene;
import atlas.graphical.Texture;
import atlas.objects.Camera;
import atlas.objects.Entity;
import atlas.objects.Terrain;
import atlas.objects.entityComponents.Material;
import atlas.objects.entityComponents.Mesh;
import atlas.objects.lights.PointLight;
import atlas.userInput.Keys;
import atlas.userInput.UserInput;
import atlas.utils.Loader;
import atlas.utils.Noise;

public class TitleScene extends Scene {

	Entity e = null;
	float time = 0;

	PointSoundSource pss = null;
	@Override
	protected void init() throws Exception {		
		
		Mesh box = Loader.getMesh("test/barrel/model.obj");
		Texture normal = Loader.getTexture("test/barrel/normal.png");
		Texture texture = Loader.getTexture("test/barrel/texture.png");
			
		e = new Entity(box);
		e.setMaterial(new Material(texture));
		e.getMaterial().setNormalMap(normal);
		e.setScale(0.1f);
		e.getPosition().x = 10;
		this.addEntity(e);
		
		float[][] heights = new float[400][400];
		Noise n = new Noise();
		for (int x = 0; x < 400; x++) {
			for (int y = 0; y < 400; y++) {
				heights[x][y] = (float) n.eval(x/20f, y/20f) * 10 + (float) n.eval(x/4f, y/4f) * 2;
			}
		}
		
		Terrain t = new Terrain(heights, 200, 200);
		t.setPosition(new Vector3f(-100, -10f, -100));
		t.setMaterial(new Material(new Vector3f(1,1,1)));
		t.getMaterial().setReflectance(1f);
		this.addTerrain(t);
		
		this.directionalLight.setIntensity(1);
		
		PointLight pl1 = new PointLight(new Vector3f(30, -9f, 20), new Vector3f(1,1,0));
		this.addPointLight(pl1);
		
		PointLight pl2 = new PointLight(new Vector3f(30, -9f, -20), new Vector3f(0,1,1));
		this.addPointLight(pl2);
		
		
		this.skybox.setSkyboxOverlay(Loader.getSkyboxTexture("right.png", "left.png", "top.png", "bottom.png", "back.png", "front.png"), 0f);
		
		UserInput.disableCursor();

		
		Sound background = Loader.getSound("test/sounds/background/lost-paradise.ogg");
		RelativeSoundSource rss = new RelativeSoundSource(background);
		rss.setVolume(0.05f);
		rss.setLooping(true);
		rss.play();
		

		
		Sound run = Loader.getSound("test/sounds/sfx/fire.ogg");
		pss = new PointSoundSource(run, new Vector3f(10,0,0));
		pss.setVolume(0.1f);
		pss.setLooping(true);
		pss.play();
	}

	@Override
	public void update(float interval) {
		Audio.listener.updateListenerPosition(this.getCamera().getPosition(), this.getCamera().getRotation());
		
		time += interval;
		this.skybox.setSkyboxOverlayAlpha(((float)-Math.cos(time) + 1) / 2f);
		this.skybox.setRotation(this.skybox.getRotation() + 10 * interval);
		e.getRotation().y += 10 * interval;
		
		Camera c = this.getCamera();
		c.getRotation().y += UserInput.getDisplVec().x/100;
		c.getRotation().x += UserInput.getDisplVec().y/100;
		
		float camRot = c.getRotation().y;
		if (UserInput.keyDown(Keys.KEY_W)) {
			c.getPosition().x += Math.cos(camRot) * interval * 10;
			c.getPosition().z += Math.sin(camRot) * interval * 10;
		}
		if (UserInput.keyDown(Keys.KEY_A)) {
			c.getPosition().x += Math.sin(camRot) * interval * 10;
			c.getPosition().z -= Math.cos(camRot) * interval * 10;
		}
		if (UserInput.keyDown(Keys.KEY_S)) {
			c.getPosition().x -= Math.cos(camRot) * interval * 10;
			c.getPosition().z -= Math.sin(camRot) * interval * 10;
		}
		if (UserInput.keyDown(Keys.KEY_D)) {
			c.getPosition().x -= Math.sin(camRot) * interval * 10;
			c.getPosition().z += Math.cos(camRot) * interval * 10;
		}

		if (UserInput.keyDown(Keys.KEY_SPACE)) {c.getPosition().y += 2 * interval;}
		if (UserInput.keyDown(Keys.KEY_LEFT_SHIFT)) {c.getPosition().y -= 2 * interval;}

		if (UserInput.keyDown(Keys.KEY_PAGE_UP)) {Camera.FOV += 10 * interval;}
		if (UserInput.keyDown(Keys.KEY_PAGE_DOWN)) {Camera.FOV -= 10 * interval;}
		
		if (UserInput.keyDown(Keys.KEY_ENTER)) {
			game.setScene(new SceneMain());
		}
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}

}
