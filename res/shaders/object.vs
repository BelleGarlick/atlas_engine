#version 330

const int MAX_WEIGHTS = 4;
const int MAX_JOINTS = 150;

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;
layout (location=3) in vec4 jointWeights;
layout (location=4) in ivec4 jointIndices;

out vec2 outTexCoord;
out vec3 outVertexPos;
out vec3 outVertexNormal;
out mat4 outModelMatrix;

uniform mat4 modelMatrix;
uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

uniform mat4 jointsMatrix[MAX_JOINTS];

uniform int atlas_size;
uniform int atlas_selected;

void main()
{
    
    vec4 initPos = vec4(0, 0, 0, 1);
    vec4 initNormal = vec4(0, 0, 0, 0);
    
    int count = 0;
    for(int i = 0; i < MAX_WEIGHTS; i++){
        float weight = jointWeights[i];
        if (weight > 0) {
            count++;
            int jointIndex = jointIndices[i];
            vec4 tmpPos = jointsMatrix[jointIndex] * vec4(position, 1.0);
            initPos += weight * tmpPos;

            vec4 tmpNormal = jointsMatrix[jointIndex] * vec4(vertexNormal, 0.0);
            initNormal += weight * tmpNormal;
        }
        
    }
    if (count == 0){
        initPos = vec4(position, 1.0);
        initNormal = vec4(vertexNormal, 0.0);
    }
    
    
    gl_Position = projectionMatrix * modelViewMatrix * initPos;

    // Support for texture atlas, update texture coordinates
    float x = (texCoord.x + (atlas_selected % atlas_size)) / atlas_size;
    float y = (texCoord.y + (atlas_selected / atlas_size)) / atlas_size;
    outTexCoord = vec2(x, y);
    
    outModelMatrix = modelMatrix;
    outVertexNormal = initNormal.xyz;
    outVertexPos = (modelMatrix * initPos).xyz;
}