package net.diebuddies.mixins.optifine;

import net.diebuddies.compat.Optifine;
import net.diebuddies.physics.ocean.ShaderInjectionOcean;
import net.diebuddies.util.ShaderFixes;
import net.diebuddies.util.ShaderType;
import net.optifine.shaders.Program;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
   targets = {"net.optifine.shaders.Shaders"},
   remap = false
)
public class MixinShaders {
   @Inject(
      at = {@At("HEAD")},
      method = {"setupProgram"}
   )
   private static void physicsmod$getCompilingProgram(Program program, String vShaderPath, String gShaderPath, String fShaderPath, CallbackInfo info) {
      Optifine.compilingProgram = program;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"createCompShader"}
   )
   private static void physicsmod$switchToCompStage(@Coerce Object program, String filename, CallbackInfoReturnable<Integer> info) {
      Optifine.compileStage = ShaderType.COMPUTE;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"createVertShader"}
   )
   private static void physicsmod$switchToVertStage(@Coerce Object program, String filename, CallbackInfoReturnable<Integer> info) {
      Optifine.compileStage = ShaderType.VERTEX;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"createGeomShader"}
   )
   private static void physicsmod$switchToGeomStage(@Coerce Object program, String filename, CallbackInfoReturnable<Integer> info) {
      Optifine.compileStage = ShaderType.GEOMETRY;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"createFragShader"}
   )
   private static void physicsmod$switchToFragStage(@Coerce Object program, String filename, CallbackInfoReturnable<Integer> info) {
      Optifine.compileStage = ShaderType.FRAGMENT;
   }

   @ModifyVariable(
      method = {"shaderSource"},
      at = @At("HEAD"),
      ordinal = 0
   )
   private static String physicsmod$applyOceanChanges(String code) {
      if (Optifine.compilingProgram == Optifine.oceanProgram || Optifine.compilingProgram == Optifine.oceanShadowProgram) {
         boolean shadow = Optifine.compilingProgram == Optifine.oceanShadowProgram;
         if (code.contains("#define PHYSICS_OCEAN_SUPPORT")) {
            return StringUtils.replace(code, "#define PHYSICS_OCEAN_SUPPORT", "#define PHYSICS_OCEAN");
         }

         if (code.contains("#define PHYSICS_OCEAN_INJECTION")) {
            code = StringUtils.replace(code, "#define PHYSICS_OCEAN_INJECTION", "#define PHYSICS_OCEAN");
         }

         if (Optifine.compileStage == ShaderType.VERTEX) {
            code = ShaderFixes.preprocessOptifineSource(code);
            code = ShaderInjectionOcean.getVertexSource(code);
         } else if (Optifine.compileStage == ShaderType.FRAGMENT) {
            code = ShaderFixes.preprocessOptifineSource(code);
            code = ShaderInjectionOcean.getFragmentSource(code, shadow);
         }
      }

      return code;
   }
}
