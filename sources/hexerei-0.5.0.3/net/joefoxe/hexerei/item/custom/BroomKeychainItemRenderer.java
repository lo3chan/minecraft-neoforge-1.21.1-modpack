package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

public class BroomKeychainItemRenderer extends CustomItemRenderer {
   @Override
   public void renderByItem(
      ItemStack stack, ItemDisplayContext displayContext, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      this.renderTileStuff(stack, displayContext, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
   }

   public static int getCustomColor(CompoundTag tag) {
      CompoundTag compoundtag = tag.contains("display") ? tag.getCompound("display") : null;
      return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 4337438;
   }

   private void renderItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      Minecraft.getInstance()
         .getItemRenderer()
         .renderStatic(stack, ItemDisplayContext.GUI, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, Minecraft.getInstance().level, 1);
   }

   private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
      Minecraft.getInstance()
         .getBlockRenderer()
         .renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
   }

   public void renderTileStuff(
      ItemStack stack, ItemDisplayContext displayContext, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.2, -0.1, -0.1);
      matrixStackIn.translate(0.5, 0.5, 0.5);
      this.renderItem(new ItemStack((ItemLike)ModItems.BROOM_KEYCHAIN_BASE.get(), 1), displayContext, matrixStackIn, bufferIn, combinedLightIn);
      CompoundTag tag2 = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      if (tag2.contains("Items")) {
         ListTag list = tag2.getList("Items", 10);
         ItemStack other = ItemStack.parseOptional(Hexerei.DynamicRegistries.get(), list.getCompound(0));
         if (!other.isEmpty() && !list.isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(-0.25, -0.28500000000000003, 0.0);
            matrixStackIn.scale(0.4F, 0.4F, 0.4F);
            this.renderItem(other, displayContext, matrixStackIn, bufferIn, combinedLightIn);
            matrixStackIn.popPose();
         }
      }

      matrixStackIn.popPose();
   }

   private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
      this.renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);
   }

   public void renderSingleBlock(
      BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color
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
                     p_110914_.last(),
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
               IClientItemExtensions.of(stack.getItem())
                  .getCustomRenderer()
                  .renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
         }
      }
   }
}
