package mezz.jei.library.gui.recipes.layout.builder;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;

public class IngredientAcceptorVoid implements IIngredientAcceptor<IngredientAcceptorVoid> {
   public static final IngredientAcceptorVoid INSTANCE = new IngredientAcceptorVoid();

   private IngredientAcceptorVoid() {
   }

   public IngredientAcceptorVoid addIngredientsUnsafe(List<?> ingredients) {
      return this;
   }

   public <I> IngredientAcceptorVoid addIngredients(IIngredientType<I> ingredientType, List<I> ingredients) {
      return this;
   }

   public <I> IngredientAcceptorVoid addIngredient(IIngredientType<I> ingredientType, I ingredient) {
      return this;
   }

   public IngredientAcceptorVoid addTypedIngredients(List<ITypedIngredient<?>> ingredients) {
      return this;
   }

   public IngredientAcceptorVoid addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> ingredients) {
      return this;
   }

   public IngredientAcceptorVoid addFluidStack(Fluid fluid) {
      return this;
   }

   public IngredientAcceptorVoid addFluidStack(Fluid fluid, long amount) {
      return this;
   }

   public IngredientAcceptorVoid addFluidStack(Fluid fluid, long amount, DataComponentPatch componentPatch) {
      return this;
   }
}
