package net.diebuddies.mixins.ocean;

import net.diebuddies.compat.Iris;
import net.diebuddies.util.ShaderType;
import net.irisshaders.iris.shaderpack.preprocessor.JcppProcessor;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin({JcppProcessor.class})
public class MixinJcppProcessor {
   @ModifyVariable(
      at = @At("HEAD"),
      method = {"glslPreprocessSource"},
      remap = false,
      ordinal = 0
   )
   private static String glslPreprocessSource(String source) {
      if (Iris.preprocessOceanStage.get() == null || source == null) {
         Iris.vertexShaderSupportsOcean.set(false);
         Iris.geometryShaderSupportsOcean.set(false);
         Iris.fragmentShaderSupportsOcean.set(false);
      } else if (source.contains("#define PHYSICS_OCEAN_SUPPORT")) {
         source = StringUtils.replace(source, "#define PHYSICS_OCEAN_SUPPORT", "#define PHYSICS_OCEAN");
         switch ((ShaderType)Iris.preprocessOceanStage.get()) {
            case VERTEX:
               Iris.vertexShaderSupportsOcean.set(true);
               break;
            case GEOMETRY:
               Iris.geometryShaderSupportsOcean.set(true);
               break;
            case FRAGMENT:
               Iris.fragmentShaderSupportsOcean.set(true);
         }
      } else {
         if (source.contains("#define PHYSICS_OCEAN_INJECTION")) {
            source = StringUtils.replace(source, "#define PHYSICS_OCEAN_INJECTION", "#define PHYSICS_OCEAN");
         }

         switch ((ShaderType)Iris.preprocessOceanStage.get()) {
            case VERTEX:
               Iris.vertexShaderSupportsOcean.set(false);
               break;
            case GEOMETRY:
               Iris.geometryShaderSupportsOcean.set(false);
               break;
            case FRAGMENT:
               Iris.fragmentShaderSupportsOcean.set(false);
         }
      }

      return source;
   }
}
