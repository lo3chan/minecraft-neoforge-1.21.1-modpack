/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 */
package net.diebuddies.physics.smoke;

import com.google.common.base.Supplier;

public enum SmokeShadowTransformer {
    DISABLED("physicsmod.gui.disabled", (Supplier<String>)((Supplier)SmokeShadowTransformer::getEmptyTransformer)),
    COMPLEMENTARY("Complementary", (Supplier<String>)((Supplier)SmokeShadowTransformer::getComplementaryReimaginedShadowTransformer)),
    BSL("BSL", (Supplier<String>)((Supplier)SmokeShadowTransformer::getBSLShadowTransformer)),
    SEUS_RENEWED("SEUS Renewed", (Supplier<String>)((Supplier)SmokeShadowTransformer::getSEUSRenewedShadowTransformer)),
    NOSTALGIA_VX("Nostalgia VX", (Supplier<String>)((Supplier)SmokeShadowTransformer::getNostalgiaVXShadowTransformer)),
    SILDUR_LITE("Sildur Lite", (Supplier<String>)((Supplier)SmokeShadowTransformer::getSildurLiteShadowTransformer)),
    SILDUR_MEDIUM("Sildur Medium", (Supplier<String>)((Supplier)SmokeShadowTransformer::getSildurMediumShadowTransformer)),
    SILDUR_HIGH("Sildur High", (Supplier<String>)((Supplier)SmokeShadowTransformer::getSildurHighShadowTransformer)),
    SILDUR_EXTREME("Sildur Extreme", (Supplier<String>)((Supplier)SmokeShadowTransformer::getSildurExtremeShadowTransformer)),
    ASTRA_LEX("AstraLex", (Supplier<String>)((Supplier)SmokeShadowTransformer::getAstraLexShadowTransformer)),
    MAKE_UP("MakeUp", (Supplier<String>)((Supplier)SmokeShadowTransformer::getMakeUpShadowTransformer)),
    VANILLA_PLUS("Vanilla Plus", (Supplier<String>)((Supplier)SmokeShadowTransformer::getVanillaPlusShadowTransformer));

    private String translationId;
    private Supplier<String> transformer;

    private SmokeShadowTransformer(String translationId, Supplier<String> transformer) {
        this.translationId = translationId;
        this.transformer = transformer;
    }

    public Supplier<String> getTransformer() {
        return this.transformer;
    }

    public String toString() {
        return this.translationId;
    }

    private static String getComplementaryReimaginedShadowTransformer() {
        return "const float shadowDistance = 192.0;\nconst float shadowMapBias = 1.0 - 25.6 / shadowDistance;\nfloat lVertexPos = sqrt(gl_Position.x * gl_Position.x + gl_Position.y * gl_Position.y);\nfloat distortFactor = lVertexPos * shadowMapBias + (1.0 - shadowMapBias);\ngl_Position.xy *= 1.0 / distortFactor;\ngl_Position.z = gl_Position.z * 0.2;\n";
    }

    private static String getBSLShadowTransformer() {
        return "const float shadowDistance = 256.0;\nconst float shadowMapBias = 1.0 - 25.6 / shadowDistance;\nfloat lVertexPos = sqrt(gl_Position.x * gl_Position.x + gl_Position.y * gl_Position.y);\nfloat distortFactor = lVertexPos * shadowMapBias + (1.0 - shadowMapBias);\ngl_Position.xy *= 1.0 / distortFactor;\ngl_Position.z = gl_Position.z * 0.2;\n";
    }

    private static String getSEUSRenewedShadowTransformer() {
        return "#define SHADOW_MAP_BIAS 0.90\nfloat dist = sqrt(gl_Position.x * gl_Position.x + gl_Position.y * gl_Position.y);\nfloat distortFactor = (1.0f - SHADOW_MAP_BIAS) + dist * SHADOW_MAP_BIAS + 0.0;\ngl_Position.xy *= 0.95f / distortFactor;\ngl_Position.z = mix(gl_Position.z, 0.5, 0.8);\n";
    }

