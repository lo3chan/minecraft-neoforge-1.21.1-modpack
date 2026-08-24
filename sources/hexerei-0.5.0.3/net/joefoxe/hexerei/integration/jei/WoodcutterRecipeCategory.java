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
import net.joefoxe.hexerei.data.recipes.WoodcutterRecipe;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public class WoodcutterRecipeCategory implements IRecipeCategory<WoodcutterRecipe> {
   public static final ResourceLocation UID = HexereiUtil.getResource("woodcutter");
   public static final ResourceLocation TEXTURE = HexereiUtil.getResource("textures/gui/drying_rack_jei.png");
   private final IDrawable background;
   private final IDrawable icon;

   public WoodcutterRecipeCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(TEXTURE, 0, 0, 100, 53);
      this.icon = helper.createDrawableItemStack(new ItemStack((ItemLike)ModBlocks.WILLOW_WOODCUTTER.get()));
   }

   public int getWidth() {
      return this.background.getWidth();
   }

   public int getHeight() {
      return this.background.getHeight();
   }

   public RecipeType<WoodcutterRecipe> getRecipeType() {
      return new RecipeType(UID, WoodcutterRecipe.class);
   }

   public Component getTitle() {
      return Component.translatable("hexerei.container.woodcutter");
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, WoodcutterRecipe recipe, IFocusGroup focuses) {
      int count = recipe.ingredientCount;
      Ingredient ingredient = (Ingredient)recipe.getIngredients().get(0);
      ItemStack[] stacks = ingredient.getItems();

      for (ItemStack stack : stacks) {
         stack.setCount(count);
      }

      builder.addSlot(RecipeIngredientRole.INPUT, 14, 16).addIngredients(ingredient);
      builder.addSlot(RecipeIngredientRole.OUTPUT, 70, 16).addItemStack(getResultItem(recipe));
   }

   public void draw(WoodcutterRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics);
      Minecraft minecraft = Minecraft.getInstance();
      guiGraphics.pose().pushPose();
      guiGraphics.pose().scale(0.6F, 0.6F, 0.6F);
      String outputName = recipe.getResultItem(Minecraft.getInstance().level.registryAccess()).getHoverName().getString();
      minecraft.font
         .drawInBatch(
            outputName, 8.33F, 6.664F, -12566464, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 15728880
         );
      guiGraphics.pose().popPose();
   }

   public static ItemStack getResultItem(Recipe<?> recipe) {
      Minecraft minecraft = Minecraft.getInstance();
      ClientLevel level = minecraft.level;
      if (level == null) {
         throw new NullPointerException("level must not be null.");
      } else {
         RegistryAccess registryAccess = level.registryAccess();
         return recipe.getResultItem(registryAccess);
      }
   }
}
