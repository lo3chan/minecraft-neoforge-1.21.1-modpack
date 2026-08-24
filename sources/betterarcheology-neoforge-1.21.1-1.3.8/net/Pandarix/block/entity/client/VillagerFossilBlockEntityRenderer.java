package net.Pandarix.block.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Objects;
import net.Pandarix.block.custom.FossilBaseWithEntityBlock;
import net.Pandarix.block.entity.VillagerFossilBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

public class VillagerFossilBlockEntityRenderer implements BlockEntityRenderer<VillagerFossilBlockEntity> {
   public VillagerFossilBlockEntityRenderer(Context context) {
   }

   public void render(
      VillagerFossilBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay
   ) {
      if (pBlockEntity.getLevel() != null) {
         ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
         pPoseStack.pushPose();
         BlockState state = pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos());
         Direction facing = state.getBlock() instanceof FossilBaseWithEntityBlock
            ? (Direction)state.getValue(FossilBaseWithEntityBlock.FACING)
            : Direction.NORTH;
         switch (facing) {
            case EAST:
               pPoseStack.translate(0.75F, 0.95F, 0.5F);
               pPoseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
               break;
            case WEST:
               pPoseStack.translate(0.25F, 0.95F, 0.5F);
               pPoseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
               break;
            case NORTH:
               pPoseStack.translate(0.5F, 0.95F, 0.25F);
               break;
            case SOUTH:
               pPoseStack.translate(0.5F, 0.95F, 0.75F);
               pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
               break;
            default:
               pPoseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
         }

         pPoseStack.scale(0.5F, 0.5F, 0.5F);
         itemRenderer.renderStatic(
            (ItemStack)pBlockEntity.getItems().getFirst(),
            ItemDisplayContext.FIXED,
            this.getLightLevel(Objects.requireNonNull(pBlockEntity.getLevel()), pBlockEntity.getBlockPos()),
            OverlayTexture.NO_OVERLAY,
            pPoseStack,
            pBufferSource,
            pBlockEntity.getLevel(),
            1
         );
         pPoseStack.popPose();
      }
   }

   private int getLightLevel(Level level, BlockPos pos) {
      int bLight = level.getBrightness(LightLayer.BLOCK, pos);
      int sLight = level.getBrightness(LightLayer.SKY, pos);
      return LightTexture.pack(bLight, sLight);
   }
}
