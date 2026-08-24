package dev.latvian.mods.rhino.type;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemappedEnumConstant;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class EnumTypeInfo extends ClassTypeInfo implements TypeWrapperFactory<Object> {
   private List<Object> constants;
   private Map<String, Object> constantMap;

   public static String getName(Object e) {
      if (e instanceof RemappedEnumConstant c) {
         String s = c.getRemappedEnumConstantName();
         if (!s.isEmpty()) {
            return s;
         }
      }

      return ((Enum)e).name();
   }

   EnumTypeInfo(Class<?> type) {
      super(type);
   }

   @Override
   public List<Object> enumConstants() {
      if (this.constants == null) {
         this.constants = List.of(this.asClass().getEnumConstants());
      }

      return this.constants;
   }

   @Override
   public Object wrap(Context cx, Object from, TypeInfo target) {
      if (from instanceof CharSequence) {
         String s = from.toString();
         if (s.isEmpty()) {
            return null;
         } else {
            List<Object> constants = this.enumConstants();
            if (this.constantMap == null) {
               this.constantMap = new HashMap<>(constants.size());

               for (Object entry : constants) {
                  String name = getName(entry);
                  this.constantMap.put(name.toLowerCase(Locale.ROOT), entry);
                  this.constantMap.put(name, entry);
               }
            }

            Object lookup = this.constantMap.get(s);
            if (lookup != null) {
               return lookup;
            } else {
               for (Object entry : constants) {
                  if (getName(entry).equalsIgnoreCase(s)) {
                     return entry;
                  }
               }

               throw new IllegalArgumentException(
                  "'"
                     + s
                     + "' is not a valid enum constant! Valid values are: "
                     + constants.stream().map(EnumTypeInfo::getName).map(s1 -> "'" + s1 + "'").collect(Collectors.joining(", "))
               );
            }
         }
      } else if (from instanceof Number) {
         int index = ((Number)from).intValue();
         List<Object> constantsx = this.enumConstants();
         if (index >= 0 && index < constantsx.size()) {
            return constantsx.get(index);
         } else {
            throw new IllegalArgumentException(index + " is not a valid enum index! Valid values are: 0 - " + (constantsx.size() - 1));
         }
      } else {
         return from;
      }
   }
}
