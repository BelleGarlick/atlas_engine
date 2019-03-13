#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;

out vec2 outTexCoord;
out vec3 outVertexNormal;
out vec3 outVertexPos;

uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;

void main()
{
    vec4 initPos = vec4(position, 1);
    vec4 initNormal = vec4(vertexNormal, 0);
    
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * initPos;
    outTexCoord = texCoord;
    
    
    outVertexNormal = normalize(modelMatrix * initNormal).xyz;
    outVertexPos = (modelMatrix * initPos).xyz;
}