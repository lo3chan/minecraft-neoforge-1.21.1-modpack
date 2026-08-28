/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import javax.annotation.Nullable;
import net.diebuddies.compat.Iris;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.util.GLSLModifier;
import org.apache.commons.lang3.StringUtils;

public class ShaderInjectionOcean {
    private static String position = "gl_Vertex";
    private static String normalMatrix = "gl_NormalMatrix";
    private static String modelViewProjectionMatrix = "gl_ModelViewProjectionMatrix";
    private static String outputColor0 = "gl_FragData[0]";
    private static String outputColor1 = "gl_FragData[1]";
    private static String outputColor2 = "gl_FragData[2]";
    private static String outputColor3 = "gl_FragData[3]";
    private static String outputColor4 = "gl_FragData[4]";
    private static String lmCoord = "(mat4(vec4(0.00390625, 0.0, 0.0, 0.0), vec4(0.0, 0.00390625, 0.0, 0.0), vec4(0.0, 0.0, 0.00390625, 0.0), vec4(0.03125, 0.03125, 0.03125, 1.0)) * gl_MultiTexCoord1).xy";

    private static void setupOptifineInjection() {
        position = "vec4(vaPosition + chunkOffset, 1.0)";
        normalMatrix = "normalMatrix";
        modelViewProjectionMatrix = "(projectionMatrix * modelViewMatrix)";
        outputColor0 = "outColor0";
        outputColor1 = "outColor1";
        outputColor2 = "outColor2";
        outputColor3 = "outColor3";
        outputColor4 = "outColor4";
        lmCoord = "(mat4(vec4(0.00390625, 0.0, 0.0, 0.0), vec4(0.0, 0.00390625, 0.0, 0.0), vec4(0.0, 0.0, 0.00390625, 0.0), vec4(0.03125, 0.03125, 0.03125, 1.0)) * vec4(vaUV2, 0.0, 1.0)).xy";
    }

    @Nullable
    public static String getVertexSource(String vertexSource) {
        if (StarterClient.optifabric) {
            ShaderInjectionOcean.setupOptifineInjection();
        }
        if (vertexSource == null) {
            return null;
        }
        return ShaderInjectionOcean.transformVertexOceanShader(ShaderInjectionOcean.evaluatePack(vertexSource), vertexSource);
    }

    private static ShaderPack evaluatePack(String source) {
        if (source.contains("// Complementary Shaders by EminGT //")) {
            return ShaderPack.COMPLEMENTARY_REIMAGINED;
        }
        if (source.contains("Complementary Shaders by EminGT")) {
            return ShaderPack.COMPLEMENTARY;
        }
        if (source.contains("Complementary Reimagined by EminGT")) {
            return ShaderPack.COMPLEMENTARY_REIMAGINED;
        }
        return ShaderPack.OTHER;
    }

    @Nullable
    public static String getFragmentSource(String fragmentSource, boolean shadowShader) {
        if (StarterClient.optifabric) {
            ShaderInjectionOcean.setupOptifineInjection();
        }
        if (fragmentSource == null) {
            return null;
        }
        if (!shadowShader) {
            if (StarterClient.iris) {
                Iris.oceanError = "";
            }
            return ShaderInjectionOcean.transformFragmentOceanShader(ShaderInjectionOcean.evaluatePack(fragmentSource), fragmentSource);
        }
        fragmentSource = GLSLModifier.removeComments(fragmentSource);
        return GLSLModifier.insertBeforeFirstFunction(fragmentSource, "in vec3 physics_localPosition;\nin vec3 physics_foamColor;\nin float physics_localWaviness;\n");
    }

