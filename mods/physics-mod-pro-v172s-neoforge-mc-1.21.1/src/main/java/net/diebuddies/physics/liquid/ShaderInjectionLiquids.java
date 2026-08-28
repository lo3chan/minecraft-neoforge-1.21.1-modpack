/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package net.diebuddies.physics.liquid;

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

public class ShaderInjectionLiquids {
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
            ShaderInjectionLiquids.setupOptifineInjection();
        }
        if (vertexSource == null) {
            return null;
        }
        return ShaderInjectionLiquids.transformVertexLiquidShader(ShaderInjectionLiquids.evaluatePack(vertexSource), vertexSource);
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
    public static String getFragmentSource(String fragmentSource) {
        if (StarterClient.optifabric) {
            ShaderInjectionLiquids.setupOptifineInjection();
        }
        if (fragmentSource == null) {
            return null;
        }
        if (StarterClient.iris) {
            Iris.liquidsError = "";
        }
        return ShaderInjectionLiquids.transformFragmentLiquidShader(ShaderInjectionLiquids.evaluatePack(fragmentSource), fragmentSource);
    }

    @Nullable
    public static String getVertexShadowSource(String vertexSource) {
        if (StarterClient.optifabric) {
            ShaderInjectionLiquids.setupOptifineInjection();
        }
        if (vertexSource == null) {
            return null;
        }
        return ShaderInjectionLiquids.transformVertexLiquidShadowShader(ShaderInjectionLiquids.evaluatePack(vertexSource), vertexSource);
    }

    @Nullable
    public static String getFragmentShadowSource(String fragmentSource) {
        if (StarterClient.optifabric) {
            ShaderInjectionLiquids.setupOptifineInjection();
        }
        if (fragmentSource == null) {
            return null;
        }
        if (StarterClient.iris) {
            Iris.liquidsError = "";
        }
        return fragmentSource;
    }

    private static String transformVertexLiquidShadowShader(ShaderPack pack, String vertex) {
        vertex = GLSLModifier.removeComments(vertex);
        vertex = StringUtils.replace((String)vertex, (String)position, (String)"physics_finalPosition");
        vertex = StringUtils.replace((String)vertex, (String)"physics_finalPositionID", (String)"gl_VertexID");
        vertex = GLSLModifier.insertBeforeFirstFunction(vertex, "in vec4 physics_offset;\nin vec4 physics_offsetNew;\nuniform vec3 physics_liquidCameraPos;\nuniform float physics_renderPercent;\nvec4 physics_finalPosition;\n");
        vertex = GLSLModifier.insertAtFunctionStart(vertex, "main", "float physics_scale = physics_offsetNew.w;\nphysics_finalPosition = vec4(gl_Vertex.xyz * physics_scale + mix(physics_offset.xyz - physics_liquidCameraPos, physics_offsetNew.xyz - physics_liquidCameraPos, physics_renderPercent), 1.0);\n");
        vertex = GLSLModifier.replaceFunctionCalls(vertex, "ftransform", "(" + modelViewProjectionMatrix + " * physics_finalPosition)");
        return vertex;
    }

    private static String transformVertexLiquidShader(ShaderPack pack, String vertex) {
        vertex = GLSLModifier.removeComments(vertex);
        vertex = GLSLModifier.insertAtFunctionEnd(vertex, "main", "float physics_x = -1.0 + float((gl_VertexID & 1) << 2);\nfloat physics_y = -1.0 + float((gl_VertexID & 2) << 1);\ngl_Position = vec4(physics_x, physics_y, 0, 1);\n");
        return vertex;
    }

    private static String transformFragmentLiquidShader(ShaderPack pack, String fragment) {
        fragment = GLSLModifier.removeComments(fragment);
        boolean injectNormalMatrix = StarterClient.optifabric && !fragment.contains(normalMatrix);
        List<String> liquidsInjection = ShaderInjectionLiquids.getLiquidsInjection();
        fragment = GLSLModifier.insertBeforeFirstFunction(fragment, GLSLModifier.convertToString(liquidsInjection));
        if (pack == ShaderPack.COMPLEMENTARY) {
            fragment = ShaderInjectionLiquids.complementary(fragment);
        } else if (pack == ShaderPack.COMPLEMENTARY_REIMAGINED) {
            fragment = ShaderInjectionLiquids.complementaryReimagined(fragment);
        } else if (pack == ShaderPack.OTHER) {
            fragment = ShaderInjectionLiquids.other(fragment);
        }
        return fragment;
    }

    private static void printError() {
        if (StarterClient.iris) {
            Iris.liquidsError = "This shader (or shaderpack settings) is not supported by liquid physics!";
        }
    }

    private static String complementary(String source) {
        boolean hasWaveNormalFunction = GLSLModifier.hasFunction(source, "GetWaterNormal");
        if (!hasWaveNormalFunction) {
            ShaderInjectionLiquids.printError();
            return source;
        }
        source = GLSLModifier.replaceVariableReferences(source, "gl_FragCoord", "physics_fragdepth");
        source = GLSLModifier.replaceFunctionContent(source, "GetWaterNormal", "return normalize(" + normalMatrix + " * physics_normal);\n");
        source = GLSLModifier.insertAtFunctionStart(source, "main", "float physics_fragZ = texture(physics_depth, gl_FragCoord.xy / textureSize(physics_depth, 0)).x;\nphysics_fragdepth = vec4(gl_FragCoord.xy, physics_fragZ, gl_FragCoord.w);\nphysics_normal = physics_getNormalFromDepth();\nif (physics_fragZ == 0.0) discard;\n");
        source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
        source = GLSLModifier.replaceVariableReferences(source, "VdotN", "dot(nViewPos, normalize(newNormal))");
        return source;
    }

    private static String complementaryReimagined(String source) {
        source = GLSLModifier.replaceVariableReferences(source, "gl_FragCoord", "physics_fragdepth");
        source = GLSLModifier.insertAtFunctionStart(source, "main", "float physics_fragZ = texture(physics_depth, gl_FragCoord.xy / textureSize(physics_depth, 0)).x;\nphysics_fragdepth = vec4(gl_FragCoord.xy, physics_fragZ, gl_FragCoord.w);\nphysics_normal = normalize(" + normalMatrix + " * physics_getNormalFromDepth());\nif (physics_fragZ == 0.0) discard;\n");
        source = GLSLModifier.replaceWithinFunctionContent(source, "main", "clamp(normalize(normalMap", "clamp(normalize(physics_normal");
        source = GLSLModifier.replaceWithinFunctionContent(source, "main", "dot(normalM", "dot(physics_normal");
        source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
        source = GLSLModifier.replaceVariableReferences(source, "yPosDif", "-2.0");
        return source;
    }

    private static String other(String source) {
        source = GLSLModifier.replaceVariableReferences(source, "gl_FragCoord", "physics_fragdepth");
        source = GLSLModifier.insertAtFunctionStart(source, "main", "float physics_fragZ = texture(physics_depth, gl_FragCoord.xy / textureSize(physics_depth, 0)).x;\nphysics_fragdepth = vec4(gl_FragCoord.xy, physics_fragZ, gl_FragCoord.w);\nphysics_normal = physics_getNormalFromDepth();\nif (physics_fragZ == 0.0) discard;\n");
        boolean hasWaveNormalFunction = GLSLModifier.hasFunction(source, "GetWavesNormal");
        String normalFunctionName = null;
        boolean ignoreNormalMatrix = false;
        if (hasWaveNormalFunction) {
            normalFunctionName = "GetWavesNormal";
            ignoreNormalMatrix = !source.contains("tbnMatrix");
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            source = GLSLModifier.replaceVariableReferences(source, "tbn", "mat3(1.0)");
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "calcBump"))) {
            normalFunctionName = "calcBump";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "GetWaterNormal"))) {
            normalFunctionName = "GetWaterNormal";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getWaterNormal"))) {
            normalFunctionName = "getWaterNormal";
            if ((source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)")).contains("tbnMatrixWorld")) {
                ignoreNormalMatrix = true;
            } else {
                source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "newNormal = normalize(" + normalMatrix + " * physics_normal);\n");
                return source;
            }
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getWaveHeight"))) {
            normalFunctionName = "getWaveHeight";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "get_normals"))) {
            normalFunctionName = "get_normals";
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "noiseNormals"))) {
            normalFunctionName = "noiseNormals";
            source = GLSLModifier.replaceVariableReferences(source, "tbn", "mat3(1.0)");
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "waterNormal"))) {
            normalFunctionName = "waterNormal";
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return physics_normal;\n");
            return source;
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getNormals"))) {
            normalFunctionName = "getNormals";
            ignoreNormalMatrix = true;
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(" + normalMatrix + " * physics_normal);\n");
            return source;
        }
        if (!hasWaveNormalFunction && (hasWaveNormalFunction = GLSLModifier.hasFunction(source, "get_water_normal"))) {
            normalFunctionName = "get_water_normal";
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(physics_normal.xyz);\n");
            source = GLSLModifier.replaceVariableReferences(source, "tbn", "mat3(1.0)");
            return source;
        }
        if (!hasWaveNormalFunction) {
            ShaderInjectionLiquids.printError();
            return source;
        }
        source = ignoreNormalMatrix ? GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(physics_normal);\n") : GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(" + normalMatrix + " * physics_normal);\n");
        return source;
    }

    private static List<String> getLiquidsInjection() {
        String asset = "assets/physicsmod/shaders/include/liquids.glsl";
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

