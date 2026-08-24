package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import mezz.jei.api.gui.drawable.IDrawable;
import net.joefoxe.hexerei.data.recipes.CrowFluteRecipe;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.custom.CrowFluteItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;

public class ExtraFluteIcon implements IDrawable {
   private Supplier<ItemStack> extraSupplier;
   private ItemStack extraStack;
   private boolean findNewRecipe;
   private Recipe<?> recipeShown;
   private List<CraftingRecipe> flute_recipe;
   private int color1 = 0;
   private int color2 = 1;
   private Random rand = new Random();

   public ExtraFluteIcon(Supplier<ItemStack> secondary) {
      this.extraSupplier = secondary;
      this.findNewRecipe = true;
      if (Minecraft.getInstance().level != null) {
         List<CraftingRecipe> recipes = Minecraft.getInstance()
            .level
            .getRecipeManager()
            .getAllRecipesFor(RecipeType.CRAFTING)
            .stream()
            .<CraftingRecipe>map(RecipeHolder::value)
            .toList();
         this.flute_recipe = recipes.stream().filter(craftingRecipe -> craftingRecipe instanceof CrowFluteRecipe).toList();
      }
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
      if (timer <= 0.1 && this.findNewRecipe || this.recipeShown == null) {
         this.findNewRecipe = false;
         this.recipeShown = (Recipe<?>)this.flute_recipe.get(new Random().nextInt(this.flute_recipe.size()));
         this.color1 = this.rand.nextInt(DyeColor.values().length - 1);
         this.color2 = this.rand.nextInt(DyeColor.values().length - 1);
      }

      if (timer > 0.1) {
         this.findNewRecipe = true;
      }

      RenderSystem.enableDepthTest();
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(xOffset, yOffset, 0.0F);
      guiGraphics.pose().mulPose(new Matrix4f().scale(1.0F, -1.0F, 1.0F));
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(9.0F, -9.0F, 9.0F);
      guiGraphics.pose().scale(20.0F, 20.0F, 20.0F);
      Vec3 rotationOffset = new Vec3(0.0, 0.0, 0.0);
      float zRot = 0.0F;
      float xRot = 20.0F;
      float yRot = 30.0F;
      guiGraphics.pose().translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
      guiGraphics.pose().translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);
      BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
      RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      ItemStack output_stack = CrowFluteItem.withColors(this.color1, this.color2);
      this.renderItem(output_stack, Minecraft.getInstance().level, guiGraphics.pose(), buffer, 15728880);
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
