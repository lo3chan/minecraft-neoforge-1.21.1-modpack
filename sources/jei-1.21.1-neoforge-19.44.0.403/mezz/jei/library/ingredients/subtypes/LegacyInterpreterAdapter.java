package mezz.jei.library.ingredients.subtypes;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;

public class LegacyInterpreterAdapter<T> implements ISubtypeInterpreter<T> {
   private final IIngredientSubtypeInterpreter<T> legacyInterpreter;

   public LegacyInterpreterAdapter(IIngredientSubtypeInterpreter<T> legacyInterpreter) {
      this.legacyInterpreter = legacyInterpreter;
   }

   @Override
   public Object getSubtypeData(T ingredient, UidContext context) {
      String result = this.legacyInterpreter.apply(ingredient, context);
      return result.isEmpty() ? null : result;
   }

   @Override
   public String getLegacyStringSubtypeInfo(T ingredient, UidContext context) {
      return this.legacyInterpreter.apply(ingredient, context);
   }
}
