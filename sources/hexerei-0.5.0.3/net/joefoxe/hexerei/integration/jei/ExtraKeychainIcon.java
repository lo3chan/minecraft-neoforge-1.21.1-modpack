package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Collection;
import java.util.Random;
import java.util.function.Supplier;
import mezz.jei.api.gui.drawable.IDrawable;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.KeychainItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;

public class ExtraKeychainIcon implements IDrawable {
   private Supplier<ItemStack> extraSupplier;
   private ItemStack extraStack;
   private boolean findNewItem;
   private ItemStack attachedItem;

   public ExtraKeychainIcon(Supplier<ItemStack> secondary) {
      this.extraSupplier = secondary;
      this.findNewItem = true;
   }

   public int getWidth() {
      return 18;
   }

   public int getHeight() {
      return 18;
   }

   public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
      if (this.extraStack == null) {
         this.extraStack = this.extraSupplier.get();
      }

      float timer = ClientEvents.getClientTicks() % 100.0F / 100.0F;
      if (timer <= 0.1 && this.findNewItem || this.attachedItem == null) {
         this.findNewItem = false;
         Collection<Item> col = BuiltInRegistries.ITEM.stream().toList();
         Random rand = new Random();
         if (col.toArray()[(int)(col.size() * rand.nextFloat())] instanceof Item item) {
            this.attachedItem = new ItemStack(item);
         }
      }

      if (timer > 0.1) {
         this.findNewItem = true;
      }

      RenderSystem.enableDepthTest();
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(xOffset, yOffset, 0.0F);
      guiGraphics.pose().mulPose(new Matrix4f().scale(1.0F, -1.0F, 1.0F));
      Lighting.setupForFlatItems();
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(9.0F, -9.0F, 9.0F);
      guiGraphics.pose().scale(15.0F, 15.0F, 15.0F);
      Vec3 rotationOffset = new Vec3(0.0, 0.0, 0.0);
      float zRot = 0.0F;
      float xRot = 20.0F;
      float yRot = 210.0F;
      guiGraphics.pose().translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
      guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(zRot));
      guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(xRot));
      guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(yRot));
      guiGraphics.pose().translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);
      BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
      RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      ItemStack recipe_stack = this.attachedItem;
      new ItemStack((ItemLike)ModItems.BROOM_KEYCHAIN.get());
      ItemStack keychain = new ItemStack((ItemLike)ModItems.BROOM_KEYCHAIN.get());
      ItemStack other = this.attachedItem;
      if (keychain.getItem() instanceof KeychainItem && !other.isEmpty()) {
         CompoundTag tag = ((CustomData)keychain.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         ListTag listtag = new ListTag();
         if (!other.isEmpty()) {
            Tag tag1 = other.save(Hexerei.DynamicRegistries.get());
            listtag.add(tag1);
         }

         tag.put("Items", listtag);
         keychain.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      }

      guiGraphics.pose().last().normal().rotate(Axis.YP.rotationDegrees(-45.0F));
      this.renderItemFixed(keychain, Minecraft.getInstance().level, guiGraphics.pose(), buffer, 15728880);
      guiGraphics.pose().popPose();
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(14.0F, -14.0F, 100.0F);
      guiGraphics.pose().scale(0.5F, 0.5F, 0.5F);
      guiGraphics.pose().scale(16.0F, 16.0F, 16.0F);
      guiGraphics.pose().last().normal().rotate(Axis.YP.rotationDegrees(-45.0F));
      if (!recipe_stack.isEmpty()) {
         this.renderItem(recipe_stack, Minecraft.getInstance().level, guiGraphics.pose(), buffer, 15728880);
      }

      guiGraphics.pose().popPose();
      guiGraphics.pose().popPose();
      buffer.endBatch();
      RenderSystem.enableDepthTest();
      Lighting.setupFor3DItems();
   }

   private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      Minecraft.getInstance()
         .getItemRenderer()
         .renderStatic(stack, ItemDisplayContext.GUI, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, level, 1);
   }

   private void renderItemFixed(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      Minecraft.getInstance()
         .getItemRenderer()
         .renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, level, 1);
   }

   @OnlyIn(Dist.CLIENT)
   private void renderBlock(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
      this.renderSingleBlock(state, matrixStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);
   }

   @OnlyIn(Dist.CLIENT)
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
