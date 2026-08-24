package net.joefoxe.hexerei.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.PestleAndMortar;
import net.joefoxe.hexerei.data.recipes.PestleAndMortarRecipe;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class PestleAndMortarRecipeCategory implements IRecipeCategory<PestleAndMortarRecipe> {
   public static final ResourceLocation UID = HexereiUtil.getResource("pestle_and_mortar");
   public static final ResourceLocation TEXTURE = HexereiUtil.getResource("textures/gui/pestle_and_mortar_jei.png");
   private final IDrawable background;
   private final IDrawable icon;

   public PestleAndMortarRecipeCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(TEXTURE, 0, 0, 143, 80);
      this.icon = helper.createDrawableItemStack(new ItemStack((ItemLike)ModBlocks.PESTLE_AND_MORTAR.get()));
   }

   public int getWidth() {
      return this.background.getWidth();
   }

   public int getHeight() {
      return this.background.getHeight();
   }

   public RecipeType<PestleAndMortarRecipe> getRecipeType() {
      return new RecipeType(UID, PestleAndMortarRecipe.class);
   }

   public Component getTitle() {
      return ((PestleAndMortar)ModBlocks.PESTLE_AND_MORTAR.get()).getName();
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, PestleAndMortarRecipe recipe, IFocusGroup focuses) {
      builder.setShapeless();
      if (recipe.getIngredients().size() > 0) {
         builder.addSlot(RecipeIngredientRole.INPUT, 11, 14).addIngredients((Ingredient)recipe.getIngredients().get(0));
      }

      if (recipe.getIngredients().size() > 1) {
         builder.addSlot(RecipeIngredientRole.INPUT, 20, 36).addIngredients((Ingredient)recipe.getIngredients().get(1));
      }

      if (recipe.getIngredients().size() > 2) {
         builder.addSlot(RecipeIngredientRole.INPUT, 42, 45).addIngredients((Ingredient)recipe.getIngredients().get(2));
      }

      if (recipe.getIngredients().size() > 3) {
         builder.addSlot(RecipeIngredientRole.INPUT, 64, 36).addIngredients((Ingredient)recipe.getIngredients().get(3));
      }

      if (recipe.getIngredients().size() > 4) {
         builder.addSlot(RecipeIngredientRole.INPUT, 73, 14).addIngredients((Ingredient)recipe.getIngredients().get(4));
      }

      builder.addSlot(RecipeIngredientRole.OUTPUT, 117, 31).addItemStack(recipe.getOutput());
   }

   public void draw(PestleAndMortarRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics);
      int grindingTime = recipe.getGrindingTime();
      Minecraft minecraft = Minecraft.getInstance();
      guiGraphics.pose().pushPose();
      guiGraphics.pose().scale(0.6F, 0.6F, 0.6F);
      String grindingTimeString = grindingTime < 2147483647 ? grindingTime / 20 + (grindingTime % 20 == 0 ? "" : "." + grindingTime % 20) : "?";
      if (grindingTimeString.charAt(grindingTimeString.length() - 1) == '0' && grindingTime != 0 && grindingTime % 20 != 0) {
         grindingTimeString = grindingTimeString.substring(0, grindingTimeString.length() - 1);
      }

      MutableComponent dip_time_1 = Component.translatable("gui.jei.category.pestle_and_mortar.grind_time_1");
      MutableComponent dip_time_3 = Component.translatable("gui.jei.category.dipper.resultSeconds", new Object[]{grindingTimeString});
      minecraft.font
         .drawInBatch(
            dip_time_1, 9.996F, 114.121F, -8355712, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 15728880
         );
      minecraft.font
         .drawInBatch(
            dip_time_3, 96.628F, 114.121F, -8355712, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 15728880
         );
      String outputName = recipe.getOutput().getHoverName().getString();
      minecraft.font
         .drawInBatch(
            outputName, 8.33F, 6.664F, -12566464, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 15728880
         );
      guiGraphics.pose().popPose();
   }
}
