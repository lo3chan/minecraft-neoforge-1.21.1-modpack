package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import mezz.jei.api.gui.drawable.IDrawable;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.joefoxe.hexerei.data.recipes.MixingCauldronRecipe;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.fluid.PotionMixingRecipes;
import net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.Tags.Fluids;
import org.joml.Matrix4f;

public class ExtraCauldronIcon implements IDrawable {
   private Supplier<ItemStack> extraSupplier;
   private ItemStack extraStack;
   private boolean findNewRecipe;
   private Recipe<?> recipeShown;
   private String type;
   private boolean showOutputItemInstead;

   public ExtraCauldronIcon(Supplier<ItemStack> secondary) {
      this.extraSupplier = secondary;
      this.findNewRecipe = true;
      this.type = "Fluid";
      this.showOutputItemInstead = false;
   }

   public ExtraCauldronIcon(Supplier<ItemStack> secondary, boolean showOutputItemInstead) {
      this.extraSupplier = secondary;
      this.findNewRecipe = true;
      this.type = "Fluid";
      this.showOutputItemInstead = showOutputItemInstead;
   }

   public ExtraCauldronIcon(Supplier<ItemStack> secondary, String type, boolean showOutputItemInstead) {
      this.extraSupplier = secondary;
      this.findNewRecipe = true;
      this.type = type;
      this.showOutputItemInstead = showOutputItemInstead;
   }

