#version 330

in  vec2 outTexCoord;
in vec3 outVertexNormal;
in vec3 outVertexPos;

out vec4 fragColor;


struct BlendMap {
    sampler2D blendMap;
    sampler2D rtexture;
    sampler2D gtexture;
    sampler2D btexture;
};

struct Attenuation {float constant;float linear;float exponent;};
struct PointLight {vec3 colour;vec3 position;float intensity;Attenuation att;};
struct SpotLight {PointLight pl;vec3 conedir;float cutoff;};
struct DirectionalLight {vec3 colour;vec3 direction;float intensity;};


//Lighting Stuff
uniform vec3 ambientLight;
uniform vec3 cameraPos;
uniform int spotLightCount;
uniform int pointLightCount;
uniform SpotLight spotLights[16];
uniform PointLight pointLights[16];
uniform DirectionalLight directionalLight;


//Terrain Stuff
uniform sampler2D texture_sampler;
uniform float textureTiles;
uniform int useTexture;
uniform vec3 materialColor;
uniform float reflectance;

uniform int blendMaps;
uniform BlendMap blendMap[3]; //max 3 blend maps


vec4 calcLightColour(vec3 light_colour, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal, vec4 texColour, vec3 camera_pos) {
    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specColour = vec4(0, 0, 0, 0);

    // Diffuse Light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColour = texColour * vec4(light_colour, 1.0) * light_intensity * diffuseFactor;


    // Specular Light
    vec3 camera_direction = normalize(camera_pos - position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflected_light = normalize(reflect(from_light_dir, normal));
    
    float specularFactor = max(dot(camera_direction, reflected_light), 0.0);
    
    float specularPower = 2;
    specularFactor = pow(specularFactor, specularPower);
    
    specColour = texColour * light_intensity * specularFactor * reflectance * vec4(light_colour, 1.0);
 
    return (diffuseColour + specColour);
}


vec4 calcPointLight(PointLight light, vec3 position, vec3 normal, vec4 texColour, vec3 camera_pos) {
    vec3 light_direction = light.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec4 light_colour = calcLightColour(light.colour, light.intensity, position, to_light_dir, normal, texColour, camera_pos);

    // Apply Attenuation
    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance +
        light.att.exponent * distance * distance;
    return light_colour / attenuationInv;
}


vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal, vec4 texColour, vec3 camera_pos) {
    return calcLightColour(light.colour, light.intensity, position, normalize(light.direction), normal, texColour, camera_pos);
}


vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal, vec4 texColor, vec3 camera_pos) {
    vec3 light_direction = light.pl.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec3 from_light_dir  = -to_light_dir;
    float spot_alfa = dot(from_light_dir, normalize(light.conedir));
    
    vec4 colour = vec4(0, 0, 0, 0);
    
    if ( spot_alfa > light.cutoff ) 
    {
        colour = calcPointLight(light.pl, position, normal, texColor, camera_pos);
        colour *= (1.0 - (1.0 - spot_alfa)/(1.0 - light.cutoff));
    }
    return colour;    
}





void main() {

	//Normal enhanser
	vec3 terrainNormal = outVertexNormal;

	
	//Set Texture stuff
	vec4 texColor = vec4(materialColor,1);
	if (useTexture == 1) {texColor = texture(texture_sampler, outTexCoord * textureTiles);}


	//Blend Map
	for (int bm = 0; bm < blendMaps; bm++) {
		float totalBlendColour = 0;
		vec4 blend = texture(blendMap[bm].blendMap, outTexCoord);
		totalBlendColour += blend.x + blend.y + blend.z;
		
		vec4 r = texture(blendMap[bm].rtexture, outTexCoord * textureTiles);
		vec4 g = texture(blendMap[bm].gtexture, outTexCoord * textureTiles);
		vec4 b = texture(blendMap[bm].btexture, outTexCoord * textureTiles);
		
		float backTextureAmount = max(1 - totalBlendColour, 0);
		texColor *= backTextureAmount;
		
		if (totalBlendColour > 1) {
			texColor += r * (blend.x / totalBlendColour);
			texColor += g * (blend.y / totalBlendColour);
			texColor += b * (blend.z / totalBlendColour);
		} else { 
			texColor += r * (blend.x * totalBlendColour);
			texColor += g * (blend.y * totalBlendColour);
			texColor += b * (blend.z * totalBlendColour);
		}
	}
	texColor.w = 1;
	
	
	
	//Lighting
	vec4 diffuseSpecularComp = vec4(0.0);
	for (int i = 0; i < pointLightCount; i++) {
	    if (pointLights[i].intensity > 0) {
	        diffuseSpecularComp += calcPointLight(pointLights[i], outVertexPos, terrainNormal, texColor, cameraPos);
	    }
	}
	
	for (int i = 0; i < spotLightCount; i++) {
	    if (spotLights[i].pl.intensity > 0) {
	        diffuseSpecularComp += calcSpotLight(spotLights[i], outVertexPos, terrainNormal, texColor, cameraPos);
	    }
	}
	diffuseSpecularComp += calcDirectionalLight(directionalLight, outVertexPos, terrainNormal, texColor, cameraPos);
	
    fragColor = texColor * vec4(ambientLight, 1) + diffuseSpecularComp;
	
}