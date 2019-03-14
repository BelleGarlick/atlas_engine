package test;

import org.joml.Vector3f;

import atlas.engine.Scene;
import atlas.graphical.Texture;
import atlas.objects.Camera;
import atlas.objects.Entity;
import atlas.objects.Skybox;
import atlas.objects.Skybox.SkyboxTexture;
import atlas.objects.entityComponents.Material;
import atlas.objects.entityComponents.Mesh;
import atlas.objects.lights.PointLight;
import atlas.userInput.Keys;
import atlas.userInput.UserInput;
import atlas.utils.Loader;

public class TitleScene extends Scene {

	Entity t = null;
	PointLight p = null;
	
	@Override
	protected void init() {		
		p = new PointLight(new Vector3f(this.getCamera().getPosition()), new Vector3f(1f,1f,1f));
//		this.directionalLight.setIntensity(1);
		
		Mesh box = null;
		Texture normal = null;
		Texture texture = null;
		try {
			ClassLoader cl = Scene.class.getClassLoader();
			
			box = Loader.getMesh(cl, "crate/model.obj");
			normal = Loader.getTexture("crate/normal.png");
			texture = Loader.getTexture("crate/texture.png");
			


			//treeMesh = Loader.getMesh(cl, "lamp/ model.obj");
//			texture = new Material(Loader.getTexture("lamp/texture.png"));

//			UserInput.createCustomCursor("cursor.png",new Vector2f(0,0));
		}catch(Exception e){
			e.printStackTrace();
		}

		t = new Entity(box);
		t.getRotation().y = 90;
		Material m = new Material(texture);
		m.setNormalMap(normal);
		t.setMaterial(m);
		t.setPosition(6, 0, 0);
		t.setScale(0.03f);
		

//		Entity t2 = new Entity(box);
//		t2.getPosition().y = 5;
//		t.addChild(t2);
		

		this.addEntity(t);
		
		this.addPointLight(p);
		UserInput.disableCursor();
	}

	@Override
	public void update(float interval) {
		this.getSkybox().setRotation(this.getSkybox().getRotation() - 1 * interval);
		
//		p.setPosition(new Vector3f(this.getCamera().getPosition()));
		p.setColour(new Vector3f(1,1,1));
//		System.out.println(this.getPointLights().size());
//		System.out.println("======");
//		System.out.println("Colour: " + p.getColour());
//		System.out.println("Position: " + p.getPosition());
		p.setIntensity(1);
		atlas.objects.lights.ALight.Attenuation atten = new atlas.objects.lights.ALight.Attenuation();
		atten.linear = 0.5f;
		p.setAttenuation(atten);
		this.ambientLight.x = 0;
		this.ambientLight.y = 0;
		this.ambientLight.z = 0;
		
		t.getRotation().x =0;
		t.getRotation().y +=10 * interval;
		t.getRotation().z =0;
		
		Camera c = this.getCamera();
//		c.getRotation().y += UserInput.getDisplVec().x / 10;
//		c.getRotation().x += UserInput.getDisplVec().y / 10;
		
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
