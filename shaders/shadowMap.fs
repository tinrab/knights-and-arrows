#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

#define PI 3.14159265359
varying vec2 vTexCoord0;
varying LOWP vec4 vColor;

uniform sampler2D u_texture;
uniform vec2 resolution;
uniform float fraction;

const float THRESHOLD = 0.75;

void main(void) {
  float distance = fraction;
  for (float y=0.0; y<resolution.y; y+=1.0) {
		vec2 norm = vec2(vTexCoord0.s, y/resolution.y) * 2.0 - 1.0;
		float theta = PI*1.5 + norm.x * PI; 
		float r = (1.0 + norm.y) * 0.5;
		
		vec2 coord = vec2(-r * sin(theta), -r * cos(theta))/2.0 + 0.5;
		
		vec4 data = texture2D(u_texture, coord);
		
		float dst = y/resolution.y;
		
		float caster = data.a;
		if (caster > THRESHOLD) {
			distance = min(distance, dst);
		}
  } 
  gl_FragColor = vec4(vec3(distance), 1.0);
}