package mezz.jei.api.gui.builder;

import mezz.jei.api.gui.widgets.ISlottedWidgetFactory;
import mezz.jei.api.recipe.RecipeIngredientRole;

public interface IRecipeLayoutBuilder {
   default IRecipeSlotBuilder addInputSlot(int x, int y) {
      return this.addSlot(RecipeIngredientRole.INPUT).setPosition(x, y);
   }

   default IRecipeSlotBuilder addInputSlot() {
      return this.addSlot(RecipeIngredientRole.INPUT);
   }

   default IRecipeSlotBuilder addOutputSlot(int x, int y) {
      return this.addSlot(RecipeIngredientRole.OUTPUT).setPosition(x, y);
   }

   default IRecipeSlotBuilder addOutputSlot() {
      return this.addSlot(RecipeIngredientRole.OUTPUT);
   }

   default IRecipeSlotBuilder addSlot(RecipeIngredientRole role, int x, int y) {
      return this.addSlot(role).setPosition(x, y);
   }

   IRecipeSlotBuilder addSlot(RecipeIngredientRole var1);

   @Deprecated(
      since = "19.19.3",
      forRemoval = true
   )
   IRecipeSlotBuilder addSlotToWidget(RecipeIngredientRole var1, ISlottedWidgetFactory<?> var2);

   IIngredientAcceptor<?> addInvisibleIngredients(RecipeIngredientRole var1);

   void moveRecipeTransferButton(int var1, int var2);

   void setShapeless();

   void setShapeless(int var1, int var2);

   void createFocusLink(IIngredientAcceptor<?>... var1);
}
