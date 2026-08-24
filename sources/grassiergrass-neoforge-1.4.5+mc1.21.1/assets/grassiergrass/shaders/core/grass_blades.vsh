#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2; // lightmap
uniform sampler2D Sampler3; // animation/color noise
uniform sampler2D Sampler4; // entity interaction trail field

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 SectionOffset;
uniform int FogShape;
uniform float NoiseScale;
uniform vec2 NoiseScrollOffset;
uniform float WindStrength;
uniform vec2 WindDirection;
uniform float WindFlutterPhase;
uniform float BladeVisualHeight;
uniform float BladeWidth;
uniform float StyleVanilla;
uniform float HeightVariation;
uniform vec4 TrailParams;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
flat out vec2 noiseCoord;
out float bladeHeight;
flat out float windForceMagnitude;
flat out float segmentCount;
// Old-style brightness shimmer: the scrolling noise, shaped, sampled once per blade here (noiseCoord
// is flat) so the fragment shader needs no texture fetch. Reuses the windNoise fetch below.
flat out float colorWave;

const float MAX_WIND_INTENSITY = 5.0;
const float MAX_ROTATION_RADIANS = 1.05;

// Scrolling-noise brightness wave shaping (ported from the old fragment method).
const float COLOR_WAVE_CONTRAST = 1.0;
const float COLOR_WAVE_BIAS = 0.5;
const float COLOR_WAVE_SOFTNESS = 0.0;

const float BEND_AMOUNT = 0.1;
const float BEND_LOWER_CONTROL_HEIGHT = 0.45;
const float BEND_UPPER_CONTROL_HEIGHT = 0.82;
const float BEND_LOWER_CONTROL_FRACTION = 0.0;
const float BEND_UPPER_CONTROL_FRACTION = 0.22;
const float BEND_TIP_FRACTION = 1.0;
const float FLUTTER_SPEED_STEPS = 64.0;
const float FLUTTER_SPEED_MIN_STEP = 58.0;
const float FLUTTER_SPEED_MAX_STEP = 70.0;
const float CLUMP_COORD_SCALE = 2.0;
const float WIND_CURVE_ROTATION = 0.4;
const float WIND_CURVE_PEAK_ROTATION = 0.52;
const float WIND_CURVE_LOWER_ROTATION_FRACTION = 0.30;
const float WIND_CURVE_UPPER_ROTATION_FRACTION = 0.72;

const float REST_CURVE_ROTATION_MIN = -0.52;
const float REST_CURVE_ROTATION_MAX = 0.52;
const float REST_CURVE_FADE_END = 0.35;

const float HEIGHT_NOISE_SCALE = 1.0;
const float HEIGHT_NOISE_CONTRAST = 1.35;
const float HEIGHT_NOISE_BIAS = 0.5;
const float HEIGHT_NOISE_THIN_WIDTH = 0.65;
// Regional blade length comes from the stationary height noise alone: broad tall/short patches that
// stay put. REGIONAL_LENGTH_FRACTION damps the term so the noise reads as patches rather than
// cliffs. GrassComputeAnimator and GrassClumpField mirror all three -- keep the values in sync.
const float HEIGHT_NOISE_SHORT_MULTIPLIER = 0.1;
const float HEIGHT_NOISE_TALL_MULTIPLIER = 1.28;
const float REGIONAL_LENGTH_FRACTION = 0.59;

// Floor for the final blade length, in blocks. The regional and per-blade terms are both negative in
// short regions and HeightVariation scales them past -1x baseBladeLength, which would invert the
// blade and hang it below the block. GrassClumpField applies the same floor on the CPU.
const float MIN_BLADE_LENGTH = 0.02;

const float TALL_PLANT_LENGTH_MULTIPLIER = 3.0;

// Segmented-style band count follows final blade length so short blades don't squish every band
// into a sliver. Roughly constant world-space band thickness, capped at the full-height count.
const float SEGMENT_MAX_COUNT = 4.0;
const float SEGMENT_MIN_BAND_HEIGHT = 0.14;

const float LEAN_RESPONSE_MIN = 0.3;
const float LEAN_RESPONSE_MAX = 0.78;

