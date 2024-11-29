precision mediump float;

varying vec2 vCoordinate;

uniform sampler2D uTexture;

uniform float uAlpha;

uniform float uTexWidth;
uniform float uTexHeight;
uniform float uSteps;
uniform int uRadius;

uniform int uRendererType;

uniform int uBlurRadius;
uniform vec2 uBlurOffset;
uniform float uSumWeight;
const float PI = 3.1415926;

//边界值处理
vec2 clampCoordinate(vec2 coordinate) {
    return vec2(clamp(coordinate.x, 0.0, 1.0), clamp(coordinate.y, 0.0, 1.0));
}

//计算权重
float getWeight(int i) {
    float sigma = float(uBlurRadius) / 3.0;
    return (1.0 / sqrt(2.0 * PI * sigma * sigma)) * exp(-float(i * i) / (2.0 * sigma * sigma)) / uSumWeight;
}

void main()
{
  //gl_FragColor = texture2D(uTexture, vCoordinate);

  //gl_FragColor = vec4(vec3(texture2D(uTexture, vCoordinate)), uAlpha);

   vec4 rgbColor;

   if(uRendererType == 1)
   {
       float weight = float(uRadius * 2 + 1);
       weight = weight * weight;
       vec2 txtUnit = vec2(1.0 / uTexWidth, 1.0 / uTexHeight);
       txtUnit *= uSteps;
       for(int y = -uRadius; y <= uRadius; y++) {
           for(int x = -uRadius;x <= uRadius; x++) {
               rgbColor += texture2D(uTexture, vCoordinate + vec2(txtUnit.x * float(x), txtUnit.y * float(y)));
           }
        }
       rgbColor = rgbColor / weight;
   }
   else if(uRendererType == 2)
   {
       vec4 sourceColor = texture2D(uTexture, vCoordinate);
       if (uBlurRadius <= 1)
       {
           rgbColor = sourceColor;
       }
       else
       {
           float weight = getWeight(0);
           vec3 finalColor = sourceColor.rgb * weight;
           for (int i = 1; i < uBlurRadius; i++)
           {
               weight = getWeight(i);
               finalColor += texture2D(uTexture, clampCoordinate(vCoordinate - uBlurOffset * float(i))).rgb * weight;
               finalColor += texture2D(uTexture, clampCoordinate(vCoordinate + uBlurOffset * float(i))).rgb * weight;
           }
           rgbColor = vec4(finalColor, sourceColor.a);
       }
   }
   else
   {
       rgbColor = vec4(vec3(texture2D(uTexture, vCoordinate)), uAlpha);
   }

   gl_FragColor = rgbColor;
}