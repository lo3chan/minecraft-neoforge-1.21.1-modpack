package mezz.jei.library.plugins.vanilla.brewing;

import java.util.List;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.textures.Textures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class BrewingRecipeCategory extends AbstractRecipeCategory<IJeiBrewingRecipe> {
   private final IDrawable background;
   private final IDrawableAnimated arrow;
   private final IDrawableAnimated bubbles;
   private final IDrawableStatic blazeHeat;

   public BrewingRecipeCategory(IGuiHelper guiHelper) {
      super(RecipeTypes.BREWING, Component.translatable("gui.jei.category.brewing"), guiHelper.createDrawableItemLike(Blocks.BREWING_STAND), 114, 61);
      Textures textures = Internal.getTextures();
      this.background = textures.getBrewingStandBackground();
      this.arrow = guiHelper.createAnimatedDrawable(textures.getBrewingStandArrow(), 400, IDrawableAnimated.StartDirection.TOP, false);
      ITickTimer bubblesTickTimer = new BrewingRecipeCategory.BrewingBubblesTickTimer(guiHelper);
      this.bubbles = guiHelper.createAnimatedDrawable(textures.getBrewingStandBubbles(), bubblesTickTimer, IDrawableAnimated.StartDirection.BOTTOM);
      this.blazeHeat = textures.getBrewingStandBlazeHeat();
   }

   public void draw(IJeiBrewingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics, 0, 1);
      this.blazeHeat.draw(guiGraphics, 5, 30);
      this.bubbles.draw(guiGraphics, 9, 1);
      this.arrow.draw(guiGraphics, 43, 3);
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, IJeiBrewingRecipe recipe, IFocusGroup focuses) {
      int brewingSteps = recipe.getBrewingSteps();
      String brewingStepsString = brewingSteps < 2147483647 ? Integer.toString(brewingSteps) : "?";
      Component steps = Component.translatable("gui.jei.category.brewing.steps", new Object[]{brewingStepsString});
      builder.addText(steps, 42, 12).setPosition(70, 28).setColor(-8355712);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, IJeiBrewingRecipe recipe, IFocusGroup focuses) {
      List<ItemStack> potionInputs = recipe.getPotionInputs();
      builder.addInputSlot(1, 37).addItemStacks(potionInputs);
      builder.addInputSlot(24, 44).addItemStacks(potionInputs);
      builder.addInputSlot(47, 37).addItemStacks(potionInputs);
      builder.addInputSlot(24, 3).addItemStacks(recipe.getIngredients());
      builder.addOutputSlot(81, 3).addItemStack(recipe.getPotionOutput()).setStandardSlotBackground();
   }

   @Nullable
   public ResourceLocation getRegistryName(IJeiBrewingRecipe recipe) {
      return recipe.getUid();
   }

   private static class BrewingBubblesTickTimer implements ITickTimer {
      private static final int[] BUBBLE_LENGTHS = new int[]{29, 23, 18, 13, 9, 5, 0};
      private final ITickTimer internalTimer;

      public BrewingBubblesTickTimer(IGuiHelper guiHelper) {
         this.internalTimer = guiHelper.createTickTimer(14, BUBBLE_LENGTHS.length - 1, false);
      }

      @Override
      public int getValue() {
         int timerValue = this.internalTimer.getValue();
         return BUBBLE_LENGTHS[timerValue];
      }

      @Override
      public int getMaxValue() {
         return BUBBLE_LENGTHS[0];
      }
   }
}
