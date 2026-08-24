package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.joefoxe.hexerei.data.recipes.CrowFluteRecipe;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

public class FluteRecipeCategory implements IRecipeCategory<CrowFluteRecipe> {
   public static final ResourceLocation UID = HexereiUtil.getResource("crow_flute_dye");
   public static final ResourceLocation TEXTURE = HexereiUtil.getResource("textures/gui/crow_flute_dye_gui_jei.png");
   private IDrawable background;
   private final IDrawable icon;

   public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height) {
      return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
   }

   public FluteRecipeCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(TEXTURE, 0, 0, 144, 86);
      this.icon = new ExtraFluteIcon(() -> new ItemStack((ItemLike)ModItems.CROW_FLUTE.get()));
   }

   public int getWidth() {
      return this.background.getWidth();
   }

   public int getHeight() {
      return this.background.getHeight();
   }

   public RecipeType<CrowFluteRecipe> getRecipeType() {
      return new RecipeType(HexereiUtil.getResource("crow_flute_dye"), CrowFluteRecipe.class);
   }

   public Component getTitle() {
      return Component.translatable("Crow Flute Crafting");
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, CrowFluteRecipe recipe, IFocusGroup focuses) {
      builder.moveRecipeTransferButton(160, 90);
      int size = recipe.getInputs().size();
      if (size > 0) {
         builder.addSlot(RecipeIngredientRole.INPUT, 15, 19).addIngredients((Ingredient)recipe.getInputs().get(0));
      }

      if (size > 1) {
         builder.addSlot(RecipeIngredientRole.INPUT, 33, 19).addIngredients((Ingredient)recipe.getInputs().get(1));
      }

      if (size > 2) {
         builder.addSlot(RecipeIngredientRole.INPUT, 51, 19).addIngredients((Ingredient)recipe.getInputs().get(2));
      }

      if (size > 3) {
         builder.addSlot(RecipeIngredientRole.INPUT, 15, 37).addIngredients((Ingredient)recipe.getInputs().get(3));
      }

      if (size > 4) {
         builder.addSlot(RecipeIngredientRole.INPUT, 33, 37).addIngredients((Ingredient)recipe.getInputs().get(4));
      }

      if (size > 5) {
         builder.addSlot(RecipeIngredientRole.INPUT, 51, 37).addIngredients((Ingredient)recipe.getInputs().get(5));
      }

      if (size > 6) {
         builder.addSlot(RecipeIngredientRole.INPUT, 15, 55).addIngredients((Ingredient)recipe.getInputs().get(6));
      }

      if (size > 7) {
         builder.addSlot(RecipeIngredientRole.INPUT, 33, 55).addIngredients((Ingredient)recipe.getInputs().get(7));
      }

      if (size > 8) {
         builder.addSlot(RecipeIngredientRole.INPUT, 51, 55).addIngredients((Ingredient)recipe.getInputs().get(8));
      }

      builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 37).addItemStack(recipe.getOutput());
   }

   public void draw(CrowFluteRecipe recipe, IRecipeSlotsView view, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics);
      Minecraft minecraft = Minecraft.getInstance();
      Component outputName = recipe.getOutput().getHoverName();
      int width = minecraft.font.width(outputName);
      float lineHeight = 9.0F / 2.0F;
      if (width > 131) {
         float percent = width / 131.0F;
         guiGraphics.pose().pushPose();
         guiGraphics.pose().scale(1.0F / percent, 1.0F / percent, 1.0F / percent);
         minecraft.font
            .drawInBatch(
               outputName,
               7.0F * percent,
               (5.0F + lineHeight) * percent - 4.5F,
               -12566464,
               false,
               guiGraphics.pose().last().pose(),
               guiGraphics.bufferSource(),
               DisplayMode.NORMAL,
               0,
               15728880
            );
         guiGraphics.pose().popPose();
      } else {
         minecraft.font
            .drawInBatch(
               outputName,
               7.0F,
               5.0F + lineHeight - 4.5F,
               -12566464,
               false,
               guiGraphics.pose().last().pose(),
               guiGraphics.bufferSource(),
               DisplayMode.NORMAL,
               0,
               15728880
            );
      }
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
