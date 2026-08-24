package net.joefoxe.hexerei.client.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.block.custom.PickableDoublePlant;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.events.CrowWhitelistEvent;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw.Layer;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;

public class CrowWhitelistRenderer implements Layer {
   private static final ResourceLocation GUI = HexereiUtil.getResource("textures/gui/crow_gui.png");

   public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
      int screenWidth = guiGraphics.guiWidth();
      int screenHeight = guiGraphics.guiHeight();
      PoseStack poseStack = guiGraphics.pose();
      if (CrowWhitelistEvent.whiteListingCrow != null) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, GUI);
         guiGraphics.blit(GUI, screenWidth / 2 - 9, screenHeight - 42, 238.0F, 178.0F, 18, 18, 256, 256);
         InventoryScreen.renderEntityInInventoryFollowsAngle(
            guiGraphics,
            screenWidth / 2 - 16,
            screenHeight - 94,
            screenWidth / 2 + 16,
            screenHeight - 62,
            40,
            0.0625F,
            (float)Math.toRadians(-50.0),
            (float)Math.toRadians(10.0),
            CrowWhitelistEvent.whiteListingCrow
         );
         if (!CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.isEmpty()) {
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.pushPose();
            poseStack.translate(
               screenWidth / 2.0F - 14.0F - (CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.size() - 1) / 2.0F * 21.0F, screenHeight - 40, 100.0F
            );
            poseStack.translate(8.0F, -8.0F, 0.0F);
            poseStack.scale(12.0F, 12.0F, 12.0F);
            poseStack.mulPose(new Matrix4f().scale(1.0F, -1.0F, 1.0F));
            Vec3 rotationOffset = new Vec3(0.5, 0.0, 0.5);
            BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            Lighting.setupFor3DItems();
            poseStack.last().normal().rotate(Axis.YP.rotationDegrees(-90.0F));

            for (int itor = 0; itor < CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.size(); itor++) {
               poseStack.pushPose();
               poseStack.translate(itor * 1.7F, Math.sin((ClientEvents.getClientTicks() + itor * 30) / 30.0F) / 4.0, 0.0);
               float zRot = 0.0F;
               float xRot = 20.0F;
               float yRot = 30.0F + ClientEvents.getClientTicks() + itor * 30;
               poseStack.translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
               poseStack.mulPose(Axis.ZP.rotationDegrees(zRot));
               poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
               poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
               poseStack.translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);
               BlockState state = CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.get(itor).defaultBlockState();
               if (state.hasProperty(BlockStateProperties.AGE_1)) {
                  state = (BlockState)state.setValue(
                     BlockStateProperties.AGE_1, Mth.clamp((int)((Math.sin((ClientEvents.getClientTicks() + itor * 30) / 30.0F) + 1.0) / 2.0 * 2.0), 0, 1)
                  );
               } else if (state.hasProperty(BlockStateProperties.AGE_2)) {
                  state = (BlockState)state.setValue(
                     BlockStateProperties.AGE_2, Mth.clamp((int)((Math.sin((ClientEvents.getClientTicks() + itor * 30) / 30.0F) + 1.0) / 2.0 * 3.0), 0, 2)
                  );
               } else if (state.hasProperty(BlockStateProperties.AGE_3)) {
                  state = (BlockState)state.setValue(
                     BlockStateProperties.AGE_3, Mth.clamp((int)((Math.sin((ClientEvents.getClientTicks() + itor * 30) / 30.0F) + 1.0) / 2.0 * 4.0), 0, 3)
                  );
               } else if (state.hasProperty(BlockStateProperties.AGE_4)) {
                  state = (BlockState)state.setValue(
                     BlockStateProperties.AGE_4, Mth.clamp((int)((Math.sin((ClientEvents.getClientTicks() + itor * 30) / 30.0F) + 1.0) / 2.0 * 5.0), 0, 4)
                  );
               } else if (state.hasProperty(BlockStateProperties.AGE_5)) {
                  state = (BlockState)state.setValue(
                     BlockStateProperties.AGE_5, Mth.clamp((int)((Math.sin((ClientEvents.getClientTicks() + itor * 30) / 30.0F) + 1.0) / 2.0 * 6.0), 0, 5)
                  );
               } else if (state.hasProperty(BlockStateProperties.AGE_7)) {
                  state = (BlockState)state.setValue(
                     BlockStateProperties.AGE_7, Mth.clamp((int)((Math.sin((ClientEvents.getClientTicks() + itor * 30) / 30.0F) + 1.0) / 2.0 * 8.0), 0, 7)
                  );
               }

               if (state.hasProperty(BlockStateProperties.BERRIES)) {
                  state = (BlockState)state.setValue(BlockStateProperties.BERRIES, true);
               }

               this.renderBlock(poseStack, buffer, 15728880, state, -1);
               if (state.hasProperty(PickableDoublePlant.HALF)) {
                  poseStack.pushPose();
                  poseStack.translate(0.0F, 1.0F, 0.0F);
                  state = (BlockState)state.setValue(PickableDoublePlant.HALF, DoubleBlockHalf.UPPER);
                  this.renderBlock(poseStack, buffer, 15728880, state, -1);
                  poseStack.popPose();
               }

               poseStack.popPose();
            }

            buffer.endBatch();
            poseStack.popPose();
         }
      }
   }

   private void renderBlock(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
      this.renderSingleBlock(state, matrixStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);
   }

   public void renderSingleBlock(
      BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color
   ) {
      RenderShape rendershape = p_110913_.getRenderShape();
      if (rendershape != RenderShape.INVISIBLE) {
         switch (rendershape) {
            case MODEL:
               BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
               BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
               float f = (color >> 16 & 0xFF) / 255.0F;
               float f1 = (color >> 8 & 0xFF) / 255.0F;
               float f2 = (color & 0xFF) / 255.0F;
               dispatcher.getModelRenderer()
                  .renderModel(
                     poseStack.last(),
                     p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)),
                     p_110913_,
                     bakedmodel,
                     f,
                     f1,
                     f2,
                     p_110916_,
                     p_110917_,
                     modelData,
                     null
                  );
               break;
            case ENTITYBLOCK_ANIMATED:
               ItemStack stack = new ItemStack(p_110913_.getBlock());
               poseStack.translate(0.2, -0.1, -0.1);
               IClientItemExtensions.of(stack.getItem())
                  .getCustomRenderer()
                  .renderByItem(stack, ItemDisplayContext.NONE, poseStack, p_110915_, p_110916_, p_110917_);
         }
      }
   }
}
