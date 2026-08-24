package me.flashyreese.mods.sodiumextra.client.fog;

import java.util.concurrent.atomic.AtomicBoolean;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.resources.ResourceLocation;

public final class FogShaderTransformer {
   private static final ResourceLocation SODIUM_FOG_INCLUDE = ResourceLocation.fromNamespaceAndPath("sodium", "include/fog.glsl");
   private static final ResourceLocation SODIUM_TERRAIN_VERTEX_SHADER = ResourceLocation.fromNamespaceAndPath("sodium", "blocks/block_layer_opaque.vsh");
   private static final ResourceLocation SODIUM_TERRAIN_FRAGMENT_SHADER = ResourceLocation.fromNamespaceAndPath("sodium", "blocks/block_layer_opaque.fsh");
   private static final String SHAPE_HELPER_MARKER = "sodiumExtra_fogDistance";
   private static final String PLANAR_VARYING_MARKER = "v_PlanarDistance";
   private static final String CYLINDRICAL_VARYING_MARKER = "v_SodiumExtraCylindricalDistance";
   private static final String LINEAR_FOG_ANCHOR = "vec4 _linearFog(vec4 fragColor, float fragDistance, vec4 fogColor, float fogStart, float fogEnd) {";
   private static final String LINEAR_FOG_BODY_ANCHOR = "vec4 _linearFog(vec4 fragColor, float fragDistance, vec4 fogColor, float fogStart, float fogEnd) {\n#ifdef USE_FOG\n";
   private static final String VERTEX_DECL_ANCHOR = "out float v_FragDistance;";
   private static final String VERTEX_COMPUTE_ANCHOR = "gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * vec4(position, 1.0);";
   private static final String FRAGMENT_DECL_ANCHOR = "in float v_FragDistance;";
   private static final String FRAGMENT_FOG_CALL_ANCHOR = "fragColor = _linearFog(";
   private static final String SHAPE_HELPER = "const float SODIUM_EXTRA_PLANAR_FOG_OFFSET = 2097152.0;\nconst float SODIUM_EXTRA_CYLINDRICAL_FOG_OFFSET = 3145728.0;\nconst float SODIUM_EXTRA_CYLINDRICAL_VERTICAL_SCALE = %s;\n\nfloat sodiumExtra_planarDistance = 0.0;\nvec2 sodiumExtra_cylindricalDistance = vec2(0.0);\n\nfloat sodiumExtra_cylindricalFogDistance(float horizontalDistance, float verticalDistance) {\n    return max(horizontalDistance, verticalDistance / SODIUM_EXTRA_CYLINDRICAL_VERTICAL_SCALE);\n}\n\nfloat sodiumExtra_fogDistance(float fragDistance, float fogStart, float fogEnd) {\n    if (fogStart >= SODIUM_EXTRA_CYLINDRICAL_FOG_OFFSET && fogEnd >= SODIUM_EXTRA_CYLINDRICAL_FOG_OFFSET) {\n        return sodiumExtra_cylindricalFogDistance(sodiumExtra_cylindricalDistance.x, sodiumExtra_cylindricalDistance.y);\n    }\n\n    if (fogStart >= SODIUM_EXTRA_PLANAR_FOG_OFFSET && fogEnd >= SODIUM_EXTRA_PLANAR_FOG_OFFSET) {\n        return sodiumExtra_planarDistance;\n    }\n\n    return fragDistance;\n}\n\nfloat sodiumExtra_fogStart(float fogStart) {\n    if (fogStart >= SODIUM_EXTRA_CYLINDRICAL_FOG_OFFSET) {\n        return fogStart - SODIUM_EXTRA_CYLINDRICAL_FOG_OFFSET;\n    }\n\n    return fogStart >= SODIUM_EXTRA_PLANAR_FOG_OFFSET ? fogStart - SODIUM_EXTRA_PLANAR_FOG_OFFSET : fogStart;\n}\n\nfloat sodiumExtra_fogEnd(float fogEnd) {\n    if (fogEnd >= SODIUM_EXTRA_CYLINDRICAL_FOG_OFFSET) {\n        return fogEnd - SODIUM_EXTRA_CYLINDRICAL_FOG_OFFSET;\n    }\n\n    return fogEnd >= SODIUM_EXTRA_PLANAR_FOG_OFFSET ? fogEnd - SODIUM_EXTRA_PLANAR_FOG_OFFSET : fogEnd;\n}\n\n"
      .formatted(Float.toString(16.0F));
   private static final String LINEAR_FOG_SETUP = "    fragDistance = sodiumExtra_fogDistance(fragDistance, fogStart, fogEnd);\n    fogStart = sodiumExtra_fogStart(fogStart);\n    fogEnd = sodiumExtra_fogEnd(fogEnd);\n";
   private static final String VERTEX_PLANAR_DECL = "\nout float v_PlanarDistance;";
   private static final String VERTEX_PLANAR_COMPUTE = "v_PlanarDistance = -(u_ModelViewMatrix * vec4(position, 1.0)).z;\n\n    ";
   private static final String VERTEX_CYLINDRICAL_DECL = "\nout vec2 v_SodiumExtraCylindricalDistance;";
   private static final String VERTEX_CYLINDRICAL_COMPUTE = "v_SodiumExtraCylindricalDistance = vec2(length(position.xz), abs(position.y));\n    ";
   private static final String FRAGMENT_PLANAR_DECL = "\nin float v_PlanarDistance;";
   private static final String FRAGMENT_PLANAR_ASSIGN = "sodiumExtra_planarDistance = v_PlanarDistance;\n    ";
   private static final String FRAGMENT_CYLINDRICAL_DECL = "\nin vec2 v_SodiumExtraCylindricalDistance;";
   private static final String FRAGMENT_CYLINDRICAL_ASSIGN = "sodiumExtra_cylindricalDistance = v_SodiumExtraCylindricalDistance;\n    ";
   private static final AtomicBoolean WARNED = new AtomicBoolean(false);
   private static volatile boolean shapeSupported = true;

