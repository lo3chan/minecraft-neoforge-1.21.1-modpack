package dev.latvian.mods.kubejs.util.registrypredicate;

import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public record RegistryTagIDPredicate<T>(RegistryTagIDPredicate.TagKeyPredicate predicate) implements RegistryPredicate<T> {
   public RegistryTagIDPredicate(ResourceLocation tag) {
      this(new RegistryTagIDPredicate.TagKeyPredicate(tag));
   }

   public boolean test(Holder<T> holder) {
      return holder.tags().anyMatch(this.predicate);
   }

   @Override
   public String toString() {
      return this.predicate.toString();
   }

   private record TagKeyPredicate(ResourceLocation tag) implements Predicate<TagKey<?>> {
      public boolean test(TagKey<?> key) {
         return key.location().equals(this.tag);
      }

      @Override
      public String toString() {
         return "#" + this.tag;
      }
   }
}
