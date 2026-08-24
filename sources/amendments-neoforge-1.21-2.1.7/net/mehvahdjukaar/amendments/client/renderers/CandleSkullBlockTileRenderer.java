package net.mehvahdjukaar.amendments.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.amendments.common.block.WallCandleSkullBlock;
import net.mehvahdjukaar.amendments.common.tile.CandleSkullBlockTile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CandleSkullBlockTileRenderer extends SkullWithWaxTileRenderer<CandleSkullBlockTile> {
   public CandleSkullBlockTileRenderer(Context context) {
      super(context);
   }

   public void render(CandleSkullBlockTile tile, float pPartialTicks, PoseStack poseStack, MultiBufferSource buffer, int pCombinedLight, int pCombinedOverlay) {
      super.render(tile, pPartialTicks, poseStack, buffer, pCombinedLight, pCombinedOverlay);
      BlockState blockstate = tile.getBlockState();
      BlockState candle = tile.getCandle();
      if (!candle.isAir()) {
         candle = (BlockState)((BlockState)candle.setValue(CandleBlock.LIT, (Boolean)blockstate.getValue(CandleBlock.LIT)))
            .setValue(CandleBlock.CANDLES, (Integer)blockstate.getValue(CandleBlock.CANDLES));
         float yaw;
         if (blockstate.hasProperty(WallCandleSkullBlock.FACING)) {
            yaw = ((Direction)blockstate.getValue(WallCandleSkullBlock.FACING)).toYRot();
         } else {
            yaw = -22.5F * ((Integer)blockstate.getValue(SkullBlock.ROTATION)).intValue();
         }

         this.renderWax(poseStack, buffer, pCombinedLight, tile.getWaxTexture(), yaw);
         poseStack.translate(0.0, 0.5, 0.0);
         this.blockRenderer.renderSingleBlock(candle, poseStack, buffer, pCombinedLight, pCombinedOverlay);
      }
   }
}
