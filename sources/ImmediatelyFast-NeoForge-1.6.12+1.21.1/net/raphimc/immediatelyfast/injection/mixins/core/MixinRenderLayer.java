package net.raphimc.immediatelyfast.injection.mixins.core;

import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(
   value = {RenderType.class},
   priority = 500
)
public abstract class MixinRenderLayer {
   @ModifyArg(
      method = {"lambda$static$17", "lambda$static$18", "lambda$static$19", "lambda$static$20", "lambda$static$21", "lambda$static$22"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/RenderType;create(Ljava/lang/String;Lcom/mojang/blaze3d/vertex/VertexFormat;Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;IZZLnet/minecraft/client/renderer/RenderType$CompositeState;)Lnet/minecraft/client/renderer/RenderType$CompositeRenderType;"
      ),
      index = 5
   )
   private static boolean changeTranslucency(boolean value) {
      return false;
   }
}
