package net.diebuddies.mixins.liquid;

import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Shader;
import net.diebuddies.compat.Iris;
import net.diebuddies.opengl.Data;
import net.diebuddies.physics.StarterClient;
import org.lwjgl.opengl.GL32C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ProgramManager.class})
public class MixinProgramManager {
   @Inject(
      at = {@At("HEAD")},
      method = {"linkShader"}
   )
   private static void physicsmod$changeLiquidAttributes(Shader shader, CallbackInfo info) {
      if (StarterClient.iris && Iris.compilingLiquidShadowShader.get()) {
         GL32C.glBindAttribLocation(shader.getId(), Data.LIQUID_POS.getAttribute(), "physics_offset");
         GL32C.glBindAttribLocation(shader.getId(), Data.LIQUID_POS_NEW.getAttribute(), "physics_offsetNew");
         System.out.println("binding wonky attributes");
      }
   }
}
