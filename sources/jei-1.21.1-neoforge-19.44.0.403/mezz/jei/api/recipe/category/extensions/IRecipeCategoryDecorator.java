package mezz.jei.api.recipe.category.extensions;

import java.util.List;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public interface IRecipeCategoryDecorator<T> {
   default void draw(T recipe, IRecipeCategory<T> recipeCategory, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
   }

   @Deprecated(
      since = "19.5.4",
      forRemoval = true
   )
   default List<Component> decorateExistingTooltips(
      List<Component> tooltips, T recipe, IRecipeCategory<T> recipeCategory, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY
   ) {
      return tooltips;
   }

   default void decorateTooltips(
      ITooltipBuilder tooltip, T recipe, IRecipeCategory<T> recipeCategory, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY
   ) {
   }
}
