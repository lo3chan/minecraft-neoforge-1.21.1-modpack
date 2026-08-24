package dev.latvian.mods.rhino.util;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.EnumTypeInfo;

public interface SpecialEquality {
   static boolean checkSpecialEquality(Context cx, Object o, Object o1, boolean shallow) {
      if (o == o1) {
         return true;
      } else if (o instanceof SpecialEquality s) {
         return s.specialEquals(cx, o1, shallow);
      } else if (o1 != null && o instanceof Enum<?> e) {
         return o1 instanceof Number ? e.ordinal() == ((Number)o1).intValue() : EnumTypeInfo.getName(e).equalsIgnoreCase(String.valueOf(o1));
      } else {
         return false;
      }
   }

   default boolean specialEquals(Context cx, Object o, boolean shallow) {
      return this.equals(o);
   }
}
