package atlas.renderer;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import atlas.objects.Fog;
import atlas.objects.Fog.FogMode;
import atlas.objects.entityComponents.Material;
import atlas.objects.lights.DirectionalLight;
import atlas.objects.lights.PointLight;
import atlas.objects.lights.SpotLight;
import atlas.utils.Loader;

public class ShaderProgram {

    private final int programId;

    private int vertexShaderId;

    private int fragmentShaderId;

    private int geometryShaderId;

    private final Map<String, Integer> uniforms;

    public ShaderProgram(String vertexShaderLocation, String fragmentShaderLocation) throws Exception {
        programId = GL20.glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }
        uniforms = new HashMap<>();
        vertexShaderId = createShader(loadShader(vertexShaderLocation), GL20.GL_VERTEX_SHADER);
        fragmentShaderId = createShader(loadShader(fragmentShaderLocation), GL20.GL_FRAGMENT_SHADER);
    }    
    
    private String loadShader(String location) throws IOException {
    	String shaderCode = "";
    	InputStream is = Loader.getStream(Loader.class.getClassLoader(), "shaders/" + location);
    	BufferedReader br = new BufferedReader(new InputStreamReader(is));
    	String line = "";
    	while ((line = br.readLine()) != null) {
    		shaderCode += line + "\n";
    	}
    	return shaderCode;
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = GL20.glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
        	System.err.println("Could not find uniform:" + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    public void createPointLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            createPointLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createPointLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".att.constant");
        createUniform(uniformName + ".att.linear");
        createUniform(uniformName + ".att.exponent");
    }

    public void createSpotLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            createSpotLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createSpotLightUniform(String uniformName) throws Exception {
        createPointLightUniform(uniformName + ".pl");
        createUniform(uniformName + ".conedir");
        createUniform(uniformName + ".cutoff");
    }

    public void createDirectionalLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    public void createMaterialUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".normalMap");
        createUniform(uniformName + ".hasNormalMap");
        createUniform(uniformName + ".reflectance");
    }

    public void createFogUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".density");
        createUniform(uniformName + ".activated");
        createUniform(uniformName + ".radius");
    }

    public void setUniformsetUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Dump the matrix into a float buffer
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            GL20.glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Dump the matrix into a float buffer
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    public void setUniform(String uniformName, Matrix4f[] matrices) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int length = matrices != null ? matrices.length : 0;
            FloatBuffer fb = stack.mallocFloat(16 * length);
            for (int i = 0; i < length; i++) {
                matrices[i].get(16 * i, fb);
            }
            GL20.glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    public void setUniform(String uniformName, int value) {
    	GL20.glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, float value) {
    	GL20.glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, Vector3f value) {
    	GL20.glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

	public void setUniform(String uniformName, Vector2f vector2f) {
		GL20.glUniform2f(uniforms.get(uniformName),vector2f.x,vector2f.y);
		
	}

    public void setUniform(String uniformName, Vector4f value) {
    	GL20.glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

//    public void setUniform(String uniformName, PointLight[] pointLights) {
//        int numLights = pointLights != null ? pointLights.length : 0;
//        for (int i = 0; i < numLights; i++) {
//            setUniform(uniformName, pointLights[i], i);
//        }
//    }
//
    public void setUniform(String uniformName, PointLight pointLight, int pos) {
        setUniform(uniformName + "[" + pos + "]", pointLight);
    }

    public void setUniform(String uniformName, PointLight pointLight) {
        setUniform(uniformName + ".colour", pointLight.getColour());
        setUniform(uniformName + ".position", pointLight.getPosition());
        setUniform(uniformName + ".intensity", pointLight.getIntensity());
        PointLight.Attenuation att = pointLight.getAttenuation();
        setUniform(uniformName + ".att.constant", att.constant);
        setUniform(uniformName + ".att.linear", att.linear);
        setUniform(uniformName + ".att.exponent", att.exponent);
    }

    public void setUniform(String uniformName, SpotLight spotLight, int pos) {
        setUniform(uniformName + "[" + pos + "]", spotLight);
    }

    public void setUniform(String uniformName, SpotLight spotLight) {
        setUniform(uniformName + ".pl", spotLight.getPointLight());
        setUniform(uniformName + ".conedir", spotLight.getConeDirection());
        setUniform(uniformName + ".cutoff", spotLight.getCutOff());
    }

    public void setUniform(String uniformName, DirectionalLight dirLight) {
        setUniform(uniformName + ".colour", dirLight.getColour());
        setUniform(uniformName + ".direction", dirLight.getDirection());
        setUniform(uniformName + ".intensity", dirLight.getIntensity());
    }

    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".colour", material.getColor());
        setUniform(uniformName + ".hasTexture", material.useTexture() ? 1 : 0);
        setUniform(uniformName + ".hasNormalMap", material.getNormalMap()!=null? 1 : 0);
        setUniform(uniformName + ".reflectance", material.getReflectance());
	    
	    if (material.getNormalMap()!=null) {
		    setUniform(uniformName + ".normalMap", 15);
		  	GL13.glActiveTexture(GL13.GL_TEXTURE15);
		  	GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.getNormalMap().getId());
	    }
    }

    public void setUniform(String uniformName, Fog fog) {
        setUniform(uniformName + ".colour", fog.getColour());
        setUniform(uniformName + ".density", fog.getDensity());
        setUniform(uniformName + ".activated", fog.isActive() ? 1 : 0);
        setUniform(uniformName + ".radius", fog.getFogMode().equals(FogMode.Radial) ? 1 : 0);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = GL20.glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        GL20.glShaderSource(shaderId, shaderCode);
        GL20.glCompileShader(shaderId);

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + GL20.glGetShaderInfoLog(shaderId, 1024));
        }

        GL20.glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
    	GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
        	GL20.glDetachShader(programId, vertexShaderId);
        }
        if (geometryShaderId != 0) {
        	GL20.glDetachShader(programId, geometryShaderId);
        }
        if (fragmentShaderId != 0) {
        	GL20.glDetachShader(programId, fragmentShaderId);
        }

        GL20.glValidateProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0) {
//            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

    }

    public void bind() {
        GL20.glUseProgram(programId);
    }

    public void unbind() {
    	GL20.glUseProgram(0);
    }

    public void cleanUp() {
        unbind();
        if (programId != 0) {
        	GL20.glDeleteProgram(programId);
        }
    }
}
