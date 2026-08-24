package net.mehvahdjukaar.amendments.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.amendments.common.block.CeilingBannerBlock;
import net.mehvahdjukaar.moonlight.api.client.util.RotHlpr;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;

public class CeilingBannerBlockTileRenderer extends BannerRenderer {
   public CeilingBannerBlockTileRenderer(Context context) {
      super(context);
   }

   public void render(BannerBlockEntity tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
      BannerPatternLayers patterns = tile.getPatterns();
      poseStack.pushPose();
      long i = tile.getLevel().getGameTime();
      BlockState blockstate = tile.getBlockState();
      if ((Boolean)blockstate.getValue(CeilingBannerBlock.ATTACHED)) {
         poseStack.translate(0.0, 0.625, 0.0);
      }

      poseStack.translate(0.5, -0.3333333333333, 0.5);
      poseStack.mulPose(RotHlpr.rot((Direction)blockstate.getValue(CeilingBannerBlock.FACING)));
      poseStack.pushPose();
      poseStack.scale(-0.6666667F, -0.6666667F, 0.6666667F);
      VertexConsumer buffer = ModelBakery.BANNER_BASE.buffer(bufferSource, RenderType::entitySolid);
      this.bar.render(poseStack, buffer, packedLight, packedOverlay);
      BlockPos blockpos = tile.getBlockPos();
      float f2 = ((float)Math.floorMod(blockpos.getX() * 7 + blockpos.getY() * 9 + blockpos.getZ() * 13 + i, 100L) + partialTick) / 100.0F;
      this.flag.xRot = (-0.0125F + 0.01F * Mth.cos(6.2831855F * f2)) * 3.1415927F;
      this.flag.y = -32.0F;
      BannerRenderer.renderPatterns(
         poseStack, bufferSource, packedLight, packedOverlay, this.flag, ModelBakery.BANNER_BASE, true, tile.getBaseColor(), patterns
      );
      poseStack.popPose();
      poseStack.popPose();
   }
}
