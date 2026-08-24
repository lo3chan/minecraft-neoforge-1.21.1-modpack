#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2; // lightmap
uniform sampler2D Sampler3; // wind-field noise
uniform sampler2D Sampler4; // entity interaction trail field

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 SectionOffset;
uniform int FogShape;
uniform float NoiseScale;
uniform vec2 NoiseScrollOffset;
uniform float WindStrength;
uniform vec2 WindDirection;
uniform float PlantBobPhase;
uniform vec4 TrailParams;
uniform vec2 TrailNoiseOrigin; // trail-field origin reduced mod NOISE_TILE, for center recovery

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
flat out vec2 noiseCoord;

const float PLANT_SWAY_STRENGTH = 0.32;
const float PLANT_SWAY_BASE_LEAN = 0.4;
const float PLANT_SWAY_GUST_RANGE = 0.8;
const float PLANT_BOB_STRENGTH = 0.06;
const float PLANT_IDLE_BOB_STRENGTH = 0.14;
const float WIND_FIELD_SCALE = 0.5;
const float TRAIL_WIND_SUPPRESSION = 0.95;
// World size of the noise tile baked into noiseCoord (GrassGeometry.NOISE_WORLD_TILE_BLOCKS).
const float NOISE_TILE = 32.0;
// How far the trail leans a plant. height^2-weighted so the base stays planted and the head
// (incl. the upper half of a two-block plant, fraction 0.5->1) bends away from the player.
const float PLANT_TRAIL_PUSH = 1.2;

float sampleSmoothNoise(vec2 uv) {
    vec2 size = vec2(textureSize(Sampler3, 0));
    vec2 pixel = fract(uv) * size - 0.5;
    vec2 base = floor(pixel);
    vec2 blend = smoothstep(vec2(0.0), vec2(1.0), fract(pixel));
    return texture(Sampler3, (base + blend + 0.5) / size).r;
}

vec4 sampleSmoothTrail(vec2 uv) {
    vec2 textureSizePixels = vec2(textureSize(Sampler4, 0));
    vec2 halfPixel = 0.5 / textureSizePixels;
    vec2 clampedUv = clamp(uv, halfPixel, vec2(1.0) - halfPixel);
    vec2 pixel = clampedUv * textureSizePixels - 0.5;
    vec2 pixelFloor = floor(pixel);
    vec2 blend = smoothstep(vec2(0.0), vec2(1.0), fract(pixel));
    vec2 baseUv = (pixelFloor + 0.5) / textureSizePixels;
    vec2 pixelStep = 1.0 / textureSizePixels;
    vec2 maxUv = vec2(1.0) - halfPixel;

    vec4 bottomLeft = texture(Sampler4, clamp(baseUv, halfPixel, maxUv));
    vec4 bottomRight = texture(Sampler4, clamp(baseUv + vec2(pixelStep.x, 0.0), halfPixel, maxUv));
    vec4 topLeft = texture(Sampler4, clamp(baseUv + vec2(0.0, pixelStep.y), halfPixel, maxUv));
    vec4 topRight = texture(Sampler4, clamp(baseUv + pixelStep, halfPixel, maxUv));
    vec4 bottom = mix(bottomLeft, bottomRight, blend.x);
    vec4 top = mix(topLeft, topRight, blend.x);
    return mix(bottom, top, blend.y);
}

void main() {
    float windStrength = WindStrength;
    float bobStrength = max(windStrength, PLANT_IDLE_BOB_STRENGTH);
    vec2 windDir = length(WindDirection) > 0.001 ? normalize(WindDirection) : vec2(1.0, 0.0);

    vec3 pos = Position + SectionOffset;
    float height = clamp(Normal.z, 0.0, 1.0);
    noiseCoord = Normal.xy * 0.5 + 0.5;

    // Sample the trail once per plant, at the block CENTER -- never per vertex. The two
    // crossed sprites' corners sit at different XZ, so sampling each independently gives
    // them different trail directions/strengths and shears them apart. noiseCoord =
    // frac(worldCenter / NOISE_TILE) is shared by every vertex of the plant, so recover the
    // block center in trail-field space from it and pick the representative nearest this
    // vertex (always the plant's own center, <1 block away).
    vec2 vertexTrail = pos.xz - TrailParams.xy;
    vec2 centerMod = mod(NOISE_TILE * noiseCoord - TrailNoiseOrigin, NOISE_TILE);
    vec2 plantCenter = vertexTrail
            + mod(centerMod - vertexTrail + 0.5 * NOISE_TILE, NOISE_TILE) - 0.5 * NOISE_TILE;
    vec2 trailDir = windDir;
    float trailStrength = 0.0;
    float trailEffect = 0.0;
    float trailWindMultiplier = 1.0;
    if (TrailParams.w > 0.0) {
        vec2 trailUv = plantCenter * TrailParams.z;
        vec2 trailInside = step(vec2(0.0), trailUv) * step(trailUv, vec2(1.0));
        vec4 trailSample = sampleSmoothTrail(trailUv);
        vec2 sampledDir = trailSample.rg * 2.0 - 1.0;
        float sampledDirLength = length(sampledDir);
        trailDir = sampledDirLength > 0.001 ? sampledDir / sampledDirLength : windDir;
        trailStrength = trailSample.b * trailInside.x * trailInside.y * TrailParams.w;
        trailEffect = clamp(trailStrength, 0.0, 1.0);
        trailWindMultiplier = 1.0 - trailEffect * TRAIL_WIND_SUPPRESSION;
    }

    vec2 windFieldUv = noiseCoord * WIND_FIELD_SCALE + NoiseScrollOffset;
    float windField = sampleSmoothNoise(windFieldUv);

    float lean = PLANT_SWAY_BASE_LEAN + windField * PLANT_SWAY_GUST_RANGE;
    float sway = lean * windStrength * trailWindMultiplier * height * height * PLANT_SWAY_STRENGTH;
    pos.xz += windDir * sway;

    vec2 crossWindDir = vec2(-windDir.y, windDir.x);
    float phase = dot(noiseCoord, vec2(97.0, 151.0));
    float bob = sin(PlantBobPhase + phase) * PLANT_BOB_STRENGTH * height * height;
    pos.xz += crossWindDir * (bob * bobStrength * trailWindMultiplier);
    pos.xz += trailDir * (trailStrength * height * height * PLANT_TRAIL_PUSH);

    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
    vertexDistance = fog_distance(pos, FogShape);
    vertexColor = vec4(Color.rgb, 1.0) * minecraft_sample_lightmap(Sampler2, UV2);
    texCoord0 = UV0;
}
