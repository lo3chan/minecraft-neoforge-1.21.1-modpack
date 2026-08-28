#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler5; // segmented brightness bands (one stripe per band, stacked in V)

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float StyleVanilla;
uniform float GrassBrightness;
uniform float BladeGradientBottom;
uniform float BladeGradientTop;
uniform float BladeGradientCurve;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
flat in vec2 noiseCoord;
in float bladeHeight;
// Scrolling-noise brightness wave, sampled once per blade in the vertex shader.
flat in float colorWave;

// Number of brightness bands for this blade, scaled to its final length in the vertex shader.
flat in float segmentCount;

out vec4 fragColor;

const float WAVE_DARK_MULTIPLIER = 0.85;
const float WAVE_BRIGHT_MULTIPLIER = 1.0;
const float TAPERED_GRADIENT_BASE_SCALE = 0.75;

const float SEGMENT_MAX_COUNT = 4.0; // must match grass_blades.vsh AND the stripe count in grass_bands.png
const float SEGMENT_BAND_VARIANTS = 8.0; // must match the column count in grass_bands.png

float bladeGradient(float height, float bottomMultiplier) {
    float shaped = pow(clamp(height, 0.0, 1.0), max(BladeGradientCurve, 0.001));
    return mix(BladeGradientBottom * bottomMultiplier, BladeGradientTop, shaped);
}

void main() {
    vec4 color = vertexColor * ColorModulator;

    // Defensive clamps. The vertex shader should already emit 0..1 values, but this prevents
    // accidental mix() extrapolation if future shader changes push either value out of range.
    float height = clamp(bladeHeight, 0.0, 1.0);
    float wind = clamp(colorWave, 0.0, 1.0);
    float snowAmount = clamp(texCoord0.y, 0.0, 1.0);

    // Style is now just a swapped shape texture on Sampler5 (both draw the same constant-width strip):
    //  - SEGMENTED: a stripe texture, rgb = equal-brightness bands, alpha opaque. Height-aware V samples
    //    fewer stripes on short blades (segmentCount is world-length scaled), so band pitch stays
    //    ~constant instead of squishing. Clamp inside the top texel so the tip doesn't wrap to the base.
    //  - TAPERED: a silhouette texture, alpha carves the taper, rgb white. texCoord0 is (widthU, 1-t).
    // Everything below (gradient, wind shimmer, cutout) is shared.
    vec4 shape;
    float gradientBase;
    if (StyleVanilla > 0.5) {
        float bandV = min(height * segmentCount / SEGMENT_MAX_COUNT, 0.999);
        // Pick a per-blade variant column (stable per blade via world-locked noiseCoord) so neighbouring
        // segmented blades don't all show the identical band pattern.
        float variant = fract(sin(dot(noiseCoord, vec2(12.9898, 78.233))) * 43758.5453);
        float bandU = (floor(variant * SEGMENT_BAND_VARIANTS) + 0.5) / SEGMENT_BAND_VARIANTS;
        shape = texture(Sampler5, vec2(bandU, bandV));
        gradientBase = 1.0;
    } else {
        shape = texture(Sampler5, vec2(texCoord0.x, 1.0 - height));
        gradientBase = TAPERED_GRADIENT_BASE_SCALE;
    }

    color.a *= shape.a;
    if (color.a < 0.1) {
        discard;
    }

    float gradient = bladeGradient(height, gradientBase);
    float waveMultiplier = mix(WAVE_DARK_MULTIPLIER, WAVE_BRIGHT_MULTIPLIER, wind);
    vec3 decorativeMultiplier = shape.rgb * gradient * waveMultiplier;
    color.rgb *= mix(decorativeMultiplier, vec3(1.0), snowAmount);

    color.rgb = clamp(color.rgb * GrassBrightness, 0.0, 1.0);
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
