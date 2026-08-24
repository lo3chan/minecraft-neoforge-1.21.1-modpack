package net.nycto_team.overpacked.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nycto_team.overpacked.recipe.GiantBackpackRecipe;

public class ModRecipes {
   public static final DeferredRegister<RecipeSerializer<?>> reg = DeferredRegister.create(Registries.RECIPE_SERIALIZER, "overpacked");
   public static final Supplier<RecipeSerializer<GiantBackpackRecipe>> backpack_coloring = reg(
      "backpack_coloring", () -> new SimpleCraftingRecipeSerializer(GiantBackpackRecipe::new)
   );

   public static void Register(IEventBus bus) {
      reg.register(bus);
   }

   private static <T extends Recipe<?>> Supplier<RecipeSerializer<T>> reg(String name, Supplier<RecipeSerializer<T>> value) {
      return reg.register(name, value);
   }
}
