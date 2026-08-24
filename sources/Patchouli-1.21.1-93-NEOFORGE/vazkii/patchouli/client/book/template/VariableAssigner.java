package vazkii.patchouli.client.book.template;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.IVariablesAvailableCallback;
import vazkii.patchouli.common.util.EntityUtil;

public class VariableAssigner {
   private static final Pattern INLINE_VAR_PATTERN = Pattern.compile("([^#]*)(#[^#]+)#(.*)");
   private static final Pattern FUNCTION_PATTERN = Pattern.compile("(.+)->(.+)");
   private static final Map<String, BiFunction<IVariable, Provider, IVariable>> FUNCTIONS = new HashMap<>();

   public static void assignVariableHolders(
      Level level, IVariablesAvailableCallback object, IVariableProvider variables, IComponentProcessor processor, TemplateInclusion encapsulation
   ) {
      VariableAssigner.Context c = new VariableAssigner.Context(variables, processor, encapsulation);
      object.onVariablesAvailable(input -> {
         if (input == null) {
            return IVariable.empty();
         } else {
            if (input.unwrap().isJsonPrimitive() && input.unwrap().getAsJsonPrimitive().isString()) {
               IVariable resolved = resolveString(level, input.asString(), c);
               if (resolved != null) {
                  return resolved;
               }
            }

            return input;
         }
      }, level.registryAccess());
   }

   private static IVariable resolveString(Level level, @Nullable String curr, VariableAssigner.Context c) {
      if (curr != null && !curr.isEmpty()) {
         String s = curr;

         for (Matcher m = INLINE_VAR_PATTERN.matcher(curr); m.matches(); m = INLINE_VAR_PATTERN.matcher(s)) {
            String before = m.group(1);
            String var = m.group(2);
            String after = m.group(3);
            String resolved = resolveStringFunctions(level, var, c).asString();
            s = String.format("%s%s%s", before, resolved, after);
         }

         return resolveStringFunctions(level, s, c);
      } else {
         return null;
      }
   }

   private static IVariable resolveStringFunctions(Level level, String curr, VariableAssigner.Context c) {
      IVariable cached = c.getCached(curr);
      if (cached != null) {
         return cached;
      } else {
         Matcher m = FUNCTION_PATTERN.matcher(curr);
         if (m.matches()) {
            String funcStr = m.group(2);
            String arg = m.group(1);
            if (FUNCTIONS.containsKey(funcStr)) {
               BiFunction<IVariable, Provider, IVariable> func = FUNCTIONS.get(funcStr);
               IVariable parsedArg = resolveStringFunctions(level, arg, c);
               return c.cache(curr, func.apply(parsedArg, level.registryAccess()));
            } else {
               throw new IllegalArgumentException("Invalid Function " + funcStr);
            }
         } else {
            IVariable ret = resolveStringVar(level, curr, c);
            return c.cache(curr, ret);
         }
      }
   }

   private static IVariable resolveStringVar(Level level, String original, VariableAssigner.Context c) {
      String curr = original;
      IVariable val = null;
      if (original == null) {
         return IVariable.empty();
      } else if (original.startsWith("#")) {
         if (c.encapsulation != null) {
            val = c.encapsulation.attemptVariableLookup(original, level.registryAccess());
            if (val != null) {
               return val;
            }

            curr = c.encapsulation.qualifyName(original);
         }

         String key = curr.startsWith("#") ? curr.substring(1) : curr;
         String originalKey = original.substring(1);
         if (c.processor != null) {
            val = c.processor.process(level, originalKey);
         }

         if (val == null && c.variables.has(key)) {
            val = c.variables.get(key, level.registryAccess());
         }

         return val == null ? IVariable.empty() : val;
      } else {
         return IVariable.wrap(original, level.registryAccess());
      }
   }

   private static BiFunction<IVariable, Provider, IVariable> wrapStringFunc(Function<String, String> inner) {
      return (x, r) -> IVariable.wrap(inner.apply(x.asString()), r);
   }

   private static IVariable iname(IVariable arg, Provider registries) {
      ItemStack stack = arg.as(ItemStack.class);
      return IVariable.wrap(stack.getHoverName().getString(), registries);
   }

   private static IVariable icount(IVariable arg, Provider registries) {
      ItemStack stack = arg.as(ItemStack.class);
      return IVariable.wrap(stack.getCount(), registries);
   }

   private static IVariable exists(IVariable arg, Provider registries) {
      return IVariable.wrap(!arg.unwrap().isJsonNull(), registries);
   }

   private static IVariable iexists(IVariable arg, Provider registries) {
      ItemStack stack = arg.as(ItemStack.class);
      return IVariable.wrap(stack != null && !stack.isEmpty(), registries);
   }

   private static IVariable inv(IVariable arg, Provider registries) {
      return IVariable.wrap(!arg.unwrap().getAsBoolean(), registries);
   }

   private static IVariable stacks(IVariable arg, Provider registries) {
      return IVariable.from(arg.as(Ingredient.class).getItems(), registries);
   }

   private static String ename(String arg) {
      return EntityUtil.getEntityName(arg);
   }

   static {
      FUNCTIONS.put("iname", VariableAssigner::iname);
      FUNCTIONS.put("icount", VariableAssigner::icount);
      FUNCTIONS.put("ename", wrapStringFunc(VariableAssigner::ename));
      FUNCTIONS.put("lower", wrapStringFunc(String::toLowerCase));
      FUNCTIONS.put("upper", wrapStringFunc(String::toUpperCase));
      FUNCTIONS.put("trim", wrapStringFunc(String::trim));
      FUNCTIONS.put("capital", wrapStringFunc(WordUtils::capitalize));
      FUNCTIONS.put("fcapital", wrapStringFunc(WordUtils::capitalizeFully));
      FUNCTIONS.put("i18n", wrapStringFunc(x$0 -> I18n.get(x$0, new Object[0])));
      FUNCTIONS.put("exists", VariableAssigner::exists);
      FUNCTIONS.put("iexists", VariableAssigner::iexists);
      FUNCTIONS.put("inv", VariableAssigner::inv);
      FUNCTIONS.put("stacks", VariableAssigner::stacks);
   }

   private static class Context {
      final IVariableProvider variables;
      final IComponentProcessor processor;
      final TemplateInclusion encapsulation;
      final Map<String, IVariable> cachedVars = new HashMap<>();

      Context(IVariableProvider variables, IComponentProcessor processor, TemplateInclusion encapsulation) {
         this.variables = variables;
         this.processor = processor;
         this.encapsulation = encapsulation;
      }

      IVariable getCached(String s) {
         return this.cachedVars.get(s);
      }

      IVariable cache(String k, IVariable v) {
         this.cachedVars.put(k, v);
         return v;
      }
   }
}
