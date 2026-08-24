package net.cibernet.alchemancy.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class RootedItemRenderer implements BlockEntityRenderer<ItemStackHolderBlockEntity> {
   private final ItemRenderer itemRenderer;
   protected final EntityRenderDispatcher entityRenderDispatcher;

   public RootedItemRenderer(Context pContext) {
      this.itemRenderer = pContext.getItemRenderer();
      this.entityRenderDispatcher = pContext.getEntityRenderer();
   }

   public void render(
      ItemStackHolderBlockEntity pBlockEntity, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay
   ) {
      pMatrixStack.pushPose();
      pMatrixStack.translate(0.5F, 0.25F, 0.5F);
      float rotation = RotationSegment.convertToDegrees((Integer)pBlockEntity.getBlockState().getValue(SkullBlock.ROTATION));
      pMatrixStack.mulPose(Axis.YN.rotationDegrees(rotation));
      pMatrixStack.scale(2.0F, 2.0F, 2.0F);
      this.itemRenderer
         .renderStatic(
            pBlockEntity.getItem(), ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pMatrixStack, pBuffer, pBlockEntity.getLevel(), 0
         );
      pMatrixStack.popPose();
   }
}
