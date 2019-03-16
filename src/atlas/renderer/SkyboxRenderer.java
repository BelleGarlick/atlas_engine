package atlas.renderer;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import atlas.engine.Scene;
import atlas.objects.Camera;
import atlas.objects.Skybox;
import atlas.objects.entityComponents.Mesh;

public class SkyboxRenderer {

	final float SIZE = 1;
	private ShaderProgram shader;
	private Mesh cube;
	
	public void init() throws Exception {
		shader = new ShaderProgram("skybox.vs", "skybox.fs");
		shader.link();
		shader.createUniform("projectionMatrix");
		shader.createUniform("viewMatrix");		
		shader.createUniform("modelMatrix");		
		shader.createUniform("cubeMap");
		
		shader.createUniform("overlayCubeMap");	
		shader.createUniform("overlayAlpha");

		shader.createUniform("fog.activated");
		shader.createUniform("fog.colour");
		
		cube = new Mesh(new float[] {        
		    -1,  1, -1, -1, -1, -1,  1, -1, -1,  1, -1, -1,  1,  1, -1, -1,  1, -1,
		    -1, -1,  1, -1, -1, -1, -1,  1, -1, -1,  1, -1, -1,  1,  1, -1, -1,  1,
		     1, -1, -1,  1, -1,  1,  1,  1,  1,  1,  1,  1,  1,  1, -1,  1, -1, -1,
	 		-1, -1,  1, -1,  1,  1,  1,  1,  1,  1,  1,  1,  1, -1,  1, -1, -1,  1,
			-1,  1, -1,  1,  1, -1,  1,  1,  1,  1,  1,  1, -1,  1,  1, -1,  1, -1,
			-1, -1, -1, -1, -1,  1,  1, -1, -1,  1, -1, -1, -1, -1,  1,  1, -1,  1
		}, new float[]{}, new float[]{}, new int[]{
				1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36
		});
	}

	public void render(Scene scene, Camera camera) {
		if (scene.getSkybox() != null) {
			shader.bind();
			
			shader.setUniform("fog.activated", scene.fog.isActive() ? 1 : 0);
			if (scene.fog.isActive()) {
				shader.setUniform("fog.colour", scene.fog.getColour());
			}
			
			

			shader.setUniform("projectionMatrix", camera.getProjectionMatrix());
            Matrix4f viewMatrix = camera.getViewMatrix();
            float m30 = viewMatrix.m30();viewMatrix.m30(0);
            float m31 = viewMatrix.m31();viewMatrix.m31(0);
            float m32 = viewMatrix.m32();viewMatrix.m32(0);
			shader.setUniform("viewMatrix", viewMatrix);
			shader.setUniform("modelMatrix", getModelMatrix(scene.skybox));

			shader.setUniform("cubeMap", 0);
			shader.setUniform("overlayCubeMap", 1);
			shader.setUniform("overlayAlpha", scene.skybox.getSkyboxOverlayAlpha());
			
			GL30.glBindVertexArray(cube.getVaoId());
			GL20.glEnableVertexAttribArray(0);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, scene.getSkybox().getTexture().getId());
			
			if (scene.skybox.getSkyboxOverlayAlpha() > 0 && scene.getSkybox().getSkyboxOverlay() != null) {
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, scene.getSkybox().getSkyboxOverlay().getId());
			}
			
			GL11.glDrawArrays(GL13.GL_TRIANGLES, 0, cube.getVertexCount());
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);

            viewMatrix.m30(m30);
            viewMatrix.m31(m31);
            viewMatrix.m32(m32);            
			
			shader.unbind();
		}
	}
	
	public Matrix4f getModelMatrix(Skybox sb) {
	    Matrix4f modelMatrix = new Matrix4f();
	    modelMatrix.identity().
	        rotateY((float)Math.toRadians(-sb.getRotation())).
	        scale(sb.getRadius());
		return modelMatrix;
	}

	public void cleanUp() {
		if (shader != null) {
	    	shader.cleanUp();
	    	cube.cleanUp();
	    	System.out.println("Clean up skybox stuff");
		}
	}
}
