precision mediump float;

varying vec2 vTextureCoord;

uniform sampler2D uTexture;
uniform float uAlpha;

void main() {
  gl_FragColor = texture2D(uTexture, vTextureCoord) * uAlpha;
}