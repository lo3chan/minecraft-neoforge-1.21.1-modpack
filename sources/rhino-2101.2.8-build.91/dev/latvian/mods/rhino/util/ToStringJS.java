package dev.latvian.mods.rhino.util;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import java.util.Map;
import java.util.Map.Entry;

public interface ToStringJS {
   static String toStringJS(Context cx, Object o) {
      o = Wrapper.unwrapped(o);

      return switch (o) {
         case null -> "null";
         case ToStringJS toStringJS -> toStringJS.toStringJS(cx);
         case Iterable<?> itr -> {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            boolean first = true;

            for (Object i : itr) {
               if (!first) {
                  sb.append(", ");
               }

               sb.append(toStringJS(cx, i));
               first = false;
            }

            sb.append(']');
            yield sb.toString();
         }
         case Map<?, ?> map -> {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            sb.append('{');

            for (Entry<?, ?> entry : map.entrySet()) {
               if (!first) {
                  sb.append(", ");
               }

               sb.append(toStringJS(cx, entry.getKey()));
               sb.append(": ");
               sb.append(toStringJS(cx, entry.getValue()));
               first = false;
            }

            sb.append('}');
            yield sb.toString();
         }
         default -> o.toString();
      };
   }

   default String toStringJS(Context cx) {
      return this.toString();
   }
}
