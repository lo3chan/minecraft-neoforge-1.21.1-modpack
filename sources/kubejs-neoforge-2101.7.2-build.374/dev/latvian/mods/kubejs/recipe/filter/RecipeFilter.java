package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface RecipeFilter extends Predicate<RecipeMatchContext> {
   boolean test(RecipeMatchContext cx);

   static RecipeFilter wrap(Context cx, @Nullable Object o) {
      if (o == null || o == ConstantFilter.TRUE) {
         return ConstantFilter.TRUE;
      } else if (o == ConstantFilter.FALSE) {
         return ConstantFilter.FALSE;
      } else if (o instanceof CharSequence || o instanceof NativeRegExp || o instanceof Pattern) {
         String s = o.toString();
         if (s.equals("*")) {
            return ConstantFilter.TRUE;
         } else if (s.equals("-")) {
            return ConstantFilter.FALSE;
         } else {
            Pattern r = RegExpKJS.wrap(s);
            return (RecipeFilter)(r == null ? new IDFilter(ID.mc(s)) : RegexIDFilter.of(r));
         }
      } else if (o instanceof BaseFunction func) {
         return (RecipeFilter)cx.createInterfaceAdapter(TypeInfo.of(RecipeFilter.class), func);
      } else {
         List<?> list = ListJS.orSelf(o);
         if (list.isEmpty()) {
            return ConstantFilter.FALSE;
         } else if (list.size() > 1) {
            OrFilter predicate = new OrFilter();

            for (Object o1 : list) {
               RecipeFilter p = wrap(cx, o1);
               if (p == ConstantFilter.TRUE) {
                  return ConstantFilter.TRUE;
               }

               if (p != ConstantFilter.FALSE) {
                  predicate.list.add(p);
               }
            }

            return (RecipeFilter)(predicate.list.isEmpty()
               ? ConstantFilter.FALSE
               : (predicate.list.size() == 1 ? (RecipeFilter)predicate.list.getFirst() : predicate));
         } else {
            Map<String, Object> map = cx.optionalMapOf(list.getFirst());
            if (map != null && !map.isEmpty()) {
               AndFilter predicate = new AndFilter();
               if (map.get("or") != null) {
                  predicate.list.add(wrap(cx, map.get("or")));
               }

               if (map.get("not") != null) {
                  predicate.list.add(new NotFilter(wrap(cx, map.get("not"))));
               }

               try {
                  Object id = map.get("id");
                  if (id != null) {
                     Pattern pattern = RegExpKJS.wrap(id);
                     predicate.list.add((RecipeFilter)(pattern == null ? new IDFilter(ID.mc(id)) : RegexIDFilter.of(pattern)));
                  }

                  Object type = map.get("type");
                  if (type != null) {
                     predicate.list.add(new TypeFilter(ID.mc(type)));
                  }

                  Object group = map.get("group");
                  if (group != null) {
                     predicate.list.add(new GroupFilter(group.toString()));
                  }

                  Object mod = map.get("mod");
                  if (mod != null) {
                     predicate.list.add(new ModFilter(mod.toString()));
                  }

                  Object input = map.get("input");
                  if (input != null) {
                     ReplacementMatchInfo m = ReplacementMatchInfo.wrap(cx, input, ReplacementMatchInfo.TYPE_INFO);
                     if (m == ReplacementMatchInfo.NONE) {
                        throw Context.reportRuntimeError("Unable to parse recipe input filter `" + input + "`", cx);
                     }

                     predicate.list.add(new InputFilter(m));
                  }

                  Object output = map.get("output");
                  if (output != null) {
                     ReplacementMatchInfo m = ReplacementMatchInfo.wrap(cx, output, ReplacementMatchInfo.TYPE_INFO);
                     if (m == ReplacementMatchInfo.NONE) {
                        throw Context.reportRuntimeError("Unable to parse recipe output filter `" + output + "`", cx);
                     }

                     predicate.list.add(new OutputFilter(m));
                  }

                  NeoForge.EVENT_BUS.post(new RecipeFilterParseEvent(cx, predicate.list, map));
                  if (predicate.list.isEmpty() && !map.isEmpty()) {
                     throw Context.reportRuntimeError("Unable to parse recipe filter " + map, cx);
                  } else {
                     return (RecipeFilter)(predicate.list.isEmpty()
                        ? ConstantFilter.TRUE
                        : (predicate.list.size() == 1 ? (RecipeFilter)predicate.list.getFirst() : predicate));
                  }
               } catch (KubeRuntimeException var12) {
                  ConsoleJS.getCurrent(cx).warn("", var12, RecipesKubeEvent.POST_SKIP_ERROR);
                  return ConstantFilter.FALSE;
               }
            } else {
               return ConstantFilter.TRUE;
            }
         }
      }
   }
}
