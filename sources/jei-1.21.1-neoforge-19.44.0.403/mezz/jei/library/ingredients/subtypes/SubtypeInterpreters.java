package mezz.jei.library.ingredients.subtypes;

import java.util.IdentityHashMap;
import java.util.Map;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import org.jetbrains.annotations.Nullable;

public class SubtypeInterpreters {
   private final Map<Object, ISubtypeInterpreter<?>> map = new IdentityHashMap<>();

   public <B, I> boolean addInterpreter(IIngredientTypeWithSubtypes<B, I> type, B base, ISubtypeInterpreter<I> interpreter) {
      if (!type.getIngredientBaseClass().isInstance(base)) {
         throw new IllegalArgumentException(String.format("base must be instance of %s but got %s instead", type.getIngredientBaseClass(), base.getClass()));
      } else if (this.map.containsKey(base)) {
         return false;
      } else {
         this.map.put(base, interpreter);
         return true;
      }
   }

   public <B, I> boolean addInterpreter(IIngredientTypeWithSubtypes<B, I> type, B base, IIngredientSubtypeInterpreter<I> interpreter) {
      if (!type.getIngredientBaseClass().isInstance(base)) {
         throw new IllegalArgumentException(String.format("base must be instance of %s but got %s instead", type.getIngredientBaseClass(), base.getClass()));
      } else if (this.map.containsKey(base)) {
         return false;
      } else {
         this.map.put(base, new LegacyInterpreterAdapter<>(interpreter));
         return true;
      }
   }

   @Nullable
   public <B, I> ISubtypeInterpreter<I> get(IIngredientTypeWithSubtypes<B, I> type, I ingredient) {
      B base = type.getBase(ingredient);
      return this.getFromBase(type, base);
   }

   @Nullable
   public <B, I> ISubtypeInterpreter<I> getFromBase(IIngredientTypeWithSubtypes<B, I> type, B ingredientBase) {
      return (ISubtypeInterpreter<I>)this.map.get(ingredientBase);
   }

   public <B, T> boolean contains(IIngredientTypeWithSubtypes<B, T> type, T ingredient) {
      B base = type.getBase(ingredient);
      return this.map.containsKey(base);
   }
}
