package me.lucko.spark.lib.adventure.text;

import java.util.Objects;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.internal.Internals;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class TranslationArgumentImpl implements TranslationArgument {
   private static final Component TRUE = Component.text("true");
   private static final Component FALSE = Component.text("false");
   private final Object value;

   TranslationArgumentImpl(final Object value) {
      this.value = value;
   }

   @NotNull
   @Override
   public Object value() {
      return this.value;
   }

   @NotNull
   @Override
   public Component asComponent() {
      if (this.value instanceof Component) {
         return (Component)this.value;
      } else if (this.value instanceof Boolean) {
         return (Boolean)this.value ? TRUE : FALSE;
      } else {
         return Component.text(String.valueOf(this.value));
      }
   }

   @Override
   public boolean equals(@Nullable final Object other) {
      if (this == other) {
         return true;
      } else if (other != null && this.getClass() == other.getClass()) {
         TranslationArgumentImpl that = (TranslationArgumentImpl)other;
         return Objects.equals(this.value, that.value);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.value);
   }

   @Override
   public String toString() {
      return Internals.toString(this);
   }

   @NotNull
   @Override
   public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(ExaminableProperty.of("value", this.value));
   }
}
