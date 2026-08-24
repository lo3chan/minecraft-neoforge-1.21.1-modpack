package jeresources.jei;

import java.util.List;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public abstract class BlankJEIRecipeCategory<T> implements IRecipeCategory<T> {
   private final IDrawable icon;
   protected final IRecipeCategoryExtension<T> recipeCategoryExtension;

   protected BlankJEIRecipeCategory(IDrawable icon, IRecipeCategoryExtension<T> recipeCategoryExtension) {
      this.icon = icon;
      this.recipeCategoryExtension = recipeCategoryExtension;
   }

   @NotNull
   public IDrawable getIcon() {
      return this.icon;
   }

   public void draw(T recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.recipeCategoryExtension.drawInfo(recipe, this.getBackground().getWidth(), this.getBackground().getHeight(), guiGraphics, mouseX, mouseY);
   }

   @NotNull
   public List<Component> getTooltipStrings(T recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
      return this.recipeCategoryExtension.getTooltipStrings(recipe, mouseX, mouseY);
   }
}
