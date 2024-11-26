precision mediump float;

varying vec2 vTextureCoord;

uniform sampler2D uTexture;
uniform float uAlpha;

void main() {
  //gl_FragColor = texture2D(uTexture, vTextureCoord);
  gl_FragColor = vec4(vec3(texture2D(uTexture, vTextureCoord)), uAlpha);
}