const float BLADE_BASE_WIDTH = 0.060;
const float BLADE_MIDDLE_WIDTH = 0.045;
const float BLADE_TIP_WIDTH = 0.0075;
const float BLADE_SPLIT_FRACTION = 0.6666667;
const float SEGMENTED_WIDTH = 0.050;
const float TAPERED_WIDTH_SCALE = 0.8333333;
const float DEFAULT_ANIMATION_BLADE_LENGTH = 0.5075;

const float TRAIL_WIND_SUPPRESSION = 0.95;
const float TRAIL_CURVE_SUPPRESSION = 0.9;
const float TRAIL_FLUTTER_SUPPRESSION = 1.0;

// Stable per-blade height variation.
// Keep this subtle; too much starts looking noisy/furry.
const float PER_BLADE_HEIGHT_MIN = 0.88;
const float PER_BLADE_HEIGHT_MAX = 1.14;
const float PER_BLADE_WIDTH_COMPENSATION = 0.12;

// Low-frequency clump coherence, inspired by Ghost of Tsushima's Voronoi grass clumps.
// This keeps nearby blades sharing some width/posture character instead of looking like independent
// random samples. Clump deliberately does not touch length -- height comes from the stationary
// height noise alone, so clumps don't read as domes. The Voronoi field (grid scale 6, jitter 0.72)
// is precomputed into Sampler3's G/B/A by tools/bake_clump.py; see sampleClump. Repeats with the
// 32-block noise tile.
const float CLUMP_WIDTH_MIN = 0.92;
const float CLUMP_WIDTH_MAX = 1.08;
const float CLUMP_LEAN_COHESION = 0.42;
const float CLUMP_REST_COHESION = 0.55;
// Resting lean toward the clump centre at the rim (radians). A sub-linear distance curve spreads
// visible curvature farther into each clump while keeping the exact centre upright and rim unchanged.
const float CLUMP_INWARD_ROTATION = 0.21;
const float CLUMP_INWARD_DISTANCE_CURVE = 0.55;

// Subtle camera-facing width turn. This makes grass look fuller without full billboarding.
const float CAMERA_FACE_STRENGTH = 0.38;
const float CAMERA_FACE_START_DISTANCE = 2.0;
const float CAMERA_FACE_END_DISTANCE = 42.0;
const float CAMERA_FACE_MIN_HEIGHT = 0.08;
const float MAX_LIGHTMAP_SKY = 240.0;

vec2 wrappedPixelCenter(vec2 pixel, vec2 textureSizePixels) {
    return (mod(pixel, textureSizePixels) + 0.5) / textureSizePixels;
}

