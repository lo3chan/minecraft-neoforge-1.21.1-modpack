package mezz.jei.library.plugins.vanilla.anvil;

import java.util.Arrays;
import java.util.List;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import mezz.jei.common.platform.IPlatformRecipeHelper;
import mezz.jei.library.util.RecipeUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;

public abstract class SmithingCategoryExtension<R extends SmithingRecipe> implements ISmithingCategoryExtension<R> {
   private final IPlatformRecipeHelper recipeHelper;

   public SmithingCategoryExtension(IPlatformRecipeHelper recipeHelper) {
      this.recipeHelper = recipeHelper;
   }

   @Override
   public <T extends IIngredientAcceptor<T>> void setTemplate(R recipe, T ingredientAcceptor) {
      Ingredient ingredient = this.recipeHelper.getTemplate(recipe);
      ingredientAcceptor.addIngredients(ingredient);
   }

   @Override
   public <T extends IIngredientAcceptor<T>> void setBase(R recipe, T ingredientAcceptor) {
      Ingredient ingredient = this.recipeHelper.getBase(recipe);
      ingredientAcceptor.addIngredients(ingredient);
   }

   @Override
   public <T extends IIngredientAcceptor<T>> void setAddition(R recipe, T ingredientAcceptor) {
      Ingredient ingredient = this.recipeHelper.getAddition(recipe);
      ingredientAcceptor.addIngredients(ingredient);
   }

   @Override
   public <T extends IIngredientAcceptor<T>> void setOutput(R recipe, T ingredientAcceptor) {
      Ingredient templateIngredient = this.recipeHelper.getTemplate(recipe);
      Ingredient baseIngredient = this.recipeHelper.getBase(recipe);
      Ingredient additionIngredient = this.recipeHelper.getAddition(recipe);
      List<ItemStack> templateStacks = Arrays.asList(templateIngredient.getItems());
      if (templateStacks.isEmpty()) {
         templateStacks = List.of(ItemStack.EMPTY);
      }

      List<ItemStack> baseStacks = Arrays.asList(baseIngredient.getItems());
      if (baseStacks.isEmpty()) {
         baseStacks = List.of(ItemStack.EMPTY);
      }

      ItemStack addition = ItemStack.EMPTY;
      ItemStack[] additions = additionIngredient.getItems();
      if (additions.length > 0) {
         addition = additions[0];
      }

      for (ItemStack template : templateStacks) {
         for (ItemStack base : baseStacks) {
            SmithingRecipeInput recipeInput = new SmithingRecipeInput(template, base, addition);
            ItemStack output = RecipeUtil.assembleResultItem(recipeInput, recipe);
            ingredientAcceptor.addItemStack(output);
         }
      }
   }
}