    private static String transformVertexOceanShader(ShaderPack pack, String vertex) {
        vertex = GLSLModifier.removeComments(vertex);
        vertex = StringUtils.replace((String)vertex, (String)position, (String)"physics_finalPosition");
        vertex = StringUtils.replace((String)vertex, (String)"physics_finalPositionID", (String)"gl_VertexID");
        boolean addOptifineUV2 = StarterClient.optifabric && !vertex.contains("vaUV2");
        List<String> oceanInjection = ShaderInjectionOcean.getOceanWaveInjection(true);
        oceanInjection.add("out vec3 physics_localPosition;");
        oceanInjection.add("out vec3 physics_foamColor;");
        oceanInjection.add("out float physics_localWaviness;");
        oceanInjection.add("vec4 physics_finalPosition;");
        if (addOptifineUV2) {
            oceanInjection.add("in ivec2 vaUV2;");
        }
        vertex = GLSLModifier.insertBeforeFirstFunction(vertex, GLSLModifier.convertToString(oceanInjection));
        vertex = GLSLModifier.replaceFunctionCalls(vertex, "ftransform", "(" + modelViewProjectionMatrix + " * physics_finalPosition)");
        vertex = GLSLModifier.insertAtFunctionStart(vertex, "main", "physics_foamColor = textureLod(physics_lightmap, " + lmCoord + ", 0).rgb;\nphysics_localWaviness = texelFetch(physics_waviness, ivec2(" + position + ".xz) - physics_textureOffset, 0).r;\nphysics_finalPosition = vec4(" + position + ".x, " + position + ".y + physics_waveHeight(" + position + ".xz, PHYSICS_ITERATIONS_OFFSET, physics_localWaviness, physics_gameTime), " + position + ".z, " + position + ".w);\nphysics_localPosition = physics_finalPosition.xyz;\n");
        return vertex;
    }

    private static String transformFragmentOceanShader(ShaderPack pack, String fragment) {
        fragment = GLSLModifier.removeComments(fragment);
        boolean injectNormalMatrix = StarterClient.optifabric && !fragment.contains(normalMatrix);
        List<String> oceanInjection = ShaderInjectionOcean.getOceanWaveInjection();
        oceanInjection.add("in vec3 physics_localPosition;");
        oceanInjection.add("in vec3 physics_foamColor;");
        oceanInjection.add("in float physics_localWaviness;");
        oceanInjection.add("WavePixelData physics_waveData;");
        if (injectNormalMatrix) {
            oceanInjection.add("uniform mat3 normalMatrix;");
        }
        fragment = GLSLModifier.insertBeforeFirstFunction(fragment, GLSLModifier.convertToString(oceanInjection));
        if (pack == ShaderPack.COMPLEMENTARY) {
            fragment = ShaderInjectionOcean.complementary(fragment);
        } else if (pack == ShaderPack.COMPLEMENTARY_REIMAGINED) {
            fragment = ShaderInjectionOcean.complementaryReimagined(fragment);
        } else if (pack == ShaderPack.OTHER) {
            fragment = ShaderInjectionOcean.other(fragment);
        }
        return fragment;
    }

    private static void printError() {
        if (StarterClient.iris) {
            Iris.oceanError = "This shader (or shaderpack settings) is not supported by ocean physics!";
        }
    }