    private static String getNostalgiaVXShadowTransformer() {
        return "#define shadowmap_bias 0.85\nfloat distortion = length(gl_Position.xy * 1.169) * shadowmap_bias + (1.0 - shadowmap_bias);\ngl_Position.xy = gl_Position.xy / distortion;\ngl_Position.z *= 0.2;\ngl_Position.x = gl_Position.x * 0.5 + 0.5;\ngl_Position.xy = gl_Position.yx;\n";
    }

    private static String getSildurLiteShadowTransformer() {
        return "const float k = 1.8;\nconst float shadowDistance = 70.0;\n#define Nearshadowplane 0.05\n#define Farshadowplane 1.4\nfloat a = exp(Nearshadowplane);\nfloat b = (exp(Farshadowplane) - a) * shadowDistance / 128.0;\n\nfloat distortion = log(length(gl_Position.xy) * b + a) * k;\ngl_Position.xy = gl_Position.xy / distortion;\ngl_Position.z /= 6.0;\n";
    }

    private static String getSildurMediumShadowTransformer() {
        return "const float k = 1.8;\nconst float shadowDistance = 80.0;\n#define Nearshadowplane 0.05\n#define Farshadowplane 1.2\nfloat a = exp(Nearshadowplane);\nfloat b = (exp(Farshadowplane) - a) * shadowDistance / 128.0;\n\nfloat distortion = log(length(gl_Position.xy) * b + a) * k;\ngl_Position.xy = gl_Position.xy / distortion;\ngl_Position.z /= 6.0;\n";
    }

    private static String getSildurHighShadowTransformer() {
        return "const float k = 1.8;\nconst float shadowDistance = 80.0;\n#define Nearshadowplane 0.05\n#define Farshadowplane 1.0\nfloat a = exp(Nearshadowplane);\nfloat b = (exp(Farshadowplane) - a) * shadowDistance / 128.0;\n\nfloat distortion = log(length(gl_Position.xy) * b + a) * k;\ngl_Position.xy = gl_Position.xy / distortion;\ngl_Position.z /= 6.0;\n";
    }

    private static String getSildurExtremeShadowTransformer() {
        return "const float k = 1.8;\nconst float shadowDistance = 110.0;\n#define Nearshadowplane 0.05\n#define Farshadowplane 0.8\nfloat a = exp(Nearshadowplane);\nfloat b = (exp(Farshadowplane) - a) * shadowDistance / 128.0;\n\nfloat distortion = log(length(gl_Position.xy) * b + a) * k;\ngl_Position.xy = gl_Position.xy / distortion;\ngl_Position.z /= 6.0;\n";
    }

    private static String getAstraLexShadowTransformer() {
        return "const float shadowDistance = 192.0;\nconst float shadowMapBias = 1.0 - 25.6 / shadowDistance;\nfloat dist = sqrt(gl_Position.x * gl_Position.x + gl_Position.y * gl_Position.y);\nfloat distortFactor = dist * shadowMapBias + (1.0 - shadowMapBias);\ngl_Position.xy *= 1.0 / distortFactor;\ngl_Position.z = gl_Position.z * 0.2;\n";
    }

    private static String getMakeUpShadowTransformer() {
        return "#define SHADOW_DIST 0.8\nfloat distortion = ((1.0 - SHADOW_DIST) + length(gl_Position.xy * 1.25) * SHADOW_DIST) * 0.85;\ngl_Position.xy /= distortion;\ngl_Position.z -= 0.001;\n";
    }

    private static String getVanillaPlusShadowTransformer() {
        return "#define shadowmapDistortionBias 0.85\nfloat distortion = length(gl_Position.xy * 1.169) * shadowmapDistortionBias + (1.0 - shadowmapDistortionBias);\ngl_Position.xy /= distortion;\ngl_Position.z *= shadowMapDepthConst;\n";
    }

    private static String getEmptyTransformer() {
        return "";
    }
}

