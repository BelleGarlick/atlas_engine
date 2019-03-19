package atlas.renderer;

import java.util.HashSet;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import atlas.engine.Scene;
import atlas.objects.Camera;
import atlas.objects.entityComponents.Mesh;
import atlas.objects.particles.Particle;
import atlas.objects.particles.ParticleEmitter;

public class ParticleRenderer {

	private ShaderProgram shader;
	private Mesh particleMesh;
	
	public void init() throws Exception {
		shader = new ShaderProgram("particle.vs", "particle.fs");
		shader.link();
		shader.createUniform("projectionMatrix");
		shader.createUniform("viewMatrix");
		shader.createUniform("modelMatrix");
		shader.createUniform("texture_sampler");
		
		particleMesh = new Mesh(new float[] {        
			    -1,  1, 0,    
			     1,  1, 0,    
			     1, -1, 0,    
			    -1,  1, 0,    
			     1, -1, 0,  
			    -1, -1, 0, 
		}, new float[]{}, new float[]{}, new int[]{
				1, 2, 3,
				4, 5, 6
		});
	}

	public void render(Scene scene, Camera camera) {
		GL13.glDepthMask(false);
		shader.bind();

		shader.setUniform("texture_sampler", 0);
		    Matrix4f projectionMatrix = camera.getProjectionMatrix();
		    shader.setUniform("projectionMatrix", projectionMatrix);
		    shader.setUniform("viewMatrix", camera.getViewMatrix());

		    HashSet<ParticleEmitter> emitters = scene.particleEmitters;
		    for (ParticleEmitter pe : emitters) {
		    	
		    	
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL13.GL_TEXTURE_2D, pe.getBaseParticle().getTexture().getId());
				
				for (Particle p : pe.getParticles()) {
					GL30.glBindVertexArray(particleMesh.getVaoId());
				    shader.setUniform("modelMatrix", getModelMatrix(p));
					GL20.glEnableVertexAttribArray(0);
					GL20.glEnableVertexAttribArray(1);
					GL20.glEnableVertexAttribArray(2);

					GL11.glDrawArrays(GL13.GL_TRIANGLES, 0, particleMesh.getVertexCount());

					GL20.glDisableVertexAttribArray(0);
					GL20.glDisableVertexAttribArray(1);
					GL20.glDisableVertexAttribArray(2);
					GL30.glBindVertexArray(0);
				}
		    }

	    shader.unbind();
		GL13.glDepthMask(true);
	}
	
	public Matrix4f getModelMatrix(Particle p) {
	    Matrix4f modelMatrix = new Matrix4f();
	    modelMatrix.identity().
	    	translate(p.getPosition()).
	        scale(p.getScale());
		return modelMatrix;
	}

	public void cleanUp() {
		if (shader != null) {
	    	shader.cleanUp();
	    	particleMesh.cleanUp();
	    	System.out.println("Clean up skybox stuff");
		}
	}
}
