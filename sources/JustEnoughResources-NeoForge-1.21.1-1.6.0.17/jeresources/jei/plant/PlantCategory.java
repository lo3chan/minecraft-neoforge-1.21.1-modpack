package jeresources.jei.plant;

import jeresources.entry.PlantEntry;
import jeresources.jei.BlankJEIRecipeCategory;
import jeresources.jei.JEIConfig;
import jeresources.reference.Resources;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class PlantCategory extends BlankJEIRecipeCategory<PlantEntry> {
   private static final int GRASS_X = 80;
   private static final int GRASS_Y = 11;
   private static final int OUTPUT_X = 7;
   private static final int OUTPUT_SCALE = 20;
   private static final int OUTPUT_Y = 61;

   public PlantCategory() {
      super(JEIConfig.getJeiHelpers().getGuiHelper().createDrawable(Resources.Gui.Jei.TABS, 0, 16, 16, 16), new PlantWrapper());
   }

   @NotNull
   public Component getTitle() {
      return Component.translatable("jer.plant.title");
   }

   @NotNull
   public IDrawable getBackground() {
      return Resources.Gui.Jei.PLANT;
   }

   @NotNull
   public RecipeType<PlantEntry> getRecipeType() {
      return JEIConfig.PLANT_TYPE;
   }

   public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull PlantEntry recipe, @NotNull IFocusGroup focuses) {
      PlantTooltip plantTooltip = new PlantTooltip(recipe);
      ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 80, 11).addItemStack(recipe.getPlantItemStack())).addTooltipCallback(plantTooltip);
      int xOffset = 0;
      int yOffset = 0;

      for (int i = 0; i < recipe.getLootDropStacks().size(); i++) {
         ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 7 + xOffset, 61 + yOffset).addItemStack(recipe.getLootDropStacks().get(i)))
            .addTooltipCallback(plantTooltip);
         xOffset += 20;
         if (xOffset > 147) {
            xOffset = 0;
            yOffset += 20;
         }
      }
   }
}
