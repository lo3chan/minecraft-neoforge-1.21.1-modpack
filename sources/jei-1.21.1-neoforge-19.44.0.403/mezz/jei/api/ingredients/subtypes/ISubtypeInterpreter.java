package mezz.jei.api.ingredients.subtypes;

import org.jetbrains.annotations.Nullable;

public interface ISubtypeInterpreter<T> {
   @Nullable
   Object getSubtypeData(T var1, UidContext var2);

   @Deprecated(
      since = "19.9.0"
   )
   String getLegacyStringSubtypeInfo(T var1, UidContext var2);
}
