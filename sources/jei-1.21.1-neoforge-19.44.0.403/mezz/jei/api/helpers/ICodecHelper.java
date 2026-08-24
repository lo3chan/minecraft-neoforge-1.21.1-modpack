package mezz.jei.api.helpers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.world.item.crafting.RecipeHolder;

public interface ICodecHelper {
   Codec<IIngredientType<?>> getIngredientTypeCodec();

   Codec<RecipeType<?>> getRecipeTypeCodec(IRecipeManager var1);

   MapCodec<ITypedIngredient<?>> getTypedIngredientCodec();

   <T> Codec<ITypedIngredient<T>> getTypedIngredientCodec(IIngredientType<T> var1);

   <T extends RecipeHolder<?>> Codec<T> getRecipeHolderCodec();

   <T> Codec<T> getSlowRecipeCategoryCodec(IRecipeCategory<T> var1, IRecipeManager var2);
}
