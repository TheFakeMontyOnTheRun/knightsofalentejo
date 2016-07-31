precision mediump float;
varying vec2 vTextureCoords;
varying vec4 vColour;
uniform sampler2D sTexture;
uniform vec4 uMod;

void main() {
    gl_FragColor = texture2D( sTexture, vTextureCoords ) * uMod;
    if ( gl_FragColor.a < 0.5 ) {
        discard;
    }
}
