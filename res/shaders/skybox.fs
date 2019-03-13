#version 330

in vec3 outTexCoord;
out vec4 out_Color;

uniform samplerCube cubeMap;


void main() {
	out_Color = texture(cubeMap, outTexCoord);
	//out_Color = vec4(1,1,1,1);
}