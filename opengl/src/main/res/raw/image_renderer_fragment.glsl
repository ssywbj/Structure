precision mediump float;

varying vec2 vTextureCoord;

uniform sampler2D uTexture;

uniform float uAlpha;

uniform float uTexWidth;
uniform float uTexHeight;
uniform float uSteps;
uniform int uRadius;

uniform int uRendererType;

void main()
{
  //gl_FragColor = texture2D(uTexture, vTextureCoord);

  //gl_FragColor = vec4(vec3(texture2D(uTexture, vTextureCoord)), uAlpha);

   vec4 rgbColor;

   if(uRendererType == 1)
   {
       float weight = float(uRadius *2 + 1);
       weight = weight * weight;
       vec2 txtUnit = vec2(1.0 / uTexWidth, 1.0 / uTexHeight);
       txtUnit *= uSteps;
       for(int y = -uRadius; y <= uRadius; y++) {
           for(int x = -uRadius;x <= uRadius; x++) {
               rgbColor += texture2D(uTexture, vTextureCoord + vec2(txtUnit.x * float(x), txtUnit.y * float(y)));
           }
        }
       rgbColor = rgbColor / weight;
   }
   else
   {
       rgbColor = vec4(vec3(texture2D(uTexture, vTextureCoord)), uAlpha);
   }

   gl_FragColor = rgbColor;
}