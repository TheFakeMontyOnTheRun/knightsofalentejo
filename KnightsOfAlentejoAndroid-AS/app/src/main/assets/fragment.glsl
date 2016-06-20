precision mediump float;
varying vec2 vTextureCoords;
varying vec4 vColour;
uniform sampler2D sTexture;

void main() {
    gl_FragColor = texture2D( sTexture, vTextureCoords );
    if ( gl_FragColor.a < 0.5 ) {
        discard;
    }
}
