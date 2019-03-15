#version 330

in vec2 outTexCoord;
in vec3 outVertexNormal;
in vec3 outVertexPos;
in mat4 outModelMatrix;

out vec4 fragColor;

struct Attenuation {float constant;float linear;float exponent;};
struct PointLight {vec3 colour;vec3 position;float intensity;Attenuation att;};
struct SpotLight {PointLight pl;vec3 conedir;float cutoff;};
struct DirectionalLight {vec3 colour;vec3 direction;float intensity;};

struct Material {
    vec3 colour;
    int hasTexture;
    int hasNormalMap;
	sampler2D normalMap;
    float reflectance;
};

struct Fog {
	int activated;
	int radius; //if 1, then radial, if 0 then cylindrical
	vec3 colour;
	float density;
};

	//Set at start of render
uniform vec3 ambientLight;
uniform vec3 cameraPos;
uniform int spotLightCount;
uniform int pointLightCount;
uniform SpotLight spotLights[16];
uniform PointLight pointLights[16];
uniform DirectionalLight directionalLight;
uniform Fog fog;

	//set per item
uniform sampler2D texture_sampler;
uniform Material material;


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
    
    specColour = texColour * light_intensity * specularFactor * material.reflectance * vec4(light_colour, 1.0);
 
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


void main()
{
	vec4 texColor = vec4(material.colour,1);
	if (material.hasTexture==1) {
		texColor = texture(texture_sampler, outTexCoord);
		if (texColor.a < 0.5) {
			discard;
		}
	} 
	
	vec3 entityNormal = outVertexNormal;
	if (material.hasNormalMap == 1) {
    	vec4 normalMapValue = 2.0 * texture(material.normalMap, outTexCoord) - 1.0;
		entityNormal = normalize(normalMapValue.rgb);
	    
	}
	entityNormal = normalize(outModelMatrix * vec4(entityNormal,0.0)).xyz;


	vec4 diffuseSpecularComp = vec4(0.0);
	for (int i = 0; i < pointLightCount; i++) {
	    if (pointLights[i].intensity > 0) {
	        diffuseSpecularComp += calcPointLight(pointLights[i], outVertexPos, entityNormal, texColor, cameraPos);
	    }
	}
	
	for (int i = 0; i < spotLightCount; i++) {
	    if (spotLights[i].pl.intensity > 0) {
	        diffuseSpecularComp += calcSpotLight(spotLights[i], outVertexPos, entityNormal, texColor, cameraPos);
	    }
	}
	diffuseSpecularComp += calcDirectionalLight(directionalLight, outVertexPos, entityNormal, texColor, cameraPos);
	
    fragColor = texColor * vec4(ambientLight, 1) + diffuseSpecularComp;
    
	if (material.hasNormalMap == 1) {
    	//fragColor = vec4(entityNormal,1.0);
    }
    
    //Apply Fog
    if (fog.activated == 1) {
    	float distance = length(outVertexPos - cameraPos);
    	if (fog.radius == 0) {
    		distance = length(outVertexPos.xz - cameraPos.xz);
    	}
    	float fogFactor = 1.0 / exp((distance * fog.density) * (distance * fog.density));
    	fogFactor = clamp(fogFactor, 0.0, 1.0);
    	
    	vec3 resultColour = mix(fog.colour, fragColor.xyz, fogFactor);
    	fragColor = vec4(resultColour.xyz, fragColor.w); 
    }
}