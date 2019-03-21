package atlas.renderer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.nio.FloatBuffer;
import java.util.HashSet;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL33.*;

import atlas.engine.Engine;
import atlas.engine.Scene;
import atlas.objects.Camera;
import atlas.objects.Entity;
import atlas.objects.entityComponents.BillboardMesh;
import atlas.objects.entityComponents.EntityModel;
import atlas.objects.entityComponents.InstancedMesh;
import atlas.objects.entityComponents.Mesh;
import atlas.objects.entityComponents.animation.AnimatedModel;
import atlas.objects.lights.PointLight;
import atlas.objects.lights.SpotLight;

public class EntityRenderer {

	ShaderProgram shader;
	
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int MATRIX_SIZE_FLOATS = 4 * 4;    
    private static final int MATRIX_SIZE_BYTES = MATRIX_SIZE_FLOATS * FLOAT_SIZE_BYTES;
    private static final int INSTANCE_SIZE_BYTES = MATRIX_SIZE_BYTES * 2 + FLOAT_SIZE_BYTES * 2;    
    private static final int INSTANCE_SIZE_FLOATS = MATRIX_SIZE_FLOATS * 2 + 2;
	
	public void init() throws Exception {
		shader = new ShaderProgram("object.vs", "object.fs");
		shader.link();
		shader.createUniform("projectionMatrix");
		shader.createUniform("modelViewMatrix");
		shader.createUniform("viewMatrix");
		
		shader.createUniform("texture_sampler");
	    shader.createMaterialUniform("material");
	    
	    shader.createUniform("atlas_size");  
	    shader.createUniform("atlas_selected");  
	    shader.createUniform("jointsMatrix");
	    
	    shader.createUniform("instanced");
	    
	    shader.createUniform("cameraPos");
	    shader.createUniform("spotLightCount");
	    shader.createUniform("pointLightCount");
	    shader.createSpotLightListUniform("spotLights", 16);
	    shader.createPointLightListUniform("pointLights", 16);
	    shader.createDirectionalLightUniform("directionalLight");
	    shader.createUniform("ambientLight");
	    
	    shader.createFogUniform("fog");
	}

	
	public void render(Scene scene, Camera camera) {
		shader.bind();
		if (Engine.renderEntityWireFrame) {GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);}
		shader.setUniform("projectionMatrix", camera.getProjectionMatrix());
		shader.setUniform("viewMatrix", camera.getViewMatrix());

		shader.setUniform("cameraPos", new Vector3f());

		
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
        for(EntityModel em : scene.getEntities().keySet()) {
        	HashSet<Entity> entities = scene.getEntities().get(em);
        	renderEntitySet(camera, em, entities);
//        	renderEntityTree(camera, entity, new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(1,1,1));
        }
        
