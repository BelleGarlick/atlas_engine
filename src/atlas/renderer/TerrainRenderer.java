package atlas.renderer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import atlas.engine.Engine;
import atlas.engine.Scene;
import atlas.engine.Window;
import atlas.objects.Camera;
import atlas.objects.Terrain;
import atlas.objects.entityComponents.Mesh;
import atlas.objects.lights.PointLight;
import atlas.objects.lights.SpotLight;

public class TerrainRenderer {

	public static float LOW_RENDER_DISTANCE = 800;
	public static float MIN_RENDER_DISTANCE = 1300;
	
	ShaderProgram shader;
	
	public void init() throws Exception {
		shader = new ShaderProgram("terrain.vs", "terrain.fs");
		shader.link();
		shader.createUniform("projectionMatrix");
		shader.createUniform("viewMatrix");
		shader.createUniform("modelMatrix");
		shader.createUniform("texture_sampler");
	    shader.createUniform("useTexture");
	    shader.createUniform("materialColor");
	    shader.createUniform("textureTiles");
	    shader.createUniform("reflectance");
	    
	    shader.createUniform("blendMaps");
	    for (int bm = 0; bm < 3; bm++) {  //max 3 blend maps
	    	shader.createUniform("blendMap["+bm+"].blendMap");
	    	shader.createUniform("blendMap["+bm+"].rtexture");
	    	shader.createUniform("blendMap["+bm+"].gtexture");
	    	shader.createUniform("blendMap["+bm+"].btexture");
	    }

	    
	    shader.createUniform("cameraPos");
	    shader.createUniform("spotLightCount");
	    shader.createUniform("pointLightCount");
	    shader.createSpotLightListUniform("spotLights", 16);
	    shader.createPointLightListUniform("pointLights", 16);
	    shader.createDirectionalLightUniform("directionalLight");
	    shader.createUniform("ambientLight");

	    shader.createFogUniform("fog");
	}

	public void render(Window window, Scene scene, Camera camera) {
		shader.bind();
		if (Engine.renderTerrainWireFrame) {GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);}
	    
		shader.setUniform("projectionMatrix", camera.getProjectionMatrix());
    	shader.setUniform("texture_sampler", 0);
		shader.setUniform("cameraPos", camera.getPosition());

		
		//Lights
		shader.setUniform("ambientLight", scene.ambientLight);
		shader.setUniform("directionalLight", scene.directionalLight);
		//Spot lights
		shader.setUniform("spotLightCount", scene.getSpotLights().size()); int spotLightCount = 0;
		for (SpotLight sl : scene.getSpotLights()) {
			shader.setUniform("spotLights", sl, spotLightCount);
			spotLightCount++;
		}
		//Point Lights
		shader.setUniform("pointLightCount", scene.getPointLights().size()); int pointLightCount = 0;
		for (PointLight pl : scene.getPointLights()) {
			shader.setUniform("pointLights", pl, pointLightCount);
			pointLightCount++;
		}

	    shader.setUniform("fog", scene.fog);
        
	    // Render each gameItem
        for(Terrain terrain : scene.getTerrains()) {
        	if (terrain.getVisibility()) {
	            // Set world matrix for this item

	        	shader.setUniform("viewMatrix", camera.getViewMatrix());
	        	shader.setUniform("modelMatrix", getModelMatrix(terrain.getPosition()));
	        	shader.setUniform("textureTiles", terrain.getTiling());
	            
	        	shader.setUniform("reflectance", terrain.getMaterial().getReflectance());
	        	
	            // Activate first texture unit
	            if (terrain.getMaterial().useTexture()) {
	            	shader.setUniform("useTexture", 1);
	            	GL13.glActiveTexture(GL13.GL_TEXTURE0);
	            	GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getMaterial().getTexture().getId());
	             
	            	shader.setUniform("blendMaps", terrain.getBlendMaps().size());
	            	int textureBinding = GL13.GL_TEXTURE1; int textureNumber = 1;
		            for (int bm = 0; bm < terrain.getBlendMaps().size(); bm++) {
		            	
		    	    	shader.setUniform("blendMap["+bm+"].blendMap", textureNumber);
		    	    	shader.setUniform("blendMap["+bm+"].rtexture", textureNumber+1);
		    	    	shader.setUniform("blendMap["+bm+"].gtexture", textureNumber+2);
		    	    	shader.setUniform("blendMap["+bm+"].btexture", textureNumber+3);
		    	    	
		    	    	int defaultMaterial = terrain.getMaterial().getTexture().getId();
		            	GL13.glActiveTexture(textureBinding);
		            	GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMaps().get(bm).getBlendMap().getId());
		    			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		    			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		    			
		            	GL13.glActiveTexture(textureBinding+1);
		            	GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMaps().get(bm).getRTexture()==null? defaultMaterial : terrain.getBlendMaps().get(bm).getRTexture().getId());
		            	
		            	GL13.glActiveTexture(textureBinding+2);
		            	GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMaps().get(bm).getGTexture()==null? defaultMaterial : terrain.getBlendMaps().get(bm).getGTexture().getId());
		            	
		            	GL13.glActiveTexture(textureBinding+3);
		            	GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMaps().get(bm).getBTexture()==null? defaultMaterial : terrain.getBlendMaps().get(bm).getBTexture().getId());
		            	
		            	textureBinding+=4;
		            	textureNumber+=4;
		            }
	            	
	            } else { 
	            	shader.setUniform("useTexture", 0);
	            	shader.setUniform("blendMaps", 0);
	            	shader.setUniform("materialColor", terrain.getMaterial().getColor());
	            }
	            
	            
	            Mesh terrainMesh = terrain.getMesh();
	            if (terrain.getDistance(camera.getPosition()) > LOW_RENDER_DISTANCE) {
	            	terrainMesh = terrain.getLowMesh();
	            }
	            if (terrain.getDistance(camera.getPosition()) > MIN_RENDER_DISTANCE) {
	            	terrainMesh = terrain.getMinMesh();
	            }
	            
	            renderMesh(terrainMesh);
        	}
        }

		if (Engine.renderTerrainWireFrame) {GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);}
        shader.unbind();
	}


	public void renderMesh(Mesh mesh) {
	    // Draw the mesh
	    GL30.glBindVertexArray(mesh.getVaoId());
	    GL20.glEnableVertexAttribArray(0);
	    GL20.glEnableVertexAttribArray(1);
	    GL20.glEnableVertexAttribArray(2);

	    GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

	    // Restore state
	    GL20.glDisableVertexAttribArray(0);
	    GL20.glDisableVertexAttribArray(1);
	    GL20.glDisableVertexAttribArray(2);
	    GL30.glBindVertexArray(0);
	}
	
	private Matrix4f getModelMatrix(Vector3f position) {
	    return (new Matrix4f()).identity().translate(position).
        rotateX(0).
	        rotateY(0).
	        rotateZ(0).
	        scale(1);
	}
	
	public void cleanUp() {
		if (shader != null) {
	    	shader.cleanUp();
	    }
	}
}
