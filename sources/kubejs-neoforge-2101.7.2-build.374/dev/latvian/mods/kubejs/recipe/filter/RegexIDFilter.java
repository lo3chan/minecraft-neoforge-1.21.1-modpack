package dev.latvian.mods.kubejs.recipe.filter;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;

public class RegexIDFilter implements RecipeFilter {
   private final Pattern pattern;
   private final ConcurrentHashMap<ResourceLocation, Boolean> matchCache = new ConcurrentHashMap<>();
   private static Interner<RegexIDFilter> INTERNER;

   private RegexIDFilter(Pattern i) {
      this.pattern = i;
   }

   public static RegexIDFilter of(Pattern i) {
      return (RegexIDFilter)INTERNER.intern(new RegexIDFilter(i));
   }

   public static void clearInternCache() {
      INTERNER = Interners.newStrongInterner();
   }

   @Override
   public boolean test(RecipeMatchContext cx) {
      return this.matchCache.computeIfAbsent(cx.recipe().kjs$getOrCreateId(), location -> this.pattern.matcher(location.toString()).find());
   }

   @Override
   public String toString() {
      return "RegexIDFilter{pattern=" + this.pattern + "}";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         RegexIDFilter that = (RegexIDFilter)o;
         return this.pattern.pattern().equals(that.pattern.pattern()) && this.pattern.flags() == that.pattern.flags();
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.pattern.pattern(), this.pattern.flags());
   }

   static {
      clearInternCache();
   }
}
