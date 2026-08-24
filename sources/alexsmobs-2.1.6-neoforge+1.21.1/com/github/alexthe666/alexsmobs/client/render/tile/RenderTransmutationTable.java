package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockTransmutationTable;
import com.github.alexthe666.alexsmobs.client.model.ModelTransmutationTable;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.AMVertex;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityTransmutationTable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderTransmutationTable<T extends TileEntityTransmutationTable> implements BlockEntityRenderer<T> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/farseer/transmutation_table.png");
   private static final ResourceLocation OVERLAY = AMCompat.rl("alexsmobs:textures/entity/farseer/transmutation_table_overlay.png");
   private static final ResourceLocation GLOW_TEXTURE = AMCompat.rl("alexsmobs:textures/entity/farseer/transmutation_table_glow.png");
   private static final ModelTransmutationTable MODEL = new ModelTransmutationTable(0.0F);
   private static final ModelTransmutationTable OVERLAY_MODEL = new ModelTransmutationTable(0.01F);

   public RenderTransmutationTable(Context rendererDispatcherIn) {
   }

   public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
      matrixStackIn.pushPose();
      Direction dir = (Direction)tileEntityIn.getBlockState().getValue(BlockTransmutationTable.FACING);
      switch (dir) {
         case NORTH:
            matrixStackIn.translate(0.5, 1.5, 0.5);
            break;
         case EAST:
            matrixStackIn.translate(0.5F, 1.5F, 0.5F);
            break;
         case SOUTH:
            matrixStackIn.translate(0.5, 1.5, 0.5);
            break;
         case WEST:
            matrixStackIn.translate(0.5F, 1.5F, 0.5F);
      }

      float ageInTicks = partialTicks + tileEntityIn.ticksExisted;
      matrixStackIn.mulPose(dir.getOpposite().getRotation());
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));
      matrixStackIn.pushPose();
      MODEL.animate(tileEntityIn, partialTicks);
      MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
      MODEL.renderToBuffer(
         matrixStackIn,
         bufferIn.getBuffer(AMRenderTypes.getEyesAlphaEnabled(GLOW_TEXTURE)),
         240,
         combinedOverlayIn,
         1.0F,
         1.0F,
         1.0F,
         0.5F + (float)Math.sin(ageInTicks * 0.05F) * 0.25F
      );
      OVERLAY_MODEL.animate(tileEntityIn, partialTicks);
      AMRenderTypes.renderStaticOverlay(
         bufferIn,
         AMRenderTypes.STATIC_PORTAL,
         RenderType.entityCutoutNoCull(OVERLAY),
         staticyOverlay -> OVERLAY_MODEL.renderToBuffer(matrixStackIn, staticyOverlay, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F)
      );
      matrixStackIn.popPose();
      matrixStackIn.popPose();
   }

   private static void vertex(
      VertexConsumer p_114090_, Matrix4f p_114091_, Matrix3f p_114092_, int p_114093_, float p_114094_, float p_114095_, int p_114096_, int p_114097_
   ) {
      AMVertex.normal(
         p_114090_.addVertex(p_114091_, p_114094_, p_114095_, 0.0F)
            .setColor(255, 255, 255, 100)
            .setUv(p_114096_, p_114097_)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(p_114093_),
         p_114092_,
         0.0F,
         1.0F,
         0.0F
      );
   }
}
