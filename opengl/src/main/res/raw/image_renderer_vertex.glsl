uniform mat4 uMVPMatrix;

attribute vec4 aPosition;
attribute vec2 aCoordinate;

varying vec2 vCoordinate;

void main() {
    gl_Position = uMVPMatrix * aPosition;
    vCoordinate = aCoordinate;
}