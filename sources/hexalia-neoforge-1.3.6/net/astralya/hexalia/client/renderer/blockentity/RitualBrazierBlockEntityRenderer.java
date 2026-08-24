package net.astralya.hexalia.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.astralya.hexalia.block.entity.custom.RitualBrazierBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class RitualBrazierBlockEntityRenderer implements BlockEntityRenderer<RitualBrazierBlockEntity> {
   private final ItemRenderer itemRenderer;

   public RitualBrazierBlockEntityRenderer(Context context) {
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(
      RitualBrazierBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay
   ) {
      ItemStack stack = blockEntity.getStoredItem();
      if (!stack.isEmpty()) {
         poseStack.pushPose();
         poseStack.translate(0.5F, 0.45F, 0.5F);
         poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getRenderingRotation()));
         poseStack.scale(0.4F, 0.4F, 0.4F);
         this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffer, blockEntity.getLevel(), 0);
         poseStack.popPose();
      }
   }
}
