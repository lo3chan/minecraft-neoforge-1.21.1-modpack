package me.lucko.spark.lib.adventure.key;

import java.util.Comparator;
import java.util.OptionalInt;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Key extends Comparable<Key>, Examinable, Namespaced, Keyed {
   String MINECRAFT_NAMESPACE = "minecraft";
   char DEFAULT_SEPARATOR = ':';

   @NotNull
   static Key key(@NotNull @KeyPattern final String string) {
      return key(string, ':');
   }

   @NotNull
   static Key key(@NotNull final String string, final char character) {
      int index = string.indexOf(character);
      String namespace = index >= 1 ? string.substring(0, index) : "minecraft";
      String value = index >= 0 ? string.substring(index + 1) : string;
      return key(namespace, value);
   }

   @NotNull
   static Key key(@NotNull final Namespaced namespaced, @NotNull @KeyPattern.Value final String value) {
      return key(namespaced.namespace(), value);
   }

   @NotNull
   static Key key(@NotNull @KeyPattern.Namespace final String namespace, @NotNull @KeyPattern.Value final String value) {
      return new KeyImpl(namespace, value);
   }

   @NotNull
   static Comparator<? super Key> comparator() {
      return KeyImpl.COMPARATOR;
   }

   static boolean parseable(@Nullable final String string) {
      if (string == null) {
         return false;
      } else {
         int index = string.indexOf(58);
         String namespace = index >= 1 ? string.substring(0, index) : "minecraft";
         String value = index >= 0 ? string.substring(index + 1) : string;
         return parseableNamespace(namespace) && parseableValue(value);
      }
   }

   static boolean parseableNamespace(@NotNull final String namespace) {
      return !checkNamespace(namespace).isPresent();
   }

   @NotNull
   static OptionalInt checkNamespace(@NotNull final String namespace) {
      int i = 0;

      for (int length = namespace.length(); i < length; i++) {
         if (!allowedInNamespace(namespace.charAt(i))) {
            return OptionalInt.of(i);
         }
      }

      return OptionalInt.empty();
   }

   static boolean parseableValue(@NotNull final String value) {
      return !checkValue(value).isPresent();
   }

   @NotNull
   static OptionalInt checkValue(@NotNull final String value) {
      int i = 0;

      for (int length = value.length(); i < length; i++) {
         if (!allowedInValue(value.charAt(i))) {
            return OptionalInt.of(i);
         }
      }

      return OptionalInt.empty();
   }

   static boolean allowedInNamespace(final char character) {
      return KeyImpl.allowedInNamespace(character);
   }

   static boolean allowedInValue(final char character) {
      return KeyImpl.allowedInValue(character);
   }

   @NotNull
   @KeyPattern.Namespace
   @Override
   String namespace();

   @NotNull
   @KeyPattern.Value
   String value();

   @NotNull
   String asString();

   @NotNull
   default String asMinimalString() {
      return this.namespace().equals("minecraft") ? this.value() : this.asString();
   }

   @NotNull
   @Override
   default Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(ExaminableProperty.of("namespace", this.namespace()), ExaminableProperty.of("value", this.value()));
   }

   default int compareTo(@NotNull final Key that) {
      return comparator().compare(this, that);
   }

   @NotNull
   @Override
   default Key key() {
      return this;
   }
}
