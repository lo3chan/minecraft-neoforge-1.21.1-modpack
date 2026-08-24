package net.irisshaders.iris.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.Program.Type;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Program.class})
public class MixinProgram {
   @Redirect(
      method = {"compileShaderInternal"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/preprocessor/GlslPreprocessor;process(Ljava/lang/String;)Ljava/util/List;"
      )
   )
   private static List<String> iris$allowSkippingMojImportDirectives(GlslPreprocessor includeHandler, String shaderSource) {
      return !shaderSource.contains("moj_import") ? Collections.singletonList(shaderSource) : includeHandler.process(shaderSource);
   }

   @Inject(
      method = {"compileShaderInternal"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/platform/GlStateManager;glGetShaderInfoLog(II)Ljava/lang/String;",
         remap = false
      )},
      cancellable = true
   )
   private static void iris$causeException(
      Type arg, String string, InputStream inputStream, String string2, GlslPreprocessor arg2, CallbackInfoReturnable<Integer> cir, @Local int i
   ) {
      cir.setReturnValue(i);
      throw new ShaderCompileException(string + arg.getExtension(), GlStateManager.glGetShaderInfoLog(i, 32768));
   }
}
