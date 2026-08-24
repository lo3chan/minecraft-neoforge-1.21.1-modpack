package mezz.jei.api.gui.ingredient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface IRecipeSlotView {
   Stream<ITypedIngredient<?>> getAllIngredients();

   @Unmodifiable
   List<ITypedIngredient<?>> getAllIngredientsList();

   Optional<ITypedIngredient<?>> getDisplayedIngredient();

   RecipeIngredientRole getRole();

   void drawHighlight(GuiGraphics var1, int var2);

   default <T> Stream<T> getIngredients(IIngredientType<T> ingredientType) {
      return this.getAllIngredients().map(i -> i.getIngredient(ingredientType)).flatMap(Optional::stream);
   }

   default Stream<ItemStack> getItemStacks() {
      return this.getIngredients(VanillaTypes.ITEM_STACK);
   }

   default boolean isEmpty() {
      return this.getAllIngredients().findAny().isEmpty();
   }

   default Optional<ItemStack> getDisplayedItemStack() {
      return this.getDisplayedIngredient(VanillaTypes.ITEM_STACK);
   }

   default <T> Optional<T> getDisplayedIngredient(IIngredientType<T> ingredientType) {
      return this.getDisplayedIngredient().flatMap(i -> i.getIngredient(ingredientType));
   }

   Optional<String> getSlotName();
}