    private static String complementary(String source) {
        boolean hasWaveNormalFunction = GLSLModifier.hasFunction(source, "GetWaterNormal");
        if (!hasWaveNormalFunction) {
            ShaderInjectionOcean.printError();
            return source;
        }
        source = GLSLModifier.replaceFunctionContent(source, "GetWaterNormal", "return normalize(" + normalMatrix + " * physics_waveData.normal);\n");
        source = GLSLModifier.insertAtFunctionStart(source, "main", "physics_waveData = physics_wavePixel(physics_localPosition.xz, physics_localWaviness, physics_iterationsNormal, physics_gameTime);\n");
        source = GLSLModifier.insertAtFunctionEnd(source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n");
        source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
        source = GLSLModifier.replaceVariableReferences(source, "VdotN", "dot(nViewPos, normalize(newNormal))");
        source = StringUtils.replace((String)source, (String)"PHYSICS_NORMAL_STRENGTH = 0.6", (String)"PHYSICS_NORMAL_STRENGTH = 1.4");
        return source;
    }

    private static String complementaryReimagined(String source) {
        source = GLSLModifier.replaceFunctionContent(source, "GetCustomMaterials", "");
        source = GLSLModifier.insertAtFunctionStart(source, "main", "physics_waveData = physics_wavePixel(physics_localPosition.xz, physics_localWaviness, physics_iterationsNormal, physics_gameTime);\nvec3 physics_normal = normalize(" + normalMatrix + " * physics_waveData.normal);\n");
        source = GLSLModifier.insertAtFunctionEnd(source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n");
        source = GLSLModifier.replaceWithinFunctionContent(source, "main", "clamp(normalize(normalMap", "clamp(normalize(physics_normal");
        source = GLSLModifier.replaceWithinFunctionContent(source, "main", "dot(normalM", "dot(physics_normal");
        source = GLSLModifier.replaceWithinFunctionContent(source, "main", "vec3 normalM = VdotN > 0.0 ? -normal : normal;", "vec3 normalM = VdotN > 0.0 ? -physics_normal : physics_normal;");
        source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
        source = GLSLModifier.replaceVariableReferences(source, "yPosDif", "-2.0");
        source = StringUtils.replace((String)source, (String)"PHYSICS_NORMAL_STRENGTH = 0.6", (String)"PHYSICS_NORMAL_STRENGTH = 1.4");
        return source;
    }

    private static String other(String source) {
        source = StringUtils.replace((String)source, (String)"PHYSICS_NORMAL_STRENGTH = 0.6", (String)"PHYSICS_NORMAL_STRENGTH = 1.4");
        source = GLSLModifier.insertAtFunctionStart(source, "main", "physics_waveData = physics_wavePixel(physics_localPosition.xz, physics_localWaviness, physics_iterationsNormal, physics_gameTime);\n");
        boolean hasWaveNormalFunction = GLSLModifier.hasFunction(source, "GetWavesNormal");
        String normalFunctionName = null;
        boolean ignoreNormalMatrix = false;
        if (hasWaveNormalFunction) {
            normalFunctionName = "GetWavesNormal";
            ignoreNormalMatrix = !source.contains("tbnMatrix");
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            source = GLSLModifier.replaceVariableReferences(source, "tbn", "mat3(1.0)");
            source = ignoreNormalMatrix ? GLSLModifier.insertAtFunctionEnd(source, "main", "if (physics_waveData.foam > 0.2) {\n" + outputColor1 + " = vec4(physics_foamColor, 1.0);\n" + outputColor0 + " = vec4(EncodeNormal(normal), EncodeVec2(lmcoord.x, lmcoord.y), EncodeVec2(1.0, f0), 1.0);\n}\n") : GLSLModifier.insertAtFunctionEnd(source, "main", "if (physics_waveData.foam > 0.2) { " + outputColor4 + " = vec4(physics_foamColor, 1.0); " + outputColor2 + ".b = 7.0 / 255.0; }\n");
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "calcBump"))) {
            normalFunctionName = "calcBump";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            source = GLSLModifier.insertAtFunctionEnd(source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n");
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "GetWaterNormal"))) {
            normalFunctionName = "GetWaterNormal";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            source = GLSLModifier.insertAtFunctionEnd(source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n");
            source = StringUtils.replace((String)source, (String)"float f0 = 0.0, porosity = 0.5, ao = 1.0, skyOcclusion = 0.0;", (String)("normalMap = normalize(" + normalMatrix + " * physics_waveData.normal);\nnewNormal = normalMap;\nfloat f0 = 0.0, porosity = 0.5, ao = 1.0, skyOcclusion = 0.0;"));
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getWaterNormal"))) {
            normalFunctionName = "getWaterNormal";
            if ((source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)")).contains("tbnMatrixWorld")) {
                ignoreNormalMatrix = true;
            } else {
                source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "newNormal = normalize(" + normalMatrix + " * physics_waveData.normal);\n");
                source = GLSLModifier.insertAtFunctionEnd(source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n");
                return source;
            }
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getWaveHeight"))) {
            normalFunctionName = "getWaveHeight";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            source = GLSLModifier.insertAtFunctionEnd(source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n" + outputColor1 + " = mix(" + outputColor1 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n");
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getWaveNormal"))) {
            normalFunctionName = "getWaveNormal";
            source = GLSLModifier.replaceWithinFunctionContent(source, "main", "viewToWorld(normal)", "normalize(physics_waveData.normal)");
            source = GLSLModifier.replaceWithinFunctionContent(source, "main", "bumpmult = 10.0 * 1.0;", "bumpmult = 1.0;");
            source = GLSLModifier.replaceWithinFunctionContent(source, "main", "applyBump(tbnMatrix, NormalTex.xyz, 1.0)", "normalize(" + normalMatrix + " * physics_waveData.normal)");
            source = GLSLModifier.insertAtFunctionEnd(source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n" + outputColor1 + " = mix(" + outputColor1 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n");
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "get_normals"))) {
            normalFunctionName = "get_normals";
            source = GLSLModifier.insertAtFunctionEnd(source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n");
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "noiseNormals"))) {
            normalFunctionName = "noiseNormals";
            source = GLSLModifier.replaceVariableReferences(source, "tbn", "mat3(1.0)");
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "waterNormal"))) {
            normalFunctionName = "waterNormal";
            source = source.contains("sceneDataB") ? GLSLModifier.insertAtFunctionEnd(source, "main", "translucentColor = clamp(mix(sceneColor, vec4(5.0 * physics_foamColor, 5.0), physics_waveData.foam), 0.0, 65535.0);\nreflectionAux = packReflectionAux(mix(directLight * directCol, vec3(physics_foamColor), physics_waveData.foam), mix(albedo, vec3(1.0), physics_waveData.foam));\n") : (source.contains("sceneData.xy    = encodeNormal(sceneNormal);") ? GLSLModifier.insertAtFunctionEnd(source, "main", "sceneColor = mix(sceneColor, vec4(physics_foamColor, 1.0), physics_waveData.foam);\n") : GLSLModifier.insertAtFunctionEnd(source, "main", "sceneColor = mix(sceneColor, vec4(5.0 * physics_foamColor, 5.0), physics_waveData.foam);\nlightingData = packReflectionAux(mix(directLight, vec3(physics_foamColor), physics_waveData.foam), mix(sceneTint.rgb, vec3(1.0), physics_waveData.foam));\n"));
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(physics_waveData.normal);\n");
            return source;
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getNormals"))) {
            normalFunctionName = "getNormals";
            ignoreNormalMatrix = true;
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(" + normalMatrix + " * physics_waveData.normal);\n");
            source = GLSLModifier.insertAtFunctionEnd(source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n");
            return source;
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "get_water_normal"))) {
            normalFunctionName = "get_water_normal";
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(physics_waveData.normal.xzy);\n");
            source = GLSLModifier.replaceVariableReferences(source, "tbn", "mat3(vec3(1.0, 0.0, 0.0),vec3(0.0, 0.0, 1.0),vec3(0.0, 1.0, 0.0))");
            if ((source = GLSLModifier.replaceFunctionCalls(source, "purkinje_shift", "purkinje_shift(mix(fragment_color.rgb, physics_foamColor, physics_waveData.foam), adjusted_light_levels)")).contains("vec3 color_to_store = is_water ? shadows : base_color.rgb;")) {
                source = GLSLModifier.insertAtFunctionEnd(source, "main", "gbuffer_data_0.z  = pack_unorm_2x8(encode_unit_vector(physics_waveData.normal.xyz));");
            }
            return source;
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "H2NWater"))) {
            normalFunctionName = "H2NWater";
            source = GLSLModifier.insertAtFunctionEnd(source, "main", "normalDataOut = physics_waveData.normal;\n");
            return source;
        }
        if (!hasWaveNormalFunction) {
            if (source.contains(outputColor1)) {
                source = GLSLModifier.insertAtFunctionEnd(source, "main", outputColor1 + " = vec4(normalize(" + normalMatrix + " * physics_waveData.normal).xyz, 1.0);\n");
            } else {
                ShaderInjectionOcean.printError();
            }
            return source;
        }
        source = ignoreNormalMatrix ? GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(physics_waveData.normal);\n") : GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(" + normalMatrix + " * physics_waveData.normal);\n");
        return source;
    }

    private static List<String> getOceanWaveInjection() {
        return ShaderInjectionOcean.getOceanWaveInjection(false);
    }

    private static List<String> getOceanWaveInjection(boolean vertex) {
        String asset = "assets/physicsmod/shaders/include/ocean" + (vertex ? "_vanilla" : "") + ".glsl";
        ObjectArrayList lines = new ObjectArrayList();
        Object all = "";
        int brackets = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(PhysicsMod.class.getClassLoader().getResourceAsStream(asset)));){
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                if (line.contains("{")) {
                    ++brackets;
                }
                if (line.contains("}")) {
                    --brackets;
                }
                all = (String)all + line;
                if (brackets != 0) continue;
                lines.add(all);
                all = "";
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static enum ShaderPack {
        COMPLEMENTARY,
        COMPLEMENTARY_REIMAGINED,
        OTHER;

    }
}

