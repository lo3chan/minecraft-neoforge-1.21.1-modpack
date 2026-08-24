package net.blay09.mods.balm.api.recipe;

import java.util.function.Function;
import java.util.function.Supplier;
import net.blay09.mods.balm.api.DeferredObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

@Deprecated
public interface BalmRecipes {
   @Deprecated
   <T extends Recipe<?>> DeferredObject<RecipeType<T>> registerRecipeType(Function<ResourceLocation, RecipeType<T>> var1, ResourceLocation var2);

   @Deprecated
   <T extends Recipe<?>> DeferredObject<RecipeSerializer<T>> registerRecipeSerializer(Supplier<RecipeSerializer<T>> var1, ResourceLocation var2);

   @Deprecated
   default <T extends Recipe<?>> DeferredObject<RecipeType<T>> registerRecipeType(
      Supplier<RecipeType<T>> typeSupplier, Supplier<RecipeSerializer<T>> serializerSupplier, ResourceLocation identifier
   ) {
      this.registerRecipeSerializer(serializerSupplier, identifier);
      return this.registerRecipeType(id -> typeSupplier.get(), identifier);
   }
}
