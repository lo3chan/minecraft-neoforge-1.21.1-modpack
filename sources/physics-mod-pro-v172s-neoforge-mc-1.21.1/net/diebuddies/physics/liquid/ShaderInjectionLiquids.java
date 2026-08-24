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
         setupOptifineInjection();
      }

      return vertexSource == null ? null : transformVertexLiquidShader(evaluatePack(vertexSource), vertexSource);
   }

   private static ShaderInjectionLiquids.ShaderPack evaluatePack(String source) {
      if (source.contains("// Complementary Shaders by EminGT //")) {
         return ShaderInjectionLiquids.ShaderPack.COMPLEMENTARY_REIMAGINED;
      } else if (source.contains("Complementary Shaders by EminGT")) {
         return ShaderInjectionLiquids.ShaderPack.COMPLEMENTARY;
      } else {
         return source.contains("Complementary Reimagined by EminGT")
            ? ShaderInjectionLiquids.ShaderPack.COMPLEMENTARY_REIMAGINED
            : ShaderInjectionLiquids.ShaderPack.OTHER;
      }
   }

   @Nullable
   public static String getFragmentSource(String fragmentSource) {
      if (StarterClient.optifabric) {
         setupOptifineInjection();
      }

      if (fragmentSource == null) {
         return null;
      } else {
         if (StarterClient.iris) {
            Iris.liquidsError = "";
         }

         return transformFragmentLiquidShader(evaluatePack(fragmentSource), fragmentSource);
      }
   }

   @Nullable
   public static String getVertexShadowSource(String vertexSource) {
      if (StarterClient.optifabric) {
         setupOptifineInjection();
      }

      return vertexSource == null ? null : transformVertexLiquidShadowShader(evaluatePack(vertexSource), vertexSource);
   }

   @Nullable
   public static String getFragmentShadowSource(String fragmentSource) {
      if (StarterClient.optifabric) {
         setupOptifineInjection();
      }

      if (fragmentSource == null) {
         return null;
      } else {
         if (StarterClient.iris) {
            Iris.liquidsError = "";
         }

         return fragmentSource;
      }
   }

   private static String transformVertexLiquidShadowShader(ShaderInjectionLiquids.ShaderPack pack, String vertex) {
      vertex = GLSLModifier.removeComments(vertex);
      vertex = StringUtils.replace(vertex, position, "physics_finalPosition");
      vertex = StringUtils.replace(vertex, "physics_finalPositionID", "gl_VertexID");
      vertex = GLSLModifier.insertBeforeFirstFunction(
         vertex,
         "in vec4 physics_offset;\nin vec4 physics_offsetNew;\nuniform vec3 physics_liquidCameraPos;\nuniform float physics_renderPercent;\nvec4 physics_finalPosition;\n"
      );
      vertex = GLSLModifier.insertAtFunctionStart(
         vertex,
         "main",
         "float physics_scale = physics_offsetNew.w;\nphysics_finalPosition = vec4(gl_Vertex.xyz * physics_scale + mix(physics_offset.xyz - physics_liquidCameraPos, physics_offsetNew.xyz - physics_liquidCameraPos, physics_renderPercent), 1.0);\n"
      );
      return GLSLModifier.replaceFunctionCalls(vertex, "ftransform", "(" + modelViewProjectionMatrix + " * physics_finalPosition)");
   }

   private static String transformVertexLiquidShader(ShaderInjectionLiquids.ShaderPack pack, String vertex) {
      vertex = GLSLModifier.removeComments(vertex);
      return GLSLModifier.insertAtFunctionEnd(
         vertex,
         "main",
         "float physics_x = -1.0 + float((gl_VertexID & 1) << 2);\nfloat physics_y = -1.0 + float((gl_VertexID & 2) << 1);\ngl_Position = vec4(physics_x, physics_y, 0, 1);\n"
      );
   }

   private static String transformFragmentLiquidShader(ShaderInjectionLiquids.ShaderPack pack, String fragment) {
      fragment = GLSLModifier.removeComments(fragment);
      if (StarterClient.optifabric && !fragment.contains(normalMatrix)) {
         boolean var6 = true;
      } else {
         boolean var10000 = false;
      }

      List<String> liquidsInjection = getLiquidsInjection();
      fragment = GLSLModifier.insertBeforeFirstFunction(fragment, GLSLModifier.convertToString(liquidsInjection));
      if (pack == ShaderInjectionLiquids.ShaderPack.COMPLEMENTARY) {
         fragment = complementary(fragment);
      } else if (pack == ShaderInjectionLiquids.ShaderPack.COMPLEMENTARY_REIMAGINED) {
         fragment = complementaryReimagined(fragment);
      } else if (pack == ShaderInjectionLiquids.ShaderPack.OTHER) {
         fragment = other(fragment);
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
         printError();
         return source;
      } else {
         source = GLSLModifier.replaceVariableReferences(source, "gl_FragCoord", "physics_fragdepth");
         source = GLSLModifier.replaceFunctionContent(source, "GetWaterNormal", "return normalize(" + normalMatrix + " * physics_normal);\n");
         source = GLSLModifier.insertAtFunctionStart(
            source,
            "main",
            "float physics_fragZ = texture(physics_depth, gl_FragCoord.xy / textureSize(physics_depth, 0)).x;\nphysics_fragdepth = vec4(gl_FragCoord.xy, physics_fragZ, gl_FragCoord.w);\nphysics_normal = physics_getNormalFromDepth();\nif (physics_fragZ == 0.0) discard;\n"
         );
         source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
         return GLSLModifier.replaceVariableReferences(source, "VdotN", "dot(nViewPos, normalize(newNormal))");
      }
   }

   private static String complementaryReimagined(String source) {
      source = GLSLModifier.replaceVariableReferences(source, "gl_FragCoord", "physics_fragdepth");
      source = GLSLModifier.insertAtFunctionStart(
         source,
         "main",
         "float physics_fragZ = texture(physics_depth, gl_FragCoord.xy / textureSize(physics_depth, 0)).x;\nphysics_fragdepth = vec4(gl_FragCoord.xy, physics_fragZ, gl_FragCoord.w);\nphysics_normal = normalize("
            + normalMatrix
            + " * physics_getNormalFromDepth());\nif (physics_fragZ == 0.0) discard;\n"
      );
      source = GLSLModifier.replaceWithinFunctionContent(source, "main", "clamp(normalize(normalMap", "clamp(normalize(physics_normal");
      source = GLSLModifier.replaceWithinFunctionContent(source, "main", "dot(normalM", "dot(physics_normal");
      source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
      return GLSLModifier.replaceVariableReferences(source, "yPosDif", "-2.0");
   }

   private static String other(String source) {
      source = GLSLModifier.replaceVariableReferences(source, "gl_FragCoord", "physics_fragdepth");
      source = GLSLModifier.insertAtFunctionStart(
         source,
         "main",
         "float physics_fragZ = texture(physics_depth, gl_FragCoord.xy / textureSize(physics_depth, 0)).x;\nphysics_fragdepth = vec4(gl_FragCoord.xy, physics_fragZ, gl_FragCoord.w);\nphysics_normal = physics_getNormalFromDepth();\nif (physics_fragZ == 0.0) discard;\n"
      );
      boolean hasWaveNormalFunction = GLSLModifier.hasFunction(source, "GetWavesNormal");
      String normalFunctionName = null;
      boolean ignoreNormalMatrix = false;
      if (hasWaveNormalFunction) {
         normalFunctionName = "GetWavesNormal";
         ignoreNormalMatrix = !source.contains("tbnMatrix");
         source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
         source = GLSLModifier.replaceVariableReferences(source, "tbn", "mat3(1.0)");
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "calcBump");
         if (hasWaveNormalFunction) {
            normalFunctionName = "calcBump";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "GetWaterNormal");
         if (hasWaveNormalFunction) {
            normalFunctionName = "GetWaterNormal";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getWaterNormal");
         if (hasWaveNormalFunction) {
            normalFunctionName = "getWaterNormal";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            if (!source.contains("tbnMatrixWorld")) {
               return GLSLModifier.replaceFunctionContent(source, normalFunctionName, "newNormal = normalize(" + normalMatrix + " * physics_normal);\n");
            }

            ignoreNormalMatrix = true;
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getWaveHeight");
         if (hasWaveNormalFunction) {
            normalFunctionName = "getWaveHeight";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "get_normals");
         if (hasWaveNormalFunction) {
            normalFunctionName = "get_normals";
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "noiseNormals");
         if (hasWaveNormalFunction) {
            normalFunctionName = "noiseNormals";
            source = GLSLModifier.replaceVariableReferences(source, "tbn", "mat3(1.0)");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "waterNormal");
         if (hasWaveNormalFunction) {
            normalFunctionName = "waterNormal";
            return GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return physics_normal;\n");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getNormals");
         if (hasWaveNormalFunction) {
            normalFunctionName = "getNormals";
            ignoreNormalMatrix = true;
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            return GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(" + normalMatrix + " * physics_normal);\n");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "get_water_normal");
         if (hasWaveNormalFunction) {
            normalFunctionName = "get_water_normal";
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(physics_normal.xyz);\n");
            return GLSLModifier.replaceVariableReferences(source, "tbn", "mat3(1.0)");
         }
      }

      if (!hasWaveNormalFunction) {
         printError();
         return source;
      } else {
         if (ignoreNormalMatrix) {
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(physics_normal);\n");
         } else {
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(" + normalMatrix + " * physics_normal);\n");
         }

         return source;
      }
   }

   private static List<String> getLiquidsInjection() {
      String asset = "assets/physicsmod/shaders/include/liquids.glsl";
      List<String> lines = new ObjectArrayList();
      String all = "";
      int brackets = 0;

      String line;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(PhysicsMod.class.getClassLoader().getResourceAsStream(asset)))) {
         while ((line = reader.readLine()) != null) {
            if (!line.isBlank()) {
               if (line.contains("{")) {
                  brackets++;
               }

               if (line.contains("}")) {
                  brackets--;
               }

               all = all + line;
               if (brackets == 0) {
                  lines.add(all);
                  all = "";
               }
            }
         }
      } catch (IOException var9) {
         var9.printStackTrace();
      }

      return lines;
   }

   public static enum ShaderPack {
      COMPLEMENTARY,
      COMPLEMENTARY_REIMAGINED,
      OTHER;
   }
}
