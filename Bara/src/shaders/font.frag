#version 330 compatibility

uniform sampler2D fontTexture;



void main(){
	
	float distance = texture(fontTexture, gl_TexCoord[0].st).r;
	
	vec2 texRes = textureSize(fontTexture, 0);
	float df = 16.0;
	float sharpness = 1.5;
	
	vec2 coords = gl_TexCoord[0].st * texRes / (df * sharpness);
	
	float len = min(1.0, length(fwidth(coords)));
	
	float edgeWidth = len;
	gl_FragColor = gl_Color * smoothstep(-edgeWidth, +edgeWidth, distance);
	
	//gl_FragColor = vec4(gl_TexCoord[0].st, 0, 1);
}