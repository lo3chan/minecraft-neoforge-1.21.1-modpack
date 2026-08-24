package dev.tr7zw.notenoughanimations.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.notenoughanimations.NEAnimationsLoader;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemInHandRenderer.class})
public class ItemInHandRendererMixin {
   @Inject(
      method = {"renderPlayerArm(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IFFLnet/minecraft/world/entity/HumanoidArm;)V"},
      at = {@At("HEAD")}
   )
   private void renderPlayerArm(
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      int packedLight,
      float equippedProgress,
      float swingProgress,
      HumanoidArm humanoidArm,
      CallbackInfo info
   ) {
      NEAnimationsLoader.INSTANCE.playerTransformer.renderingFirstPersonArm(true);
   }

   @Inject(
      method = {"renderPlayerArm(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IFFLnet/minecraft/world/entity/HumanoidArm;)V"},
      at = {@At("RETURN")}
   )
   private void renderPlayerArmEnd(
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      int packedLight,
      float equippedProgress,
      float swingProgress,
      HumanoidArm humanoidArm,
      CallbackInfo info
   ) {
      NEAnimationsLoader.INSTANCE.playerTransformer.renderingFirstPersonArm(false);
   }
}
