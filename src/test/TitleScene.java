package test;

import org.joml.Vector3f;

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
import atlas.utils.MeshDefaults;
import atlas.utils.Noise;

public class TitleScene extends Scene {

	Entity e = null;
	float time = 0;
	
	@Override
	protected void init() throws Exception {		

		Mesh box = null;
		Texture normal = null;
		Texture texture = null;
			ClassLoader cl = Scene.class.getClassLoader();
//			
			box = Loader.getMesh(cl, "barrel/model.obj");
//			normal = Loader.getTexture("barrel/normal.png");
			texture = Loader.getTexture("barrel/texture.png");
			
			e = new Entity(MeshDefaults.loadIsoSphere());
//		e.setMaterial(new Material(texture));
//		e.getMaterial().setNormalMap(normal);
		e.setScale(1f);
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
	}

	@Override
	public void update(float interval) {
		time += interval;
		this.skybox.setSkyboxOverlayAlpha(((float)-Math.cos(time) + 1) / 2f);
		this.skybox.setRotation(this.skybox.getRotation() + 10 * interval);
		e.getRotation().y += 100 * interval;
		
		Camera c = this.getCamera();
		c.getRotation().y += UserInput.getDisplVec().x;
		c.getRotation().x += UserInput.getDisplVec().y;
		
		float camRot = (float) (c.getRotation().y / 180 * Math.PI);
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

		if (UserInput.keyDown(Keys.KEY_RIGHT_SHIFT)) {c.getPosition().y += 2 * interval;}
		if (UserInput.keyDown(Keys.KEY_LEFT_SHIFT)) {c.getPosition().y -= 2 * interval;}

		if (UserInput.keyDown(Keys.KEY_PAGE_UP)) {Camera.FOV += 10 * interval;}
		if (UserInput.keyDown(Keys.KEY_PAGE_DOWN)) {Camera.FOV -= 10 * interval;}
		
		
		if (UserInput.keyDown(Keys.KEY_SPACE)) {
			game.setScene(new SceneMain());
		}
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

}
