package traben.entity_texture_features.mixin.mixins.entity.renderer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.ETF;

@Mixin({EndCrystalRenderer.class})
public abstract class MixinEndCrystalRenderer {
   @Shadow
   @Final
   private static ResourceLocation END_CRYSTAL_LOCATION;

   @ModifyArg(
      method = {"render(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
      )
   )
   private RenderType etf$modifyTexture(RenderType renderType) {
      return ETF.config().getConfig().canDoCustomTextures() ? RenderType.entityCutoutNoCull(END_CRYSTAL_LOCATION) : renderType;
   }
}
