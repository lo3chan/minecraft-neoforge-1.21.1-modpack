package mezz.jei.api.ingredients;

public interface IIngredientTypeWithSubtypes<B, I> extends IIngredientType<I> {
   @Override
   Class<? extends I> getIngredientClass();

   Class<? extends B> getIngredientBaseClass();

   B getBase(I var1);

   default I getDefaultIngredient(B base) {
      throw new UnsupportedOperationException();
   }
}
