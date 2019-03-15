#version 330

in vec3 outTexCoord;
out vec4 out_Color;

struct Fog {
	int activated;
	vec3 colour;
};

uniform samplerCube cubeMap;
uniform Fog fog;

const float lowerLimit = 0.05;
const float upperLimit = 0.20;


void main() {
	out_Color = texture(cubeMap, outTexCoord);
	
	if (fog.activated == 1) {
		float factor = (outTexCoord.y - lowerLimit) / (upperLimit - lowerLimit);
		factor = clamp(factor, 0.0, 1.0);
		
		out_Color = mix(vec4(fog.colour,1.0), out_Color, factor);
	}
	
}