   public ExtraCauldronIcon(Supplier<ItemStack> secondary, String type) {
      this.extraSupplier = secondary;
      this.findNewRecipe = true;
      this.type = type;
      this.showOutputItemInstead = false;
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

      float craftPercent = ClientEvents.getClientTicks() % 100.0F / 100.0F;
      if (craftPercent <= 0.1 && this.findNewRecipe || this.recipeShown == null) {
         this.findNewRecipe = false;
         if (Minecraft.getInstance().level != null) {
            if (this.type.equals("Fluid")) {
               List<RecipeHolder<FluidMixingRecipe>> list = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(FluidMixingRecipe.Type.INSTANCE);
               this.recipeShown = list.get(new Random().nextInt(list.size())).value();
            } else if (this.type.equals("Potion")) {
               this.recipeShown = PotionMixingRecipes.ALL.get(new Random().nextInt(PotionMixingRecipes.ALL.size()));
            } else {
               List<RecipeHolder<MixingCauldronRecipe>> list = Minecraft.getInstance()
                  .level
                  .getRecipeManager()
                  .getAllRecipesFor(MixingCauldronRecipe.Type.INSTANCE);
               this.recipeShown = list.get(new Random().nextInt(list.size())).value();
            }
         }
      }

      if (craftPercent > 0.1) {
         this.findNewRecipe = true;
      }

      RenderSystem.enableDepthTest();
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(xOffset, yOffset, 0.0F);
      guiGraphics.pose().mulPose(new Matrix4f().scale(1.0F, -1.0F, 1.0F));
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(2.0F, -13.0F, 0.0F);
      guiGraphics.pose().scale(10.0F, 10.0F, 10.0F);
      BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
      Vec3 rotationOffset = new Vec3(0.0, 0.0, 0.0);
      float zRot = 0.0F;
      float xRot = 20.0F;
      float yRot = 30.0F;
      guiGraphics.pose().translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
      guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(zRot));
      guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(xRot));
      guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(yRot));
      guiGraphics.pose().translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);
      BlockState blockState = (BlockState)((MixingCauldron)ModBlocks.MIXING_CAULDRON.get()).defaultBlockState().setValue(MixingCauldron.GUI_RENDER, true);
      BlockRenderDispatcher rendererer = Minecraft.getInstance().getBlockRenderer();
      rendererer.getBlockModel(blockState);
      BakedModel bakedModel = rendererer.getBlockModel(blockState);
      RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      this.renderBlock(guiGraphics.pose(), buffer, 15728880, blockState, -12566464);
      if (this.recipeShown instanceof FluidMixingRecipe || this.recipeShown instanceof MixingCauldronRecipe) {
         float fillPercentage = 1.0F;
         if (this.recipeShown instanceof FluidMixingRecipe recipe) {
            if (recipe.getLiquid().getFluid().is(Fluids.GASEOUS)) {
               MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, recipe.getLiquid(), fillPercentage, 1.0F, OverlayTexture.NO_OVERLAY);
            } else {
               MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, recipe.getLiquid(), 1.0F, fillPercentage, OverlayTexture.NO_OVERLAY);
            }
         }

         if (this.recipeShown instanceof MixingCauldronRecipe recipex) {
            if (recipex.getLiquid().getFluid().is(Fluids.GASEOUS)) {
               MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, recipex.getLiquid(), fillPercentage, 1.0F, OverlayTexture.NO_OVERLAY);
            } else {
               MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, recipex.getLiquid(), 1.0F, fillPercentage, OverlayTexture.NO_OVERLAY);
            }
         }

         float height = 0.25F + 0.6875F * fillPercentage;
         Lighting.setupFor3DItems();

         for (int i = 0; i < this.recipeShown.getIngredients().size(); i++) {
            ItemStack[] items = ((Ingredient)this.recipeShown.getIngredients().get(i)).getItems();
            if (items.length > 0) {
               guiGraphics.pose().pushPose();
               guiGraphics.pose().translate(0.5, height + 0.00390625F, 0.5);
               double itemRotationOffset = 0.8 * i + craftPercent * (20.0F * craftPercent);
               guiGraphics.pose()
                  .translate(
                     0.0 + Math.sin(itemRotationOffset) / (3.5F + craftPercent * craftPercent * 10.0F),
                     Math.sin(3.141592653589793 * ClientEvents.getClientTicks() / 30.0 + i * 20) / 10.0 * 0.2,
                     0.0 + Math.cos(itemRotationOffset) / (3.5F + craftPercent * craftPercent * 10.0F)
                  );
               guiGraphics.pose().mulPose(Axis.YP.rotationDegrees((float)(45 * i - 1.0F + 2.0 * Math.sin((ClientEvents.getClientTicks() + i * 20) / 40.0F))));
               guiGraphics.pose().mulPose(Axis.XP.rotationDegrees((float)(82.5 + 5.0 * Math.cos((ClientEvents.getClientTicks() + i * 22) / 40.0F))));
               guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees((float)(-2.5 + 5.0 * Math.cos((ClientEvents.getClientTicks() + i * 24) / 40.0F))));
               guiGraphics.pose().scale(1.0F - craftPercent * 0.5F, 1.0F - craftPercent * 0.5F, 1.0F - craftPercent * 0.5F);
               guiGraphics.pose().scale(0.4F, 0.4F, 0.4F);
               this.renderItemFixed(
                  items[(int)ClientEvents.getClientTicksWithoutPartial() / 40 % items.length],
                  Minecraft.getInstance().level,
                  guiGraphics.pose(),
                  buffer,
                  15728880
               );
               guiGraphics.pose().popPose();
            }
         }
      }

      guiGraphics.pose().popPose();
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(14.0F, -14.0F, 100.0F);
      guiGraphics.pose().scale(0.5F, 0.5F, 0.5F);
      guiGraphics.pose().scale(16.0F, 16.0F, 16.0F);
      guiGraphics.pose().last().normal().rotate(Axis.YP.rotationDegrees(-45.0F));
      if (!this.extraStack.isEmpty() || this.showOutputItemInstead) {
         if (!this.showOutputItemInstead) {
            this.renderItem(this.extraStack, Minecraft.getInstance().level, guiGraphics.pose(), buffer, 15728880);
         } else {
            this.renderItem(
               this.recipeShown.getResultItem(Minecraft.getInstance().level.registryAccess()),
               Minecraft.getInstance().level,
               guiGraphics.pose(),
               buffer,
               15728880
            );
         }
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