float sampleSmoothNoise(vec2 uv) {
    vec2 size = vec2(textureSize(Sampler3, 0));
    vec2 pixel = fract(uv) * size - 0.5;
    vec2 base = floor(pixel);
    vec2 blend = smoothstep(vec2(0.0), vec2(1.0), fract(pixel));
    // Warp the hardware-linear lookup through smoothstep. This preserves the
    // optimized single fetch while removing velocity jumps at texel boundaries.
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

float stationaryHeightNoise(vec2 coord) {
    float noise = sampleSmoothNoise(coord * HEIGHT_NOISE_SCALE);
    noise = clamp((noise - HEIGHT_NOISE_BIAS) * HEIGHT_NOISE_CONTRAST + HEIGHT_NOISE_BIAS, 0.0, 1.0);
    return smoothstep(0.0, 1.0, noise);
}

// Half-width around the spine. Pass BLADE_TIP_WIDTH to recover the baked centerline (mirrors the CPU
// bake -- use this for the centerline subtract); pass tipWidth 0.0 for the drawn blade so a tapered
// tip ends in a true point instead of a flat top edge.
float bladeWidth(float h, float tipWidth) {
    if (StyleVanilla > 0.5) {
        return SEGMENTED_WIDTH * BladeWidth;
    }

    float taperedWidth = BladeWidth * TAPERED_WIDTH_SCALE;
    if (h <= BLADE_SPLIT_FRACTION) {
        return mix(BLADE_BASE_WIDTH, BLADE_MIDDLE_WIDTH, h / BLADE_SPLIT_FRACTION) * taperedWidth;
    }

    return mix(
        BLADE_MIDDLE_WIDTH,
        tipWidth,
        (h - BLADE_SPLIT_FRACTION) / (1.0 - BLADE_SPLIT_FRACTION)
    ) * taperedWidth;
}

float hash2D(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

// clump.x/y/z (per-blade Voronoi character) is baked into Sampler3's G/B/A by tools/bake_clump.py, so
// the 3x3 Voronoi scan doesn't run per vertex. Point-fetch at the texel center so the per-cell values
// don't bilinear-smear across clump borders, independent of the sampler filter.
vec3 sampleClump(vec2 coord) {
    vec2 textureSizePixels = vec2(textureSize(Sampler3, 0));
    vec2 pixel = floor(fract(coord) * textureSizePixels);
    return texture(Sampler3, wrappedPixelCenter(pixel, textureSizePixels)).gba;
}

// Direction from a blade toward its clump centre. The baked distance field (clump.z, the A channel)
// rises away from the centre, so its negative gradient points inward. Central differences a few texels
// apart lift the gradient above 8-bit quantisation; a clump cell (~85 texels) is far wider, so the
// samples stay inside it. Returns 0 on the ridge between cells, where the inward direction is undefined.
vec2 clumpToCenterDir(vec2 coord) {
    vec2 texStep = 2.0 / vec2(textureSize(Sampler3, 0));
    float zxp = sampleClump(coord + vec2(texStep.x, 0.0)).z;
    float zxn = sampleClump(coord - vec2(texStep.x, 0.0)).z;
    float zyp = sampleClump(coord + vec2(0.0, texStep.y)).z;
    float zyn = sampleClump(coord - vec2(0.0, texStep.y)).z;
    vec2 grad = vec2(zxp - zxn, zyp - zyn);
    float len = length(grad);
    return len > 0.0001 ? -grad / len : vec2(0.0);
}

vec3 cubicBezier(vec3 p0, vec3 p1, vec3 p2, vec3 p3, float t) {
    float invT = 1.0 - t;

    return invT * invT * invT * p0
         + 3.0 * invT * invT * t * p1
         + 3.0 * invT * t * t * p2
         + t * t * t * p3;
}

vec3 rotatedSpinePoint(
        vec3 basePoint,
        vec2 dir,
        float visualLength,
        float animationLength,
        float height,
        float rotation) {
    vec3 point = basePoint;
    point.xz += dir * (sin(rotation) * animationLength * height);
    point.y += cos(rotation) * visualLength * height;
    return point;
}

float windCurveResponse(float rotation) {
    float rise = smoothstep(0.0, WIND_CURVE_PEAK_ROTATION, rotation);
    float fall = 1.0 - smoothstep(WIND_CURVE_PEAK_ROTATION, MAX_ROTATION_RADIANS, rotation);
    return rise * fall;
}

vec2 cameraFacingSideDir(vec3 center, vec2 originalSideDir, float height) {
    // Position + SectionOffset is camera-relative in this pipeline.
    vec2 toCamera = -center.xz;
    float dist = length(toCamera);

    if (dist < 0.001 || height < CAMERA_FACE_MIN_HEIGHT) {
        return originalSideDir;
    }

    toCamera /= dist;

    // Horizontal side direction for a vertical quad whose face points toward the camera.
    vec2 cameraSideDir = vec2(-toCamera.y, toCamera.x);

    // Keep the result in the same hemisphere as the baked side direction to avoid flips.
    if (dot(cameraSideDir, originalSideDir) < 0.0) {
        cameraSideDir = -cameraSideDir;
    }

    // Only help when the blade is relatively edge-on to the camera.
    float edgeOn = 1.0 - abs(dot(originalSideDir, cameraSideDir));

    // Fade out with distance to reduce far-field shimmer while rotating.
    float distanceFade = 1.0 - smoothstep(
        CAMERA_FACE_START_DISTANCE,
        CAMERA_FACE_END_DISTANCE,
        dist
    );

    float amount = CAMERA_FACE_STRENGTH * edgeOn * distanceFade;
    return normalize(mix(originalSideDir, cameraSideDir, amount));
}

float skyWindResponse(ivec2 lightUv) {
    return clamp(float(lightUv.y) / MAX_LIGHTMAP_SKY, 0.0, 1.0);
}

void main() {
    float windIntensity = max(WindStrength, 0.0) * skyWindResponse(UV2);
    float windAmount = clamp(windIntensity, 0.0, 1.0);
    float noiseBias = clamp(windIntensity / MAX_WIND_INTENSITY, 0.0, 1.0);

    vec2 windDir = length(WindDirection) > 0.001
            ? normalize(WindDirection)
            : vec2(1.0, 0.0);

    vec3 pos = Position + SectionOffset;
    float height = clamp(Normal.z, 0.0, 1.0);

    float packedBladeData = floor(Color.a * 255.0 + 0.5);
    float rightSide = step(128.0, packedBladeData);
    float sideSign = mix(-1.0, 1.0, rightSide);

    float bladeDataWithoutSide = packedBladeData - rightSide * 128.0;
    float heightClass = floor(bladeDataWithoutSide / 32.0);
    float angleBucket = bladeDataWithoutSide - heightClass * 32.0;

    float sideAngle = angleBucket / 31.0 * 6.2831853;
    vec2 originalSideDir = vec2(cos(sideAngle), sin(sideAngle));

    noiseCoord = Normal.xy * 0.5 + 0.5;

    // Regional + per-blade height/width.
    float heightNoise = stationaryHeightNoise(noiseCoord);
    vec2 clumpCoord = noiseCoord * CLUMP_COORD_SCALE;
    vec3 clump = sampleClump(clumpCoord);
    float clumpInfluence = 0.6 + 0.4 * (1.0 - smoothstep(0.25, 0.85, clump.z));
    float clumpWidthMultiplier = mix(1.0, mix(CLUMP_WIDTH_MIN, CLUMP_WIDTH_MAX,
            smoothstep(0.0, 1.0, clump.y)), clumpInfluence);

    // Short grass/fern (heightClass 1) render at grass-field height; only tall grass stays taller.
    float bladeLengthMultiplier = heightClass < 1.5 ? 1.0 : TALL_PLANT_LENGTH_MULTIPLIER;

    float baseBladeLength = BladeVisualHeight * bladeLengthMultiplier;

    // Stable random height per blade. It uses world-locked noiseCoord plus packed blade identity,
    // so it does not shimmer frame-to-frame.
    float bladeHeightRandom = hash2D(
        noiseCoord + vec2(angleBucket * 0.071, heightClass * 13.37)
    );

    float shapedBladeRandom = smoothstep(0.0, 1.0, bladeHeightRandom);

    float perBladeHeightMultiplier = mix(
        PER_BLADE_HEIGHT_MIN,
        PER_BLADE_HEIGHT_MAX,
        shapedBladeRandom
    );

    // Shorter blades become slightly wider; taller blades slightly thinner.
    float perBladeWidthMultiplier = mix(
        1.0 + PER_BLADE_WIDTH_COMPENSATION,
        1.0 - PER_BLADE_WIDTH_COMPENSATION,
        shapedBladeRandom
    );

    float regionalWidth = mix(HEIGHT_NOISE_THIN_WIDTH, 1.0, heightNoise)
            * clumpWidthMultiplier
            * perBladeWidthMultiplier;

    // Regional length: broad stationary tall/short patches from the height noise, damped so the
    // patches read as gentle regions. Clump no longer contributes to length at all.
    float heightMultiplier = mix(HEIGHT_NOISE_SHORT_MULTIPLIER, HEIGHT_NOISE_TALL_MULTIPLIER,
            heightNoise);
    float regionalExtraLength = baseBladeLength * (heightMultiplier - 1.0)
            * REGIONAL_LENGTH_FRACTION;
    float perBladeExtraLength = baseBladeLength * (perBladeHeightMultiplier - 1.0);
    // HeightVariation is the user height-variation scale: 1.0 = default look, 0 = uniform blades.
    float totalExtraLength = (regionalExtraLength + perBladeExtraLength) * HeightVariation;

    // Clamp the length, then feed the clamp back into the displacement below. Both terms go negative
    // in short regions and HeightVariation scales them past -1x baseBladeLength, so displacing by the
    // raw value would put the tip below the root.
    float bladeLength = max(baseBladeLength + totalExtraLength, MIN_BLADE_LENGTH);
    totalExtraLength = bladeLength - baseBladeLength;

    bladeHeight = height;

    // Keep the root planted because base vertices have height == 0.
    pos.y += totalExtraLength * height;

    float bakedWidth = bladeWidth(height, BLADE_TIP_WIDTH);
    // Both styles now draw a constant-width strip; the silhouette (taper vs rectangle) comes from the
    // Sampler5 shape texture in the fragment shader, not geometry. bakedWidth stays tapered so the
    // centerline recovered from the CPU-baked quad (which is still tapered) doesn't drift.
    float drawnWidth = StyleVanilla > 0.5
            ? SEGMENTED_WIDTH * BladeWidth
            : BLADE_BASE_WIDTH * BladeWidth * TAPERED_WIDTH_SCALE;
    float width = drawnWidth * regionalWidth;

    vec3 sourceCenter = pos;
    sourceCenter.xz -= originalSideDir * sideSign * bakedWidth;

    vec2 noiseUv = noiseCoord * NoiseScale + NoiseScrollOffset;
    float windNoise = sampleSmoothNoise(noiseUv);

    // Old brightness shimmer: shape the same scrolling noise and hand it to the fragment shader.
    float shapedWave = clamp((windNoise - COLOR_WAVE_BIAS) * COLOR_WAVE_CONTRAST + COLOR_WAVE_BIAS, 0.0, 1.0);
    colorWave = smoothstep(-COLOR_WAVE_SOFTNESS, 1.0 + COLOR_WAVE_SOFTNESS, shapedWave);

    float localWind = mix(windNoise, 1.0, noiseBias);
    float leanRandom = mix(
        hash2D(noiseCoord + vec2(43.17, 11.91)),
        clump.y,
        CLUMP_LEAN_COHESION * clumpInfluence
    );
    float leanResponse = mix(
        LEAN_RESPONSE_MIN,
        LEAN_RESPONSE_MAX,
        leanRandom
    );

    float resistedWind = localWind * windAmount * leanResponse;
    float windRotation = clamp(resistedWind, 0.0, 1.0) * MAX_ROTATION_RADIANS;

    // Skip the trail fetch entirely when the trail system is inactive (strength 0). TrailParams.w
    // is a uniform, so this branch is coherent. Defaults leave every term downstream unchanged.
    vec2 trailDir = windDir;
    float trailEffect = 0.0;
    if (TrailParams.w > 0.0) {
        vec2 trailUv = (sourceCenter.xz - TrailParams.xy) * TrailParams.z;
        vec2 trailInside = step(vec2(0.0), trailUv) * step(trailUv, vec2(1.0));
        float trailMask = trailInside.x * trailInside.y;

        vec4 trailSample = sampleSmoothTrail(trailUv);

        vec2 sampledDir = trailSample.rg * 2.0 - 1.0;
        float sampledLen = length(sampledDir);
        trailDir = sampledLen > 0.001 ? sampledDir / sampledLen : windDir;

        trailEffect = clamp(trailSample.b * trailMask * TrailParams.w, 0.0, 1.0);
    }

    float trailWindMultiplier = 1.0 - trailEffect * TRAIL_WIND_SUPPRESSION;
    resistedWind *= trailWindMultiplier;
    windRotation *= trailWindMultiplier;

    float trailRotation = trailEffect * MAX_ROTATION_RADIANS;

    // Resting tuft: lean each blade toward its clump centre so a clump reads as a rounded mound
    // instead of every blade curving the same way. Bias the centre-distance upward so middle blades
    // curve visibly too; rim blades still lean most, and wind takes over as it rises.
    vec2 toClumpCenter = clumpToCenterDir(clumpCoord);
    float curvedClumpDistance = pow(clamp(clump.z, 0.0, 1.0), CLUMP_INWARD_DISTANCE_CURVE);
    float inwardRotation = curvedClumpDistance * CLUMP_INWARD_ROTATION * (1.0 - windAmount);

    vec2 leanVector = windDir * windRotation + trailDir * trailRotation
            + toClumpCenter * inwardRotation;
    float leanLength = length(leanVector);
    float rotation = clamp(leanLength, 0.0, MAX_ROTATION_RADIANS);

    // Normalize by the true length, not the clamped rotation.
    vec2 leanDir = leanLength > 0.001 ? leanVector / leanLength : windDir;
    vec2 crossLeanDir = vec2(-leanDir.y, leanDir.x);

    windForceMagnitude = clamp(max(resistedWind, trailEffect), 0.0, 1.0);

    segmentCount = clamp(floor(bladeLength / SEGMENT_MIN_BAND_HEIGHT), 1.0, SEGMENT_MAX_COUNT);
    float animationBladeLength =
            max(bladeLength, DEFAULT_ANIMATION_BLADE_LENGTH * bladeLengthMultiplier);

    vec3 basePoint = sourceCenter;
    basePoint.y -= bladeLength * height;

    float windCurveRotation = WIND_CURVE_ROTATION * windCurveResponse(rotation);
    windCurveRotation *= 1.0 - trailEffect * TRAIL_CURVE_SUPPRESSION;

    float restCurveRandom = mix(
        hash2D(noiseCoord + vec2(7.13, 91.77)),
        clump.x,
        CLUMP_REST_COHESION * clumpInfluence
    );
    float restCurveFade = 1.0 - smoothstep(0.0, REST_CURVE_FADE_END, windForceMagnitude);

    float restCurveRotation = mix(
        REST_CURVE_ROTATION_MIN,
        REST_CURVE_ROTATION_MAX,
        restCurveRandom
    ) * restCurveFade;

    restCurveRotation *= 1.0 - trailEffect * TRAIL_CURVE_SUPPRESSION;

    float tipRotation = rotation + windCurveRotation + restCurveRotation;
    vec3 tipPoint = rotatedSpinePoint(
        basePoint,
        leanDir,
        bladeLength,
        animationBladeLength,
        1.0,
        tipRotation
    );

    float bendCalmness = 1.0 - smoothstep(0.25, 0.85, windForceMagnitude);
    // Stable, independently salted phase per blade. Use the full circle so neighbouring
    // blades do not inherit visibly coherent timing from their shared clump.
    float bendPhase = hash2D(noiseCoord + vec2(
        angleBucket * 0.371 + 19.19,
        heightClass * 7.13 + 53.71
    )) * 6.2831853;
    float speedRandom = hash2D(noiseCoord + vec2(
        angleBucket * 1.913 + 73.17,
        heightClass * 11.71 + 29.43
    ));
    float speedStep = floor(mix(
        FLUTTER_SPEED_MIN_STEP,
        FLUTTER_SPEED_MAX_STEP + 1.0,
        speedRandom
    ));
    float bendWave = sin(WindFlutterPhase * (speedStep / FLUTTER_SPEED_STEPS) + bendPhase);

    float bend = bendWave * BEND_AMOUNT * animationBladeLength * bendCalmness;
    bend *= 1.0 - trailEffect * TRAIL_FLUTTER_SUPPRESSION;

    vec2 bendOffset = crossLeanDir * bend;

    vec3 lowerControlPoint = rotatedSpinePoint(
        basePoint,
        leanDir,
        bladeLength,
        animationBladeLength,
        BEND_LOWER_CONTROL_HEIGHT,
        rotation
            + windCurveRotation * WIND_CURVE_LOWER_ROTATION_FRACTION
            + restCurveRotation * WIND_CURVE_LOWER_ROTATION_FRACTION
    );

    lowerControlPoint.xz += bendOffset * BEND_LOWER_CONTROL_FRACTION;

    vec3 upperControlPoint = rotatedSpinePoint(
        basePoint,
        leanDir,
        bladeLength,
        animationBladeLength,
        BEND_UPPER_CONTROL_HEIGHT,
        rotation
            + windCurveRotation * WIND_CURVE_UPPER_ROTATION_FRACTION
            + restCurveRotation * WIND_CURVE_UPPER_ROTATION_FRACTION
    );

    upperControlPoint.xz += bendOffset * BEND_UPPER_CONTROL_FRACTION;

    tipPoint.xz += bendOffset * BEND_TIP_FRACTION;

    vec3 bladeCenter = cubicBezier(
        basePoint,
        lowerControlPoint,
        upperControlPoint,
        tipPoint,
        height
    );

    pos = bladeCenter;

    vec2 visualSideDir = cameraFacingSideDir(sourceCenter, originalSideDir, height);
    pos.xz += visualSideDir * sideSign * width;

    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
    vertexDistance = fog_distance(pos, FogShape);
    vertexColor = vec4(Color.rgb, 1.0) * minecraft_sample_lightmap(Sampler2, UV2);
    texCoord0 = UV0;
}
