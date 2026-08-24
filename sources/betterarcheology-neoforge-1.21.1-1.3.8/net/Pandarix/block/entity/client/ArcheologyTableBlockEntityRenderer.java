package net.Pandarix.block.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Objects;
import net.Pandarix.block.entity.ArcheologyTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class ArcheologyTableBlockEntityRenderer implements BlockEntityRenderer<ArcheologyTableBlockEntity> {
   public ArcheologyTableBlockEntityRenderer(Context context) {
   }

   public void render(
      ArcheologyTableBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay
   ) {
      ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
      List<ItemStack> inventoryContents = pBlockEntity.getItems();
      ItemStack brush = inventoryContents.get(0);
      ItemStack unidentified = inventoryContents.get(1);
      ItemStack identified = inventoryContents.get(2);
      pPoseStack.pushPose();
      pPoseStack.translate(0.35F, 1.025F, 0.7F);
      pPoseStack.scale(0.65F, 0.65F, 0.65F);
      pPoseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
      itemRenderer.renderStatic(
         brush,
         ItemDisplayContext.GUI,
         this.getLightLevel(Objects.requireNonNull(pBlockEntity.getLevel()), pBlockEntity.getBlockPos().above()),
         OverlayTexture.NO_OVERLAY,
         pPoseStack,
         pBufferSource,
         pBlockEntity.getLevel(),
         1
      );
      pPoseStack.popPose();
      pPoseStack.pushPose();
      pPoseStack.translate(0.550000011920929, 1.025, 0.4000000059604645);
      pPoseStack.scale(0.55F, 0.55F, 0.55F);
      pPoseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
      if (identified.isEmpty()) {
         itemRenderer.renderStatic(
            unidentified,
            ItemDisplayContext.GUI,
            this.getLightLevel(Objects.requireNonNull(pBlockEntity.getLevel()), pBlockEntity.getBlockPos().above()),
            OverlayTexture.NO_OVERLAY,
            pPoseStack,
            pBufferSource,
            pBlockEntity.getLevel(),
            1
         );
      } else {
         itemRenderer.renderStatic(
            identified,
            ItemDisplayContext.GUI,
            this.getLightLevel(Objects.requireNonNull(pBlockEntity.getLevel()), pBlockEntity.getBlockPos().above()),
            OverlayTexture.NO_OVERLAY,
            pPoseStack,
            pBufferSource,
            pBlockEntity.getLevel(),
            1
         );
      }

      pPoseStack.popPose();
   }

   private int getLightLevel(Level world, BlockPos pos) {
      int bLight = world.getBrightness(LightLayer.BLOCK, pos);
      int sLight = world.getBrightness(LightLayer.SKY, pos);
      return LightTexture.pack(bLight, sLight);
   }
}
