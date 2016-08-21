precision mediump float;
varying vec2 vTextureCoords;
varying vec4 vColour;
uniform sampler2D sTexture;
uniform vec4 uMod;
uniform vec4 uFade;

void main() {
    gl_FragColor = texture2D( sTexture, vTextureCoords ) * uMod;

    if ( gl_FragColor.a < 0.5 ) {
        discard;
    }

    if ( uFade.a >= 0.1 ) {
        gl_FragColor = gl_FragColor * vec4( uFade.xyz, 1.0 );
    }
}
