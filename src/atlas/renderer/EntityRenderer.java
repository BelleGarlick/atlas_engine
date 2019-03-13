package atlas.renderer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import atlas.engine.Engine;
import atlas.engine.Scene;
import atlas.objects.Camera;
import atlas.objects.Entity;
import atlas.objects.entityComponents.BillboardMesh;
import atlas.objects.entityComponents.Mesh;
import atlas.objects.entityComponents.animation.AnimatedModel;
import atlas.objects.lights.PointLight;
import atlas.objects.lights.SpotLight;

public class EntityRenderer {

	ShaderProgram shader;
	
	public void init() throws Exception {
		shader = new ShaderProgram("object.vs", "object.fs");
		shader.link();
		shader.createUniform("projectionMatrix");
		shader.createUniform("modelViewMatrix");
		shader.createUniform("modelMatrix");
		
		shader.createUniform("texture_sampler");
	    shader.createMaterialUniform("material");
	    
	    shader.createUniform("atlas_size");  
	    shader.createUniform("atlas_selected");  
	    shader.createUniform("jointsMatrix");
	    
	    shader.createUniform("cameraPos");
	    shader.createUniform("spotLightCount");
	    shader.createUniform("pointLightCount");
	    shader.createSpotLightListUniform("spotLights", 16);
	    shader.createPointLightListUniform("pointLights", 16);
	    shader.createDirectionalLightUniform("directionalLight");
	    shader.createUniform("ambientLight");
	}

	public void render(Scene scene, Camera camera) {
		shader.bind();
		if (Engine.renderEntityWireFrame) {GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);}
		shader.setUniform("projectionMatrix", camera.getProjectionMatrix());
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
		
		
	    // Render each gameItem
        for(Entity entity : scene.getEntities()) {
        	renderEntityTree(camera, entity, new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(1,1,1));
        }
        
		if (Engine.renderEntityWireFrame) {GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);}
        shader.unbind();
	}
	
	private void renderEntityTree(Camera camera, Entity entity, Vector3f inheritedPosition, Vector3f inheritedRotation, Vector3f inheritedScale) {
		Vector3f pos = new Vector3f(inheritedPosition);
		Vector3f rot = new Vector3f(inheritedRotation);
		Vector3f scale = inheritedScale;

		pos.add(entity.getPosition());
		rot.add(entity.getRotation());
		scale.x *= entity.getScale().x;
		scale.y *= entity.getScale().y;
		scale.z *= entity.getScale().z;
		renderEntity(camera, entity, pos, rot, scale);

		for (Entity subTree : entity.getChildren()) {
			renderEntityTree(camera, subTree, pos, rot, scale);
		}
	}
	
	private void renderEntity(Camera camera, Entity entity, Vector3f pos, Vector3f rot, Vector3f scale) {
		//Calc model view matrix
		Matrix4f viewMatrix = new Matrix4f(camera.getViewMatrix());
		Matrix4f modelMatrix = getModelMatrix(pos, rot, scale);
		shader.setUniform("modelMatrix", modelMatrix);
//		viewMatrix.transpose3x3(modelMatrix);} //billboard
		Matrix4f mvm = viewMatrix.mul(modelMatrix);
		if (!entity.animated() && entity.getMesh() instanceof BillboardMesh) {
			mvm.m01(0f);mvm.m02(0f);
			mvm.m20(0f);mvm.m21(0f);
			mvm.m00(1);mvm.m22(1f);
		}
		
		
		shader.setUniform("modelViewMatrix", mvm);
    	shader.setUniform("texture_sampler", 0);
        
    	
        // Activate first texture unit
        if (entity.getMaterial().useTexture()) {
    	    shader.setUniform("atlas_size", entity.getMaterial().getTexture().getAtlasSize());  
    	    shader.setUniform("atlas_selected", entity.getSelectedTextureAtlas());  
        	GL13.glActiveTexture(GL13.GL_TEXTURE0);
        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getMaterial().getTexture().getId());
        } 
        shader.setUniform("material", entity.getMaterial());

    	if (entity.animated()) {
            AnimatedModel animGameItem = (AnimatedModel) entity.getAnimation();
            Matrix4f[] frame = animGameItem.getCurrentAnimation().getJointMatricies();
            shader.setUniform("jointsMatrix", frame);
            renderMesh(animGameItem.meshes[0]);
//            System.out.println(entity.getMaterial());
    	} else {
    		renderMesh(entity.getMesh());
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
	
	public Matrix4f getModelMatrix(Vector3f pos, Vector3f rot, Vector3f scale) {
	    Matrix4f modelMatrix = new Matrix4f();
	    modelMatrix.identity().translate(pos).
	        rotateX((float)Math.toRadians(-rot.x)).
	        rotateY((float)Math.toRadians(-rot.y)).
	        rotateZ((float)Math.toRadians(-rot.z)).
	        scale(scale);
		return modelMatrix;
	}

	public void cleanUp() {
		if (shader != null) {
	    	shader.cleanUp();
	    }
	}
}
