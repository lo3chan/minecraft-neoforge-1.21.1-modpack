package vectorwing.farmersdelight.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

public class ModRecipeTypes {
   public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, "farmersdelight");
   public static final Supplier<RecipeType<CookingPotRecipe>> COOKING = RECIPE_TYPES.register("cooking", () -> registerRecipeType("cooking"));
   public static final Supplier<RecipeType<CuttingBoardRecipe>> CUTTING = RECIPE_TYPES.register("cutting", () -> registerRecipeType("cutting"));

   public static <T extends Recipe<?>> RecipeType<T> registerRecipeType(final String identifier) {
      return new RecipeType<T>() {
         @Override
         public String toString() {
            return "farmersdelight:" + identifier;
         }
      };
   }
}