		if (Engine.renderEntityWireFrame) {GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);}
        shader.unbind();
	}
	
	
	private void renderEntitySet(Camera camera, EntityModel em , HashSet<Entity> entities) {
		//Bind Information per set of instances
    	shader.setUniform("texture_sampler", 0);       
    	
        // Activate first texture unit
        if (em.material.useTexture()) {
    	    shader.setUniform("atlas_size", em.material.getTexture().getAtlasSize());  
        	GL13.glActiveTexture(GL13.GL_TEXTURE0);
        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, em.material.getTexture().getId());
        } 
        shader.setUniform("material", em.material);

        
        if (em instanceof InstancedMesh) {
        	shader.setUniform("instanced", 1);
        	renderInstancedBatch(camera, em, entities);
        } else {
        	shader.setUniform("instanced", 0);
    		for (Entity entity : entities) {
    			renderEntity(camera, entity);
    		}
        }
	}
	
	private void renderEntity(Camera camera, Entity entity) {
		//Calc model view matrix
		Matrix4f viewMatrix = new Matrix4f(camera.getViewMatrix());
		Matrix4f modelMatrix = getModelMatrix(entity.getPosition(), entity.rotation, entity.getScale());
		Matrix4f mvm = this.getModelViewMatrix(viewMatrix, modelMatrix, entity.getModel());
		
		shader.setUniform("modelViewMatrix", mvm);
	    shader.setUniform("atlas_selected", entity.getSelectedTextureAtlas());  

    	if (entity.getModel() instanceof AnimatedModel) {
            AnimatedModel animGameItem = (AnimatedModel) entity.getModel();
            Matrix4f[] frame = animGameItem.getCurrentAnimation().getJointMatricies();
            shader.setUniform("jointsMatrix", frame);
            renderMesh(animGameItem.meshes[0]);
    	} else {
    		renderMesh((Mesh)entity.getModel());
    	}
	}
	
	private void renderMesh(Mesh mesh) {
        // Draw the mesh
        glBindVertexArray(mesh.getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glEnableVertexAttribArray(4);
        
        if (mesh.getRenderBothSides()){
        	GL11.glDisable(GL11.GL_CULL_FACE);
        }

	    GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

	    // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(4);
        glBindVertexArray(0);

    	GL11.glEnable(GL11.GL_CULL_FACE);
        glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	private void renderInstancedBatch(Camera camera, EntityModel em, HashSet<Entity> entities) {
		// Draw the mesh
        glBindVertexArray(((InstancedMesh)em).getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glEnableVertexAttribArray(4);

        glEnableVertexAttribArray(5);
        glEnableVertexAttribArray(6);
        glEnableVertexAttribArray(7);
        glEnableVertexAttribArray(8);
//        glEnableVertexAttribArray(9);
        
        if (((InstancedMesh)em).getRenderBothSides()){
        	GL11.glDisable(GL11.GL_CULL_FACE);
        }
        
        
        
        
		FloatBuffer instanceDataBuffer = ((InstancedMesh)em).instanceDataBuffer;
	    int i = 0;	    
	    for (Entity entity : entities) {
	        Matrix4f modelMatrix = getModelMatrix(entity.getPosition(), entity.rotation, entity.getScale());
            Matrix4f modelViewMatrix = this.getModelViewMatrix(camera.getViewMatrix(), modelMatrix, em);
            
            modelViewMatrix.get(INSTANCE_SIZE_FLOATS  * i, instanceDataBuffer);
            
	        i++;
	    }
	    glBindBuffer(GL_ARRAY_BUFFER, ((InstancedMesh)em).instanceDataVBO);
	    glBufferData(GL_ARRAY_BUFFER, instanceDataBuffer, GL_DYNAMIC_DRAW);
	    glDrawElementsInstanced(GL_TRIANGLES, ((InstancedMesh)em).getVertexCount(), GL_UNSIGNED_INT, 0, entities.size());
	    glBindBuffer(GL_ARRAY_BUFFER, 0);

	    // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(4);

        glDisableVertexAttribArray(5);
        glDisableVertexAttribArray(6);
        glDisableVertexAttribArray(7);
        glDisableVertexAttribArray(8);
//        glDisableVertexAttribArray(9);
        glBindVertexArray(0);

    	GL11.glEnable(GL11.GL_CULL_FACE);
        glBindTexture(GL_TEXTURE_2D, 0);
	}

	private Matrix4f getModelMatrix(Vector3f pos, Quaternionf rot, Vector3f scale) {
	    Matrix4f modelMatrix = new Matrix4f();
	    modelMatrix.identity().translate(pos).
	    	rotate(rot).
	        scale(scale);
		return modelMatrix;
	}
	
	private Matrix4f getModelViewMatrix(Matrix4f viewMatrix, Matrix4f modelMatrix, EntityModel em) {
		Matrix4f mvm = viewMatrix.mul(modelMatrix);
		if (em instanceof BillboardMesh) {
			mvm.m01(0f);mvm.m02(0f);
			mvm.m20(0f);mvm.m21(0f);
			mvm.m00(1);mvm.m22(1f);
		}
		return mvm;
	}

	public void cleanUp() {
		if (shader != null) {
	    	shader.cleanUp();
	    }
	}
}
