package mezz.jei.api.ingredients.subtypes;

@Deprecated(
   since = "19.9.0",
   forRemoval = true
)
@FunctionalInterface
public interface IIngredientSubtypeInterpreter<T> {
   @Deprecated(
      since = "19.9.0",
      forRemoval = true
   )
   String NONE = "";

   @Deprecated(
      since = "19.9.0",
      forRemoval = true
   )
   String apply(T var1, UidContext var2);
}
