package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.OptionalInt;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

public class CrowBlankAmuletItemRenderer extends CustomItemRenderer {
   @Override
   public void renderByItem(
      ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      this.renderTileStuff(stack, itemDisplayContext, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
   }

   private void renderItem(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      Minecraft.getInstance()
         .getItemRenderer()
         .renderStatic(stack, ItemDisplayContext.GUI, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, Minecraft.getInstance().level, 1);
   }

   private void renderItem(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, ItemDisplayContext itemDisplayContext) {
      Minecraft.getInstance()
         .getItemRenderer()
         .renderStatic(stack, itemDisplayContext, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, Minecraft.getInstance().level, 1);
   }

   private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
      Minecraft.getInstance()
         .getBlockRenderer()
         .renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
   }

   public OptionalInt getFramedMapId(ItemStack stack) {
      if (stack.is(Items.FILLED_MAP)) {
         MapId mapId = (MapId)stack.get(DataComponents.MAP_ID);
         if (mapId != null) {
            return OptionalInt.of(mapId.id());
         }
      }

      return OptionalInt.empty();
   }

   public void renderTileStuff(
      ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.703125F, 0.40625F, 0.4375F);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      ItemStack otherItem = ItemStack.EMPTY;
      CompoundTag tag2 = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      if (tag2.contains("Items")) {
         ListTag list = tag2.getList("Items", 10);
         ItemStack other = ItemStack.parseOptional(Hexerei.DynamicRegistries.get(), list.getCompound(0));
         if (!other.isEmpty() && !list.isEmpty()) {
            otherItem = other;
         }
      }

      Level level = Minecraft.getInstance().level;
      if (otherItem.is(Items.FILLED_MAP) && level != null) {
         matrixStackIn.pushPose();
         matrixStackIn.translate(-0.5F, -0.5F, 0.0F);
         matrixStackIn.scale(0.0078125F, 0.0078125F, 1.0F);
         matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
         matrixStackIn.translate(-128.0F, -128.0F, 0.0F);
         matrixStackIn.scale(0.8695F, 0.8695F, 1.0F);
         matrixStackIn.translate(9.5F, 9.5F, 0.0078125F);
         MapItemSavedData mapitemsaveddata = MapItem.getSavedData(otherItem, level);
         if (mapitemsaveddata != null && otherItem.get(DataComponents.MAP_ID) != null) {
            Minecraft.getInstance()
               .gameRenderer
               .getMapRenderer()
               .render(matrixStackIn, bufferIn, (MapId)otherItem.get(DataComponents.MAP_ID), mapitemsaveddata, true, combinedLightIn);
         }

         matrixStackIn.popPose();
         matrixStackIn.translate(0.0F, 0.0F, 0.03F);
         this.renderItem(
            new ItemStack((ItemLike)ModItems.CROW_BLANK_AMULET_TRINKET_FRAME.get()), matrixStackIn, bufferIn, combinedLightIn, ItemDisplayContext.FIXED
         );
      } else {
         this.renderItem(
            new ItemStack((ItemLike)ModItems.CROW_BLANK_AMULET_TRINKET.get(), 1), matrixStackIn, bufferIn, combinedLightIn, ItemDisplayContext.FIXED
         );
         matrixStackIn.pushPose();
         BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(otherItem, null, null, 0);
         if (itemModel.isGui3d()) {
            matrixStackIn.translate(0.0F, -0.025F, -0.035F);
            matrixStackIn.scale(1.0F, 1.0F, 0.1F);
            matrixStackIn.last().normal().transpose();
         } else {
            matrixStackIn.translate(0.0F, -0.025F, -0.025F);
            matrixStackIn.scale(0.85F, 0.85F, 1.0F);
         }

         matrixStackIn.scale(0.7F, 0.7F, 0.7F);
         this.renderItem(otherItem, matrixStackIn, bufferIn, combinedLightIn, ItemDisplayContext.FIXED);
         matrixStackIn.popPose();
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
