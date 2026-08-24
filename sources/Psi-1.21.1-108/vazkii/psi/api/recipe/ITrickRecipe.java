package vazkii.psi.api.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.piece.PieceCraftingTrick;

public interface ITrickRecipe extends Recipe<SingleRecipeInput> {
   ResourceLocation TYPE_ID = PsiAPI.location("trick_crafting");

   @Nullable
   PieceCraftingTrick getPiece();

   @NotNull
   Ingredient getInput();

   @NotNull
   ItemStack getResultItem(@NotNull Provider var1);

   ItemStack getAssembly();

   @NotNull
   default RecipeType<?> getType() {
      return (RecipeType<?>)BuiltInRegistries.RECIPE_TYPE.get(TYPE_ID);
   }

   @NotNull
   default NonNullList<Ingredient> getIngredients() {
      return NonNullList.withSize(1, this.getInput());
   }
}
