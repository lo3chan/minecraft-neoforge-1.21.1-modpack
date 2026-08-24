package at.petrak.hexcasting.client.render.be;

import at.petrak.hexcasting.client.RegisterClientStuff;
import at.petrak.hexcasting.client.render.GaslightingTracker;
import at.petrak.hexcasting.common.blocks.BlockQuenchedAllay;
import at.petrak.hexcasting.common.blocks.entity.BlockEntityQuenchedAllay;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;

public class BlockEntityQuenchedAllayRenderer implements BlockEntityRenderer<BlockEntityQuenchedAllay> {
   private final Context ctx;

   public BlockEntityQuenchedAllayRenderer(Context ctx) {
      this.ctx = ctx;
   }

   private static void doRender(
      BlockQuenchedAllay block, BlockRenderDispatcher dispatcher, PoseStack ps, MultiBufferSource bufSource, int packedLight, int packedOverlay
   ) {
      VertexConsumer buffer = bufSource.getBuffer(RenderType.translucent());
      Pose pose = ps.last();
      int idx = Math.abs(GaslightingTracker.getGaslightingAmount() % 4);
      BakedModel model = RegisterClientStuff.QUENCHED_ALLAY_VARIANTS.get(BuiltInRegistries.BLOCK.getKey(block)).get(idx);
      dispatcher.getModelRenderer().renderModel(pose, buffer, null, model, 1.0F, 1.0F, 1.0F, packedLight, packedOverlay);
   }

   public void render(
      BlockEntityQuenchedAllay blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      doRender(
         (BlockQuenchedAllay)blockEntity.getBlockState().getBlock(), this.ctx.getBlockRenderDispatcher(), poseStack, bufferSource, packedLight, packedOverlay
      );
   }

   public boolean shouldRenderOffScreen(BlockEntityQuenchedAllay blockEntity) {
      return false;
   }
}
