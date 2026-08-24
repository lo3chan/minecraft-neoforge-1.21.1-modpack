package net.raphimc.immediatelyfast.neoforge.injection.mixins.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(
   targets = {"net/neoforged/neoforge/client/NeoForgeRenderTypes$Internal"},
   priority = 500
)
public abstract class MixinNeoForgeRenderTypes {
   @ModifyArg(
      method = {"getText", "getTextIntensity", "getTextPolygonOffset", "getTextIntensityPolygonOffset", "getTextSeeThrough", "getTextIntensitySeeThrough"},
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
