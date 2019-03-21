package test;

import java.util.ArrayList;
import java.util.Random;

import org.joml.Vector3f;

import atlas.engine.Scene;
import atlas.objects.Entity;
import atlas.objects.Terrain;
import atlas.objects.entityComponents.BlendMap;
import atlas.objects.entityComponents.Material;
import atlas.objects.entityComponents.Mesh;
import atlas.objects.entityComponents.animation.AnimatedModel;
import atlas.objects.lights.PointLight;
import atlas.userInput.Keys;
import atlas.userInput.UserInput;
import atlas.utils.Loader;
import atlas.utils.MeshDefaults;
import atlas.utils.Noise;

public class SceneMain extends Scene {

	private Mesh treeMesh = null;
	private ArrayList<Entity> a = new ArrayList<>();
	private ArrayList<Entity> tree = new ArrayList<>();
//	private Material treeMaterial = null;

	private Mesh fernMesh = null;
	private Mesh lampMesh = null;
	private Mesh barrelMesh = null;
//	private Mesh crateMesh = null;
	
	Terrain t = null;
	private Entity player; 
	
	private Entity dragon;
	
	@Override
	public void init() {
		this.directionalLight.setIntensity(0);
		
//		UserInput.clearCustomCursor();

//		Camera c1 = new Camera();
//		c1.viewport = new Vector4f(0f,0.0f,0.5f,1f);
//		Camera c2 = new Camera();
//		c2.viewport = new Vector4f(0.5f,0.0f,0.5f,1f);
//		this.cameras.clear();
//		this.cameras.add(c1);
//		this.cameras.add(c2);

		
//		c1.getRotation().x = 20f;
//		c1.getPosition().y = 10;
//		c1.getPosition().z = 20;
		

//		this.cameras.get(0).getRotation().x = 20f;
//		this.cameras.get(0).getPosition().y = 10;
//		this.cameras.get(0).getPosition().z = 20;
		
		this.cameras.get(0).getPosition().y = 1;
		this.cameras.get(0).getPosition().z = 0f;
		
		Noise n1 = new Noise();
		Noise n2 = new Noise();
		Noise n3 = new Noise();
		int size = 100;
		float[][] heights = new float[size][size];
		for (int i = 0; i < size; i++) {
			float[] row = new float[size];
			for (int j = 0; j < size; j++) {
				float mountain = (float) n1.eval(i/100f, j/100f);
				if (mountain > 0.4) {
					mountain -= 0.4;
				} else { mountain = 0; }
				row[j] = (float) ((mountain * 30) + 
						(n2.eval(i/100f, j/100f) * 5) +
						(n3.eval(i/10f, j/10f) * 3));
			}
			heights[i] = row;
		}

		Material treeTexture = null;
		Material lampTexture = null;
//		Material crateTexture = null;
		Material fernTexture = null;
		Material terrain = null;
		
		AnimatedModel anim = null;

		BlendMap bm= null;
		BlendMap bm2= null;
			try {
				treeMesh = Loader.getMesh("model.obj");treeMesh.setRenderBothSides(true);
				lampMesh = Loader.getMesh("test/lamp/model.obj");
				barrelMesh = Loader.getMesh("test/barrel/model.obj");
//				crateMesh = Loader.getMesh("crate/model.obj");
				fernMesh = Loader.getMesh("test/crate/model.obj");
//				treeMesh = MeshDefaults.loadBox();
				
				anim = Loader.getAnimatedModel("test/players/run/animation.dae");
				
			    fernTexture = new Material(Loader.getTexture("test/crate/texture.png"));//fernTexture.getTexture().setAtlasSize(2);
				lampTexture = new Material(Loader.getTexture("test/lamp/texture.png"));
//				crateTexture = new Material(Loader.getTexture("crate/texture.png"));
				treeTexture = new Material(Loader.getTexture("texture.png"));treeTexture.getTexture().setAtlasSize(2);
								
				terrain = new Material(Loader.getTexture("1.png"));
				bm = new BlendMap(Loader.getTexture("blendmap.png"),Loader.getTexture("2.png"),Loader.getTexture("3.png"),Loader.getTexture("4.png"));
				bm2 = new BlendMap(Loader.getTexture("blendmap2.png"));
				bm2.setBTexture(Loader.getTexture("5.png"));
				
				
				Mesh m = MeshDefaults.loadDragon();
				dragon = new Entity(m);
				m.material.setReflectance(1);
				dragon.setPosition(0, 10, 0);
				this.addEntity(dragon);
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			
			t = new Terrain(heights, 100, 100);
			t.setPosition(new Vector3f(-50,0,-50));
			t.setMaterial(terrain);
			t.addBlendMap(bm);
			t.addBlendMap(bm2);
			t.setTiling(300);
			this.addTerrain(t);



			int cloestTree = 10, furthestTree = 20;
			treeMesh.material = treeTexture;treeTexture.setReflectance(1f);
			for (int i = 0; i < 100; i++) {
				float angle = (float) (Math.random() * 2 * Math.PI);
				float r = (float) ((Math.random() * (furthestTree - cloestTree))  + cloestTree);
				float x = (float) (Math.sin(angle) * r);
				float z = (float) (Math.cos(angle) * r);
				float y = t.getHeightOfTerrain(x, z);
				
				Entity tree = new Entity(treeMesh);
				tree.setSelectedTextureAtlas((new Random()).nextInt(4));
				tree.setScale(0.3f);
				tree.setPosition(x, y, z);
				this.tree.add(tree);
				this.addEntity(tree);
			}

			lampMesh.material = lampTexture;
			int cloestlamp = 5, furthestlamp = 15;
			for (int i = 0; i < 20; i++) {
				float angle = (float) (Math.random() * 2 * Math.PI);
				float r = (float) ((Math.random() * (furthestlamp - cloestlamp))  + cloestlamp);
				float x = (float) (Math.sin(angle) * r);
				float z = (float) (Math.cos(angle) * r);
				float y = t.getHeightOfTerrain(x, z);
				
				Entity lamp = new Entity(lampMesh);
				lamp.setScale(0.08f);
				lamp.setPosition(x, y, z);
				this.addEntity(lamp);
			}

			int furthestcrate = 10;
			for (int i = 0; i < 20; i++) {
				float angle = (float) (Math.random() * 2 * Math.PI);
				float r = (float) ((Math.random() * (furthestcrate)));
				float x = (float) (Math.sin(angle) * r);
				float z = (float) (Math.cos(angle) * r);
				float y = t.getHeightOfTerrain(x, z);
				
//				Entity crate = new Entity(crateMesh);
//				crate.setMaterial(crateTexture);
//				crate.setScale(0.002f);
//				crate.setPosition(x, (y + 0.2f), z);
//				this.addEntity(crate);
				

				Entity a = new Entity(anim);
				a.setPosition(x, (y + 0.2f), z);
				a.setScale(0.3f);
//				a.setRotation(0, (float)(Math.random() * 360), 0);
				this.a.add(a);
				this.addEntity(a);
			}

			fernMesh.material = fernTexture;
			for (int i = 0; i < 200; i++) {
				float angle = (float) (Math.random() * 2 * Math.PI);
				float r = (float) ((Math.random() * (50)));
				float x = (float) (Math.sin(angle) * r);
				float z = (float) (Math.cos(angle) * r);
				float y = t.getHeightOfTerrain(x, z);
				
				Entity crate = new Entity(fernMesh);
				crate.setScale(0.002f);
				crate.setPosition(x, (y + 0.2f), z);
				this.addEntity(crate);
			}
			
			player = new Entity(barrelMesh);

			player.setScale(0.03f);
			this.addEntity(player);

			UserInput.disableCursor();
//			Engine.renderTerrainWireFrame = true;
			
			PointLight pl = new PointLight(new Vector3f(10,t.getHeightOfTerrain(10, 10)+3,10), new Vector3f(0,1,1));
			this.addPointLight(pl);
			
			PointLight pl2 = new PointLight(new Vector3f(-10,t.getHeightOfTerrain(-10, 10)+3,10), new Vector3f(1,0,1));
			this.addPointLight(pl2);
			
	}

	@Override
	public void update(float interval) {
//		pl.setPosition(this.getCamera().getPosition());
//		pl.setIntensity(0.4f);
		
		if (UserInput.getControllers().size() > 0 && UserInput.getControllers().get(0).isRightTriggerPressed()) {
			dragon.rotation.y += 40 * interval;
		}
//		
//			Engine.renderEntityWireFrame = UserInput.getControllers().get(0).isLeftTriggerPressed();
		
		if (this.a.get(0).getModel() instanceof AnimatedModel) {
			((AnimatedModel)(this.a.get(0).getModel())).update(interval);
		}
//		for (Entity a : this.a) {
//			a.getRotation().y += 80 * interval;
//		}
//		this.entities.get(0).setMaterial(new Material(new Vector3f(0.4f,0.9f,1f)));
//		System.out.println(this.entities.size());
		float camRot = this.cameras.get(0).getRotation().y;
		

		if (UserInput.keyDown(Keys.KEY_W)) {
			this.player.getPosition().x += 5 * interval * Math.cos(camRot);
			this.player.getPosition().z += 5 * interval * Math.sin(camRot);
		}
		if (UserInput.keyDown(Keys.KEY_S)) {
			this.player.getPosition().x -= 5 * interval * Math.cos(camRot);
			this.player.getPosition().z -= 5 * interval * Math.sin(camRot);
		}
		
		if (UserInput.keyDown(68)) {
			this.player.rotation.y -= 60 * interval;
		} 
		if (UserInput.keyDown(65)) {
			this.player.rotation.y += 40 * interval;
		}
		if (UserInput.keyDown(340) || UserInput.keyDown(344)) {
			this.player.getPosition().y -= 4 * interval;
		}
		if (UserInput.keyDown(32)) {
			this.player.getPosition().y += 4 * interval;
		}
		if (UserInput.getControllers().size() > 0) {
			this.player.rotation.y -= 60 * interval *  UserInput.getControllers().get(0).getRightJoyStickHorz();

			float leftVert = UserInput.getControllers().get(0).getLeftJoyStickVert();
			if (Math.abs(leftVert) > 0.01) {
				this.player.getPosition().x -= leftVert * 5 * interval * Math.sin(camRot);
				this.player.getPosition().z += leftVert * 5 * interval * Math.cos(camRot);
			}
			
			float leftHorz = UserInput.getControllers().get(0).getLeftJoyStickHorz();
			if (Math.abs(leftHorz) > 0.01) {
				this.player.getPosition().x += leftHorz * 5 * interval * Math.cos(camRot);
				this.player.getPosition().z += leftHorz * 5 * interval * Math.sin(camRot);
			}
			
			float rightVert = UserInput.getControllers().get(0).getRightJoyStickVert();
			if (Math.abs(rightVert) > 0.01) {
				this.cameras.get(0).getRotation().x += rightVert* 2;
			}
			if (UserInput.getControllers().get(0).isOptionsButtonPressed()) {
				game.setScene(new TitleScene());
			}
		}
//		this.cameras.get(0).getPosition().x = -1;
//		this.cameras.get(1).getPosition().x = 1;
//		this.cameras.get(0).s
		
//		game.setScene(new TitleScene());
		
//		float height = t.getHeightOfTerrain(player.getPosition().x, player.getPosition().z);
//		if (player.getPosition().y > height) {
//			player.getPosition().y -= interval * 5;
//		} 
//		if (player.getPosition().y < height) { 
//			player.getPosition().y = t.getHeightOfTerrain(player.getPosition().x, player.getPosition().z);
//		}

		player.rotation.y -= UserInput.getDisplVec().x / 10;
		this.getCamera().getRotation().x += UserInput.getDisplVec().y / 10;
		this.cameras.get(0).setPosition(new Vector3f(player.getPosition()));
		this.cameras.get(0).getPosition().y += 0.5f;
		this.cameras.get(0).getRotation().y = (float) (-player.rotation.y+Math.PI);
//		this.cameras.get(0).getRotation().x = (float) (0.5f/Math.PI);

//		player.getRotation().y -= UserInput.getDisplVec().x * interval * 6;
//		this.cameras.get(0).getRotation().x += UserInput.getDisplVec().y * interval * 10;
		
//		if (UserInput.keyDown(Keys.KEY_Q)) {
//			this.pointLight = new PointLight(new Vector3f(1,1,1), new Vector3f(this.getCamera().getPosition()));
//			this.pointLight.setIntensity(4);
//			this.pointLight.getAttenuation().linear = 0.3f;
//		}
		if (UserInput.keyDown(Keys.KEY_0)) {
			this.ambientLight.x -= 0.5 * interval;
			this.ambientLight.y -= 0.5 * interval;
			this.ambientLight.z -= 0.5 * interval;
		}
		if (UserInput.keyDown(Keys.KEY_9)) {
			this.ambientLight.x += 0.5 * interval;
			this.ambientLight.y += 0.5 * interval;
			this.ambientLight.z += 0.5 * interval;
		}
	}

	int i=0;
	@Override
	public void fixedUpdate() {
		i++;
		if (i > 8) {
			i = 0;
			for (Entity t : this.tree) { 
				t.setSelectedTextureAtlas(t.getSelectedTextureAtlas() + 1);
			}
		}
	}

	@Override
	public void cleanUp() {
		
	}

	
	
	
}
