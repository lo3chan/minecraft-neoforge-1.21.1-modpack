package net.raphimc.immediatelyfast.injection.mixins.hud_batching;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.raphimc.immediatelyfast.feature.batching.HudBatchingBufferSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ItemRenderer.class})
public abstract class MixinItemRenderer {
   @WrapMethod(
      method = {"render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"}
   )
   private void renderItem(
      ItemStack stack,
      ItemDisplayContext renderMode,
      boolean leftHanded,
      PoseStack matrices,
      MultiBufferSource vertexConsumers,
      int light,
      int overlay,
      BakedModel model,
      Operation<Void> original
   ) {
      if (vertexConsumers instanceof HudBatchingBufferSource hudBatchingBufferSource) {
         hudBatchingBufferSource.setRenderingItem(true);
      }

      try {
         original.call(new Object[]{stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, model});
      } finally {
         if (vertexConsumers instanceof HudBatchingBufferSource hudBatchingBufferSource) {
            hudBatchingBufferSource.setRenderingItem(false);
         }
      }
   }
}
