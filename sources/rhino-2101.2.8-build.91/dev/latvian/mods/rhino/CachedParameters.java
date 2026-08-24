package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public record CachedParameters(int count, List<Class<?>> types, List<TypeInfo> typeInfos, boolean firstArgContext, @Nullable TypeInfo varArgType) {
   public static final CachedParameters EMPTY = new CachedParameters(0, List.of(), List.of(), false, null);
   public static final CachedParameters EMPTY_FIRST_CX = new CachedParameters(0, List.of(), List.of(), true, null);

   public boolean typesMatch(Class<?>[] params) {
      if (params.length != this.types.size()) {
         return false;
      } else {
         for (int i = 0; i < params.length; i++) {
            if (this.types.get(i) != params[i]) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean isVarArg() {
      return this.varArgType != null;
   }
}
