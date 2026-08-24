package net.diebuddies.mixins.vines;

import com.mojang.blaze3d.vertex.PoseStack;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.BlockEntityVertexConsumerProvider;
import net.diebuddies.physics.vines.VineHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HangingSignRenderer.class})
public class MixinHangingSignRenderer {
   @Inject(
      at = {@At("HEAD")},
      method = {"render"},
      cancellable = true
   )
   public void render(SignBlockEntity signBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo info) {
      if (ConfigClient.areDynamicBlockPhysicsEnabled()
         && VineHelper.getSetting(signBlockEntity.getBlockState()) != null
         && VineHelper.isChunkInRange(signBlockEntity.getBlockPos())
         && (
            !(multiBufferSource instanceof BlockEntityVertexConsumerProvider)
               || multiBufferSource instanceof BlockEntityVertexConsumerProvider blockEntityProvider && blockEntityProvider.isDestruction()
         )) {
         info.cancel();
      }
   }
}
