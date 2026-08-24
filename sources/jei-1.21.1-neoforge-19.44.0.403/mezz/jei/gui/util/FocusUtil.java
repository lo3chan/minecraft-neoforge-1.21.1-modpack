package mezz.jei.gui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.common.platform.Services;
import net.minecraft.world.level.material.Fluid;

public class FocusUtil {
   private final IFocusFactory focusFactory;
   private final IClientConfig clientConfig;
   private final IIngredientManager ingredientManager;

   public FocusUtil(IFocusFactory focusFactory, IClientConfig clientConfig, IIngredientManager ingredientManager) {
      this.focusFactory = focusFactory;
      this.clientConfig = clientConfig;
      this.ingredientManager = ingredientManager;
   }

   public List<IFocus<?>> createFocuses(ITypedIngredient<?> ingredient, List<RecipeIngredientRole> roles) {
      List<ITypedIngredient<?>> ingredients = new ArrayList<>();
      ingredients.add(ingredient);
      if (this.clientConfig.lookupFluidContentsEnabled().getValue()) {
         IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
         this.getContainedFluid(fluidHelper, ingredient).ifPresent(ingredients::add);
      }

      return roles.stream().flatMap(role -> ingredients.stream().map(i -> this.focusFactory.createFocus(role, (ITypedIngredient<?>)i))).toList();
   }

   private <T> Optional<ITypedIngredient<T>> getContainedFluid(IPlatformFluidHelperInternal<T> fluidHelper, ITypedIngredient<?> ingredient) {
      return fluidHelper.getContainedFluid(ingredient).flatMap(fluid -> {
         IIngredientTypeWithSubtypes<Fluid, T> type = fluidHelper.getFluidIngredientType();
         return this.ingredientManager.createTypedIngredient(type, (T)fluid, false);
      });
   }
}