   private FogShaderTransformer() {
   }

   public static boolean isShapeSupported() {
      return shapeSupported;
   }

   public static String injectSodiumShaderSource(String source, ResourceLocation location) {
      if (source == null || location == null) {
         return source;
      } else if (location.equals(SODIUM_FOG_INCLUDE)) {
         return injectFogInclude(source);
      } else {
         return !location.equals(SODIUM_TERRAIN_VERTEX_SHADER) && !location.equals(SODIUM_TERRAIN_FRAGMENT_SHADER) ? source : injectTerrainVaryings(source);
      }
   }

   private static String injectFogInclude(String source) {
      if (source.contains("sodiumExtra_fogDistance")) {
         return source;
      } else if (!source.contains("vec4 _linearFog(vec4 fragColor, float fragDistance, vec4 fogColor, float fogStart, float fogEnd) {\n#ifdef USE_FOG\n")) {
         warnDrift();
         return source;
      } else {
         return source.replace(
            "vec4 _linearFog(vec4 fragColor, float fragDistance, vec4 fogColor, float fogStart, float fogEnd) {\n#ifdef USE_FOG\n",
            SHAPE_HELPER
               + "vec4 _linearFog(vec4 fragColor, float fragDistance, vec4 fogColor, float fogStart, float fogEnd) {\n#ifdef USE_FOG\n    fragDistance = sodiumExtra_fogDistance(fragDistance, fogStart, fogEnd);\n    fogStart = sodiumExtra_fogStart(fogStart);\n    fogEnd = sodiumExtra_fogEnd(fogEnd);\n"
         );
      }
   }

   private static String injectTerrainVaryings(String source) {
      boolean needsPlanarVarying = !source.contains("v_PlanarDistance");
      boolean needsCylindricalVarying = !source.contains("v_SodiumExtraCylindricalDistance");
      if (!needsPlanarVarying && !needsCylindricalVarying) {
         return source;
      } else if (source.contains("out float v_FragDistance;") && source.contains("gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * vec4(position, 1.0);")
         )
       {
         String declarations = "";
         String computations = "";
         if (needsPlanarVarying) {
            declarations = declarations + "\nout float v_PlanarDistance;";
            computations = computations + "v_PlanarDistance = -(u_ModelViewMatrix * vec4(position, 1.0)).z;\n\n    ";
         }

         if (needsCylindricalVarying) {
            declarations = declarations + "\nout vec2 v_SodiumExtraCylindricalDistance;";
            computations = computations + "v_SodiumExtraCylindricalDistance = vec2(length(position.xz), abs(position.y));\n    ";
         }

         return source.replace("out float v_FragDistance;", "out float v_FragDistance;" + declarations)
            .replace(
               "gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * vec4(position, 1.0);",
               computations + "gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * vec4(position, 1.0);"
            );
      } else if (source.contains("in float v_FragDistance;") && source.contains("fragColor = _linearFog(")) {
         String declarationsx = "";
         String assignments = "";
         if (needsPlanarVarying) {
            declarationsx = declarationsx + "\nin float v_PlanarDistance;";
            assignments = assignments + "sodiumExtra_planarDistance = v_PlanarDistance;\n    ";
         }

         if (needsCylindricalVarying) {
            declarationsx = declarationsx + "\nin vec2 v_SodiumExtraCylindricalDistance;";
            assignments = assignments + "sodiumExtra_cylindricalDistance = v_SodiumExtraCylindricalDistance;\n    ";
         }

         return source.replace("in float v_FragDistance;", "in float v_FragDistance;" + declarationsx)
            .replace("fragColor = _linearFog(", assignments + "fragColor = _linearFog(");
      } else {
         warnDrift();
         return source;
      }
   }

   private static void warnDrift() {
      shapeSupported = false;
      if (WARNED.compareAndSet(false, true)) {
         SodiumExtraClientMod.logger()
            .warn(
               "Sodium's terrain fog shader no longer matches the expected layout; custom fog shapes are disabled. The fog shader patch needs to be re-synced with this version."
            );
      }
   }
}
