package dev.latvian.mods.kubejs.util.registrypredicate;

import dev.latvian.mods.kubejs.util.RegExpKJS;
import java.util.regex.Pattern;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;

public record RegistryRegExpPredicate<T>(Pattern pattern) implements RegistryPredicate<T> {
   public boolean test(Holder<T> holder) {
      try {
         return holder instanceof Reference<T> ref
            ? this.pattern.matcher(ref.key().location().toString()).find()
            : this.pattern.matcher(holder.getKey().location().toString()).find();
      } catch (Exception var3) {
         return false;
      }
   }

   @Override
   public String toString() {
      return RegExpKJS.toRegExpString(this.pattern);
   }
}
