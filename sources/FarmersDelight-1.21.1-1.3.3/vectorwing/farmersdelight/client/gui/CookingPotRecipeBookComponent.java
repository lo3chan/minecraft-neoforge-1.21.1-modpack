package vectorwing.farmersdelight.client.gui;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.utility.TextUtils;

public class CookingPotRecipeBookComponent extends RecipeBookComponent {
   protected static final WidgetSprites RECIPE_BOOK_BUTTONS = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("farmersdelight", "recipe_book/cooking_pot_enabled"),
      ResourceLocation.fromNamespaceAndPath("farmersdelight", "recipe_book/cooking_pot_disabled"),
      ResourceLocation.fromNamespaceAndPath("farmersdelight", "recipe_book/cooking_pot_enabled_highlighted"),
      ResourceLocation.fromNamespaceAndPath("farmersdelight", "recipe_book/cooking_pot_disabled_highlighted")
   );

   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(RECIPE_BOOK_BUTTONS);
   }

   public void hide() {
      this.setVisible(false);
   }

   @Nonnull
   protected Component getRecipeFilterName() {
      return TextUtils.container("recipe_book.cookable");
   }

   public void setupGhostRecipe(RecipeHolder<?> recipe, List<Slot> slots) {
      ItemStack resultStack = recipe.value().getResultItem(this.minecraft.level.registryAccess());
      this.ghostRecipe.setRecipe(recipe);
      if (slots.get(6).getItem().isEmpty()) {
         this.ghostRecipe.addIngredient(Ingredient.of(new ItemStack[]{resultStack}), slots.get(6).x, slots.get(6).y);
      }

      if (recipe.value() instanceof CookingPotRecipe cookingRecipe) {
         ItemStack containerStack = cookingRecipe.getOutputContainer();
         if (!containerStack.isEmpty()) {
            this.ghostRecipe.addIngredient(Ingredient.of(new ItemStack[]{containerStack}), slots.get(7).x, slots.get(7).y);
         }
      }

      this.placeRecipe(
         this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), recipe, recipe.value().getIngredients().iterator(), 0
      );
   }
}
