package net.blay09.mods.balm.world.item.crafting;

import java.util.function.Supplier;
import net.blay09.mods.balm.core.BalmHolderRegistration;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public interface BalmRecipeTypeRegistration<TRecipeInput extends RecipeInput, TRecipe extends Recipe<TRecipeInput>>
   extends BalmHolderRegistration<RecipeType<TRecipe>> {
   BalmRecipeTypeRegistration<TRecipeInput, TRecipe> withSerializer(Supplier<RecipeSerializer<TRecipe>> var1);

   DeferredRecipeType<TRecipeInput, TRecipe> asDeferredRecipeType();
}
