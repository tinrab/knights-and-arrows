#ifdef GL_ES
precision mediump float;
#endif

varying vec2 vTexCoord0;
uniform sampler2D u_first;
uniform sampler2D u_second;
uniform float factor;

void main(void) {
	vec4 final = texture2D(u_first, vTexCoord0);
	vec4 lights = texture2D(u_second, vTexCoord0);
	gl_FragColor = mix(final, lights, factor);
}