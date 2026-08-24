package mezz.jei.api.helpers;

import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IIngredientVisibility;
import net.minecraft.resources.ResourceLocation;

public interface IJeiHelpers {
   IGuiHelper getGuiHelper();

   IStackHelper getStackHelper();

   IModIdHelper getModIdHelper();

   IFocusFactory getFocusFactory();

   IColorHelper getColorHelper();

   IPlatformFluidHelper<?> getPlatformFluidHelper();

   <T> Optional<RecipeType<T>> getRecipeType(ResourceLocation var1, Class<? extends T> var2);

   Optional<RecipeType<?>> getRecipeType(ResourceLocation var1);

   Stream<RecipeType<?>> getAllRecipeTypes();

   IIngredientManager getIngredientManager();

   ICodecHelper getCodecHelper();

   IVanillaRecipeFactory getVanillaRecipeFactory();

   IIngredientVisibility getIngredientVisibility();
}
